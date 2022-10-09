package dev.atsushieno.mugene

import com.strumenta.kotlinmultiplatform.BitSet
import dev.atsushieno.mugene.parser.MugeneParser
import org.antlr.v4.kotlinruntime.*
import org.antlr.v4.kotlinruntime.atn.ATNConfigSet
import org.antlr.v4.kotlinruntime.dfa.DFA

class MmlSemanticTreeSet {
    var baseCount: Int = 192
    var tracks: MutableList<MmlSemanticTrack> = mutableListOf()
    var macros: MutableList<MmlSemanticMacro> = mutableListOf()
    var variables: MutableMap<String, MmlSemanticVariable> = mutableMapOf()
}

class MmlSemanticTrack(var number: Double) {
    var data: MutableList<MmlOperationUse> = mutableListOf()
}

class MmlSemanticMacro(var location: MmlLineInfo, var name: String, var targetTracks: List<Double>) {
    var arguments: MutableList<MmlSemanticVariable> = mutableListOf()
    var data: MutableList<MmlOperationUse> = mutableListOf()
}

class MmlSemanticVariable(var location: MmlLineInfo, var name: String, var type: MmlDataType) {

    var defaultValue: MmlValueExpr? = null

    fun fillDefaultValue() {
        when (type) {
            MmlDataType.Number,
            MmlDataType.Length ->
                defaultValue = MmlConstantExpr(location, type, 0)
            MmlDataType.String ->
                defaultValue = MmlConstantExpr(location, type, "")
            MmlDataType.Buffer ->
                // Note that it never fills a specific StringBuilder object
                // It should be instantiated in each Resolve() evaluation instead.
                defaultValue = MmlConstantExpr(location, type, null)
            MmlDataType.Any -> {
            }
        }
    }

    override fun toString(): String {
        return if (defaultValue != null)
            "$name:$type(=$defaultValue)"
        else
            "$name:$type"
    }
}

abstract class MmlValueExpr protected constructor(var location: MmlLineInfo?) {
    companion object {
        val skippedArgument = MmlConstantExpr (MmlLineInfo.empty, MmlDataType.String, "DEFAULT ARGUMENT")

        fun computeLength(baseValue: Int, dots: Int): Int {
            var ret = baseValue
            var add = baseValue / 2
            while (dots > 0) {
                ret += add
                add /= 2
            }
            return ret
        }
    }

    lateinit var resolver: MmlValueExprResolver
}

class MmlConstantExpr(location: MmlLineInfo?, val type: MmlDataType, val value: Any?) : MmlValueExpr(location) {
    init {
        resolver = MmlConstantExprResolver(this)
    }

    override fun toString(): String {
        return when (type) {
            MmlDataType.Number -> "#${value}}"
            MmlDataType.String -> "\"$value\""
            else -> "Constant($value:$type)"
        }
    }
}

class MmlVariableReferenceExpr : MmlValueExpr {
    constructor (location: MmlLineInfo, name: String)
            : this(location, name, 1)

    constructor (location: MmlLineInfo, name: String, scope: Int)
            : super(location) {
        this.scope = scope
        this.name = name
        resolver = MmlVariableReferenceExprResolver(this)
    }

    var scope: Int

    var name: String

    override fun toString() = "\$${if (scope > 1) "\$" else ""}$name"
}

class MmlParenthesizedExpr(var content: MmlValueExpr) : MmlValueExpr(content.location) {
    init {
        resolver = MmlParenthesizedExprResolver(this)
    }

    override fun toString() = "($content)"
}

abstract class MmlArithmeticExpr protected constructor(val left: MmlValueExpr, val right: MmlValueExpr)
    : MmlValueExpr(left.location)

class MmlAddExpr(left: MmlValueExpr, right: MmlValueExpr) : MmlArithmeticExpr(left, right) {
    init {
        resolver = MmlAddExprResolver(this)
    }

    override fun toString(): String = "$left + $right"
}

class MmlSubtractExpr(left: MmlValueExpr, right: MmlValueExpr) : MmlArithmeticExpr(left, right) {
    init {
        resolver = MmlSubtractExprResolver(this)
    }

    override fun toString(): String = "$left - $right"
}

class MmlMultiplyExpr(left: MmlValueExpr, right: MmlValueExpr) : MmlArithmeticExpr(left, right) {
    init {
        resolver = MmlMultiplyExprResolver(this)
    }

    override fun toString(): String = "$left * $right"
}

