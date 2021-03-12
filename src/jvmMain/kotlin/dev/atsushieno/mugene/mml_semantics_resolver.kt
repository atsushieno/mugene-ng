package dev.atsushieno.mugene

// variable resolver structures

abstract class MmlValueExprResolver(val expr: MmlValueExpr) {
    companion object {
        internal var baseCount = 192

        var stringToBytes: (String) -> ByteArray = { s: String -> s.encodeToByteArray() }

        fun lengthDotsToMultiplier(dots: Int): Double {
            return 2.0 - Math.pow(0.5, dots.toDouble())
        }


        fun getTypedValue(
            ctx: MmlResolveContext,
            value: Any?,
            type: MmlDataType,
            location: MmlLineInfo?
        ): Any? = getTypedValue(ctx.compiler, value, type, location)

        fun getTypedValue(
            compiler: MmlCompiler,
            value: Any?,
            type: MmlDataType,
            location: MmlLineInfo?
        ): Any? {
            when (type) {
                MmlDataType.Any -> return value
                MmlDataType.String -> return value.toString()
                MmlDataType.Number -> {
                    if (value is Double)
                        return value
                    if (value is Int)
                        return value.toDouble()
                    if (value is Byte)
                        return value.toDouble()
                    if (value is MmlLength)
                        return value.getSteps(baseCount).toDouble()
                    else
                        compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            location,
                            "Cannot convert from length to $type)",
                            listOf()
                        )
                    return null
                }
                MmlDataType.Length -> {
                    if (value is MmlLength)
                        return value
                    var denom = 0
                    if (value is Double)
                        denom = value.toInt()
                    else if (value is Int)
                        denom = value
                    else if (value is Byte)
                        denom = value.toInt()
                    else
                        compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            location,
                            "Cannot convert from length to $type)",
                            listOf()
                        )
                    return MmlLength(denom)
                }
                else -> {
                    compiler.report(
                        MmlDiagnosticVerbosity.Error,
                        location,
                        "Invalid value $value for the expected data type $type)",
                        listOf()
                    )
                    return null
                }
            }
        }
    }

    var resolvedValue: Any? = null

    abstract fun resolve(ctx: MmlResolveContext, type: MmlDataType)

    val byteValue = intValue.toByte()

    val byteArrayValue: ByteArray
        get() =
            if (resolvedValue is String || resolvedValue is StringBuilder)
                stringToBytes(stringValue)
            else
                byteArrayOf(byteValue)

    val intValue: Int
        get() =
            if (resolvedValue is Int) resolvedValue as Int
            else if (resolvedValue is Byte) (resolvedValue as Byte).toInt()
            else if (resolvedValue is MmlLength) (resolvedValue as MmlLength).getSteps(baseCount)
            else (resolvedValue as Double).toInt()

    fun getDoubleValue(ctx: MmlResolveContext) =
        getTypedValue(ctx, resolvedValue, MmlDataType.Number, expr.location) as Double

    val stringValue: String
        get() = resolvedValue.toString()
}

class MmlConstantExprResolver(expr: MmlConstantExpr) : MmlValueExprResolver(expr) {
    override fun resolve(ctx: MmlResolveContext, type: MmlDataType) {
        val expr = this.expr as MmlConstantExpr
        if (resolvedValue == null) {
            if (type == MmlDataType.Buffer)
                resolvedValue = StringBuilder()
            else
                resolvedValue = getTypedValue(ctx, expr.value, type, expr.location)
        }
    }
}

