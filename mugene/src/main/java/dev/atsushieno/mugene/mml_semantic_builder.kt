package dev.atsushieno.mugene

import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.UnsupportedOperationException

class MmlSemanticTreeSet {
    constructor() {
        baseCount = 192
        tracks = mutableListOf()
        macros = mutableListOf()
        variables = mutableMapOf()
    }

    var baseCount: Int
    lateinit var tracks: MutableList<MmlSemanticTrack>
    lateinit var macros: MutableList<MmlSemanticMacro>
    lateinit var variables: MutableMap<String, MmlSemanticVariable>
}

class MmlSemanticTrack {
    constructor (number: Double) {
        this.number = number
        data = mutableListOf<MmlOperationUse>()
    }

    var number: Double = 0.0

    lateinit var data: MutableList<MmlOperationUse>
}

public class MmlSemanticMacro {
    constructor (location: MmlLineInfo, name: String, targetTracks: List<Double>) {
        this.location = location
        this.name = name
        this.targetTracks = targetTracks
        arguments = mutableListOf<MmlSemanticVariable>()
        data = mutableListOf<MmlOperationUse>()
    }

    lateinit var location: MmlLineInfo
    lateinit var name: String
    lateinit var targetTracks: List<Double>
    lateinit var arguments: MutableList<MmlSemanticVariable>
    lateinit var data: MutableList<MmlOperationUse>
}

class MmlSemanticVariable {
    constructor (location: MmlLineInfo, name: String, type: MmlDataType) {
        this.location = location
        this.name = name
        this.type = type
    }

    lateinit var location: MmlLineInfo
    lateinit var name: String
    lateinit var type: MmlDataType
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
            // it happens only for macro arg definition.
            else ->
                throw UnsupportedOperationException("Not implemented for type " + type)
        }
    }

    override fun toString(): String {
        if (defaultValue != null)
            return String.format("$0:$1(=$2)", name, type, defaultValue)
        else
            return String.format("$0:$1", name, type)
    }
}

abstract class MmlValueExpr {
    companion object {
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

    protected constructor (location: MmlLineInfo?) {
        this.location = location
    }

    var location: MmlLineInfo?

    lateinit var resolver: MmlValueExprResolver
}

class MmlConstantExpr : MmlValueExpr {
    constructor (location: MmlLineInfo?, type: MmlDataType, value: Any?)
            : super(location) {
        this.type = type
        this.value = value
        resolver = MmlConstantExprResolver(this)
    }

    val type: MmlDataType
    val value: Any?

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
            : this(location, name, 1) {
    }

    constructor (location: MmlLineInfo, name: String, scope: Int)
            : super(location) {
        this.scope = scope
        this.name = name
        resolver = MmlVariableReferenceExprResolver(this)
    }

    var scope: Int

    lateinit var name: String

    override fun toString() = "\$${if (scope > 1) "\$" else ""}$name"
}

class MmlParenthesizedExpr : MmlValueExpr {
    constructor (content: MmlValueExpr)
            : super(content.location) {
        this.content = content
        resolver = MmlParenthesizedExprResolver(this)
    }

    lateinit var content: MmlValueExpr

    override fun toString() = "($content)"
}

abstract class MmlArithmeticExpr : MmlValueExpr {
    protected constructor (left: MmlValueExpr, right: MmlValueExpr)
            : super(left.location) {
        this.left = left
        this.right = right
    }

    lateinit var left: MmlValueExpr
    lateinit var right: MmlValueExpr
}

class MmlAddExpr : MmlArithmeticExpr {
    constructor (left: MmlValueExpr, right: MmlValueExpr)
            : super(left, right) {
        resolver = MmlAddExprResolver(this)
    }

    override fun toString(): String = "$left + $right"
}

class MmlSubtractExpr : MmlArithmeticExpr {
    constructor (left: MmlValueExpr, right: MmlValueExpr)
            : super(left, right) {
        resolver = MmlSubtractExprResolver(this)
    }

    override fun toString(): String = "$left - $right"
}

class MmlMultiplyExpr : MmlArithmeticExpr {
    constructor (left: MmlValueExpr, right: MmlValueExpr)
            : super(left, right) {
        resolver = MmlMultiplyExprResolver(this)
    }

    override fun toString(): String = "$left * $right"
}

class MmlDivideExpr : MmlArithmeticExpr {
    constructor (left: MmlValueExpr, right: MmlValueExpr)
            : super(left, right) {
        resolver = MmlDivideExprResolver(this)
    }

    override fun toString(): String = "$left / $right"
}

class MmlModuloExpr : MmlArithmeticExpr {
    constructor (left: MmlValueExpr, right: MmlValueExpr)
            : super(left, right) {
    }

    override fun toString(): String = "$left % $right"
}

class MmlConditionalExpr : MmlValueExpr {
    constructor (condition: MmlValueExpr, trueExpr: MmlValueExpr, falseExpr: MmlValueExpr)
            : super(condition.location) {
        this.condition = condition
        this.trueExpr = trueExpr
        this.falseExpr = falseExpr
    }

    lateinit var condition: MmlValueExpr
    lateinit var trueExpr: MmlValueExpr
    lateinit var falseExpr: MmlValueExpr

