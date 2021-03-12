package dev.atsushieno.mugene

//region mml token sequence structure

enum class MmlTokenType {
    None,
    Identifier,
    StringLiteral,
    NumberLiteral,
    Period,
    Comma,
    Percent,
    OpenParen,
    CloseParen,
    OpenCurly,
    CloseCurly,
    Question,
    Plus,
    Minus,
    Asterisk,
    Slash,
    Dollar,
    Colon,
    Caret,
    BackSlashLesser,
    BackSlashLesserEqual,
    BackSlashGreater,
    BackSlashGreaterEqual,
    KeywordNumber,
    KeywordLength,
    KeywordString,
    KeywordBuffer,
}

class MmlTokenSet {
    constructor () {
        baseCount = 192
        conditional = MmlCompilationCondition()
        macros = mutableListOf()
        variables = mutableListOf()
        tracks = mutableListOf()
        metaTexts = mutableListOf()
    }

    var baseCount = 0

    var conditional: MmlCompilationCondition
    var macros: MutableList<MmlMacroDefinition>
    var variables: MutableList<MmlVariableDefinition>
    var tracks: MutableList<MmlTrack>
    var metaTexts: MutableList<MmlMetaTextToken>

    fun getTrack(number: Double): MmlTrack {
        var t = tracks.firstOrNull { tr -> tr.number == number }
        if (t == null) {
            t = MmlTrack(number)
            tracks.add(t)
        }
        return t
    }
}

data class MmlMetaTextToken(
    var metaType: Byte = 0,
    var typeLocation: MmlLineInfo? = null,
    var text: String? = null,
    var textLocation: MmlLineInfo? = null
)

class MmlCompilationCondition {
    val blocks = mutableListOf<String>()
    val tracks = mutableListOf<Double>()

    fun shouldCompileBlock(name: String?) =
        name == null || blocks.size == 0 || blocks.contains(name)

    fun shouldCompileTrack(track: Double) = tracks.size == 0 || tracks.contains(track)
}

data class MmlToken(
    val tokenType: MmlTokenType,
    val value: Any? = null,
    val location: MmlLineInfo
)

abstract class MmlOperationDefinition {
    val arguments = mutableListOf<MmlVariableDefinition>()
}

class MmlMacroDefinition(val name: String, targetTracks: List<Double>, var location: MmlLineInfo) :
    MmlOperationDefinition() {

    var targetTracks: MutableList<Double> = targetTracks.toMutableList()
    var tokens: MutableList<MmlToken> = mutableListOf()

}

class MmlVariableDefinition(val name: String, var location: MmlLineInfo) {

    lateinit var type: MmlDataType
    var defaultValueTokens: MutableList<MmlToken> = mutableListOf()

}

class MmlTrack(val number: Double) {

    var tokens: MutableList<MmlToken> = mutableListOf()

}



// file sources to be parsed into MmlSourceLineSet, for each track
// and macro.
data class MmlInputSource(val file: String, val text: String)

class MmlLineInfo(var file: String, line: Int, column: Int) {
    companion object {
        val empty = MmlLineInfo("__internal__", 0, 0)
    }

    var lineNumber: Int = line
    var linePosition: Int = column

    fun clone() = MmlLineInfo(file, lineNumber, linePosition)

    override fun toString() = "Location: $file ($lineNumber, $linePosition)"
}

class MmlLine(val location: MmlLineInfo, var text: String) {
    companion object {
        fun create(file: String, lineNumber: Int, linePosition: Int, text: String) =
            MmlLine(MmlLineInfo(file, lineNumber, linePosition), text)
    }

    fun tryMatch(target: String): Boolean {
        if (location.linePosition + target.length > text.length)
            return false
        if (text.indexOf(target, location.linePosition) < 0)
            return false
        location.linePosition += target.length
        return true
    }

    fun peekChar(): Int {
        if (location.linePosition == text.length)
            return -1
        return text[location.linePosition].toInt()
    }

    fun readChar(): Int {
        if (location.linePosition == text.length)
            return -1
        return text[location.linePosition++].toInt()
    }
}

class MmlTokenizerSource(compiler: MmlCompiler) {
    // can be switched
    var lexer: MmlLexer = MmlMatchLongestLexer(compiler, this)

    // It holds ongoing definition of a macro. Used for argument name lookup.
    var currentMacroDefinition: MmlMacroDefinition? = null

    // It does not differentiate tracks, but contains all of the mml track lines.
    val tracks = mutableListOf<MmlTrackSource>()

    // contains macros.
    val macros = mutableListOf<MmlMacroSource>()

