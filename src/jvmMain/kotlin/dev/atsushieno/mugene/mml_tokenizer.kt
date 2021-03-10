package dev.atsushieno.mugene

import java.io.*
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException

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
        macros = mutableListOf<MmlMacroDefinition>()
        variables = mutableListOf<MmlVariableDefinition>()
        tracks = mutableListOf<MmlTrack>()
        metaTexts = mutableListOf<MmlMetaTextToken>()
    }

    var baseCount = 0

    lateinit var conditional: MmlCompilationCondition
    lateinit var macros: MutableList<MmlMacroDefinition>
    lateinit var variables: MutableList<MmlVariableDefinition>
    lateinit var tracks: MutableList<MmlTrack>
    lateinit var metaTexts: MutableList<MmlMetaTextToken>

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
    lateinit var name: String
}

public class MmlMacroDefinition : MmlOperationDefinition {
    constructor (name: String, targetTracks: List<Double>, location: MmlLineInfo) {
        this.name = name
        this.targetTracks = targetTracks.toMutableList()
        this.location = location
        this.tokens = mutableListOf<MmlToken>()
    }

    lateinit var targetTracks: MutableList<Double>
    lateinit var location: MmlLineInfo
    lateinit var tokens: MutableList<MmlToken>
}

class MmlVariableDefinition {
    constructor (name: String, location: MmlLineInfo) {
        this.name = name
        this.location = location
        this.defaultValueTokens = mutableListOf<MmlToken>()
    }

    val name: String
    var location: MmlLineInfo
    lateinit var type: MmlDataType
    lateinit var defaultValueTokens: MutableList<MmlToken>
}

class MmlTrack {
    constructor (number: Double) {
        this.number = number
        this.tokens = mutableListOf<MmlToken>()
    }

    val number: Double
    lateinit var tokens: MutableList<MmlToken>
}


//region input sources to tokenizer sources

abstract class StreamResolver {

    fun getEntity(file: String): Reader {
        if (file == null)
            throw IllegalArgumentException("file is null")
        if (file.length == 0)
            throw IllegalArgumentException("Empty filename is passed")
        var ret = onGetEntity(file)
        if (ret == null)
            throw InvalidObjectException("MML stream \"$file\" could not be resolved.")
        return ret
    }

    internal open fun onGetEntity(file: String): Reader? {
        throw UnsupportedOperationException("You have to implement it. It is virtual only because of backward compatibility.")
    }

    val includes = mutableListOf<String>()

    fun resolveFilePath(file: String): String? {
        if (file == null)
            return null
        if (!includes.any())
            return File(file).absolutePath
        if (File(file).isAbsolute)
            return file
        return File(includes.last(), file).absolutePath
    }

    open fun pushInclude(file: String) {
        val abs = resolveFilePath(file)!!
        if (includes.contains(abs))
            throw IllegalArgumentException("File \"$abs\" is already being processed. Recursive inclusion is prohibited.")
        includes.add(abs)
    }

    open fun popInclude() {
        includes.removeLast()
    }
}

class MergeStreamResolver : StreamResolver {
    private val resolvers: MutableList<StreamResolver>

    constructor (vararg resolvers: StreamResolver) {
        this.resolvers = resolvers.toMutableList()
    }

    internal override fun onGetEntity(file: String): Reader? {
        for (r in resolvers) {
            val ret = r.onGetEntity(file)
            if (ret != null)
                return ret
        }
        return null
    }

    override fun pushInclude(file: String) {
        for (r in resolvers)
            r.pushInclude(file)
    }

    override fun popInclude() {
        for (r in resolvers)
            r.popInclude()
    }
}

public class LocalFileStreamResolver : StreamResolver() {
    override fun onGetEntity(file: String): Reader? {
        var abs = resolveFilePath(file)
        if (File(abs).exists())
            return FileReader(abs)
        return null
    }
}

// file sources to be parsed into MmlSourceLineSet, for each track
// and macro.
data class MmlInputSource(val file: String, val reader: Reader)

class MmlLineInfo {
    companion object {
        val empty = MmlLineInfo("__internal__", 0, 0)
    }

