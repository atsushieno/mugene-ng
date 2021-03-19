package dev.atsushieno.mugene

import dev.atsushieno.mugene.parser.MugeneParser
import dev.atsushieno.mugene.parser.MugeneParserBaseVisitor
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenFactory
import org.antlr.v4.runtime.TokenSource
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.ANTLRErrorListener
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token.DEFAULT_CHANNEL
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import java.util.*

class SimpleEOFToken(private val source: TokenSource) : Token {
    override fun getChannel() : Int = -1
    override fun getCharPositionInLine(): Int = 0
    override fun getInputStream(): CharStream? = null
    override fun getLine(): Int = 0
    override fun getStartIndex(): Int = 0
    override fun getStopIndex(): Int = 0
    override fun getText(): String = "" // If we set null, that causes NPE at error reporting.
    override fun getTokenIndex(): Int = Token.EOF
    override fun getTokenSource(): TokenSource? = source
    override fun getType(): Int = Token.EOF
}

class WrappedToken(private val src: MmlToken, private val sourceTokenSource: TokenSource) : Token {
    val mmlToken = src
    override fun getChannel() = DEFAULT_CHANNEL
    override fun getCharPositionInLine() = src.location.linePosition
    override fun getInputStream(): CharStream? = null
    override fun getLine() = src.location.lineNumber
    override fun getStartIndex() = src.location.linePosition
    override fun getStopIndex() = src.location.linePosition + (src.value?.toString()?.length ?: 0)
    override fun getText() = src.value.toString()
    override fun getTokenIndex() = src.tokenType.ordinal
    override fun getTokenSource() = sourceTokenSource
    override fun getType() = when (src.tokenType) {
        MmlTokenType.BackSlashGreater -> MugeneParser.BackSlashGreater
        MmlTokenType.Asterisk -> MugeneParser.Asterisk
        MmlTokenType.BackSlashGreaterEqual -> MugeneParser.BackSlashGreaterEqual
        MmlTokenType.BackSlashLesser -> MugeneParser.BackSlashLesser
        MmlTokenType.BackSlashLesserEqual -> MugeneParser.BackSlashLesserEqual
        MmlTokenType.Caret -> MugeneParser.Caret
        MmlTokenType.CloseCurly -> MugeneParser.CloseCurly
        MmlTokenType.CloseParen -> MugeneParser.CloseParen
        MmlTokenType.Colon -> MugeneParser.Colon
        MmlTokenType.Comma -> MugeneParser.Comma
        MmlTokenType.Dollar -> MugeneParser.Dollar
        MmlTokenType.Identifier -> MugeneParser.Identifier
        MmlTokenType.KeywordBuffer -> MugeneParser.KeywordBuffer
        MmlTokenType.KeywordLength -> MugeneParser.KeywordLength
        MmlTokenType.KeywordNumber -> MugeneParser.KeywordNumber
        MmlTokenType.KeywordString -> MugeneParser.KeywordString
        MmlTokenType.Minus -> MugeneParser.Minus
        MmlTokenType.NumberLiteral -> MugeneParser.NumberLiteral
        MmlTokenType.OpenCurly -> MugeneParser.OpenCurly
        MmlTokenType.OpenParen -> MugeneParser.OpenParen
        MmlTokenType.Percent -> MugeneParser.Percent
        MmlTokenType.Period -> MugeneParser.Dot
        MmlTokenType.Plus -> MugeneParser.Plus
        MmlTokenType.Question -> MugeneParser.Question
        MmlTokenType.Slash -> MugeneParser.Slash
        MmlTokenType.StringLiteral -> MugeneParser.StringLiteral
        else -> 0
    }
}

class WrappedTokenFactory<T> : TokenFactory<T> where T : Token {
    override fun create(type: Int, text: String): T {
        TODO("Not yet implemented")
    }

