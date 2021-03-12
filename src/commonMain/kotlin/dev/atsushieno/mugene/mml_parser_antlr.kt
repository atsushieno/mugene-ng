package dev.atsushieno.mugene

import dev.atsushieno.mugene.parser.MugeneParser
import dev.atsushieno.mugene.parser.MugeneParserBaseVisitor
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.TokenFactory
import org.antlr.v4.kotlinruntime.TokenSource
import org.antlr.v4.kotlinruntime.tree.pattern.DEFAULT_CHANNEL


class WrappedToken(src: MmlToken, sourceTokenSource: TokenSource) : Token {
    override val channel = DEFAULT_CHANNEL
    override val charPositionInLine = src.location.linePosition
    override val inputStream: CharStream? = null
    override val line = src.location.lineNumber
    override val startIndex = src.location.linePosition
    override val stopIndex = src.location.linePosition + (src.value?.toString()?.length ?: 0)
    override val text = src.value.toString()
    override val tokenIndex = src.tokenType.ordinal
    override val tokenSource = sourceTokenSource
    override val type = src.tokenType.ordinal
}

class WrappedTokenFactory<T> : TokenFactory<T> where T : Token {
    override fun create(type: Int, text: String): T {
        TODO("Not yet implemented")
    }

    override fun create(
        source: Pair<TokenSource?, CharStream?>,
        type: Int,
        text: String?,
        channel: Int,
        start: Int,
        stop: Int,
        line: Int,
        charPositionInLine: Int
    ): T {
        TODO("Not yet implemented")
    }

}

class WrappedTokenSource(val ts: TokenStream) : TokenSource {
    override val charPositionInLine: Int
        get() = ts.source[ts.position].location.linePosition
    override val line: Int
        get() = ts.source[ts.position].location.lineNumber
    override val sourceName: String?
        get() = ts.source[ts.position].location.file
    override var tokenFactory: TokenFactory<*> = WrappedTokenFactory<Token>()

    override fun nextToken() =
        if (ts.position < ts.source.size) WrappedToken(ts.source.last(), this)
        else WrappedToken(ts.source[ts.position], this)

    override fun readInputStream(): CharStream? {
        TODO("Not yet implemented")
    }
}

class MugeneParserVisitorImpl(private val reporter: MmlDiagnosticReporter) : MugeneParserBaseVisitor<Any>() {
    private fun getSingleContent(ctx: ParserRuleContext) = visit(ctx.getChild(0)!!)!!

    private val skippedArgument = MmlConstantExpr (MmlLineInfo.empty, MmlDataType.String, "DEFAULT ARGUMENT")

    override fun visitExpressionOrOptOperationUses(ctx: MugeneParser.ExpressionOrOptOperationUsesContext): Any {
        return when (ctx.ruleIndex) {
            0 -> mutableListOf<MmlOperationUse>()
            else -> getSingleContent(ctx)
        }
    }

