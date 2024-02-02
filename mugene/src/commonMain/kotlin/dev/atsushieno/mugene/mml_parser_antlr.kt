package dev.atsushieno.mugene

import dev.atsushieno.mugene.parser.MugeneParser
import dev.atsushieno.mugene.parser.MugeneParserBaseVisitor
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.TokenFactory
import org.antlr.v4.kotlinruntime.TokenSource
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import org.antlr.v4.kotlinruntime.Token.Companion.DEFAULT_CHANNEL

class SimpleEOFToken(source: TokenSource) : Token {
    override val channel: Int = -1
    override val charPositionInLine: Int = 0
    override val inputStream: CharStream? = null
    override val line: Int = 0
    override val startIndex: Int = 0
    override val stopIndex: Int = 0
    override val text: String = ""
    override val tokenIndex: Int = Token.EOF
    override val tokenSource: TokenSource = source
    override val type: Int = Token.EOF
}

class WrappedToken(src: MmlToken, sourceTokenSource: TokenSource) : Token {
    val mmlToken = src
    override val channel = DEFAULT_CHANNEL
    override val charPositionInLine = src.location.linePosition
    override val inputStream: CharStream? = null
    override val line = src.location.lineNumber
    override val startIndex = src.location.linePosition
    override val stopIndex = src.location.linePosition + (src.value?.toString()?.length ?: 0)
    override val text = src.value.toString()
    override val tokenIndex = src.tokenType.ordinal
    override val tokenSource = sourceTokenSource
    override val type = when (src.tokenType) {
        MmlTokenType.Asterisk -> MugeneParser.Tokens.Asterisk
        MmlTokenType.BackSlashGreater -> MugeneParser.Tokens.BackSlashGreater
        MmlTokenType.BackSlashGreaterEqual -> MugeneParser.Tokens.BackSlashGreaterEqual
        MmlTokenType.BackSlashLesser -> MugeneParser.Tokens.BackSlashLesser
        MmlTokenType.BackSlashLesserEqual -> MugeneParser.Tokens.BackSlashLesserEqual
        MmlTokenType.Caret -> MugeneParser.Tokens.Caret
        MmlTokenType.CloseCurly -> MugeneParser.Tokens.CloseCurly
        MmlTokenType.CloseParen -> MugeneParser.Tokens.CloseParen
        MmlTokenType.Colon -> MugeneParser.Tokens.Colon
        MmlTokenType.Comma -> MugeneParser.Tokens.Comma
        MmlTokenType.Dollar -> MugeneParser.Tokens.Dollar
        MmlTokenType.Identifier -> MugeneParser.Tokens.Identifier
        MmlTokenType.KeywordBuffer -> MugeneParser.Tokens.KeywordBuffer
        MmlTokenType.KeywordLength -> MugeneParser.Tokens.KeywordLength
        MmlTokenType.KeywordNumber -> MugeneParser.Tokens.KeywordNumber
        MmlTokenType.KeywordString -> MugeneParser.Tokens.KeywordString
        MmlTokenType.Minus -> MugeneParser.Tokens.Minus
        MmlTokenType.NumberLiteral -> MugeneParser.Tokens.NumberLiteral
        MmlTokenType.OpenCurly -> MugeneParser.Tokens.OpenCurly
        MmlTokenType.OpenParen -> MugeneParser.Tokens.OpenParen
        MmlTokenType.Percent -> MugeneParser.Tokens.Percent
        MmlTokenType.Period -> MugeneParser.Tokens.Dot
        MmlTokenType.Plus -> MugeneParser.Tokens.Plus
        MmlTokenType.Question -> MugeneParser.Tokens.Question
        MmlTokenType.Slash -> MugeneParser.Tokens.Slash
        MmlTokenType.StringLiteral -> MugeneParser.Tokens.StringLiteral
        else -> 0
    }
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

class WrappedTokenSource(private val ts: TokenStream) : TokenSource {
    override val charPositionInLine: Int
        get() = ts.source[ts.position].location.linePosition
    override val inputStream: CharStream?
        get() = null // FIXME: anything better?
    override val line: Int
        get() = ts.source[ts.position].location.lineNumber
    override val sourceName: String
        get() = ts.source[ts.position].location.file
    override var tokenFactory: TokenFactory<*> = WrappedTokenFactory<Token>()