    // contains variables.
    val variables = mutableListOf<MmlVariableSource>()

    // contains any other pragma directives.
    val pragmas = mutableListOf<MmlPragmaSource>()

    val primitiveOperations = mutableListOf<String>()

    init {
        for (primitive in MmlPrimitiveOperation.all)
            primitiveOperations.add(primitive.name)
    }
}

class MmlInputSourceReader(private val compiler: MmlCompiler) {
    companion object {
        fun parse(compiler: MmlCompiler, inputs: List<MmlInputSource>): MmlTokenizerSource {
            val r = MmlInputSourceReader(compiler)
            r.process(inputs)
            return r.result
        }

        fun trimComments(s: String, start: Int): String {
            val idx2 = s.indexOf("//", start, false)
            if (idx2 < 0)
                return s
            val idx1 = s.indexOf('"', start)
            if (idx1 < 0 || idx2 < idx1)
                return s.substring(0, idx2)
            val idx3 = s.indexOf('"', idx1 + 1)
            if (idx3 < 0) // it is invalid, but I don't care here
                return s.substring(0, idx2)
            if (idx3 > idx2)
                return trimComments(s, idx3 + 1) // skip this "//" inside literal
            else
                return trimComments(
                    s,
                    idx3 + 1
                ) // skip this literal. There still may be another literal to care.
        }
    }

    private lateinit var result: MmlTokenizerSource

    private var inCommentMode = false

    fun process(inputs: List<MmlInputSource>) {
        result = MmlTokenizerSource(compiler)
        doProcess(inputs.toMutableList())
    }

    private fun doProcess(inputs: MutableList<MmlInputSource>) {

        for (i in 0 until inputs.size) {  // inputs could grow up, so avoid converting to range
            var line = 0
            var s = ""
            var ls: MmlSourceLineSet? = null
            val input = inputs[i]
            compiler.resolver.pushInclude(input.file)
            var continued = false
            var wasContinued = continued
            for (ss in input.text.split('\n')) {
                line++
                wasContinued = continued
                s = trimComments(ss, 0).trimEnd()
                if (s.isEmpty()) // comment line is allowed inside multi-line MML.
                    continue

                continued = (s[s.length - 1] == '\\')
                if (continued)
                    s = s.substring(0, s.length - 1)
                if (wasContinued) {
                    if (!inCommentMode)
                        ls?.addConsecutiveLine(s)
                    continue
                }
                ls = if (s[0] == '#')
                    processPragmaLine(MmlLine.create(input.file, line, 0, s))
                else
                    processTrackLine(MmlLine.create(input.file, line, 0, s))
            }
            if (wasContinued)
                throw mmlError(
                    input,
                    line,
                    s.length - 1,
                    "Unexpected end of consecutive line by '\\' at the end of file"
                )
            compiler.resolver.popInclude()
        }
    }

    private fun processPragmaLine(line: MmlLine): MmlSourceLineSet? {
        result.lexer.setCurrentInput(line)
        line.location.linePosition++
        // get identifier
        val identifier = result.lexer.readNewIdentifier()
        when (identifier) {
            "include" -> {
                result.lexer.skipWhitespaces(true)
                return processIncludeLine(line)
            }
            "variable" -> {
                result.lexer.skipWhitespaces(true)
                return processVariableLine(line)
            }
            "macro" -> {
                result.lexer.skipWhitespaces(true)
                return processMacroLine(line)
            }
            "comment" -> {
                inCommentMode = true
                return null
            }
            "endcomment" -> {
                inCommentMode = false
                return null
            }
            "define", "conditional", "meta", "basecount" -> {
            }
            else -> throw mmlError(line.location, "Unexpected preprocessor directive: $identifier")
        }

        result.lexer.skipWhitespaces(true)
        val ps = MmlPragmaSource(identifier)
        ps.lines.add(line)
        result.pragmas.add(ps)
        return ps
    }

    private fun processIncludeLine(line: MmlLine): MmlSourceLineSet? {
        val file = line.text.substring(line.location.linePosition).trim()
        this.doProcess(
            mutableListOf(
                MmlInputSource(file, compiler.resolver.getEntity(file).readText())
            )
        )
        return MmlUntypedSource(line)
    }

    private fun processMacroLine(line: MmlLine): MmlSourceLineSet? {
        if (inCommentMode)
            return null
        val mms = MmlMacroSource()
        mms.lines.add(line)
        result.macros.add(mms)
        return mms
    }

    private fun processVariableLine(line: MmlLine): MmlSourceLineSet? {
        if (inCommentMode)
            return null
        val vs = MmlVariableSource()
        vs.lines.add(line)
        result.variables.add(vs)
        return vs
    }