    override fun visitOperationUses(ctx: MugeneParser.OperationUsesContext): Any {
        return when (ctx.ruleIndex) {
            0 -> mutableListOf<MmlOperationUse>().apply { addAll(getSingleContent(ctx) as List<MmlOperationUse>) }
            1 -> {
                val l = visit(ctx.getChild(0)!!)!! as MutableList<MmlOperationUse>
                l.add(visit(ctx.getChild(1)!!)!! as MmlOperationUse)
                l
            }
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitOperationUse(ctx: MugeneParser.OperationUseContext): Any {
        val i = visit(ctx.getChild(0)!!)!! as MmlToken
        val o = MmlOperationUse (i.value as String, i.location)
        val l = visit(ctx.getChild(1)!!)!! as List<MmlValueExpr>
        for(a in l)
            o.arguments.add(if (a == skippedArgument) null else a);
        return o;
    }

    override fun visitArgumentsOptCurly(ctx: MugeneParser.ArgumentsOptCurlyContext): Any {
        return when (ctx.ruleIndex) {
            0 -> getSingleContent(ctx)
            1 -> visit(ctx.getChild(1)!!)!!
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitOptArguments(ctx: MugeneParser.OptArgumentsContext): Any {
        return when (ctx.ruleIndex) {
            0 -> mutableListOf<MmlValueExpr>()
            1 -> getSingleContent(ctx)
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitArguments(ctx: MugeneParser.ArgumentsContext): Any {
        return when (ctx.ruleIndex) {
            0 -> mutableListOf<MmlValueExpr>().apply { add(getSingleContent(ctx) as MmlValueExpr) }
            1 -> {
                val a = visit(ctx.getChild(0)!!)!! as MmlValueExpr
                val l = visit(ctx.getChild(2)!!)!! as MutableList<MmlValueExpr>
                l.add(0, a)
                l
            }
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitOptArgument(ctx: MugeneParser.OptArgumentContext): Any {
        return when (ctx.ruleIndex) {
            0 -> skippedArgument
            1 -> getSingleContent(ctx)
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitConditionalExpr(ctx: MugeneParser.ConditionalExprContext): Any {
        return when (ctx.ruleIndex) {
            0 -> getSingleContent(ctx)
            1 -> {
                val c = visit(ctx.getChild(0)!!) as MmlValueExpr
                val t = visit(ctx.getChild(2)!!) as MmlValueExpr
                val f = visit(ctx.getChild(4)!!) as MmlValueExpr
                MmlConditionalExpr(c, t, f)
            }
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitComparisonExpr(ctx: MugeneParser.ComparisonExprContext): Any {
        return when (ctx.ruleIndex) {
            0 -> getSingleContent(ctx)
            1 -> {
                val l = visit(ctx.getChild(0)!!) as MmlValueExpr
                val c = visit(ctx.getChild(1)!!) as ComparisonType
                val r = visit(ctx.getChild(2)!!) as MmlValueExpr
                MmlComparisonExpr(l, r, c)
            }
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitComparisonOperator(ctx: MugeneParser.ComparisonOperatorContext): Any {
        return when(ctx.ruleIndex) {
            0 -> ComparisonType.Lesser
            1 -> ComparisonType.LesserEqual
            2 -> ComparisonType.Greater
            3 -> ComparisonType.GreaterEqual
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitAddSubExpr(ctx: MugeneParser.AddSubExprContext): Any {
        if (ctx.ruleIndex == 0)
            return getSingleContent(ctx)
        val l = visit(ctx.getChild(0)!!) as MmlValueExpr
        val r = visit(ctx.getChild(1)!!) as MmlValueExpr
        return when (ctx.ruleIndex) {
            1 -> MmlAddExpr(l, r)
            2 -> MmlAddExpr(l, r)
            3 -> MmlSubtractExpr(l, r)
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitMulDivModExpr(ctx: MugeneParser.MulDivModExprContext): Any {
        if (ctx.ruleIndex == 0)
            return getSingleContent(ctx)
        val l = visit(ctx.getChild(0)!!) as MmlValueExpr
        val r = visit(ctx.getChild(1)!!) as MmlValueExpr
        return when (ctx.ruleIndex) {
            1 -> MmlMultiplyExpr(l, r)
            2 -> MmlDivideExpr(l, r)
            3 -> MmlModuloExpr(l, r)
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitPrimaryExpr(ctx: MugeneParser.PrimaryExprContext): Any {
        return when (ctx.ruleIndex) {
            0, 1, 3, 4 -> getSingleContent(ctx)
            2 -> MmlParenthesizedExpr (visit(ctx.getChild(1)!!) as MmlValueExpr)
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitUnaryExpr(ctx: MugeneParser.UnaryExprContext): Any {
        return when (ctx.ruleIndex) {
            0 -> getSingleContent(ctx)
            1 -> {
                val expr = visit(ctx.getChild(1)!!) as MmlValueExpr
                MmlMultiplyExpr(MmlConstantExpr(expr.location, MmlDataType.Number, -1), expr)
            }
            2 -> {
                val expr = visit(ctx.getChild(1)!!) as MmlValueExpr
                MmlAddExpr(MmlVariableReferenceExpr(expr.location!!, "__length"), expr)
            }
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitVariableReference(ctx: MugeneParser.VariableReferenceContext): Any {
        val i = visit(ctx.getChild(1)!!) as MmlToken
        return MmlVariableReferenceExpr(i.location, i.value as String)
    }

    override fun visitStringConstant(ctx: MugeneParser.StringConstantContext): Any {
        val t = getSingleContent(ctx) as MmlToken
        return MmlConstantExpr(t.location, MmlDataType.String, t.value as String?)
    }

    override fun visitStepConstant(ctx: MugeneParser.StepConstantContext): Any {
        val mul =
            when (ctx.ruleIndex) {
                0 -> 1
                1 -> -1
                else -> error ("Unexpected parser error; unexpected token index")
            }
        val n = visit(ctx.getChild(1)!!) as MmlToken
        val l = MmlLength (mul * (MmlValueExprResolver.getTypedValue (reporter, n.value, MmlDataType.Number, n.location) as Double).toInt()).apply {
            isValueByStep = true
        }
        return MmlConstantExpr (n.location, MmlDataType.Length, l)
    }

    override fun visitNumberOrLengthConstant(ctx: MugeneParser.NumberOrLengthConstantContext): Any {
        when (ctx.ruleIndex) {
            0 -> {
                val t = visit(ctx.getChild(0)!!) as MmlToken
                return MmlConstantExpr (t.location, MmlDataType.Number, t.value)
            }
            1 -> {
                val t = visit(ctx.getChild(0)!!) as MmlToken
                var d = visit(ctx.getChild(1)!!) as Int
                return MmlConstantExpr (t.location, MmlDataType.Length, MmlLength ((t.value as Double).toInt()).apply { dots = d });
            }
            2 -> {
                val d = getSingleContent(ctx) as Int
                return MmlMultiplyExpr ( MmlConstantExpr (ctx.start!!.toMmlLineInfo(), MmlDataType.Number, MmlValueExprResolver.lengthDotsToMultiplier (d)), MmlVariableReferenceExpr (ctx.start!!.toMmlLineInfo(), "__length"));
            }
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    private fun Token.toMmlLineInfo() = MmlLineInfo(tokenSource!!.sourceName ?: "", line, charPositionInLine)

    override fun visitDots(ctx: MugeneParser.DotsContext): Any {
        return when (ctx.ruleIndex) {
            0 -> 1
            1 -> getSingleContent(ctx) as Int + 1
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }
}