    override fun nextToken() : Token =
        if (ts.position == ts.source.size) SimpleEOFToken(this)
        else WrappedToken(ts.source[ts.position++], this)
}

@Suppress("UNCHECKED_CAST")
class MugeneParserVisitorImpl(private val compiler: MmlCompiler) : MugeneParserBaseVisitor<Any>() {
    private fun getSingleContent(ctx: ParserRuleContext) = visit(ctx.getChild(0)!!)!!

    override fun visitTerminal(node: TerminalNode): Any? {
        val wt = node.symbol as WrappedToken?
        return wt?.mmlToken ?: super.visitTerminal(node)
    }

    override fun visitExpressionOrOperationUses(ctx: MugeneParser.ExpressionOrOperationUsesContext): Any {
        return getSingleContent(ctx)
    }

    override fun visitExpression(ctx: MugeneParser.ExpressionContext): Any {
        return getSingleContent(ctx)
    }

    override fun visitOperationUses(ctx: MugeneParser.OperationUsesContext): Any {
        val ret = mutableListOf<MmlOperationUse>()
        ctx.getOperationUse().forEach {
            val content = visitOperationUse(it)
            if (content is MmlOperationUse)
                ret.add(content)
        }
        return ret
    }

    override fun visitOperationUse(ctx: MugeneParser.OperationUseContext): Any {
        val i = visit(ctx.getCanBeIdentifier()!!)!! as MmlToken
        val o = MmlOperationUse (i.value as String, i.location)
        if (ctx.getArgumentsOptCurly() != null) {
            val l = visit(ctx.getArgumentsOptCurly()!!)!! as List<MmlValueExpr>
            for (a in l)
                o.arguments.add(if (a == MmlValueExpr.skippedArgument) null else a)
        }
        return o
    }

    override fun visitCanBeIdentifier(ctx: MugeneParser.CanBeIdentifierContext): Any {
        return getSingleContent(ctx)
    }

    override fun visitArgumentsOptCurly(ctx: MugeneParser.ArgumentsOptCurlyContext): Any {
        return if (ctx.getArguments() == null) mutableListOf<MmlValueExpr>() else visit(ctx.getArguments()!!)!!
    }

    override fun visitArguments(ctx: MugeneParser.ArgumentsContext): Any {
        val head = ctx.getArguments()
        val ret =
            if (head != null) visitArguments(head) as MutableList<MmlValueExpr>
            else mutableListOf()

        val commasNode = ctx.getCommas()
        if (commasNode != null) {
            val numCommas = visit(commasNode) as Int
            // only extra commas contribute to default arguments (i.e. skip "only one" comma)
            ret.addAll((0 until numCommas - 1).map { MmlValueExpr.skippedArgument })
        }

        ret.add(visitArgument(ctx.getArgument()) as MmlValueExpr)

        return ret
    }

    override fun visitArgument(ctx: MugeneParser.ArgumentContext): Any {
        return getSingleContent(ctx)
    }

    override fun visitConditionalExpr(ctx: MugeneParser.ConditionalExprContext): Any {
        return when (ctx.childCount) {
            1 -> getSingleContent(ctx)
            5 -> {
                val c = visit(ctx.getChild(0)!!) as MmlValueExpr
                val t = visit(ctx.getChild(2)!!) as MmlValueExpr
                val f = visit(ctx.getChild(4)!!) as MmlValueExpr
                MmlConditionalExpr(c, t, f)
            }
            else -> error ("Unexpected parser error; unexpected child count: ${ctx.childCount}")
        }
    }