class MmlVariableReferenceExprResolver(expr: MmlVariableReferenceExpr) :
    MmlValueExprResolver(expr) {
    override fun resolve(ctx: MmlResolveContext, type: MmlDataType) {
        val expr = this.expr as MmlVariableReferenceExpr
        if (expr.scope == 3) {
            if (ctx.globalContext == null)
                ctx.compiler.report(
                    MmlDiagnosticVerbosity.Error,
                    null,
                    "Global variable '${expr.name}' cannot be resolved at this compilation phase",
                    listOf()
                )
            else
                resolveCore(ctx.globalContext, type, true)
        } else
            resolveCore(ctx, type, expr.scope > 1)
    }

    private fun resolveCore(ctx: MmlResolveContext, type: MmlDataType, excludeMacroArgs: Boolean) {
        val expr = this.expr as MmlVariableReferenceExpr
        if (!excludeMacroArgs) { // reference to macro argument takes precedence
            val _arg = ctx.macroArguments[expr.name]
            if (_arg != null) {
                val arg = _arg as Pair<MmlSemanticVariable, Any?>
                resolvedValue = getTypedValue(ctx, arg.second, type, expr.location)
                return
            }
        }

        val variable = ctx.sourceTree.variables[expr.name]
        if (variable == null)
            ctx.compiler.report(
                MmlDiagnosticVerbosity.Error,
                expr.location,
                "Cannot resolve variable '${expr.name}'",
                listOf()
            )
        else {
            val value = ctx.ensureDefaultResolvedVariable(variable)
            resolvedValue = getTypedValue(ctx, value, type, expr.location)
        }
    }
}

class MmlParenthesizedExprResolver(expr: MmlParenthesizedExpr) : MmlValueExprResolver(expr) {

    override fun resolve(ctx: MmlResolveContext, type: MmlDataType) {
        val expr = this.expr as MmlParenthesizedExpr
        expr.content.resolver.resolve(ctx, type)
        resolvedValue = expr.content.resolver.resolvedValue
    }
}

abstract class MmlArithmeticExprResolver(expr: MmlArithmeticExpr) : MmlValueExprResolver(expr) {}

class MmlAddExprResolver(expr: MmlAddExpr) : MmlArithmeticExprResolver(expr) {
    override fun resolve(ctx: MmlResolveContext, type: MmlDataType) {
        val expr = this.expr as MmlArithmeticExpr
        expr.left.resolver.resolve(ctx, type)
        expr.right.resolver.resolve(ctx, type)
        if (type == MmlDataType.Length)
            resolvedValue = MmlLength(
                (expr.left.resolver.getDoubleValue(ctx) + expr.right.resolver.getDoubleValue(ctx)).toInt()
            ).apply { isValueByStep = true }
        else
            resolvedValue =
                expr.left.resolver.getDoubleValue(ctx) + expr.right.resolver.getDoubleValue(ctx)
    }
}

class MmlSubtractExprResolver(expr: MmlSubtractExpr) : MmlArithmeticExprResolver(expr) {
    override fun resolve(ctx: MmlResolveContext, type: MmlDataType) {
        val expr = this.expr as MmlArithmeticExpr
        expr.left.resolver.resolve(ctx, type)
        expr.right.resolver.resolve(ctx, type)
        if (type == MmlDataType.Length)
            resolvedValue = MmlLength(
                (expr.left.resolver.getDoubleValue(ctx) - expr.right.resolver.getDoubleValue(ctx)).toInt()
            ).apply { isValueByStep = true }
        else
            resolvedValue =
                expr.left.resolver.getDoubleValue(ctx) - expr.right.resolver.getDoubleValue(ctx)
    }
}

class MmlMultiplyExprResolver(expr: MmlMultiplyExpr) : MmlArithmeticExprResolver(expr) {
    override fun resolve(ctx: MmlResolveContext, type: MmlDataType) {
        // multiplication cannot be straightforward. Number * Length must be Length,
        // but the number must not be converted to a length e.g. "1" must not be
        // interpreted as %{$__base_count}. Actually Length * Length must be invalid.

        val expr = this.expr as MmlArithmeticExpr
        expr.left.resolver.resolve(ctx, MmlDataType.Number)
        expr.right.resolver.resolve(ctx, type)
        if (type == MmlDataType.Length)
            resolvedValue = MmlLength(
                (expr.left.resolver.getDoubleValue(ctx) * expr.right.resolver.getDoubleValue(ctx)).toInt()
            ).apply { isValueByStep = true }
        else
            resolvedValue =
                expr.left.resolver.getDoubleValue(ctx) * expr.right.resolver.getDoubleValue(ctx)
    }
}