class MmlDivideExpr(left: MmlValueExpr, right: MmlValueExpr) : MmlArithmeticExpr(left, right) {
    init {
        resolver = MmlDivideExprResolver(this)
    }

    override fun toString(): String = "$left / $right"
}

class MmlModuloExpr(left: MmlValueExpr, right: MmlValueExpr) : MmlArithmeticExpr(left, right) {
    init {
        resolver = MmlModuloExprResolver(this)
    }

    override fun toString(): String = "$left % $right"
}

class MmlConditionalExpr(val condition: MmlValueExpr, val trueExpr: MmlValueExpr, val falseExpr: MmlValueExpr) :
    MmlValueExpr(condition.location) {

    override fun toString(): String = "$condition ? $trueExpr % $falseExpr"

    init {
        resolver = MmlConditionalExprResolver(this)
    }
}

class MmlComparisonExpr(val left: MmlValueExpr, val right: MmlValueExpr, type: ComparisonType) :
    MmlValueExpr(left.location) {

    val comparisonType: ComparisonType = type

    override fun toString(): String = "$left ${comparisonType.toExpressionString()} $right"

    private fun ComparisonType.toExpressionString() =
        when (this) {
            ComparisonType.Lesser -> "<"
            ComparisonType.LesserEqual -> "<="
            ComparisonType.Greater -> ">"
            ComparisonType.GreaterEqual -> ">"
        }

    init {
        resolver = MmlComparisonExprResolver(this)
    }
}

enum class ComparisonType {
    Lesser,
    LesserEqual,
    Greater,
    GreaterEqual,
}

// at this phase, we cannot determine if an invoked operation is a macro, or a primitive operation.
class MmlOperationUse(val name: String, var location: MmlLineInfo?) {

    val arguments: MutableList<MmlValueExpr?> = mutableListOf()

    override fun toString(): String {
        val args = mutableListOf<String>()
        for (i in 0 until args.size)
            args.add(arguments[i].toString())
        return "$name { ${arguments.joinToString { a -> a.toString() }} }"
    }

    fun validateArguments(ctx: MmlResolveContext, minParams: Int, vararg types: MmlDataType) {
        if (arguments.size != types.size) {
            if (arguments.size < minParams || minParams < 0) {
                ctx.compiler.report(
                    MmlDiagnosticVerbosity.Error,
                    location,
                    "Insufficient argument(s)")
                return
            }
        }
        for (i in 0 until arguments.size) {
            val arg = arguments[i]
            val type = if (i < types.size) types[i] else MmlDataType.Any
            arg!!.resolver.resolve(ctx, type)
        }
    }

}


// semantic tree builder

class MmlSemanticTreeBuilder(val tokenSet: MmlTokenSet, val compiler: MmlCompiler) {
    companion object {
        fun compile(tokenSet: MmlTokenSet, compiler: MmlCompiler): MmlSemanticTreeSet {
            val b = MmlSemanticTreeBuilder(tokenSet, compiler)
            b.compile()
            return b.result
        }
    }

    val result: MmlSemanticTreeSet

    private fun compile() {
        val metaTrack = MmlSemanticTrack(0.0)
        for (p in tokenSet.metaTexts) {
            val use = MmlOperationUse(MmlPrimitiveOperation.MidiMeta.name, null)
            use.arguments.add(MmlConstantExpr(p.typeLocation, MmlDataType.Number, p.metaType))
            use.arguments.add(MmlConstantExpr(p.textLocation, MmlDataType.String, p.text))
            metaTrack.data.add(use)
        }
        if (metaTrack.data.size > 0)
            result.tracks.add(metaTrack)
        // compile variable reference tokens into expr
        for (variable in tokenSet.variables)
            result.variables[variable.name] = buildVariableDeclaration(variable)
        // build operation list for macros
        for (macro in tokenSet.macros)
            result.macros.add(buildMacroOperationList(macro))

        // build operation list for tracks
        for (track in tokenSet.tracks)
            result.tracks.add(buildTrackOperationList(track))
    }