    private var previous_section = ""
    private var previous_range: List<Double>? = null

    private fun processTrackLine(line: MmlLine): MmlSourceLineSet? {
        if (inCommentMode)
            return null
        result.lexer.setCurrentInput(line)

        var section = previous_section
        var range = previous_range
        if (result.lexer.isWhitespace(line.peekChar()))
            result.lexer.skipWhitespaces(true)
        else {
            if (result.lexer.isIdentifier(line.peekChar(), true)) {
                section = result.lexer.readNewIdentifier()
                result.lexer.skipWhitespaces(false)
            }
            if (result.lexer.isNumber(line.peekChar())) {
                range = result.lexer.readRange(false).toList()
                result.lexer.skipWhitespaces(true)
            }
        }
        if (range == null) {
            compiler.report(
                MmlDiagnosticVerbosity.Error,
                line.location,
                "Current line indicates no track number, and there was no indicated tracks previously.",
                listOf()
            )
            return null
        }

        previous_section = section
        previous_range = range
        result.lexer.skipWhitespaces(false)
        val ts = MmlTrackSource(section, range)
        ts.lines.add(line)
        result.tracks.add(ts)
        return ts
    }

    private fun mmlError(location: MmlLineInfo, msg: String): MmlException {
        return MmlException(msg, location)
    }

    private fun mmlError(input: MmlInputSource, line: Int, column: Int, msg: String): MmlException {
        return MmlException(msg, MmlLineInfo(input.file, line, column))
    }
}


// tokenizer sources to parsed mml tokens

// represents a set of lines for either a macro or a track lines.

abstract class MmlSourceLineSet {
    val lines = mutableListOf<MmlLine>()

    open fun addConsecutiveLine(text: String) {
        if (lines.size == 0)
            throw IllegalStateException("Unexpected addition to previous line while there was no registered line.")
        val prev = lines.last()
        val line = MmlLine.create(
            prev.location.file,
            prev.location.lineNumber + 1,
            prev.location.linePosition,
            text
        )
        lines.add(line)
    }
}

class MmlUntypedSource(singleLine: MmlLine) : MmlSourceLineSet() {
    init {
        lines.add(singleLine)
    }
}

class MmlTrackSource(val blockName: String, tracks: List<Double>) : MmlSourceLineSet() {

    val tracks: MutableList<Double> = tracks.toMutableList()

}

class MmlMacroSource(var parsedName: String? = null) : MmlSourceLineSet() {}

class MmlVariableSource(val parsedNames: MutableList<String> = mutableListOf()) :
    MmlSourceLineSet() {}

class MmlPragmaSource(val name: String) : MmlSourceLineSet()

abstract class MmlLexer(internal val compiler: MmlCompiler, source: MmlTokenizerSource) {

    private var input: MmlSourceLineSet? = null
    private var currentLine: Int = 0

    // It contains all macro definitions.
    val tokenizerSource: MmlTokenizerSource = source

    val line: MmlLine
        get() {
            val l = input!!.lines[currentLine]
            if (l.location.linePosition == l.text.length && currentLine + 1 < input!!.lines.size) {
                currentLine++
                return line
            }
            return input!!.lines[currentLine]
        }


    var currentToken: MmlTokenType = MmlTokenType.None
    var value: Any? = null

    var newIdentifierMode = false

    fun setCurrentInput(line: MmlLine) = setCurrentInput(MmlUntypedSource(line))

    fun setCurrentInput(input: MmlSourceLineSet) {
        this.input = input
        currentLine = 0
    }

    internal fun lexerError(msg: String): MmlException {
        return MmlException(msg, line.location)
    }

    fun createParsedToken(): MmlToken = MmlToken(currentToken, this.value, currentLocation)

    open fun isWhitespace(ch: Int) = ch == ' '.toInt() || ch == '\t'.toInt()

    open fun skipWhitespaces() = skipWhitespaces(false)

    open fun skipWhitespaces(mandatory: Boolean) {
        if (mandatory && !isWhitespace(line.peekChar()))
            throw lexerError("Whitespaces are expected")

        while (isWhitespace(line.peekChar()))
            line.readChar()
    }

    open fun isNumber(c: Int) = '0'.toInt() <= c && c <= '9'.toInt()