class MmlDivideExprResolver(expr: MmlDivideExpr) : MmlArithmeticExprResolver(expr) {
    override fun resolve(ctx: MmlResolveContext, type: MmlDataType) {
        val expr = this.expr as MmlArithmeticExpr
        expr.left.resolver.resolve(ctx, type)
        expr.right.resolver.resolve(ctx, type)
        if (type == MmlDataType.Length)
            resolvedValue = MmlLength(
                (expr.left.resolver.getDoubleValue(ctx) / expr.right.resolver.getDoubleValue(ctx)).toInt()
            ).apply { isValueByStep = true }
        else
            resolvedValue =
                expr.left.resolver.getDoubleValue(ctx) / expr.right.resolver.getDoubleValue(ctx)
    }
}

class MmlModuloExprResolver(expr: MmlModuloExpr) : MmlArithmeticExprResolver(expr) {
    override fun resolve(ctx: MmlResolveContext, type: MmlDataType) {
        val expr = this.expr as MmlArithmeticExpr
        expr.left.resolver.resolve(ctx, type)
        expr.right.resolver.resolve(ctx, type)
        if (type == MmlDataType.Length)
            resolvedValue = MmlLength(
                (expr.left.resolver.getDoubleValue(ctx) % expr.right.resolver.getDoubleValue(ctx)).toInt()
            ).apply { isValueByStep = true }
        else
            resolvedValue =
                expr.left.resolver.getDoubleValue(ctx) % expr.right.resolver.getDoubleValue(ctx)
    }
}

class MmlConditionalExprResolver(expr: MmlConditionalExpr) : MmlValueExprResolver(expr) {
    override fun resolve(ctx: MmlResolveContext, type: MmlDataType) {
        val expr = this.expr as MmlConditionalExpr
        expr.condition.resolver.resolve(ctx, MmlDataType.Number)
        expr.trueExpr.resolver.resolve(ctx, type)
        expr.falseExpr.resolver.resolve(ctx, type)
        resolvedValue =
            if (expr.condition.resolver.intValue != 0) expr.trueExpr.resolver.resolvedValue
            else expr.falseExpr.resolver.resolvedValue
    }
}

class MmlComparisonExprResolver(expr: MmlComparisonExpr) : MmlValueExprResolver(expr) {
    override fun resolve(ctx: MmlResolveContext, type: MmlDataType) {
        val expr = this.expr as MmlComparisonExpr
        expr.left.resolver.resolve(ctx, type)
        expr.right.resolver.resolve(ctx, type)
        // FIXME: make sure that this actually works...
        if (type == MmlDataType.String) {
            val l = expr.left.resolver.resolvedValue.toString()
            val r = expr.left.resolver.resolvedValue.toString()
            resolvedValue = if (when (expr.comparisonType) {
                ComparisonType.Lesser -> l < r
                ComparisonType.LesserEqual -> l <= r
                ComparisonType.Greater -> l > r
                ComparisonType.GreaterEqual -> l >= r
            }) 1 else 0
        } else {
            val l = expr.left.resolver.getDoubleValue(ctx)
            val r = expr.left.resolver.getDoubleValue(ctx)
            resolvedValue = if (when (expr.comparisonType) {
                ComparisonType.Lesser -> l < r
                ComparisonType.LesserEqual -> l <= r
                ComparisonType.Greater -> l > r
                ComparisonType.GreaterEqual -> l >= r
            }) 1 else 0
        }
    }
}


// primitive event stream structures

class MmlResolvedMusic {
    var baseCount = 192
    val tracks = mutableListOf<MmlResolvedTrack>()
}

class MmlResolvedTrack(val number: Double, source: MmlSemanticTreeSet) {
    val events = mutableListOf<MmlResolvedEvent>()
    val macros = mutableMapOf<String, MmlSemanticMacro>()

    init {
        for (m in source.macros)
            if (m.targetTracks.contains(number))
                macros[m.name] = m
    }
}

class MmlResolvedEvent {
    constructor (operation: String, tick: Int) {
        this.operation = operation
        this.tick = tick
    }

    // copy constructor
    constructor (other: MmlResolvedEvent, tick: Int) {
        operation = other.operation
        arguments.addAll(other.arguments)
        this.tick = tick
    }

    var tick: Int
    val operation: String
    val arguments = mutableListOf<Byte>()
}


// primitive resolver

class Loop(ctx: MmlResolveContext) {