    override fun visitComparisonExpr(ctx: MugeneParser.ComparisonExprContext): Any {
        return when (ctx.childCount) {
            1 -> getSingleContent(ctx)
            3 -> {
                val l = visit(ctx.getChild(0)!!) as MmlValueExpr
                val c = visit(ctx.getChild(1)!!) as ComparisonType
                val r = visit(ctx.getChild(2)!!) as MmlValueExpr
                MmlComparisonExpr(l, r, c)
            }
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitComparisonOperator(ctx: MugeneParser.ComparisonOperatorContext): Any {
        return if (ctx.BackSlashLesser() != null) ComparisonType.Lesser
            else if (ctx.BackSlashLesserEqual() != null) ComparisonType.LesserEqual
            else if (ctx.BackSlashGreater() != null) ComparisonType.Greater
            else if (ctx.BackSlashGreaterEqual() != null) ComparisonType.GreaterEqual
            else error("Unexpected parser error; unexpected token index")
    }

    override fun visitAddSubExpr(ctx: MugeneParser.AddSubExprContext): Any {
        if (ctx.childCount == 1)
            return getSingleContent(ctx)
        val l = visit(ctx.getChild(0)!!) as MmlValueExpr
        val r = visit(ctx.getChild(2)!!) as MmlValueExpr
        return when {
            ctx.Plus() != null -> MmlAddExpr(l, r)
            ctx.Caret() != null -> MmlAddExpr(l, r)
            ctx.Minus() != null -> MmlSubtractExpr(l, r)
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitMulDivModExpr(ctx: MugeneParser.MulDivModExprContext): Any {
        if (ctx.childCount == 1)
            return getSingleContent(ctx)
        val l = visit(ctx.getChild(0)!!) as MmlValueExpr
        val r = visit(ctx.getChild(2)!!) as MmlValueExpr
        return when {
            ctx.Asterisk() != null -> MmlMultiplyExpr(l, r)
            ctx.Slash() != null -> MmlDivideExpr(l, r)
            ctx.Percent() != null -> MmlModuloExpr(l, r)
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitPrimaryExpr(ctx: MugeneParser.PrimaryExprContext): Any {
        return when {
            ctx.getVariableReference() != null || ctx.getStringConstant() != null ||
                ctx.getStepConstant() != null || ctx.getUnaryExpr() != null
                -> getSingleContent(ctx)
            else -> MmlParenthesizedExpr (visit(ctx.getExpression()!!) as MmlValueExpr)
        }
    }

    override fun visitUnaryExpr(ctx: MugeneParser.UnaryExprContext): Any {
        return if (ctx.Caret() != null) {
            val expr = visit(ctx.getChild(1)!!) as MmlValueExpr
            MmlAddExpr(MmlVariableReferenceExpr(expr.location!!, "__length"), expr)
        } else {
            val mul = if (ctx.Minus() != null) -1 else 1
            val expr = visit(ctx.getNumberOrLengthConstant()!!) as MmlValueExpr
            MmlMultiplyExpr(MmlConstantExpr(expr.location, MmlDataType.Number, mul), expr)
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
        val mul = if (ctx.Minus() != null) -1 else 1
        val n = visit(ctx.NumberLiteral() !!) as MmlToken
        val l = MmlLength (mul * (MmlValueExprResolver.getTypedValue (compiler, n.value, MmlDataType.Number, n.location) as Double).toInt()).apply {
            isValueByStep = true
        }
        return MmlConstantExpr (n.location, MmlDataType.Length, l)
    }

    override fun visitNumberOrLengthConstant(ctx: MugeneParser.NumberOrLengthConstantContext): Any {
        val dots = ctx.getDots()
        return if (ctx.NumberLiteral() != null) {
            val t = visit(ctx.NumberLiteral()!!) as MmlToken
            if (dots == null) {
                MmlConstantExpr(t.location, MmlDataType.Number, t.value)
            } else {
                val d = visit(dots) as Int
                MmlConstantExpr(
                    t.location,
                    MmlDataType.Length,
                    MmlLength((t.value as Double).toInt()).apply { this.dots = d })
            }
        } else {
            val d = if (dots == null) 1 else visit(dots) as Int
            MmlMultiplyExpr ( MmlConstantExpr (MmlLineInfo.empty, MmlDataType.Number, MmlValueExprResolver.lengthDotsToMultiplier (d)), MmlVariableReferenceExpr (MmlLineInfo.empty, "__length"))
        }
    }

    override fun visitDots(ctx: MugeneParser.DotsContext): Any {
        return if (ctx.getDots() == null) 1 else getSingleContent(ctx) as Int + 1
    }

    override fun visitCommas(ctx: MugeneParser.CommasContext): Any {
        return if (ctx.getCommas() == null) 1 else getSingleContent(ctx) as Int + 1
    }
}