    /** This is the method used to create tokens in the lexer and in the
     * error handling strategy. If text!=null, than the start and stop positions
     * are wiped to -1 in the text override is set in the CommonToken.
     */
    override fun create(
        source: org.antlr.v4.runtime.misc.Pair<TokenSource, CharStream>?,
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
    override fun getCharPositionInLine(): Int = ts.source[ts.position].location.linePosition
    override fun getLine(): Int = ts.source[ts.position].location.lineNumber
    override fun getSourceName(): String? = ts.source[ts.position].location.file

    override fun getTokenFactory(): TokenFactory<*> = WrappedTokenFactory<Token>()
    override fun setTokenFactory(factory: TokenFactory<*>?) = TODO("Not yet implemented")

    override fun nextToken() : Token =
        if (ts.position == ts.source.size) SimpleEOFToken(this)
        else WrappedToken(ts.source[ts.position++], this)

    override fun getInputStream(): CharStream? = TODO("Not yet implemented")
}

class MugeneParserVisitorImpl(private val reporter: MmlDiagnosticReporter) : MugeneParserBaseVisitor<Any>() {
    private fun getSingleContent(ctx: ParserRuleContext) = visit(ctx.getChild(0)!!)!!

    private val skippedArgument = MmlConstantExpr (MmlLineInfo.empty, MmlDataType.String, "DEFAULT ARGUMENT")

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
        return when (ctx.altNumber) {
            0 -> {
                // FIXME: this smells like a bug in antlr-kotlin that it sets altNumber = 0 to the left branch
                //  (`operationUse`) which is supposed to only return a single MmlOperationUse child.
                //
                // correct implementation:
                //mutableListOf<MmlOperationUse>().apply {
                //    val content = getSingleContent(ctx)
                //    add(content as MmlOperationUse)
                //}
                // sloppy implementation:
                val content = getSingleContent(ctx)
                if (content is MmlOperationUse)
                    mutableListOf<MmlOperationUse>().apply {
                        add(content)
                    }
                else
                    content as MutableList<*> // MutableList<MmlOperationUse>, but generics are gone.
            }
            1 -> {
                val l = visit(ctx.getChild(0)!!)!! as MutableList<MmlOperationUse>
                l.add(visit(ctx.getChild(1)!!)!! as MmlOperationUse)
                l
            }
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitOperationUse(ctx: MugeneParser.OperationUseContext): Any {
        when (ctx.altNumber) {
            0, 1 -> {
                val i = visit(ctx.getChild(0)!!)!! as MmlToken
                val o = MmlOperationUse (i.value as String, i.location)
                if (ctx.altNumber == 1) {
                    val l = visit(ctx.getChild(1)!!)!! as List<MmlValueExpr>
                    for (a in l)
                        o.arguments.add(if (a == skippedArgument) null else a)
                }
                return o
            }
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitArgumentsOptCurly(ctx: MugeneParser.ArgumentsOptCurlyContext): Any {
        return when (ctx.altNumber) {
            0 -> getSingleContent(ctx)
            1 -> mutableListOf<MmlValueExpr>() // empty
            2 -> visit(ctx.getChild(1)!!)!!
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitArguments(ctx: MugeneParser.ArgumentsContext): Any {
        return when (ctx.altNumber) {
            0 -> mutableListOf<MmlValueExpr>().apply { add(getSingleContent(ctx) as MmlValueExpr) }
            1 -> {
                val args = visit(ctx.getChild(0)!!)!! as MutableList<MmlValueExpr?>
                val commas = visit(ctx.getChild(2)!!)!! as Int
                val arg = visit(ctx.getChild(2)!!)!! as MmlValueExpr
                // add default arguments (one comma works as a normal parameter separator, so -1)
                for (i in 0 until commas - 1)
                    args.add(skippedArgument)
                // add last argument (cannot omit)
                args.add(arg)
                args
            }
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitConditionalExpr(ctx: MugeneParser.ConditionalExprContext): Any {
        return when (ctx.altNumber) {
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
        return when (ctx.altNumber) {
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
        return when (ctx.altNumber) {
            0 -> ComparisonType.Lesser
            1 -> ComparisonType.LesserEqual
            2 -> ComparisonType.Greater
            3 -> ComparisonType.GreaterEqual
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitAddSubExpr(ctx: MugeneParser.AddSubExprContext): Any {
        if (ctx.altNumber == 0)
            return getSingleContent(ctx)
        val l = visit(ctx.getChild(0)!!) as MmlValueExpr
        val r = visit(ctx.getChild(1)!!) as MmlValueExpr
        return when (ctx.altNumber) {
            1 -> MmlAddExpr(l, r)
            2 -> MmlAddExpr(l, r)
            3 -> MmlSubtractExpr(l, r)
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitMulDivModExpr(ctx: MugeneParser.MulDivModExprContext): Any {
        if (ctx.altNumber == 0)
            return getSingleContent(ctx)
        val l = visit(ctx.getChild(0)!!) as MmlValueExpr
        val r = visit(ctx.getChild(1)!!) as MmlValueExpr
        return when (ctx.altNumber) {
            1 -> MmlMultiplyExpr(l, r)
            2 -> MmlDivideExpr(l, r)
            3 -> MmlModuloExpr(l, r)
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitPrimaryExpr(ctx: MugeneParser.PrimaryExprContext): Any {
        return when (ctx.altNumber) {
            0, 1, 3, 4 -> getSingleContent(ctx)
            2 -> MmlParenthesizedExpr (visit(ctx.getChild(1)!!) as MmlValueExpr)
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitUnaryExpr(ctx: MugeneParser.UnaryExprContext): Any {
        return when (ctx.altNumber) {
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
            when (ctx.altNumber) {
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
        when (ctx.altNumber) {
            0 -> {
                val t = visit(ctx.getChild(0)!!) as MmlToken
                return MmlConstantExpr (t.location, MmlDataType.Number, t.value)
            }
            1 -> {
                val t = visit(ctx.getChild(0)!!) as MmlToken
                var d = visit(ctx.getChild(1)!!) as Int
                return MmlConstantExpr (t.location, MmlDataType.Length, MmlLength ((t.value as Double).toInt()).apply { dots = d })
            }
            2 -> {
                val d = getSingleContent(ctx) as Int
                return MmlMultiplyExpr ( MmlConstantExpr (ctx.start!!.toMmlLineInfo(), MmlDataType.Number, MmlValueExprResolver.lengthDotsToMultiplier (d)), MmlVariableReferenceExpr (ctx.start!!.toMmlLineInfo(), "__length"))
            }
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    private fun Token.toMmlLineInfo() = MmlLineInfo(tokenSource!!.sourceName ?: "", line, charPositionInLine)

    override fun visitDots(ctx: MugeneParser.DotsContext): Any {
        return when (ctx.altNumber) {
            0 -> 1
            1 -> getSingleContent(ctx) as Int + 1
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }

    override fun visitCommas(ctx: MugeneParser.CommasContext): Any {
        return when (ctx.altNumber) {
            0 -> 1
            1 -> getSingleContent(ctx) as Int + 1
            else -> error ("Unexpected parser error; unexpected token index")
        }
    }
}

actual fun antlrCompileOperationUses(treeBuilder: MmlSemanticTreeBuilder, reporter: MmlDiagnosticReporter, stream: TokenStream) : Any
    = treeBuilder.antlrCompile(reporter, stream, { parser -> parser.operationUses() } )
actual fun antlrCompileExpression(treeBuilder: MmlSemanticTreeBuilder, reporter: MmlDiagnosticReporter, stream: TokenStream) : Any
    = treeBuilder.antlrCompile(reporter, stream, { parser -> parser.expression() } )

private fun MmlSemanticTreeBuilder.antlrCompile(
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
            reporter(
                MmlDiagnosticVerbosity.Error,
                MmlLineInfo.empty,
                "reportAmbiguity(startIndex: $startIndex, stopIndex: $stopIndex, exact: $exact)"
            )
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
            TODO("Context sensitivity. Not yet implemented")
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
    val visitor = MugeneParserVisitorImpl(reporter)
    return visitor.visitExpressionOrOperationUses(tree as MugeneParser.ExpressionOrOperationUsesContext)!!
}