    var beginAt: LoopLocation? = null
    var firstBreakAt: LoopLocation? = null
    val breaks = mutableMapOf<Int, LoopLocation>()
    var events = mutableListOf<MmlResolvedEvent>()
    val savedValues: MutableMap<MmlSemanticVariable, Any> = ctx.values
    var endLocations = mutableMapOf<Int, LoopLocation>() // count -> indexes for end
    var currentBreaks = mutableListOf<Int>()

}

data class LoopLocation(val source: Int, val output: Int, val tick: Int)

class MmlResolveContext(
    val sourceTree: MmlSemanticTreeSet,
    val globalContext: MmlResolveContext?,
    contextCompiler: MmlCompiler?
) {
    val compiler: MmlCompiler = contextCompiler ?: globalContext!!.compiler

    var timelinePosition: Int = 0
    var macroArguments = mutableMapOf<Any?, Any>()
    var values = mutableMapOf<MmlSemanticVariable, Any>()
    val loops = mutableListOf<Loop>()

    val currentLoop: Loop?
        get() = loops.lastOrNull()

    fun ensureDefaultResolvedVariable(variable: MmlSemanticVariable): Any? {
        var value = values[variable]
        if (value == null) {
            variable.defaultValue!!.resolver.resolve(this, variable.type)
            value = variable.defaultValue!!.resolver.resolvedValue!!
            values[variable] = value
        }
        return value
    }
}

class MmlEventStreamGenerator(private val source: MmlSemanticTreeSet, private val compiler: MmlCompiler) {
    companion object {
        fun generate(source: MmlSemanticTreeSet, contextCompiler: MmlCompiler): MmlResolvedMusic {
            val gen = MmlEventStreamGenerator(source, contextCompiler)
            gen.generate()
            return gen.result
        }
    }

    private lateinit var globalContext: MmlResolveContext
    val result: MmlResolvedMusic = MmlResolvedMusic().apply { baseCount = source.baseCount }
    private var currentOutput = mutableListOf<MmlResolvedEvent>()

    private fun generate() {
        globalContext = MmlResolveContext(source, null, compiler)

        for (track in source.tracks) {
            val rtrk = MmlResolvedTrack(track.number, source)
            result.tracks.add(rtrk)
            val tctx = MmlResolveContext(source, globalContext, compiler)
            val list = track.data
            currentOutput = rtrk.events
            processOperations(rtrk, tctx, list, 0, list.size, listOf())
            sort(currentOutput)
        }
    }

    private val chord = mutableListOf<MmlResolvedEvent>()
    private var recordNextAsChord = false
    private val storedOperations = mutableMapOf<Int, StoredOperations>()

    class StoredOperations {
        var operations = mutableListOf<MmlOperationUse>()
        var values = mutableMapOf<MmlSemanticVariable, Any>()
        var macroArguments = mutableMapOf<Any?, Any>()
    }