    private fun antlrCompile(
        reporter: MmlDiagnosticReporter,
        stream: TokenStream,
        parseFunc: (MugeneParser) -> ParserRuleContext
    ): Any {
        val tokenStream = CommonTokenStream(WrappedTokenSource(stream))
        val parser = MugeneParser(tokenStream)
        parser.addErrorListener(object : ANTLRErrorListener {
            override fun reportAmbiguity(
                recognizer: Parser,
                dfa: DFA,
                startIndex: Int,
                stopIndex: Int,
                exact: Boolean,
                ambigAlts: BitSet,
                configs: ATNConfigSet
            ) {
                when (dfa.atnStartState.ruleIndex) {
                    // known ambiguity between `OpenCurly arguments CloseCurly` in argumentsOptCurly
                    //   vs. `OpenCurly expression CloseCurly` in primaryExpr.
                    3, 11 -> return
                    else -> reporter(
                        MmlDiagnosticVerbosity.Error,
                        MmlLineInfo.empty,
                        "reportAmbiguity(startIndex: $startIndex, stopIndex: $stopIndex, exact: $exact)"
                    )
                }
            }

            override fun reportAttemptingFullContext(
                recognizer: Parser,
                dfa: DFA,
                startIndex: Int,
                stopIndex: Int,
                conflictingAlts: BitSet,
                configs: ATNConfigSet
            ) {
                //TODO("Attempting full context. Not yet implemented")
            }

            override fun reportContextSensitivity(
                recognizer: Parser,
                dfa: DFA,
                startIndex: Int,
                stopIndex: Int,
                prediction: Int,
                configs: ATNConfigSet
            ) {
                //reporter(MmlDiagnosticVerbosity.Warning, MmlLineInfo.empty, "reportContextSensitivity")
            }

            override fun syntaxError(
                recognizer: Recognizer<*, *>,
                offendingSymbol: Any?,
                line: Int,
                charPositionInLine: Int,
                msg: String,
                e: RecognitionException?
            ) {
                reporter(MmlDiagnosticVerbosity.Error, MmlLineInfo("(unknown)", line, charPositionInLine), msg)
            }

        })
        val tree = parseFunc(parser)
        val visitor = MugeneParserVisitorImpl(compiler)
        return visitor.visit(tree)!!
    }

    private fun buildVariableDeclaration(src: MmlVariableDefinition): MmlSemanticVariable {
        val ret = MmlSemanticVariable(src.location, src.name, src.type)

        if (src.defaultValueTokens.size == 0)
            return ret

        // This is the rewritten code for Kotlin...
        val stream = TokenStream(src.defaultValueTokens, src.location)
        val typed = antlrCompile(compiler.report, stream) { parser -> parser.expression() }
        ret.defaultValue = typed as MmlValueExpr
        // ...end of that.

        //ret.defaultValue = Parser.MmlParser(compiler, stream.source).ParseExpression()

        return ret
    }

    private fun buildMacroOperationList(src: MmlMacroDefinition): MmlSemanticMacro {
        val ret = MmlSemanticMacro(src.location, src.name, src.targetTracks)

        for (arg in src.arguments)
            ret.arguments.add(buildVariableDeclaration(arg))

        compileOperationTokens(ret.data, TokenStream(src.tokens, src.location))

        return ret
    }

    private fun buildTrackOperationList(src: MmlTrack): MmlSemanticTrack {
        val ret = MmlSemanticTrack(src.number)
        if (src.tokens.any())
            compileOperationTokens(ret.data, TokenStream(src.tokens, src.tokens.first().location))
        return ret
    }

    private fun compileOperationTokens(data: MutableList<MmlOperationUse>, stream: TokenStream) {
        if (stream.source.isNotEmpty()) {
            val results = antlrCompile(compiler.report, stream) { parser -> parser.operationUses() }
            data.addAll(results as List<MmlOperationUse>)
        }
    }

    init {
        result = MmlSemanticTreeSet().apply { baseCount = tokenSet.baseCount }
    }
}

class TokenStream(val source: List<MmlToken>, val definitionLocation: MmlLineInfo) {

    var position: Int = 0
}

//fun <K, V> Map<K, V?>.get(key: K): V? = this.getOrDefault(key, null)

// semantic tree expander

class MmlMacroExpander(private val source: MmlSemanticTreeSet, private val compiler: MmlCompiler) {
    companion object {
        fun expand(source: MmlSemanticTreeSet, compiler: MmlCompiler) {
            MmlMacroExpander(source, compiler).expand()
        }
    }

    private val expansionStack = mutableListOf<MmlSemanticMacro>()

    private fun expand() {
        val ctx = MmlResolveContext(source, null, compiler)

        // resolve variables without any context.
        for (variable in source.variables.values) {
            if (variable.defaultValue == null)
                variable.fillDefaultValue()
            val defValue = variable.defaultValue ?: throw Exception("INTERNAL ERROR: no default value for " + variable.name)
            defValue.resolver.resolve(ctx, variable.type)
        }

        for (macro in source.macros)
            expandMacro(macro)
    }