    override fun toString(): String = "$condition ? $trueExpr % $falseExpr"
}

class MmlComparisonExpr : MmlValueExpr {
    constructor (left: MmlValueExpr, right: MmlValueExpr, type: ComparisonType)
            : super(left.location) {
        this.left = left
        this.right = right
        comparisonType = type
    }

    lateinit var left: MmlValueExpr
    lateinit var right: MmlValueExpr
    lateinit var comparisonType: ComparisonType

    override fun toString(): String = "$left $comparisonType $right"

    fun ComparisonType.toString() {
        when (this) {
            ComparisonType.Lesser -> "<"
            ComparisonType.LesserEqual -> "<="
            ComparisonType.Greater -> ">"
            ComparisonType.GreaterEqual -> ">"
            else -> throw UnsupportedOperationException()
        }
    }
}

enum class ComparisonType {
    Lesser,
    LesserEqual,
    Greater,
    GreaterEqual,
}

// at this phase, we cannot determine if an invoked operation is a macro, or a primitive operation.
class MmlOperationUse {
    constructor (name: String, location: MmlLineInfo?) {
        this.name = name
        this.location = location
        this.arguments = mutableListOf<MmlValueExpr>()
    }

    val name: String

    var location: MmlLineInfo?

    val arguments: MutableList<MmlValueExpr>

    override fun toString(): String {
        var args = mutableListOf<String>()
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
                    "Insufficient argument(s)",
                    listOf()
                )
                return
            }
        }
        for (i in 0 until arguments.size) {
            var arg = arguments[i]
            var type = if (i < types.size) types[i] else MmlDataType.Any
            arg.resolver.resolve(ctx, type)
        }
    }
}


// semantic tree builder

class MmlSemanticTreeBuilder {
    companion object {
        fun compile(tokenSet: MmlTokenSet, contextCompiler: MmlCompiler): MmlSemanticTreeSet {
            var b = MmlSemanticTreeBuilder(tokenSet, contextCompiler)
            b.compile()
            return b.result
        }
    }

    constructor (tokenSet: MmlTokenSet, contextCompiler: MmlCompiler) {
        if (tokenSet == null)
            throw IllegalArgumentException("tokenSet")
        this.compiler = contextCompiler
        this.tokenSet = tokenSet
        result = MmlSemanticTreeSet().apply { baseCount = tokenSet.baseCount }
    }

    lateinit var compiler: MmlCompiler
    lateinit var tokenSet: MmlTokenSet
    lateinit var result: MmlSemanticTreeSet

    private fun compile() {
        var metaTrack = MmlSemanticTrack(0.0)
        for (p in tokenSet.metaTexts) {
            var use = MmlOperationUse(MmlPrimitiveOperation.MidiMeta.name, null)
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

    private fun buildVariableDeclaration(src: MmlVariableDefinition): MmlSemanticVariable {
        var ret = MmlSemanticVariable(src.location, src.name, src.type)

        if (src.defaultValueTokens.size == 0)
            return ret

        var stream = TokenStream(src.defaultValueTokens, src.location)
        ret.defaultValue = Parser.MmlParser(compiler, stream.source).ParseExpression()

        return ret
    }

    private fun buildMacroOperationList(src: MmlMacroDefinition): MmlSemanticMacro {
        var ret = MmlSemanticMacro(src.location, src.name, src.targetTracks)

        for (arg in src.arguments)
            ret.arguments.add(buildVariableDeclaration(arg))

        compileOperationTokens(ret.data, TokenStream(src.tokens, src.location))

        return ret
    }

    private fun buildTrackOperationList(src: MmlTrack): MmlSemanticTrack {
        var ret = MmlSemanticTrack(src.number)
        if (ret.data.any())
            compileOperationTokens(ret.data, TokenStream(src.tokens, ret.data.first().location!!))
        return ret
    }

    private fun compileOperationTokens(data: MutableList<MmlOperationUse>, stream: TokenStream) {
        data.addAll(Parser.MmlParser(compiler, stream.source).ParseOperations())
    }
}

class TokenStream {
    constructor (source: List<MmlToken>, definitionLocation: MmlLineInfo) {
        this.source = source
        this.definitionLocation = definitionLocation
    }

    lateinit var definitionLocation: MmlLineInfo

    lateinit var source: List<MmlToken>

    var position: Int = 0
}

@RequiresApi(Build.VERSION_CODES.N)
fun <K, V> Map<K, V?>.get(key: K): V? = this.getOrDefault(key, null)

// semantic tree expander

class MmlMacroExpander {
    companion object {
        fun expand(source: MmlSemanticTreeSet, contextCompiler: MmlCompiler) {
            MmlMacroExpander(source, contextCompiler).expand()
        }
    }

    constructor (source: MmlSemanticTreeSet, contextCompiler: MmlCompiler) {
        this.compiler = contextCompiler
        this.source = source
    }

    private lateinit var compiler: MmlCompiler
    private lateinit var source: MmlSemanticTreeSet
    private val expansionStack = mutableListOf<MmlSemanticMacro>()

    private fun expand() {
        var ctx = MmlResolveContext(source, null, compiler)

        // resolve variables without any context.
        for (variable in source.variables.values) {
            if (variable.defaultValue == null)
                variable.fillDefaultValue()
            var defValue = variable.defaultValue
            if (defValue == null)
                throw Exception("INTERNAL ERROR: no default value for " + variable.name)
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