    open fun readNumber(acceptFloatingPoint: Boolean): Double {
        val line = line
        val ch_ = line.peekChar()
        if (ch_ < 0)
            throw lexerError("Expected a number, but reached the end of input")
        var ch = ch_.toChar()
        if (ch != '#' && !isNumber(ch_))
            throw lexerError("Expected a number, but got '$ch'")
        if (ch == '#') {
            line.readChar()
            var value = 0.0
            var passed = false
            while (true) {
                ch = line.peekChar().toChar()
                val isNum = isNumber(ch.toInt())
                val isUpper = ch in 'A'..'F'
                val isLower = ch in 'a'..'f'
                if (!isNum && !isUpper && !isLower) {
                    if (!passed)
                        throw lexerError("Invalid hexadecimal digits")
                    break
                }
                passed = true
                val h =
                    if (isNum) line.readChar() - '0'.toInt()
                    else if (isUpper) line.readChar() - 'A'.toInt() + 10
                    else line.readChar() - 'a'.toInt() + 10
                value = value * 16 + h
            }
            return value
        } else {
            var value = 0.0
            var digits = 0
            var floatingPointAt = 0
            while (true) {
                var ch2 = line.readChar()
                if (ch2 == '.'.toInt())
                    floatingPointAt = digits
                else {
                    value = value * 10 + (ch2 - '0'.toInt())
                    digits++
                }
                ch2 = line.peekChar()
                if (!(acceptFloatingPoint && ch2 == '.'.toInt()) && !isNumber(ch2))
                    break
            }
            return if (floatingPointAt > 0) value * Math.pow(
                0.1,
                (digits - floatingPointAt).toDouble()
            ) else value
        }
    }

    private val stringLiteralBuffer = StringBuilder()

    open fun readStringLiteral(): String {
        val sb = stringLiteralBuffer
        sb.clear()
        line.readChar() // ' or "
        val startLoc = line.location
        while (true) {
            var ch = line.readChar()
            if (ch < 0)
                throw lexerError("Incomplete string literal starting from $startLoc")
            when (ch.toChar()) {
                '"' -> return sb.toString()
                '\\' -> {
                    ch = line.readChar()
                    val cc = ch.toChar()
                    when (cc) {
                        '/' -> sb.append('/') // This is a quick workaround to avoid "//" being treated as comment, even within a string literal
                        '"' -> sb.append('"')
                        '\\' -> sb.append('\\')
                        'r' -> sb.append('\r')
                        'n' -> sb.append('\n')
                        else -> {
                            line.location.linePosition--
                            if (cc == '#' || cc in '0'..'9') {
                                sb.append(readNumber(false).toChar())
                                line.readChar()
                                if (cc != ';')
                                    throw lexerError("Unexpected string escape sequence: ';' is expected after number escape sequence")
                            } else
                                throw lexerError("Unexpected string escape sequence: \\$cc")
                        }
                    }
                }
                else -> sb.append(ch.toChar())
            }
        }
    }

    open fun readRange(whitespacesAcceptable: Boolean): List<Double> {
        return sequence {
            var n = readNumber(true)
            val ch = line.peekChar().toChar()
            val processAtComma = {
                sequence {
                    yield(n)
                    line.readChar()
                    // recursion
                    for (ii in readRange(whitespacesAcceptable))
                        yield(ii)
                }
            }
            when (ch) {
                '-' -> {
                    line.readChar()
                    val j = readNumber(true)
                    if (j < n)
                        throw lexerError("Invalid range specification: larger number must appear later")
                    while (n <= j)
                        yield(n++)
                    if (whitespacesAcceptable)
                        skipWhitespaces()
                    if (line.peekChar().toChar() == ',')
                        for (x in processAtComma())
                            yield(x)
                }
                ',' ->
                    for (x in processAtComma())
                        yield(x)
                else -> yield(n)
            }
        }.toList()
    }

    open fun isIdentifier(c: Int, isStartChar: Boolean): Boolean {
        if (c < 0)
            return false
        if (isWhitespace(c))
            return false

        if (isNumber(c))
            return false

        when (c.toChar()) {
            '\r',
            '\n' ->
                throw lexerError("INTERNAL ERROR: this should not accept EOLs")
            '?', // conditional
            '+', // addition
            '-', // subtraction
            '^', // length-addition
            '#' -> // hex number prefix / preprocessor directive at line head
                return !isStartChar // could be part of identifier
            ':', // variable argument-type separator / loop break
            '/', // division / loop break
            '%', // modulo / length by step
            '(', // parenthesized expr / velocity down
            ')' -> // parenthesized expr / velocity up
                return isStartChar // valid only as head character
            '*', // multiplication
            '$', // variable reference
            ',', // identifier separator
            '"', // string quotation
            '{', // macro body start
            '}', // macro body end
            '\\' -> // escape sequence marker
                return false
        }

        // everything else is regarded as a valid identifier
        return true
    }

