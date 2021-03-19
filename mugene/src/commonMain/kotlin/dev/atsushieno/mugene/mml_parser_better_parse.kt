package dev.atsushieno.mugene

import com.github.h0tk3y.betterParse.combinators.and
import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.oneOrMore
import com.github.h0tk3y.betterParse.combinators.optional
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.combinators.zeroOrMore
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.TokenMatchesSequence
import com.github.h0tk3y.betterParse.lexer.TokenProducer
import com.github.h0tk3y.betterParse.parser.ParseResult
import com.github.h0tk3y.betterParse.parser.Parser


class MugeneTokenProducer(private val stream : TokenStream) : TokenProducer {
    private var index = 0

    override fun nextToken(): TokenMatch? {
        if (index == stream.source.size)
            return null
        val t = stream.source[index++]
        val token = MugeneParser.getToken(t.tokenType)
        val tokenString = t.value.toString()
        return TokenMatch(token, t.tokenType.ordinal, tokenString, 0, tokenString.length, t.location.lineNumber, t.location.linePosition)
    }
}

class MToken(name: String, ignored: Boolean = false) : Token(name, ignored)
{
    override fun match(input: CharSequence, fromIndex: Int): Int {
        TODO("Not yet implemented")
    }
}

class MugeneParser : Grammar<Any>() {
    companion object {
        val Question = MToken("?", false)
        val Slash = MToken("/", false)
        val StringLiteral = MToken("", false)
        val Plus = MToken("+", false)
        val Dot = MToken(".", false)
        val Percent = MToken("%", false)
        val OpenParen = MToken("(", false)
        val OpenCurly = MToken("{", false)
        val NumberLiteral = MToken("NumberLiteral", false)
        val Minus = MToken("-", false)
        val KeywordString = MToken("string", false)
        val KeywordNumber = MToken("number", false)
        val KeywordLength = MToken("length", false)
        val KeywordBuffer = MToken("buffer", false)
        val Identifier = MToken("Identifier", false)
        val Dollar = MToken("$", false)
        val Comma = MToken(",", false)
        val Colon = MToken(":", false)
        val CloseParen = MToken(")", false)
        val CloseCurly = MToken("}", false)
        val Caret = MToken("^", false)
        val BackSlashLesserEqual = MToken("\\<=", false)
        val BackSlashLesser = MToken("\\<", false)
        val BackSlashGreaterEqual = MToken("\\>=", false)
        val BackSlashGreater = MToken("\\>", false)
        val Asterisk = MToken("*", false)

        fun getToken(tokenType: MmlTokenType) = when(tokenType) {
            MmlTokenType.Question -> Question
            MmlTokenType.Slash -> Slash
            MmlTokenType.StringLiteral -> StringLiteral
            MmlTokenType.Plus -> Plus
            MmlTokenType.Period -> Dot
            MmlTokenType.Percent -> Percent
            MmlTokenType.OpenParen -> OpenParen
            MmlTokenType.OpenCurly -> OpenCurly
            MmlTokenType.NumberLiteral -> NumberLiteral
            MmlTokenType.Minus -> Minus
            MmlTokenType.KeywordString -> KeywordString
            MmlTokenType.KeywordNumber -> KeywordNumber
            MmlTokenType.KeywordLength -> KeywordLength
            MmlTokenType.KeywordBuffer -> KeywordBuffer
            MmlTokenType.Identifier -> Identifier
            MmlTokenType.Dollar -> Dollar
            MmlTokenType.Comma -> Comma
            MmlTokenType.Colon -> Colon
            MmlTokenType.CloseParen -> CloseParen
            MmlTokenType.CloseCurly -> CloseCurly
            MmlTokenType.Caret -> Caret
            MmlTokenType.BackSlashLesserEqual -> BackSlashLesserEqual
            MmlTokenType.BackSlashLesser -> BackSlashLesser
            MmlTokenType.BackSlashGreaterEqual -> BackSlashGreaterEqual
            MmlTokenType.BackSlashGreater -> BackSlashGreater
            MmlTokenType.Asterisk -> Asterisk
            else -> throw UnsupportedOperationException()
        }
    }

    private val dots by oneOrMore(Dot) use { size }
    private val commas by oneOrMore(Comma) use { size }
    private val canBeIdentifier by (Identifier or Colon or Slash)
    private val stringConstant by StringLiteral
    private val numberOrLengthConstant by NumberLiteral or (NumberLiteral and dots) or dots
    private val stepConstant by (Percent and NumberLiteral) or (Percent and Minus and NumberLiteral)
    private val variableReference by (Dollar and canBeIdentifier)
    private val unaryExpr by numberOrLengthConstant or (Minus and numberOrLengthConstant) or (Caret and numberOrLengthConstant)
    private val primaryExpr : Parser<Any>
        get() = variableReference or stringConstant or (OpenCurly and expression and CloseCurly) or stepConstant or unaryExpr
    // FIXME: they are not recursive enough
    private val mulDivModExpr : Parser<Any>
        get() = primaryExpr or
            leftAssociative(primaryExpr, Asterisk) { l, _, r -> MmlMultiplyExpr(l as MmlValueExpr, r as MmlValueExpr) } or
            leftAssociative(primaryExpr, Slash) { l, _, r -> MmlDivideExpr(l as MmlValueExpr, r as MmlValueExpr) } or
            leftAssociative(primaryExpr, Percent) { l, _, r -> MmlModuloExpr(l as MmlValueExpr, r as MmlValueExpr) }
    private val addSubExpr by mulDivModExpr or
            leftAssociative(mulDivModExpr, Plus) { l, _, r -> MmlAddExpr(l as MmlValueExpr, r as MmlValueExpr) } or
            leftAssociative(mulDivModExpr, Caret) { l, _, r -> MmlAddExpr(l as MmlValueExpr, r as MmlValueExpr) } or
            leftAssociative(mulDivModExpr, Minus) { l, _, r -> MmlSubtractExpr(l as MmlValueExpr, r as MmlValueExpr) }
    private val comparisonOperator by BackSlashLesser or BackSlashLesserEqual or BackSlashGreater or BackSlashGreaterEqual
    private val comparisonExpr by addSubExpr or
            leftAssociative(addSubExpr, comparisonOperator) { l, c, r -> MmlComparisonExpr(l as MmlValueExpr, r as MmlValueExpr, c as ComparisonType) }
    // FIXME: it is not recursive enough
    private val conditionalExpr by comparisonExpr or (comparisonExpr and Question and comparisonExpr and Comma and comparisonExpr)
    private val expression by conditionalExpr
    private val argument by expression
    private val arguments by argument or
            leftAssociative(argument, commas) { l, _, r -> (l as MutableList<MmlValueExpr>).apply { add(r as MmlValueExpr) } }
    private val argumentsOptCurly by arguments or (OpenCurly and optional(arguments) and CloseCurly)
    private val operationUse by canBeIdentifier and optional(argumentsOptCurly)
    private val operationUses by zeroOrMore(operationUse)

    override val rootParser by operationUses or expression
}