    private fun expandMacro(macro: MmlSemanticMacro) {
        for (variable in macro.arguments)
            if (variable.defaultValue == null)
                variable.fillDefaultValue()
    }
}


class MmlPrimitiveOperation {
    companion object {

        val all: List<MmlPrimitiveOperation>

        val Print = MmlPrimitiveOperation().apply { name = "__PRINT" }
        val Let = MmlPrimitiveOperation().apply { name = "__LET" };
        val LetPN = MmlPrimitiveOperation().apply { name = "__LET_PN" };
        val PerNote = MmlPrimitiveOperation().apply { name = "__PER_NOTE" };
        val PerNoteReset = MmlPrimitiveOperation().apply { name = "__PER_NOTE_RESET" };
        val Store = MmlPrimitiveOperation().apply { name = "__STORE" };
        val StoreFormat = MmlPrimitiveOperation().apply { name = "__STORE_FORMAT" };
        val Format = MmlPrimitiveOperation().apply { name = "__FORMAT" };
        val Apply = MmlPrimitiveOperation().apply { name = "__APPLY" };
        val Midi = MmlPrimitiveOperation().apply { name = "__MIDI" };
        val MidiNG = MmlPrimitiveOperation().apply { name = "__MIDI_NG" };
        val SyncNoteOffWithNext =
            MmlPrimitiveOperation().apply { name = "__SYNC_NOFF_WITH_NEXT" };
        val OnMidiNoteOff = MmlPrimitiveOperation().apply { name = "__ON_MIDI_NOTE_OFF" };
        val MidiMeta = MmlPrimitiveOperation().apply { name = "__MIDI_META" };
        val SaveOperationBegin = MmlPrimitiveOperation().apply { name = "__SAVE_OPER_BEGIN" };
        val SaveOperationEnd = MmlPrimitiveOperation().apply { name = "__SAVE_OPER_END" };
        val RestoreOperation = MmlPrimitiveOperation().apply { name = "__RESTORE_OPER" };
        val LoopBegin = MmlPrimitiveOperation().apply { name = "__LOOP_BEGIN" };
        val LoopBreak = MmlPrimitiveOperation().apply { name = "__LOOP_BREAK" };
        val LoopEnd = MmlPrimitiveOperation().apply { name = "__LOOP_END" };
        //#if !UNHACK_LOOP
        val LoopBegin2 =  MmlPrimitiveOperation().apply { name = "[" };
        val LoopBreak2 =  MmlPrimitiveOperation().apply { name = ":" };
        val LoopBreak3 =  MmlPrimitiveOperation().apply { name = "/" };
        val LoopEnd2 =  MmlPrimitiveOperation().apply { name = "]" };
        //#endif

        init {
            all = listOf(
                Print,
                Let,
                LetPN,
                PerNote,
                PerNoteReset,
                Store,
                StoreFormat,
                Format,
                Apply,
                Midi,
                MidiNG,
                SyncNoteOffWithNext,
                OnMidiNoteOff,
                MidiMeta,
                SaveOperationBegin,
                SaveOperationEnd,
                RestoreOperation,
                LoopBegin,
                LoopBreak,
                LoopEnd,
                //#if !UNHACK_LOOP
                LoopBegin2, LoopBreak2, LoopBreak3, LoopEnd2
                //#endif
            )
        }
    }

    lateinit var name: String
}

enum class MmlDataType {
    Any,
    Number,
    Length,
    String,
    Buffer,
}

/*struct*/ class MmlLength(num: Int) {

    val number: Int = num
    var dots: Int
    var isValueByStep: Boolean

    fun getSteps(numerator: Int): Int {
        if (isValueByStep)
            return number
        if (number == 0)
            return 0;
        var basis = numerator / number;
        var ret = basis;
        for (i in 0 until dots) {
            basis /= 2
            ret += basis;
        }
        return ret;
    }

    override fun toString() = "[${if (isValueByStep) "%" else ""}$number${".".repeat(dots)}]"

    init {
        dots = 0
        isValueByStep = false
    }
}

class MmlException(message: String = "MML error", innerException: Exception? = null) :
    Exception(message, innerException) {

    companion object {
        fun formatMessage(message: String, location: MmlLineInfo): String {
            if (location == MmlLineInfo.empty)
                return message;
            return "$message (${location.file} line ${location.lineNumber} column ${location.linePosition})"
        }
    }

    constructor (message: String, location: MmlLineInfo)
            : this(message, location, null)

    constructor (message: String, location: MmlLineInfo, innerException: Exception?)
            : this(formatMessage(message, location), innerException)
}