    fun readNewIdentifier(): String {
        val start = line.location.linePosition
        if (!isIdentifier(line.readChar(), true))
            throw lexerError("Identifier character is expected")
        while (isIdentifier(line.peekChar(), false))
            line.readChar()
        return line.text.substring(start, line.location.linePosition - start)
    }

    fun expectNext(tokenType: MmlTokenType) {
        if (!advance())
            throw lexerError("Expected token $currentToken, but reached end of the input")
        expectCurrent(tokenType)
    }

    fun expectCurrent(tokenType: MmlTokenType) {
        if (currentToken != tokenType)
            throw lexerError("Expected token $tokenType but found $currentToken")
    }

    open fun advance(): Boolean {
        return doAdvance()
    }

    private var currentLocation: MmlLineInfo = MmlLineInfo.empty

    private fun doAdvance(): Boolean {
        skipWhitespaces()
        currentLocation = line.location.clone()
        var ch_ = line.peekChar()
        if (ch_ < 0)
            return false
        var ch = ch_.toChar()
        when (ch) {
            '.' -> {
                consumeAsToken(MmlTokenType.Period)
                return true
            }
            ',' -> {
                consumeAsToken(MmlTokenType.Comma)
                return true
            }
            '%' -> {
                consumeAsToken(MmlTokenType.Percent)
                return true
            }
            '{' -> {
                consumeAsToken(MmlTokenType.OpenCurly)
                return true
            }
            '}' -> {
                consumeAsToken(MmlTokenType.CloseCurly)
                return true
            }
            '?' -> {
                consumeAsToken(MmlTokenType.Question)
                return true
            }
            '^' -> {
                consumeAsToken(MmlTokenType.Caret)
                return true
            }
            '+' -> {
                consumeAsToken(MmlTokenType.Plus)
                return true
            }
            '-' -> {
                consumeAsToken(MmlTokenType.Minus)
                return true
            }
            '*' -> {
                consumeAsToken(MmlTokenType.Asterisk)
                return true
            }
            ':' -> {
                consumeAsTokenOrIdentifier(MmlTokenType.Colon, ":")
                return true
            }
            '/' -> {
                consumeAsTokenOrIdentifier(MmlTokenType.Slash, "/")
                return true
            }
            '\\' -> {
                line.readChar()
                ch_ = line.peekChar()
                if (ch_ < 0) {
                    compiler.report(
                        MmlDiagnosticVerbosity.Error,
                        line.location,
                        "Unexpected end of stream in the middle of escaped token.",
                        listOf()
                    )
                } else {
                    ch = ch_.toChar()
                    when (ch) {
                        '<' -> {
                            line.readChar()
                            if (line.peekChar().toChar() == '=')
                                consumeAsToken(MmlTokenType.BackSlashLesserEqual)
                            else {
                                currentToken = MmlTokenType.BackSlashLesser
                                value = null
                            }
                            return true
                        }
                        '>' -> {
                            line.readChar()
                            if (line.peekChar().toChar() == '=')
                                consumeAsToken(MmlTokenType.BackSlashGreaterEqual)
                            else {
                                currentToken = MmlTokenType.BackSlashGreater
                                value = null
                            }
                            return true
                        }
                        else -> {
                            compiler.report(
                                MmlDiagnosticVerbosity.Error,
                                line.location,
                                "Unexpected escaped token: '\\$ch'",
                                listOf()
                            )
                            return false
                        }
                    }
                }
            }
            '$' -> {
                consumeAsToken(MmlTokenType.Dollar)
                return true
            }
            '"' -> {
                value = readStringLiteral()
                currentToken = MmlTokenType.StringLiteral
                return true
            }
        }
        if (ch == '#' || isNumber(ch.toInt())) {
            value = readNumber(false)
            currentToken = MmlTokenType.NumberLiteral
            return true
        }
        if (tryParseTypeName())
            return true
        if (newIdentifierMode) {
            value = readNewIdentifier()
            currentToken = MmlTokenType.Identifier
            return true
        }
        val ident = tryReadIdentifier()
        if (ident != null) {
            value = ident
            currentToken = MmlTokenType.Identifier
            return true
        }

        throw lexerError("The lexer could not read a valid token: '$ch'")
    }

    private fun tryParseTypeName(): Boolean {
        if (line.tryMatch("number")) {
            value = MmlDataType.Number
            currentToken = MmlTokenType.KeywordNumber
        } else if (line.tryMatch("length")) {
            value = MmlDataType.Length
            currentToken = MmlTokenType.KeywordLength
        } else if (line.tryMatch("string")) {
            value = MmlDataType.String
            currentToken = MmlTokenType.KeywordString
        } else if (line.tryMatch("buffer")) {
            value = MmlDataType.Buffer
            currentToken = MmlTokenType.KeywordBuffer
        } else
            return false
        return true
    }