    // extraTailArgs is a list of arguments that are passed to the context macro call e.g.
    //   #macro CHORD_A c0e0g
    //   1   CHORD_A4 CHORD_A8 CHORD_A8
    // In this case, 4, 8 and 8 are NOT passed to CHORD_A unless these extraTailArgs are passed
    // and causes unexpected outputs.
    // We can still define macros to take full arguments to the defined sequence of operators
    // (in this case, to 'g'), but that is super annoying and basically impossible unless
    // you know the macro definition details (which almost no one would know).
    private fun processOperations(
        track: MmlResolvedTrack,
        rctx: MmlResolveContext,
        list: List<MmlOperationUse>,
        start: Int,
        count: Int,
        extraTailArgs: List<MmlValueExpr?>
    ) {
        var storeIndex = -1
        var storeCurrentOutput: MutableList<MmlResolvedEvent>? = null
        val storeDummy = mutableListOf<MmlResolvedEvent>()
        var currentStoredOperations: StoredOperations? = null
        var isStringFormat = false

        for (listIndex in start until start + count) {
            val oper = list[listIndex]
            val extraTailArgsIfApplied =
                if (listIndex == start + count - 1) extraTailArgs else null

            val arguments = oper.arguments.requireNoNulls()

            when (oper.name) {
                "__PRINT" -> {
                    arguments[0].resolver.resolve(rctx, MmlDataType.String)
                    compiler.report(
                        MmlDiagnosticVerbosity.Information,
                        oper.location,
                        arguments[0].resolver.stringValue,
                        extraTailArgs.requireNoNulls()
                    )
                    break
                }
                "__LET" -> {
                    arguments[0].resolver.resolve(rctx, MmlDataType.String)
                    val name = arguments[0].resolver.stringValue
                    val variable = source.variables.get(name) as MmlSemanticVariable?
                    if (variable == null) {
                        compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            oper.location,
                            "Target variable not found: $name",
                            listOf()
                        )
                    } else {
                        arguments[1].resolver.resolve(rctx, variable.type)
                        rctx.values[variable] = arguments[1].resolver.resolvedValue!!
                        if (name == "__timeline_position")
                            rctx.timelinePosition = arguments[1].resolver.intValue
                    }
                }
                "__STORE" -> {
                    arguments[0].resolver.resolve(rctx, MmlDataType.String)
                    oper.validateArguments(rctx, arguments.size)
                    val name = arguments[0].resolver.stringValue
                    val variable = source.variables[name]
                    if (variable == null) {
                        compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            oper.location,
                            "Target variable not found: $name",
                            listOf()
                        )
                        break
                    }
                    if (variable.type != MmlDataType.Buffer) {
                        compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            oper.location,
                            "Target variable is not a buffer: $name",
                            listOf()
                        )
                        break
                    }
                    val sb = rctx.ensureDefaultResolvedVariable(variable) as StringBuilder
                    for (i in 1 until arguments.size)
                        sb.append(arguments[i].resolver.stringValue)
                }
                "__FORMAT", "__STORE_FORMAT" -> {
                    isStringFormat = oper.name == "__FORMAT"
                    arguments[0].resolver.resolve(rctx, MmlDataType.String)
                    arguments[1].resolver.resolve(rctx, MmlDataType.String)
                    oper.validateArguments(rctx, oper.arguments.size)
                    val name = arguments[0].resolver.stringValue
                    val format = arguments[1].resolver.stringValue
                    val variable = source.variables[name]
                    if (variable == null) {
                        compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            oper.location,
                            "Target variable not found: $name",
                            listOf()
                        )
                        break
                    }
                    if (isStringFormat) {
                        if (variable.type != MmlDataType.String) {
                            compiler.report(
                                MmlDiagnosticVerbosity.Error,
                                oper.location,
                                "Target variable is not a string: $name",
                                listOf()
                            )
                            break
                        }
                    } else {
                        if (variable.type != MmlDataType.Buffer) {
                            compiler.report(
                                MmlDiagnosticVerbosity.Error,
                                oper.location,
                                "Target variable is not a buffer: $name",
                                listOf()
                            )
                            break
                        }
                    }
                    //try {
                    val v = String.format(
                        format,
                        arguments.drop(2).map { x -> x.resolver.stringValue }.toTypedArray()
                    )
                    if (isStringFormat)
                        rctx.values[variable] = v
                    else
                        (rctx.ensureDefaultResolvedVariable(variable) as StringBuilder).append(v)
                    /*} catch (ex: FormatException) {
                        compiler.Report(
                            MmlDiagnosticVerbosity.Error,
                            oper.location,
                            "Format error while applying '$format' to '$name': ${ex.message}",
                            listOf()
                        );
                        break;
                    }*/
                }
                "__APPLY" -> {
                    val oa = arguments[0]
                    oa.resolver.resolve(rctx, MmlDataType.String)
                    val apparg = oa.resolver.stringValue

                    // add macro argument definitions
                    val tmpop = MmlOperationUse(apparg, oper.location)
                    for (x in 1 until arguments.size)
                        tmpop.arguments.add(arguments[x])
                    processMacroCall(track, rctx, tmpop, extraTailArgsIfApplied ?: listOf())

                }
                "__MIDI" -> {
                    oper.validateArguments(rctx, arguments.size)
                    val mop = MmlResolvedEvent("MIDI", rctx.timelinePosition)
                    for (arg in arguments)
                        mop.arguments.add(arg.resolver.byteValue)
                    currentOutput.add(mop)
                    if (recordNextAsChord)
                        chord.add(mop)
                    recordNextAsChord = false
                }
                "__SYNC_NOFF_WITH_NEXT" -> {
                    recordNextAsChord = true
                }
                "__ON_MIDI_NOTE_OFF" -> {
                    // handle zero-length note
                    oper.validateArguments(
                        rctx,
                        3,
                        MmlDataType.Number,
                        MmlDataType.Number,
                        MmlDataType.Number
                    )
                    if (arguments[0].resolver.intValue == 0)
                    // record next note as part of chord
                        recordNextAsChord = true
                    else {
                        for (cop in chord)
                            cop.tick += arguments[0].resolver.intValue
                        chord.clear()
                    }
                }
                "__MIDI_META" -> {
                    oper.validateArguments(rctx, oper.arguments.size)
                    val mmop = MmlResolvedEvent("META", rctx.timelinePosition)
                    mmop.arguments.add(0xFF.toByte())
                    for (arg in arguments)
                        mmop.arguments.addAll(arg.resolver.byteArrayValue.toList())
                    currentOutput.add(mmop)
                }
                "__SAVE_OPER_BEGIN" -> {
                    oper.validateArguments(rctx, 0)
                    if (storeIndex >= 0) {
                        compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            oper.location,
                            "__SAVE_OPER_BEGIN works only within a simple list without nested uses",
                            listOf()
                        )
                        break
                    }
                    storeIndex = listIndex + 1
                    storeCurrentOutput = currentOutput
                    currentOutput = storeDummy
                    currentStoredOperations = StoredOperations()
                    currentStoredOperations.values = rctx.values.toList().toMap().toMutableMap()
                    currentStoredOperations.macroArguments =
                        rctx.macroArguments.toList().toMap().toMutableMap()
                }
                "__SAVE_OPER_END" -> {
                    oper.validateArguments(rctx, 1, MmlDataType.Number)
                    val bufIdx = arguments[0].resolver.intValue
                    storedOperations[bufIdx] = currentStoredOperations!!
                    currentStoredOperations.operations =
                        list.drop(storeIndex).take(listIndex - storeIndex - 1).toMutableList()
                    currentOutput = storeCurrentOutput!!
                    storeDummy.clear()
                    storeIndex = -1
                    currentStoredOperations = null
                    // FIXME: might be better to restore variables
                }
                "__RESTORE_OPER" -> {
                    oper.validateArguments(rctx, 1, MmlDataType.Number)
                    val bufIdx = arguments[0].resolver.intValue
                    val ss = storedOperations[bufIdx]!!
                    val valuesBak = rctx.values
                    val macroArgsBak = rctx.macroArguments
                    rctx.values = ss.values
                    rctx.macroArguments = ss.macroArguments
                    // adjust timeline_position (no need to update rctx.TimelinePosition here).
                    rctx.values[source.variables["__timeline_position"] as MmlSemanticVariable] =
                        rctx.timelinePosition
                    processOperations(
                        track,
                        rctx,
                        ss.operations,
                        0,
                        ss.operations.size,
                        extraTailArgsIfApplied ?: listOf()
                    )
                    rctx.values = valuesBak
                    rctx.macroArguments = macroArgsBak
                }
                // #if UNHACK_LOOP
                // "__LOOP_BEGIN" ->{
                // #else
                "__LOOP_BEGIN", "[" -> {
                    // #endif
                    oper.validateArguments(rctx, 0)
                    val loop = Loop(rctx).apply {
                        beginAt = LoopLocation(listIndex, currentOutput.size, rctx.timelinePosition)
                    }
                    rctx.values = mutableMapOf<MmlSemanticVariable, Any>()
                    for (p in loop.savedValues)
                        rctx.values[p.key] = p.value
                    rctx.loops.add(loop)
                    currentOutput = loop.events
                }
                // #if UNHACK_LOOP
                // "__LOOP_BREAK"-> {
                // #else
                "__LOOP_BREAK", "/", ":" -> {
                    // #endif
                    oper.validateArguments(rctx, oper.arguments.size)
                    val loop = rctx.currentLoop
                    if (loop == null) {
                        compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            oper.location,
                            "Loop break operation must be inside a pair of loop start and end",
                            listOf()
                        )
                        break
                    }
                    if (loop.firstBreakAt == null)
                        loop.firstBreakAt =
                            LoopLocation(listIndex, currentOutput.size, rctx.timelinePosition)
                    for (cl in loop.currentBreaks)
                        loop.endLocations[cl] =
                            LoopLocation(listIndex, currentOutput.size, rctx.timelinePosition)
                    loop.currentBreaks.clear()

                    // FIXME: actually this logic does not make sense as now it is defined with fixed-length arguments...
                    if (oper.arguments.size == 0) { // default loop break
                        if (loop.breaks.containsKey(-1) && loop.breaks.values.all { b -> b.source != listIndex })
                            compiler.report(
                                MmlDiagnosticVerbosity.Error,
                                oper.location,
                                "Default loop break is already defined in current loop",
                                listOf()
                            )
                        loop.breaks[-1] = LoopLocation(
                            listIndex,
                            currentOutput.size,
                            rctx.timelinePosition
                        )
                        loop.currentBreaks.add(-1)
                    } else {
                        for (x in arguments.indices) {
                            val numexpr = arguments[x]
                            val num =
                                numexpr.resolver.intValue - 1 // "1st. loop" for musicians == 0th iteration in code.
                            if (x > 0 && num < 0)
                                break // after the last argument.
                            loop.currentBreaks.add(num)
                            if (loop.breaks.containsKey(num) && loop.breaks.values.all { b -> b.source != listIndex }) {
                                compiler.report(
                                    MmlDiagnosticVerbosity.Error,
                                    oper.location,
                                    "Loop section $num was already defined in current loop",
                                    listOf()
                                )
                                break
                            }
                            // specified loop count is for human users. Here the number is for program, hence -1.
                            loop.breaks[num] = LoopLocation(
                                listIndex,
                                currentOutput.size,
                                rctx.timelinePosition
                            )
                        }
                    }
                }
                // #if !UNHACK_LOOP
                // "__LOOP_END"->{
                // #else
                "__LOOP_END", "]" -> {
                    // #endif
                    oper.validateArguments(rctx, 0, MmlDataType.Number)
                    val loop = rctx.currentLoop
                    if (loop == null) {
                        compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            oper.location,
                            "Loop has not started",
                            listOf()
                        )
                        break
                    }
                    for (cl in loop.currentBreaks)
                        loop.endLocations[cl] =
                            LoopLocation(listIndex, currentOutput.size, rctx.timelinePosition)
                    var loopCount = 0
                    when (arguments.size) {
                        0 -> loopCount = 2
                        1 -> loopCount = arguments[0].resolver.intValue
                        else -> compiler.report(
                            MmlDiagnosticVerbosity.Error,
                            oper.location,
                            "Arguments at loop end exceeded",
                            listOf()
                        )
                    }

                    rctx.loops.removeLast()
                    val outside = rctx.currentLoop
                    currentOutput = if (outside != null) outside.events else track.events

                    // now expand loop.
                    // - verify that every loop break does not exceed the loop count
                    // - add sequence before the first break
                    // - add sequence for each break. If no explicit break, then use default.
                    for (p in loop.breaks) {
                        if (p.key > loopCount) {
                            compiler.report(
                                MmlDiagnosticVerbosity.Error,
                                list[p.value.source].location,
                                "Loop break specified beyond the loop count",
                                listOf()
                            )
                            loop.breaks.clear()
                        }
                    }

                    rctx.values = loop.savedValues

                    var baseTicks = 0
                    var baseOutputEnd = 0
                    var tickOffset = 0
                    val beginAt = loop.beginAt!!
                    val firstBreakAt = loop.firstBreakAt
                    if (firstBreakAt == null) { // w/o break
                        baseTicks = rctx.timelinePosition - beginAt.tick
                        baseOutputEnd = loop.events.size
                        rctx.timelinePosition = beginAt.tick

                        // This range of commands actually adds extra argument definitions for loop operation, but it won't hurt.
                        for (l in 0 until loopCount)
                            processOperations(
                                track,
                                rctx,
                                list,
                                beginAt.source + 1,
                                listIndex - beginAt.source - 1,
                                extraTailArgsIfApplied ?: listOf()
                            )
                    } else { // w/ breaks
                        baseTicks = firstBreakAt.tick - beginAt.tick
                        baseOutputEnd = firstBreakAt.output

                        rctx.timelinePosition = beginAt.tick

                        for (l in 0 until loopCount) {
                            processOperations(
                                track,
                                rctx,
                                list,
                                beginAt.source + 1,
                                firstBreakAt.source - beginAt.source - 1,
                                extraTailArgsIfApplied ?: listOf()
                            )
                            tickOffset += baseTicks
                            var lb = loop.breaks[l]
                            if (lb == null) {
                                if (l + 1 == loopCount)
                                    break // this is to break the loop at the last iteration.
                                lb = loop.breaks[-1]
                                if (lb == null) {
                                    compiler.report(
                                        MmlDiagnosticVerbosity.Error,
                                        list[beginAt.source].location,
                                        "No corresponding loop break specification for iteration at ${l + 1} from the innermost loop",
                                        listOf()
                                    )
                                    loop.breaks.clear()
                                }
                            }
                            if (lb == null) // final break
                                break
                            var elb = loop.endLocations[l]
                            if (elb == null)
                                elb = loop.endLocations[-1]!!
                            var breakOffset = lb.tick - beginAt.tick + baseTicks
                            processOperations(
                                track,
                                rctx,
                                list,
                                lb.source + 1,
                                elb.source - lb.source - 1,
                                extraTailArgsIfApplied ?: listOf()
                            )
                        }
                    }
                }
                else ->
                    processMacroCall(track, rctx, oper, extraTailArgsIfApplied ?: listOf())
            }
        }
    }

    private val expansionStack = mutableListOf<MmlSemanticMacro>()

    private val argCaches = mutableListOf<MutableMap<Any?, Any>>()
    private var cacheStackNum = 0

    private fun processMacroCall(
        track: MmlResolvedTrack,
        ctx: MmlResolveContext,
        oper: MmlOperationUse,
        extraTailArgs: List<MmlValueExpr?>
    ) {
        val macro = track.macros[oper.name]
        if (macro == null) {
            compiler.report(
                MmlDiagnosticVerbosity.Error,
                oper.location,
                "Macro ${oper.name} was not found",
                listOf()
            )
            return
        }
        if (expansionStack.contains(macro)) {
            compiler.report(
                MmlDiagnosticVerbosity.Error,
                oper.location,
                "Illegally recursive macro reference to ${macro.name} is found",
                listOf()
            )
            return
        }
        expansionStack.add(macro)

        if (cacheStackNum == argCaches.size)
            argCaches.add(mutableMapOf())
        val args = argCaches[cacheStackNum++]
        val operUseArgs =
            if (extraTailArgs.any()) oper.arguments + extraTailArgs else oper.arguments
        for (i in 0 until macro.arguments.size) {
            val argdef = macro.arguments[i]
            var arg = if (i < operUseArgs.size) operUseArgs[i] else null
            if (arg == null)
                arg = argdef.defaultValue!!
            arg.resolver.resolve(ctx, argdef.type)
            if (args.contains(argdef.name))
                compiler.report(
                    MmlDiagnosticVerbosity.Error,
                    oper.location,
                    "Argument name must be identical to all other argument names. Argument '${argdef.name}' in '${oper.name}' macro",
                    listOf()
                )
            args[argdef.name] = Pair(argdef, arg.resolver.resolvedValue!!)
        }
        val argsBak = ctx.macroArguments
        ctx.macroArguments = args
        val extraTailArgsToCall =
            if (macro.arguments.size < operUseArgs.size) operUseArgs.drop(macro.arguments.size) else null
        processOperations(
            track,
            ctx,
            macro.data,
            0,
            macro.data.size,
            extraTailArgsToCall ?: listOf()
        )
        ctx.macroArguments = argsBak

        expansionStack.removeLast()
        args.clear()
        --cacheStackNum
    }

    private fun sort(l: MutableList<MmlResolvedEvent>) {
        val msgBlockByTime = mutableMapOf<Int, MutableList<MmlResolvedEvent>>()
        var m = 0
        var prev = 0

        while (m < l.size) {
            val e = l[m]
            var pl = msgBlockByTime[l[m].tick]
            if (pl == null) {
                pl = mutableListOf()
                msgBlockByTime[e.tick] = pl
            }
            prev = l[m].tick
            pl.add(l[m])
            m++
            while (m < l.size && l[m].tick == prev) {
                pl.add(l[m])
                m++
            }
        }

        l.clear()
        for (k in msgBlockByTime.keys.sorted())
            l.addAll(msgBlockByTime[k]!!)
    }
}