    constructor (file: String, line: Int, column: Int) {
        this.file = file
        this.lineNumber = line
        this.linePosition = column
    }

    var file: String
    var lineNumber: Int
    var linePosition: Int

    fun clone() = MmlLineInfo(file, lineNumber, linePosition)

    override fun toString() = "Location: $file ($lineNumber, $linePosition)"
}

class MmlLine {
    companion object {
        fun create(file: String, lineNumber: Int, linePosition: Int, text: String) =
            MmlLine(MmlLineInfo(file, lineNumber, linePosition), text)
    }

    constructor (location: MmlLineInfo, text: String) {
        this.location = location
        this.text = text
    }

    val location: MmlLineInfo
    var text: String

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

class MmlTokenizerSource {
    // can be switched
    var lexer: MmlLexer

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

    constructor (compiler: MmlCompiler) {
        lexer = MmlMatchLongestLexer(compiler, this)

        for (primitive in MmlPrimitiveOperation.all)
            primitiveOperations.add(primitive.name)
    }
}

public class MmlInputSourceReader {
    companion object {
        fun parse(compiler: MmlCompiler, inputs: List<MmlInputSource>): MmlTokenizerSource {
            var r = MmlInputSourceReader(compiler)
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

    private val compiler: MmlCompiler
    private lateinit var result: MmlTokenizerSource

    constructor (compiler: MmlCompiler) {
        this.compiler = compiler
    }

    var in_comment_mode = false

    fun process(inputs: List<MmlInputSource>) {
        result = MmlTokenizerSource(compiler)
        doProcess(inputs)
    }

    fun doProcess(inputs: List<MmlInputSource>) {
        var inputList = inputs

        for (i in 0 until inputList.size) {
            var line = 0
            var s = ""
            var ls: MmlSourceLineSet? = null
            // inputs could grow up.
            var input = inputList[i]
            compiler.resolver.pushInclude(input.file)
            var continued = false
            var wasContinued = continued
            for (ss in input.reader.readLines()) {
                line++
                wasContinued = continued
                var s = trimComments(ss, 0)
                if (s.length == 0) // comment line is allowed inside multi-line MML.
                    continue

                continued = (s[s.length - 1] == '\\')
                if (continued)
                    s = s.substring(0, s.length - 1)
                if (wasContinued) {
                    if (!in_comment_mode)
                        ls?.addConsecutiveLine(s)
                    continue
                }
                if (s[0] == '#')
                    ls = processPragmaLine(MmlLine.create(input.file, line, 0, s))
                else
                    ls = processTrackLine(MmlLine.create(input.file, line, 0, s))
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
        var identifier = result.lexer.readNewIdentifier()
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
                in_comment_mode = true
                return null
            }
            "endcomment" -> {
                in_comment_mode = false
                return null
            }
            "define", "conditional", "meta", "basecount" -> {
            }
            else -> throw mmlError(line.location, "Unexpected preprocessor directive: $identifier")
        }

        result.lexer.skipWhitespaces(true)
        var ps = MmlPragmaSource(identifier)
        ps.lines.add(line)
        result.pragmas.add(ps)
        return ps
    }

    private fun processIncludeLine(line: MmlLine): MmlSourceLineSet? {
        val file = line.text.substring(line.location.linePosition).trim()
        this.doProcess(
            listOf<MmlInputSource>(
                MmlInputSource(file, compiler.resolver.getEntity(file))
            )
        )
        return MmlUntypedSource(line)
    }

    private fun processMacroLine(line: MmlLine): MmlSourceLineSet? {
        if (in_comment_mode)
            return null
        var mms = MmlMacroSource()
        mms.lines.add(line)
        result.macros.add(mms)
        return mms
    }

    private fun processVariableLine(line: MmlLine): MmlSourceLineSet? {
        if (in_comment_mode)
            return null
        var vs = MmlVariableSource()
        vs.lines.add(line)
        result.variables.add(vs)
        return vs
    }

    private var previous_section = ""
    private var previous_range: List<Double>? = null

    private fun processTrackLine(line: MmlLine): MmlSourceLineSet? {
        if (in_comment_mode)
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
        var ts = MmlTrackSource(section, range)
        ts.lines.add(line)
        result.tracks.add(ts)
        return ts
    }

    fun mmlError(location: MmlLineInfo, msg: String): MmlException {
        return MmlException(msg, location)
    }

    fun mmlError(input: MmlInputSource, line: Int, column: Int, msg: String): MmlException {
        return MmlException(msg, MmlLineInfo(input.file, line, column))
    }
}


// tokenizer sources to parsed mml tokens

// represents a set of lines for either a macro or a track lines.

abstract class MmlSourceLineSet {
    val lines = mutableListOf<MmlLine>()

    open fun addConsecutiveLine(text: String) {
        if (lines.size == 0)
            throw InvalidObjectException("Unexpected addition to previous line while there was no registered line.")
        var prev = lines.last()
        val line = MmlLine.create(
            prev.location.file,
            prev.location.lineNumber + 1,
            prev.location.linePosition,
            text
        )
        lines.add(line)
    }
}

class MmlUntypedSource : MmlSourceLineSet {
    constructor (singleLine: MmlLine)
            : super() {
        lines.add(singleLine)
    }
}

class MmlTrackSource : MmlSourceLineSet {
    constructor (blockName: String, tracks: List<Double>)
            : super() {
        this.blockName = blockName
        this.tracks = tracks.toMutableList()
    }

    val blockName: String
    val tracks: MutableList<Double>
}

class MmlMacroSource(var parsedName: String? = null) : MmlSourceLineSet() {}

class MmlVariableSource(val parsedNames: MutableList<String> = mutableListOf()) :
    MmlSourceLineSet() {}

class MmlPragmaSource(val name: String) : MmlSourceLineSet() {}

enum class MmlSourceLineSetKind {
    Track,
    Macro,
    Pragma,
}

abstract class MmlLexer {
    protected constructor (compiler: MmlCompiler, source: MmlTokenizerSource) {
        this.compiler = compiler
        tokenizerSource = source
    }

    internal val compiler: MmlCompiler
    private var input: MmlSourceLineSet? = null
    private var currentLine: Int = 0

    // It contains all macro definitions.
    val tokenizerSource: MmlTokenizerSource

    val line: MmlLine
        get() {
            var l = input!!.lines[currentLine]
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
        if (line == null)
            return MmlException(msg)
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
        var line = line
        var ch_ = line.peekChar()
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
                var isnum = isNumber(ch.toInt())
                var isupper = 'A' <= ch && ch <= 'F'
                var islower = 'a' <= ch && ch <= 'f'
                if (!isnum && !isupper && !islower) {
                    if (!passed)
                        throw lexerError("Invalid hexadecimal digits")
                    break
                }
                passed = true
                var h =
                    if (isnum) line.readChar() - '0'.toInt()
                    else if (isupper) line.readChar() - 'A'.toInt() + 10
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
        var sb = stringLiteralBuffer
        sb.clear()
        line.readChar() // ' or "
        var startLoc = line.location
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
                            if (cc == '#' || '0' <= cc && cc <= '9') {
                                sb.append(readNumber(false).toChar())
                                ch = line.readChar()
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
            var ch = line.peekChar().toChar()
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
//Util.DebugWriter.WriteLine ("NEW Identifier: " + Line.Text.Substring (start, Line.Location.LinePosition - start));
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
        var ret = _advance()
//Util.DebugWriter.WriteLine ("TOKEN: {0} : Value: {1}", CurrentToken, Value);
        return ret
    }

    var currentLocation: MmlLineInfo = MmlLineInfo.empty

    private fun _advance(): Boolean {
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
        var ident = tryReadIdentifier()
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
            var macros = tokenizerSource.currentMacroDefinition
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

    var matchpos: List<Int>? = null
    var buffer = Array<Char>(256, { _ -> 0.toChar() })
    var buffer_pos = 0

    override fun tryReadIdentifier(): String? {
        if (matchpos == null)
            matchpos = List<Int>(tokenizerSource.macros.size, { _ -> 0 })
        if (matchpos!!.size < tokenizerSource.macros.size)
            throw InvalidObjectException("Macro definition is added somewhere after the first macro search is invoked.")
        var matched: String? = null

        buffer_pos = 0 // reset

        for (name in getValidIdentifiers()) {
            if (matched != null && matched.length >= name.length)
                continue // no hope it could match.
//Util.DebugWriter.WriteLine ("!!! {0} / {1} / {2}", name, matched, buffer_pos);
            if (matches(name))
                matched = name
        }
        if (matched != null)
            return matched

        // then it could be a new identifier.
        // In such case, read up until the input comes to non-identifier.
        // If it is not a valid identifier input, then return null.
        if (buffer_pos == 0) {
            if (!isIdentifier(line.peekChar(), true))
                return null // not an identifier
            buffer[buffer_pos++] = line.readChar().toChar()
        }

        while (true) {
            val ch = line.peekChar()
            if (!isIdentifier(ch, false))
                break
            if (buffer.size == buffer_pos) {
                var newbuf = Array<Char>(buffer.size * 2) { _ -> 0.toChar() }
                buffer.copyInto(newbuf, 0, buffer.size)
                buffer = newbuf
            }
            buffer[buffer_pos++] = ch.toChar()
            line.readChar()
        }
        return String(buffer.toCharArray(), 0, buffer_pos)
    }

    // examines if current token matches the argument identifier,
    // proceeding the MmlLine.
    private fun matches(name: String): Boolean {
        var ret = false
        var savedPos = line.location.linePosition
        var savedBufferPos = buffer_pos
        ret = matchesProceed(name)
        if (!ret) {
            buffer_pos = savedBufferPos
            line.location.linePosition = savedPos
        }
        return ret
    }

    private fun matchesProceed(name: String): Boolean {
        for (i in 0 until buffer_pos) {
            if (i == name.length)
                return true // matched within the buffer
            if (buffer[i] != name[i])
                return false
        }
        while (buffer_pos < name.length) {
            if (buffer.size == buffer_pos) {
                var newbuf = Array<Char>(buffer.size * 2) { _ -> 0.toChar() }
                buffer.copyInto(newbuf, 0, buffer.size)
                buffer = newbuf
            }
            val ch_ = line.peekChar()
            if (ch_ < 0)
                return false
            buffer[buffer_pos] = ch_.toChar()
//Util.DebugWriter.WriteLine ("$$$ {0} {1} ({2})", buffer [buffer_pos], name [buffer_pos], buffer_pos);
            if (buffer[buffer_pos] != name[buffer_pos])
                return false
            buffer_pos++
            line.readChar()
        }
        return true
    }
}

class MmlTokenizer {
    companion object {
        val metaMap = mutableMapOf<String, Byte>()

        init {
            metaMap["text"] = 1
            metaMap["copyright"] = 2
            metaMap["title"] = 3
        }

        fun tokenize(source: MmlTokenizerSource): MmlTokenSet {
            var tokenizer = MmlTokenizer(source)
            tokenizer.process()
            return tokenizer.result
        }
    }

    constructor (source: MmlTokenizerSource) {
        this.source = source
        this.result = MmlTokenSet()
    }

    private val source: MmlTokenizerSource
    private val aliases = mutableMapOf<String, String>()
    private val result: MmlTokenSet

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
        var bc = MmlVariableDefinition("__base_count", MmlLineInfo.empty).apply {
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
                var category = source.lexer.readNewIdentifier()
                when (category) {
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
                        var tracks = source.lexer.readRange(true)
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
                var typeLoc = source.lexer.line.location
                source.lexer.newIdentifierMode = true
                var identifier = source.lexer.readNewIdentifier()
                source.lexer.skipWhitespaces(true)
                var textLoc = source.lexer.line.location
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
        var location = source.lexer.line.location.clone()
        var ch = source.lexer.line.peekChar()
        if (ch.toChar() == '#' || source.lexer.isNumber(ch)) {
            range = source.lexer.readRange(false).toMutableList()
            source.lexer.skipWhitespaces(true)
        }

        // get identifier
        var identifier = source.lexer.readNewIdentifier()
        source.lexer.skipWhitespaces(true)

        src.parsedName = identifier

        var m = MmlMacroDefinition(identifier, range, location)
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
            var arg =
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
        var tokens = mutableListOf<MmlToken>()
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