    private fun consumeAsToken(token: MmlTokenType) {
        line.readChar()
        currentToken = token
        value = null
    }

    private fun consumeAsTokenOrIdentifier(token: MmlTokenType, value: String) {
        consumeAsToken(token)
        this.value = value
    }

    abstract fun tryReadIdentifier(): String?

    open fun getValidIdentifiers(): List<String> {
        return sequence {
            val macros = tokenizerSource.currentMacroDefinition
            if (macros != null)
                for (a in macros.arguments)
                    yield(a.name)
            for (v in tokenizerSource.variables) for (s in v.parsedNames)
                yield(s)
            for (m in tokenizerSource.macros)
                if (m.parsedName != null)
                    yield(m.parsedName!!)
            for (name in tokenizerSource.primitiveOperations)
                yield(name)
        }.toList()
    }
}

class MmlMatchLongestLexer : MmlLexer {
    constructor (compiler: MmlCompiler, source: MmlTokenizerSource)
            : super(compiler, source) {
    }

    private var matchpos: List<Int>? = null
    private var buffer = Array(256) { 0.toChar() }
    private var bufferPos = 0

    override fun tryReadIdentifier(): String? {
        if (matchpos == null)
            matchpos = List(tokenizerSource.macros.size) { 0 }
        if (matchpos!!.size < tokenizerSource.macros.size)
            throw IllegalStateException("Macro definition is added somewhere after the first macro search is invoked.")
        var matched: String? = null

        bufferPos = 0 // reset

        for (name in getValidIdentifiers()) {
            if (matched != null && matched.length >= name.length)
                continue // no hope it could match.
            if (matches(name))
                matched = name
        }
        if (matched != null)
            return matched

        // then it could be a new identifier.
        // In such case, read up until the input comes to non-identifier.
        // If it is not a valid identifier input, then return null.
        if (bufferPos == 0) {
            if (!isIdentifier(line.peekChar(), true))
                return null // not an identifier
            buffer[bufferPos++] = line.readChar().toChar()
        }

        while (true) {
            val ch = line.peekChar()
            if (!isIdentifier(ch, false))
                break
            if (buffer.size == bufferPos) {
                val newbuf = Array(buffer.size * 2) { _ -> 0.toChar() }
                buffer.copyInto(newbuf, 0, buffer.size)
                buffer = newbuf
            }
            buffer[bufferPos++] = ch.toChar()
            line.readChar()
        }
        return String(buffer.toCharArray(), 0, bufferPos)
    }

    // examines if current token matches the argument identifier,
    // proceeding the MmlLine.
    private fun matches(name: String): Boolean {
        var ret = false
        val savedPos = line.location.linePosition
        val savedBufferPos = bufferPos
        ret = matchesProceed(name)
        if (!ret) {
            bufferPos = savedBufferPos
            line.location.linePosition = savedPos
        }
        return ret
    }

    private fun matchesProceed(name: String): Boolean {
        for (i in 0 until bufferPos) {
            if (i == name.length)
                return true // matched within the buffer
            if (buffer[i] != name[i])
                return false
        }
        while (bufferPos < name.length) {
            if (buffer.size == bufferPos) {
                val newbuf = Array(buffer.size * 2) { _ -> 0.toChar() }
                buffer.copyInto(newbuf, 0, buffer.size)
                buffer = newbuf
            }
            val ch_ = line.peekChar()
            if (ch_ < 0)
                return false
            buffer[bufferPos] = ch_.toChar()
            if (buffer[bufferPos] != name[bufferPos])
                return false
            bufferPos++
            line.readChar()
        }
        return true
    }
}

class MmlTokenizer(private val source: MmlTokenizerSource) {
    companion object {
        val metaMap = mutableMapOf<String, Byte>()

        init {
            metaMap["text"] = 1
            metaMap["copyright"] = 2
            metaMap["title"] = 3
        }

        fun tokenize(source: MmlTokenizerSource): MmlTokenSet {
            val tokenizer = MmlTokenizer(source)
            tokenizer.process()
            return tokenizer.result
        }
    }

    private val aliases = mutableMapOf<String, String>()
    private val result: MmlTokenSet = MmlTokenSet()

    private val compiler
        get() = source.lexer.compiler

    fun process() {
        // process pragmas
        for (ps in source.pragmas)
            parsePragmaLines(ps)

        // add built-in variables
        result.variables.add(MmlVariableDefinition("__timeline_position", MmlLineInfo.empty).apply {
            type = MmlDataType.Number
        })
        val bc = MmlVariableDefinition("__base_count", MmlLineInfo.empty).apply {
            type = MmlDataType.Number
        }
        bc.defaultValueTokens.add(
            MmlToken(
                MmlTokenType.NumberLiteral,
                result.baseCount,
                MmlLineInfo.empty
            )
        )
        result.variables.add(bc)

        // process variables
        for (vs in source.variables)
            parseVariableLines(vs)

        // process macros, recursively
        for (ms in source.macros)
            parseMacroLines(ms)

        // process tracks
        for (ts in source.tracks)
            parseTrackLines(ts)
    }

    private fun parsePragmaLines(src: MmlPragmaSource) {
        source.lexer.setCurrentInput(src)
        when (src.name) {
            "basecount" -> {
                source.lexer.expectNext(MmlTokenType.NumberLiteral)
                result.baseCount = (source.lexer.value as Double).toInt()
                MmlValueExprResolver.baseCount = result.baseCount
            }
            "conditional" -> {
                when (val category = source.lexer.readNewIdentifier()) {
                    "block" -> {
                        source.lexer.skipWhitespaces(true)
                        while (true) {
                            source.lexer.newIdentifierMode = true
                            source.lexer.expectNext(MmlTokenType.Identifier)
                            val s = source.lexer.value as String
                            result.conditional.blocks.add(s)
                            source.lexer.skipWhitespaces()
                            if (!source.lexer.advance() || source.lexer.currentToken != MmlTokenType.Comma)
                                break
                            source.lexer.skipWhitespaces()
                        }
                        if (source.lexer.advance())
                            compiler.report(
                                MmlDiagnosticVerbosity.Error,
                                source.lexer.line.location,
                                "Extra conditional tokens",
                                listOf()
                            )
                        source.lexer.newIdentifierMode = false
                    }
                    "track" -> {
                        source.lexer.skipWhitespaces(true)
                        val tracks = source.lexer.readRange(true)
                        result.conditional.tracks.addAll(tracks)
                        source.lexer.skipWhitespaces()
                        if (source.lexer.advance())
                            compiler.report(
                                MmlDiagnosticVerbosity.Error,
                                source.lexer.line.location,
                                "Extra conditional tokens",
                                listOf()
                            )
                    }
                    else ->
                        compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            source.lexer.line.location,
                            "Unexpected compilation condition type '$category'",
                            listOf()
                        )
                }
            }
            "meta" -> {
                val typeLoc = source.lexer.line.location
                source.lexer.newIdentifierMode = true
                val identifier = source.lexer.readNewIdentifier()
                source.lexer.skipWhitespaces(true)
                val textLoc = source.lexer.line.location
                var text = source.lexer.readStringLiteral()
                when (identifier) {
                    "title",
                    "copyright",
                    "text" -> {
                    }
                    else ->
                        compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            source.lexer.line.location,
                            "Invalid #meta directive argument: $identifier",
                            listOf()
                        )
                }
                result.metaTexts.add(MmlMetaTextToken().apply {
                    typeLocation = typeLoc
                    metaType = metaMap[identifier]!!
                    textLocation = textLoc
                    text = text
                })
                source.lexer.newIdentifierMode = false
            }
            "define" -> {
                source.lexer.newIdentifierMode = true
                val identifier = source.lexer.readNewIdentifier()
                source.lexer.skipWhitespaces(true)
                if (aliases.containsKey(identifier))
                    compiler.report(
                        MmlDiagnosticVerbosity.Warning,
                        source.lexer.line.location,
                        "Warning: overwriting definition $identifier, redefined at ${source.lexer.line.location}",
                        listOf()
                    )
                aliases[identifier] =
                    source.lexer.line.text.substring(source.lexer.line.location.linePosition)
                source.lexer.newIdentifierMode = false
            }
            else ->
                throw UnsupportedOperationException("Not implemented")
        }
    }

    private fun parseVariableLines(src: MmlVariableSource) {
        for (line in src.lines)
            for (entry in aliases)
                line.text = line.text.replace(entry.key, entry.value)
        source.lexer.setCurrentInput(src)

        source.lexer.newIdentifierMode = true
        source.lexer.advance()
        val idx = result.variables.size
        parseVariableList(result.variables, true)
        for (i in 0 until result.variables.size)
            src.parsedNames.add(result.variables[i].name)
        source.lexer.newIdentifierMode = false
    }

    private fun parseMacroLines(src: MmlMacroSource) {
//Util.DebugWriter.WriteLine ("Parsing Macro: " + src.Name);
        for (line in src.lines)
            for (entry in aliases)
                line.text = line.text.replace(entry.key, entry.value)
        source.lexer.setCurrentInput(src)

        var range = mutableListOf<Double>()
        val location = source.lexer.line.location.clone()
        val ch = source.lexer.line.peekChar()
        if (ch.toChar() == '#' || source.lexer.isNumber(ch)) {
            range = source.lexer.readRange(false).toMutableList()
            source.lexer.skipWhitespaces(true)
        }

        // get identifier
        val identifier = source.lexer.readNewIdentifier()
        source.lexer.skipWhitespaces(true)

        src.parsedName = identifier

        val m = MmlMacroDefinition(identifier, range, location)
        source.currentMacroDefinition = m
        if (m.tokens.size == 0) {
            // get args
            source.lexer.newIdentifierMode = true
            source.lexer.advance()
            parseVariableList(m.arguments, false)
        }
        source.lexer.newIdentifierMode = false
        while (source.lexer.advance())
            m.tokens.add(source.lexer.createParsedToken())
        if (m.tokens.size == 0 || m.tokens[m.tokens.size - 1].tokenType != MmlTokenType.CloseCurly)
            source.lexer.lexerError("'{{' is expected at the end of macro definition for '$identifier'")
        m.tokens.removeLast()
        result.macros.add(m)
        source.currentMacroDefinition = null
    }

    private fun parseVariableList(vars: MutableList<MmlVariableDefinition>, isVariable: Boolean) {
        var count = 0
        while (true) {
            if (source.lexer.currentToken == MmlTokenType.OpenCurly)
                break // go to parse body
            if (count > 0) {
                source.lexer.expectCurrent(MmlTokenType.Comma)
                source.lexer.newIdentifierMode = true
                source.lexer.advance()
            }
            source.lexer.expectCurrent(MmlTokenType.Identifier)
            val arg =
                MmlVariableDefinition(source.lexer.value as String, source.lexer.line.location)
            vars.add(arg)
            count++

            // FIXME: possibly use MmlToken.Colon?
            source.lexer.skipWhitespaces()
            if (source.lexer.line.peekChar().toChar() != ':') {
                arg.type = MmlDataType.Any
                if (!source.lexer.advance() && isVariable)
                    return
                continue
            }
            source.lexer.line.readChar()

            source.lexer.newIdentifierMode = false
            if (!source.lexer.advance()) {
                compiler.report(
                    MmlDiagnosticVerbosity.Error,
                    source.lexer.line.location,
                    "type name is expected after ':' in macro argument definition",
                    listOf()
                )
                return
            }
            when (source.lexer.currentToken) {
                MmlTokenType.KeywordNumber,
                MmlTokenType.KeywordString,
                MmlTokenType.KeywordLength,
                MmlTokenType.KeywordBuffer -> {
                }
                else -> {
                    compiler.report(
                        MmlDiagnosticVerbosity.Error,
                        source.lexer.line.location,
                        "Data type name is expected, but got ${source.lexer.currentToken}",
                        listOf()
                    )
                    return
                }
            }
            arg.type = source.lexer.value as MmlDataType
            source.lexer.skipWhitespaces()
            if (source.lexer.line.peekChar().toChar() != '=') {
                if (!source.lexer.advance() && isVariable)
                    return
                continue
            }
            source.lexer.line.readChar()

            var loop = true
            while (loop) {
                if (!source.lexer.advance()) {
                    if (isVariable)
                        return
                    compiler.report(
                        MmlDiagnosticVerbosity.Error,
                        source.lexer.line.location,
                        "Incomplete argument default value definition",
                        listOf()
                    )
                    return
                }
                when (source.lexer.currentToken) {
                    MmlTokenType.Comma,
                    MmlTokenType.OpenCurly -> {
                        loop = false
                        continue
                    }
                }

                arg.defaultValueTokens.add(source.lexer.createParsedToken())
            }
        }
    }

    private fun parseTrackLines(src: MmlTrackSource) {
        val tokens = mutableListOf<MmlToken>()
        for (line in src.lines)
            for (entry in aliases)
                line.text = line.text.replace(entry.key, entry.value)
        source.lexer.setCurrentInput(src)
        while (source.lexer.advance())
            tokens.add(source.lexer.createParsedToken())
        // Compilation conditionals are actually handled here.
        if (!result.conditional.shouldCompileBlock(src.blockName))
            return
        for (t in src.tracks) {
            if (result.conditional.shouldCompileTrack(t))
                result.getTrack(t).tokens.addAll(tokens)
        }
    }

}
