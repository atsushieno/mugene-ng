// Generated from src/antlr/MugeneParser.g4 by ANTLR 4.7.2
package dev.atsushieno.mugene.parser

import org.antlr.v4.runtime.dfa.DFA
import org.antlr.v4.runtime.atn.PredictionContextCache
import dev.atsushieno.mugene.parser.MugeneParser
import org.antlr.v4.runtime.Vocabulary
import org.antlr.v4.runtime.VocabularyImpl
import org.antlr.v4.runtime.atn.ATN
import org.antlr.v4.runtime.atn.ATNDeserializer
import org.antlr.v4.runtime.RuntimeMetaData
import org.antlr.v4.runtime.ParserRuleContext
import dev.atsushieno.mugene.parser.MugeneParser.OperationUsesContext
import org.antlr.v4.runtime.tree.ParseTreeVisitor
import dev.atsushieno.mugene.parser.MugeneParserVisitor
import org.antlr.v4.runtime.RecognitionException
import dev.atsushieno.mugene.parser.MugeneParser.ExpressionOrOperationUsesContext
import org.antlr.v4.runtime.NoViableAltException
import dev.atsushieno.mugene.parser.MugeneParser.OperationUseContext
import org.antlr.v4.runtime.FailedPredicateException
import dev.atsushieno.mugene.parser.MugeneParser.CanBeIdentifierContext
import dev.atsushieno.mugene.parser.MugeneParser.ArgumentsOptCurlyContext
import dev.atsushieno.mugene.parser.MugeneParser.ArgumentsContext
import org.antlr.v4.runtime.tree.TerminalNode
import dev.atsushieno.mugene.parser.MugeneParser.ArgumentContext
import dev.atsushieno.mugene.parser.MugeneParser.CommasContext
import dev.atsushieno.mugene.parser.MugeneParser.ConditionalExprContext
import dev.atsushieno.mugene.parser.MugeneParser.ComparisonExprContext
import dev.atsushieno.mugene.parser.MugeneParser.AddSubExprContext
import dev.atsushieno.mugene.parser.MugeneParser.ComparisonOperatorContext
import dev.atsushieno.mugene.parser.MugeneParser.MulDivModExprContext
import dev.atsushieno.mugene.parser.MugeneParser.PrimaryExprContext
import dev.atsushieno.mugene.parser.MugeneParser.VariableReferenceContext
import dev.atsushieno.mugene.parser.MugeneParser.StringConstantContext
import dev.atsushieno.mugene.parser.MugeneParser.StepConstantContext
import dev.atsushieno.mugene.parser.MugeneParser.UnaryExprContext
import dev.atsushieno.mugene.parser.MugeneParser.NumberOrLengthConstantContext
import dev.atsushieno.mugene.parser.MugeneParser.DotsContext
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.atn.ParserATNSimulator

class MugeneParser(input: TokenStream?) : Parser(input) {
    companion object {
        protected val _decisionToDFA: Array<DFA?>
        protected val _sharedContextCache = PredictionContextCache()
        const val StringLiteral = 1
        const val Comma = 2
        const val OpenParen = 3
        const val CloseParen = 4
        const val OpenCurly = 5
        const val CloseCurly = 6
        const val Question = 7
        const val Caret = 8
        const val Plus = 9
        const val Minus = 10
        const val Asterisk = 11
        const val Slash = 12
        const val Percent = 13
        const val Dollar = 14
        const val Colon = 15
        const val Dot = 16
        const val BackSlashLesser = 17
        const val BackSlashLesserEqual = 18
        const val BackSlashGreater = 19
        const val BackSlashGreaterEqual = 20
        const val KeywordNumber = 21
        const val KeywordLength = 22
        const val KeywordString = 23
        const val KeywordBuffer = 24
        const val NumberLiteral = 25
        const val Identifier = 26
        const val RULE_expressionOrOperationUses = 0
        const val RULE_operationUses = 1
        const val RULE_operationUse = 2
        const val RULE_argumentsOptCurly = 3
        const val RULE_arguments = 4
        const val RULE_argument = 5
        const val RULE_expression = 6
        const val RULE_conditionalExpr = 7
        const val RULE_comparisonExpr = 8
        const val RULE_comparisonOperator = 9
        const val RULE_addSubExpr = 10
        const val RULE_mulDivModExpr = 11
        const val RULE_primaryExpr = 12
        const val RULE_unaryExpr = 13
        const val RULE_variableReference = 14
        const val RULE_stringConstant = 15
        const val RULE_stepConstant = 16
        const val RULE_numberOrLengthConstant = 17
        const val RULE_dots = 18
        const val RULE_canBeIdentifier = 19
        const val RULE_commas = 20
        private fun makeRuleNames(): Array<String> {
            return arrayOf(
                "expressionOrOperationUses", "operationUses", "operationUse", "argumentsOptCurly",
                "arguments", "argument", "expression", "conditionalExpr", "comparisonExpr",
                "comparisonOperator", "addSubExpr", "mulDivModExpr", "primaryExpr", "unaryExpr",
                "variableReference", "stringConstant", "stepConstant", "numberOrLengthConstant",
                "dots", "canBeIdentifier", "commas"
            )
        }

        val ruleNames = makeRuleNames()
        private fun makeLiteralNames(): Array<String?> {
            return arrayOf(
                null, null, "','", "'('", "')'", "'{'", "'}'", "'?'", "'^'", "'+'", "'-'",
                "'*'", "'/'", "'%'", "'$'", "':'", "'.'", "'\\<'", "'\\<='", "'\\>'",
                "'\\>='", "'number'", "'length'", "'string'", "'buffer'"
            )
        }

        private val _LITERAL_NAMES = makeLiteralNames()
        private fun makeSymbolicNames(): Array<String?> {
            return arrayOf(
                null, "StringLiteral", "Comma", "OpenParen", "CloseParen", "OpenCurly",
                "CloseCurly", "Question", "Caret", "Plus", "Minus", "Asterisk", "Slash",
                "Percent", "Dollar", "Colon", "Dot", "BackSlashLesser", "BackSlashLesserEqual",
                "BackSlashGreater", "BackSlashGreaterEqual", "KeywordNumber", "KeywordLength",
                "KeywordString", "KeywordBuffer", "NumberLiteral", "Identifier"
            )
        }

        private val _SYMBOLIC_NAMES = makeSymbolicNames()
        val VOCABULARY: Vocabulary = VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES)

        @Deprecated("Use {@link #VOCABULARY} instead.")
        val tokenNames: Array<String>
        const val _serializedATN =
            "\u0003\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\u0003\u001c\u00c7\u0004\u0002\t\u0002" +
                    "\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b" +
                    "\t\u000b\u0004\u000c\t\u000c\u0004\r\t\r\u0004\u000e\t\u000e\u0004\u000f\t\u000f\u0004\u0010\t\u0010\u0004\u0011\t\u0011\u0004\u0012\t\u0012" +
                    "\u0004\u0013\t\u0013\u0004\u0014\t\u0014\u0004\u0015\t\u0015\u0004\u0016\t\u0016\u0003\u0002\u0003\u0002\u0005\u0002/\n\u0002\u0003\u0003\u0003\u0003\u0003\u0003" +
                    "\u0003\u0003\u0003\u0003\u0007\u0003\u0036\n\u0003\u000c\u0003\u000e\u00039\u000b\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0005\u0004?\n\u0004\u0003\u0005\u0003\u0005\u0003" +
                    "\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0005\u0005H\n\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0007\u0006Q\n\u0006\u000c\u0006\u000e" +
                    "\u0006T\u000b\u0006\u0003\u0007\u0003\u0007\u0003\b\u0003\b\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\ta\n\t\u0003\n\u0003\n" +
                    "\u0003\n\u0003\n\u0003\n\u0005\nh\n\n\u0003\u000b\u0003\u000b\u0003\u000c\u0003\u000c\u0003\u000c\u0003\u000c\u0003\u000c\u0003\u000c\u0003\u000c\u0003\u000c\u0003\u000c\u0003" +
                    "\u000c\u0003\u000c\u0003\u000c\u0007\u000cx\n\u000c\u000c\u000c\u000e\u000c{\u000b\u000c\u0003\r\u0003\r\u0003\r\u0003\r\u0003\r\u0003\r\u0003\r\u0003\r\u0003\r" +
                    "\u0003\r\u0003\r\u0003\r\u0007\r\u0089\n\r\u000c\r\u000e\r\u008c\u000b\r\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e" +
                    "\u0003\u000e\u0003\u000e\u0003\u000e\u0005\u000e\u0096\n\u000e\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f\u0005\u000f\u009d\n" +
                    "\u000f\u0003\u0010\u0003\u0010\u0003\u0010\u0003\u0011\u0003\u0011\u0003\u0012\u0003\u0012\u0003\u0012\u0003\u0012\u0003\u0012\u0005\u0012\u00a9\n\u0012" +
                    "\u0003\u0013\u0003\u0013\u0003\u0013\u0003\u0013\u0005\u0013\u00af\n\u0013\u0003\u0014\u0003\u0014\u0003\u0014\u0003\u0014\u0003\u0014\u0007\u0014\u00b6" +
                    "\n\u0014\u000c\u0014\u000e\u0014\u00b9\u000b\u0014\u0003\u0015\u0003\u0015\u0003\u0016\u0003\u0016\u0003\u0016\u0003\u0016\u0003\u0016\u0007\u0016\u00c2" +
                    "\n\u0016\u000c\u0016\u000e\u0016\u00c5\u000b\u0016\u0003\u0016\u0002\b\u0004\n\u0016\u0018&*\u0017\u0002\u0004\u0006\b\n\u000c\u000e\u0010" +
                    "\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*\u0002\u0004\u0003\u0002\u0013\u0016\u0005\u0002\u000e\u000e\u0011\u0011\u001c\u001c\u0002\u00ca" +
                    "\u0002.\u0003\u0002\u0002\u0002\u0004\u0030\u0003\u0002\u0002\u0002\u0006>\u0003\u0002\u0002\u0002\bG\u0003\u0002\u0002\u0002\nI\u0003\u0002\u0002\u0002\u000cU\u0003\u0002\u0002\u0002\u000e" +
                    "W\u0003\u0002\u0002\u0002\u0010`\u0003\u0002\u0002\u0002\u0012g\u0003\u0002\u0002\u0002\u0014i\u0003\u0002\u0002\u0002\u0016k\u0003\u0002\u0002\u0002\u0018|\u0003\u0002\u0002\u0002" +
                    "\u001a\u0095\u0003\u0002\u0002\u0002\u001c\u009c\u0003\u0002\u0002\u0002\u001e\u009e\u0003\u0002\u0002\u0002 \u00a1\u0003\u0002\u0002\u0002\"\u00a8" +
                    "\u0003\u0002\u0002\u0002$\u00ae\u0003\u0002\u0002\u0002&\u00b0\u0003\u0002\u0002\u0002(\u00ba\u0003\u0002\u0002\u0002*\u00bc\u0003\u0002\u0002\u0002," +
                    "/\u0005\u0004\u0003\u0002-/\u0005\u000e\b\u0002.,\u0003\u0002\u0002\u0002.-\u0003\u0002\u0002\u0002/\u0003\u0003\u0002\u0002\u0002\u0030\u0031\b\u0003\u0001\u0002\u0031\u0032" +
                    "\u0005\u0006\u0004\u0002\u0032\u0037\u0003\u0002\u0002\u0002\u0033\u0034\u000c\u0003\u0002\u0002\u0034\u0036\u0005\u0006\u0004\u0002\u0035\u0033\u0003\u0002\u0002\u0002\u00369\u0003" +
                    "\u0002\u0002\u0002\u0037\u0035\u0003\u0002\u0002\u0002\u00378\u0003\u0002\u0002\u00028\u0005\u0003\u0002\u0002\u00029\u0037\u0003\u0002\u0002\u0002:?\u0005(\u0015\u0002;<\u0005" +
                    "(\u0015\u0002<=\u0005\b\u0005\u0002=?\u0003\u0002\u0002\u0002>:\u0003\u0002\u0002\u0002>;\u0003\u0002\u0002\u0002?\u0007\u0003\u0002\u0002\u0002@H\u0005\n\u0006\u0002AB" +
                    "\u0007\u0007\u0002\u0002BH\u0007\b\u0002\u0002CD\u0007\u0007\u0002\u0002DE\u0005\n\u0006\u0002EF\u0007\b\u0002\u0002FH\u0003\u0002\u0002\u0002G@\u0003\u0002\u0002\u0002G" +
                    "A\u0003\u0002\u0002\u0002GC\u0003\u0002\u0002\u0002H\t\u0003\u0002\u0002\u0002IJ\b\u0006\u0001\u0002JK\u0005\u000c\u0007\u0002KR\u0003\u0002\u0002\u0002LM\u000c\u0003\u0002\u0002" +
                    "MN\u0005*\u0016\u0002NO\u0005\u000c\u0007\u0002OQ\u0003\u0002\u0002\u0002PL\u0003\u0002\u0002\u0002QT\u0003\u0002\u0002\u0002RP\u0003\u0002\u0002\u0002RS\u0003\u0002\u0002\u0002" +
                    "S\u000b\u0003\u0002\u0002\u0002TR\u0003\u0002\u0002\u0002UV\u0005\u000e\b\u0002V\r\u0003\u0002\u0002\u0002WX\u0005\u0010\t\u0002X\u000f\u0003\u0002\u0002\u0002Ya" +
                    "\u0005\u0012\n\u0002Z[\u0005\u0012\n\u0002[\\\u0007\t\u0002\u0002\\]\u0005\u0010\t\u0002]^\u0007\u0004\u0002\u0002^_\u0005\u0010\t\u0002_a\u0003\u0002" +
                    "\u0002\u0002`Y\u0003\u0002\u0002\u0002`Z\u0003\u0002\u0002\u0002a\u0011\u0003\u0002\u0002\u0002bh\u0005\u0016\u000c\u0002cd\u0005\u0016\u000c\u0002de\u0005\u0014\u000b\u0002" +
                    "ef\u0005\u0012\n\u0002fh\u0003\u0002\u0002\u0002gb\u0003\u0002\u0002\u0002gc\u0003\u0002\u0002\u0002h\u0013\u0003\u0002\u0002\u0002ij\t\u0002\u0002\u0002j\u0015\u0003" +
                    "\u0002\u0002\u0002kl\b\u000c\u0001\u0002lm\u0005\u0018\r\u0002my\u0003\u0002\u0002\u0002no\u000c\u0005\u0002\u0002op\u0007\u000b\u0002\u0002px\u0005\u0018\r\u0002" +
                    "qr\u000c\u0004\u0002\u0002rs\u0007\n\u0002\u0002sx\u0005\u0018\r\u0002tu\u000c\u0003\u0002\u0002uv\u0007\u000c\u0002\u0002vx\u0005\u0018\r\u0002wn\u0003\u0002\u0002" +
                    "\u0002wq\u0003\u0002\u0002\u0002wt\u0003\u0002\u0002\u0002x{\u0003\u0002\u0002\u0002yw\u0003\u0002\u0002\u0002yz\u0003\u0002\u0002\u0002z\u0017\u0003\u0002\u0002\u0002{y\u0003\u0002" +
                    "\u0002\u0002|}\b\r\u0001\u0002}~\u0005\u001a\u000e\u0002~\u008a\u0003\u0002\u0002\u0002\u007f\u0080\u000c\u0005\u0002\u0002\u0080\u0081" +
                    "\u0007\r\u0002\u0002\u0081\u0089\u0005\u001a\u000e\u0002\u0082\u0083\u000c\u0004\u0002\u0002\u0083\u0084\u0007\u000e\u0002" +
                    "\u0002\u0084\u0089\u0005\u001a\u000e\u0002\u0085\u0086\u000c\u0003\u0002\u0002\u0086\u0087\u0007\u000f\u0002\u0002\u0087" +
                    "\u0089\u0005\u001a\u000e\u0002\u0088\u007f\u0003\u0002\u0002\u0002\u0088\u0082\u0003\u0002\u0002\u0002\u0088\u0085\u0003\u0002" +
                    "\u0002\u0002\u0089\u008c\u0003\u0002\u0002\u0002\u008a\u0088\u0003\u0002\u0002\u0002\u008a\u008b\u0003\u0002\u0002\u0002\u008b" +
                    "\u0019\u0003\u0002\u0002\u0002\u008c\u008a\u0003\u0002\u0002\u0002\u008d\u0096\u0005\u001e\u0010\u0002\u008e\u0096\u0005 \u0011" +
                    "\u0002\u008f\u0090\u0007\u0007\u0002\u0002\u0090\u0091\u0005\u000e\b\u0002\u0091\u0092\u0007\b\u0002\u0002\u0092" +
                    "\u0096\u0003\u0002\u0002\u0002\u0093\u0096\u0005\"\u0012\u0002\u0094\u0096\u0005\u001c\u000f\u0002\u0095\u008d" +
                    "\u0003\u0002\u0002\u0002\u0095\u008e\u0003\u0002\u0002\u0002\u0095\u008f\u0003\u0002\u0002\u0002\u0095\u0093\u0003\u0002\u0002\u0002\u0095" +
                    "\u0094\u0003\u0002\u0002\u0002\u0096\u001b\u0003\u0002\u0002\u0002\u0097\u009d\u0005$\u0013\u0002\u0098\u0099\u0007\u000c\u0002" +
                    "\u0002\u0099\u009d\u0005$\u0013\u0002\u009a\u009b\u0007\n\u0002\u0002\u009b\u009d\u0005$\u0013\u0002\u009c\u0097" +
                    "\u0003\u0002\u0002\u0002\u009c\u0098\u0003\u0002\u0002\u0002\u009c\u009a\u0003\u0002\u0002\u0002\u009d\u001d\u0003\u0002\u0002\u0002\u009e" +
                    "\u009f\u0007\u0010\u0002\u0002\u009f\u00a0\u0005(\u0015\u0002\u00a0\u001f\u0003\u0002\u0002\u0002\u00a1\u00a2\u0007\u0003\u0002" +
                    "\u0002\u00a2!\u0003\u0002\u0002\u0002\u00a3\u00a4\u0007\u000f\u0002\u0002\u00a4\u00a9\u0007\u001b\u0002\u0002\u00a5\u00a6" +
                    "\u0007\u000f\u0002\u0002\u00a6\u00a7\u0007\u000c\u0002\u0002\u00a7\u00a9\u0007\u001b\u0002\u0002\u00a8\u00a3\u0003\u0002\u0002\u0002" +
                    "\u00a8\u00a5\u0003\u0002\u0002\u0002\u00a9#\u0003\u0002\u0002\u0002\u00aa\u00af\u0007\u001b\u0002\u0002\u00ab\u00ac\u0007" +
                    "\u001b\u0002\u0002\u00ac\u00af\u0005&\u0014\u0002\u00ad\u00af\u0005&\u0014\u0002\u00ae\u00aa\u0003\u0002\u0002\u0002\u00ae" +
                    "\u00ab\u0003\u0002\u0002\u0002\u00ae\u00ad\u0003\u0002\u0002\u0002\u00af%\u0003\u0002\u0002\u0002\u00b0\u00b1\b\u0014\u0001\u0002" +
                    "\u00b1\u00b2\u0007\u0012\u0002\u0002\u00b2\u00b7\u0003\u0002\u0002\u0002\u00b3\u00b4\u000c\u0003\u0002\u0002\u00b4\u00b6" +
                    "\u0007\u0012\u0002\u0002\u00b5\u00b3\u0003\u0002\u0002\u0002\u00b6\u00b9\u0003\u0002\u0002\u0002\u00b7\u00b5\u0003\u0002\u0002\u0002" +
                    "\u00b7\u00b8\u0003\u0002\u0002\u0002\u00b8\'\u0003\u0002\u0002\u0002\u00b9\u00b7\u0003\u0002\u0002\u0002\u00ba\u00bb\t" +
                    "\u0003\u0002\u0002\u00bb)\u0003\u0002\u0002\u0002\u00bc\u00bd\b\u0016\u0001\u0002\u00bd\u00be\u0007\u0004\u0002\u0002\u00be\u00c3" +
                    "\u0003\u0002\u0002\u0002\u00bf\u00c0\u000c\u0003\u0002\u0002\u00c0\u00c2\u0007\u0004\u0002\u0002\u00c1\u00bf\u0003\u0002\u0002\u0002\u00c2" +
                    "\u00c5\u0003\u0002\u0002\u0002\u00c3\u00c1\u0003\u0002\u0002\u0002\u00c3\u00c4\u0003\u0002\u0002\u0002\u00c4+\u0003\u0002\u0002\u0002" +
                    "\u00c5\u00c3\u0003\u0002\u0002\u0002\u0013.\u0037>GR`gwy\u0088\u008a\u0095\u009c\u00a8\u00ae" +
                    "\u00b7\u00c3"
        val _ATN = ATNDeserializer().deserialize(_serializedATN.toCharArray())

        init {
            RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION)
        }

        init {
            tokenNames = Array(_SYMBOLIC_NAMES.size) { _ -> "" }
            /* This part causes NullPointerException, and is used only by unused deprecated members anyways.
            for (i in tokenNames.indices) {
                tokenNames[i] = VOCABULARY.getLiteralName(i)
                if (tokenNames[i] == null) {
                    tokenNames[i] = VOCABULARY.getSymbolicName(i)
                }
                if (tokenNames[i] == null) {
                    tokenNames[i] = "<INVALID>"
                }
            }
            */
        }

        init {
            _decisionToDFA = arrayOfNulls(_ATN.numberOfDecisions)
            for (i in 0 until _ATN.numberOfDecisions) {
                _decisionToDFA[i] = DFA(_ATN.getDecisionState(i), i)
            }
        }
    }

    @Deprecated("")
    override fun getTokenNames(): Array<String> {
        return Companion.tokenNames
    }

    override fun getVocabulary(): Vocabulary {
        return VOCABULARY
    }

    override fun getGrammarFileName(): String {
        return "MugeneParser.g4"
    }

    override fun getRuleNames(): Array<String> {
        return Companion.ruleNames
    }

    override fun getSerializedATN(): String {
        return _serializedATN
    }

    override fun getATN(): ATN {
        return _ATN
    }

    class ExpressionOrOperationUsesContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun operationUses(): OperationUsesContext {
            return getRuleContext(OperationUsesContext::class.java, 0)
        }

        fun expression(): ExpressionContext {
            return getRuleContext(ExpressionContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_expressionOrOperationUses
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitExpressionOrOperationUses(
                this
            ) else visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun expressionOrOperationUses(): ExpressionOrOperationUsesContext {
        val _localctx = ExpressionOrOperationUsesContext(_ctx, state)
        enterRule(_localctx, 0, RULE_expressionOrOperationUses)
        try {
            state = 44
            _errHandler.sync(this)
            when (_input.LA(1)) {
                Slash, Colon, Identifier -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 42
                        operationUses(0)
                    }
                }
                StringLiteral, OpenCurly, Caret, Minus, Percent, Dollar, Dot, NumberLiteral -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 43
                        expression()
                    }
                }
                else -> throw NoViableAltException(this)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class OperationUsesContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun operationUse(): OperationUseContext {
            return getRuleContext(OperationUseContext::class.java, 0)
        }

        fun operationUses(): OperationUsesContext {
            return getRuleContext(OperationUsesContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_operationUses
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitOperationUses(
                this
            ) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun operationUses(): OperationUsesContext {
        return operationUses(0)
    }

    @Throws(RecognitionException::class)
    private fun operationUses(_p: Int): OperationUsesContext {
        val _parentctx = _ctx
        val _parentState = state
        var _localctx = OperationUsesContext(_ctx, _parentState)
        var _prevctx = _localctx
        val _startState = 2
        enterRecursionRule(_localctx, 2, RULE_operationUses, _p)
        try {
            var _alt: Int
            enterOuterAlt(_localctx, 1)
            run {
                run {
                    state = 47
                    operationUse()
                }
                _ctx.stop = _input.LT(-1)
                state = 53
                _errHandler.sync(this)
                _alt = interpreter.adaptivePredict(_input, 1, _ctx)
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent()
                        _prevctx = _localctx
                        run {
                            run {
                                _localctx = OperationUsesContext(_parentctx, _parentState)
                                pushNewRecursionContext(
                                    _localctx,
                                    _startState,
                                    MugeneParser.Companion.RULE_operationUses
                                )
                                setState(49)
                                if (!precpred(_ctx, 1)) throw FailedPredicateException(this, "precpred(_ctx, 1)")
                                setState(50)
                                operationUse()
                            }
                        }
                    }
                    state = 55
                    _errHandler.sync(this)
                    _alt = interpreter.adaptivePredict(_input, 1, _ctx)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            unrollRecursionContexts(_parentctx)
        }
        return _localctx
    }

    class OperationUseContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun canBeIdentifier(): CanBeIdentifierContext {
            return getRuleContext(CanBeIdentifierContext::class.java, 0)
        }

        fun argumentsOptCurly(): ArgumentsOptCurlyContext {
            return getRuleContext(ArgumentsOptCurlyContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_operationUse
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitOperationUse(this) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun operationUse(): OperationUseContext {
        val _localctx = OperationUseContext(_ctx, state)
        enterRule(_localctx, 4, RULE_operationUse)
        try {
            state = 60
            _errHandler.sync(this)
            when (interpreter.adaptivePredict(_input, 2, _ctx)) {
                1 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 56
                        canBeIdentifier()
                    }
                }
                2 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 57
                        canBeIdentifier()
                        state = 58
                        argumentsOptCurly()
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ArgumentsOptCurlyContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun arguments(): ArgumentsContext {
            return getRuleContext(ArgumentsContext::class.java, 0)
        }

        fun OpenCurly(): TerminalNode {
            return getToken(OpenCurly, 0)
        }

        fun CloseCurly(): TerminalNode {
            return getToken(CloseCurly, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_argumentsOptCurly
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitArgumentsOptCurly(
                this
            ) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun argumentsOptCurly(): ArgumentsOptCurlyContext {
        val _localctx = ArgumentsOptCurlyContext(_ctx, state)
        enterRule(_localctx, 6, RULE_argumentsOptCurly)
        try {
            state = 69
            _errHandler.sync(this)
            when (interpreter.adaptivePredict(_input, 3, _ctx)) {
                1 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 62
                        arguments(0)
                    }
                }
                2 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 63
                        match(OpenCurly)
                        state = 64
                        match(CloseCurly)
                    }
                }
                3 -> {
                    enterOuterAlt(_localctx, 3)
                    run {
                        state = 65
                        match(OpenCurly)
                        state = 66
                        arguments(0)
                        state = 67
                        match(CloseCurly)
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ArgumentsContext(parent: ParserRuleContext?, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun argument(): ArgumentContext {
            return getRuleContext(ArgumentContext::class.java, 0)
        }

        fun arguments(): ArgumentsContext {
            return getRuleContext(ArgumentsContext::class.java, 0)
        }

        fun commas(): CommasContext {
            return getRuleContext(CommasContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_arguments
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitArguments(this) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun arguments(): ArgumentsContext {
        return arguments(0)
    }

    @Throws(RecognitionException::class)
    private fun arguments(_p: Int): ArgumentsContext {
        val _parentctx = _ctx
        val _parentState = state
        var _localctx = ArgumentsContext(_ctx, _parentState)
        var _prevctx = _localctx
        val _startState = 8
        enterRecursionRule(_localctx, 8, RULE_arguments, _p)
        try {
            var _alt: Int
            enterOuterAlt(_localctx, 1)
            run {
                run {
                    state = 72
                    argument()
                }
                _ctx.stop = _input.LT(-1)
                state = 80
                _errHandler.sync(this)
                _alt = interpreter.adaptivePredict(_input, 4, _ctx)
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent()
                        _prevctx = _localctx
                        run {
                            run {
                                _localctx = ArgumentsContext(_parentctx, _parentState)
                                pushNewRecursionContext(_localctx, _startState, MugeneParser.Companion.RULE_arguments)
                                setState(74)
                                if (!precpred(_ctx, 1)) throw FailedPredicateException(this, "precpred(_ctx, 1)")
                                setState(75)
                                commas(0)
                                setState(76)
                                argument()
                            }
                        }
                    }
                    state = 82
                    _errHandler.sync(this)
                    _alt = interpreter.adaptivePredict(_input, 4, _ctx)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            unrollRecursionContexts(_parentctx)
        }
        return _localctx
    }

    class ArgumentContext(parent: ParserRuleContext?, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun expression(): ExpressionContext {
            return getRuleContext(ExpressionContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_argument
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitArgument(this) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun argument(): ArgumentContext {
        val _localctx = ArgumentContext(_ctx, state)
        enterRule(_localctx, 10, RULE_argument)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 83
                expression()
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ExpressionContext(parent: ParserRuleContext?, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun conditionalExpr(): ConditionalExprContext {
            return getRuleContext(ConditionalExprContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_expression
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitExpression(this) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun expression(): ExpressionContext {
        val _localctx = ExpressionContext(_ctx, state)
        enterRule(_localctx, 12, RULE_expression)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 85
                conditionalExpr()
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ConditionalExprContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun comparisonExpr(): ComparisonExprContext {
            return getRuleContext(ComparisonExprContext::class.java, 0)
        }

        fun Question(): TerminalNode {
            return getToken(Question, 0)
        }

        fun conditionalExpr(): List<ConditionalExprContext> {
            return getRuleContexts(ConditionalExprContext::class.java)
        }

        fun conditionalExpr(i: Int): ConditionalExprContext {
            return getRuleContext(ConditionalExprContext::class.java, i)
        }

        fun Comma(): TerminalNode {
            return getToken(Comma, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_conditionalExpr
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitConditionalExpr(
                this
            ) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun conditionalExpr(): ConditionalExprContext {
        val _localctx = ConditionalExprContext(_ctx, state)
        enterRule(_localctx, 14, RULE_conditionalExpr)
        try {
            state = 94
            _errHandler.sync(this)
            when (interpreter.adaptivePredict(_input, 5, _ctx)) {
                1 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 87
                        comparisonExpr()
                    }
                }
                2 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 88
                        comparisonExpr()
                        state = 89
                        match(Question)
                        state = 90
                        conditionalExpr()
                        state = 91
                        match(Comma)
                        state = 92
                        conditionalExpr()
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ComparisonExprContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun addSubExpr(): AddSubExprContext {
            return getRuleContext(AddSubExprContext::class.java, 0)
        }

        fun comparisonOperator(): ComparisonOperatorContext {
            return getRuleContext(ComparisonOperatorContext::class.java, 0)
        }

        fun comparisonExpr(): ComparisonExprContext {
            return getRuleContext(ComparisonExprContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_comparisonExpr
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitComparisonExpr(
                this
            ) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun comparisonExpr(): ComparisonExprContext {
        val _localctx = ComparisonExprContext(_ctx, state)
        enterRule(_localctx, 16, RULE_comparisonExpr)
        try {
            state = 101
            _errHandler.sync(this)
            when (interpreter.adaptivePredict(_input, 6, _ctx)) {
                1 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 96
                        addSubExpr(0)
                    }
                }
                2 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 97
                        addSubExpr(0)
                        state = 98
                        comparisonOperator()
                        state = 99
                        comparisonExpr()
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ComparisonOperatorContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun BackSlashLesser(): TerminalNode {
            return getToken(BackSlashLesser, 0)
        }

        fun BackSlashLesserEqual(): TerminalNode {
            return getToken(BackSlashLesserEqual, 0)
        }

        fun BackSlashGreater(): TerminalNode {
            return getToken(BackSlashGreater, 0)
        }

        fun BackSlashGreaterEqual(): TerminalNode {
            return getToken(BackSlashGreaterEqual, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_comparisonOperator
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitComparisonOperator(
                this
            ) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun comparisonOperator(): ComparisonOperatorContext {
        val _localctx = ComparisonOperatorContext(_ctx, state)
        enterRule(_localctx, 18, RULE_comparisonOperator)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 103
                _la = _input.LA(1)
                if (!(_la and 0x3f.inv() == 0 && 1L shl _la and (1L shl BackSlashLesser or (1L shl BackSlashLesserEqual) or (1L shl BackSlashGreater) or (1L shl BackSlashGreaterEqual)) != 0L)) {
                    _errHandler.recoverInline(this)
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true
                    _errHandler.reportMatch(this)
                    consume()
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class AddSubExprContext(parent: ParserRuleContext?, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun mulDivModExpr(): MulDivModExprContext {
            return getRuleContext(MulDivModExprContext::class.java, 0)
        }

        fun addSubExpr(): AddSubExprContext {
            return getRuleContext(AddSubExprContext::class.java, 0)
        }

        fun Plus(): TerminalNode {
            return getToken(Plus, 0)
        }

        fun Caret(): TerminalNode {
            return getToken(Caret, 0)
        }

        fun Minus(): TerminalNode {
            return getToken(Minus, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_addSubExpr
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitAddSubExpr(this) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun addSubExpr(): AddSubExprContext {
        return addSubExpr(0)
    }

    @Throws(RecognitionException::class)
    private fun addSubExpr(_p: Int): AddSubExprContext {
        val _parentctx = _ctx
        val _parentState = state
        var _localctx = AddSubExprContext(_ctx, _parentState)
        var _prevctx = _localctx
        val _startState = 20
        enterRecursionRule(_localctx, 20, RULE_addSubExpr, _p)
        try {
            var _alt: Int
            enterOuterAlt(_localctx, 1)
            run {
                run {
                    state = 106
                    mulDivModExpr(0)
                }
                _ctx.stop = _input.LT(-1)
                state = 119
                _errHandler.sync(this)
                _alt = interpreter.adaptivePredict(_input, 8, _ctx)
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent()
                        _prevctx = _localctx
                        run {
                            setState(117)
                            _errHandler.sync(this)
                            when (getInterpreter().adaptivePredict(_input, 7, _ctx)) {
                                1 -> {
                                    _localctx = AddSubExprContext(_parentctx, _parentState)
                                    pushNewRecursionContext(
                                        _localctx,
                                        _startState,
                                        MugeneParser.Companion.RULE_addSubExpr
                                    )
                                    setState(108)
                                    if (!precpred(_ctx, 3)) throw FailedPredicateException(this, "precpred(_ctx, 3)")
                                    setState(109)
                                    match(MugeneParser.Companion.Plus)
                                    setState(110)
                                    mulDivModExpr(0)
                                }
                                2 -> {
                                    _localctx = AddSubExprContext(_parentctx, _parentState)
                                    pushNewRecursionContext(
                                        _localctx,
                                        _startState,
                                        MugeneParser.Companion.RULE_addSubExpr
                                    )
                                    setState(111)
                                    if (!precpred(_ctx, 2)) throw FailedPredicateException(this, "precpred(_ctx, 2)")
                                    setState(112)
                                    match(MugeneParser.Companion.Caret)
                                    setState(113)
                                    mulDivModExpr(0)
                                }
                                3 -> {
                                    _localctx = AddSubExprContext(_parentctx, _parentState)
                                    pushNewRecursionContext(
                                        _localctx,
                                        _startState,
                                        MugeneParser.Companion.RULE_addSubExpr
                                    )
                                    setState(114)
                                    if (!precpred(_ctx, 1)) throw FailedPredicateException(this, "precpred(_ctx, 1)")
                                    setState(115)
                                    match(MugeneParser.Companion.Minus)
                                    setState(116)
                                    mulDivModExpr(0)
                                }
                                else -> throw NotImplementedError()
                            }
                        }
                    }
                    state = 121
                    _errHandler.sync(this)
                    _alt = interpreter.adaptivePredict(_input, 8, _ctx)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            unrollRecursionContexts(_parentctx)
        }
        return _localctx
    }

    class MulDivModExprContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun primaryExpr(): PrimaryExprContext {
            return getRuleContext(PrimaryExprContext::class.java, 0)
        }

        fun mulDivModExpr(): MulDivModExprContext {
            return getRuleContext(MulDivModExprContext::class.java, 0)
        }

        fun Asterisk(): TerminalNode {
            return getToken(Asterisk, 0)
        }

        fun Slash(): TerminalNode {
            return getToken(Slash, 0)
        }

        fun Percent(): TerminalNode {
            return getToken(Percent, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_mulDivModExpr
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitMulDivModExpr(
                this
            ) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun mulDivModExpr(): MulDivModExprContext {
        return mulDivModExpr(0)
    }

    @Throws(RecognitionException::class)
    private fun mulDivModExpr(_p: Int): MulDivModExprContext {
        val _parentctx = _ctx
        val _parentState = state
        var _localctx = MulDivModExprContext(_ctx, _parentState)
        var _prevctx = _localctx
        val _startState = 22
        enterRecursionRule(_localctx, 22, RULE_mulDivModExpr, _p)
        try {
            var _alt: Int
            enterOuterAlt(_localctx, 1)
            run {
                run {
                    state = 123
                    primaryExpr()
                }
                _ctx.stop = _input.LT(-1)
                state = 136
                _errHandler.sync(this)
                _alt = interpreter.adaptivePredict(_input, 10, _ctx)
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent()
                        _prevctx = _localctx
                        run {
                            setState(134)
                            _errHandler.sync(this)
                            when (getInterpreter().adaptivePredict(_input, 9, _ctx)) {
                                1 -> {
                                    _localctx = MulDivModExprContext(_parentctx, _parentState)
                                    pushNewRecursionContext(
                                        _localctx,
                                        _startState,
                                        MugeneParser.Companion.RULE_mulDivModExpr
                                    )
                                    setState(125)
                                    if (!precpred(_ctx, 3)) throw FailedPredicateException(this, "precpred(_ctx, 3)")
                                    setState(126)
                                    match(MugeneParser.Companion.Asterisk)
                                    setState(127)
                                    primaryExpr()
                                }
                                2 -> {
                                    _localctx = MulDivModExprContext(_parentctx, _parentState)
                                    pushNewRecursionContext(
                                        _localctx,
                                        _startState,
                                        MugeneParser.Companion.RULE_mulDivModExpr
                                    )
                                    setState(128)
                                    if (!precpred(_ctx, 2)) throw FailedPredicateException(this, "precpred(_ctx, 2)")
                                    setState(129)
                                    match(MugeneParser.Companion.Slash)
                                    setState(130)
                                    primaryExpr()
                                }
                                3 -> {
                                    _localctx = MulDivModExprContext(_parentctx, _parentState)
                                    pushNewRecursionContext(
                                        _localctx,
                                        _startState,
                                        MugeneParser.Companion.RULE_mulDivModExpr
                                    )
                                    setState(131)
                                    if (!precpred(_ctx, 1)) throw FailedPredicateException(this, "precpred(_ctx, 1)")
                                    setState(132)
                                    match(MugeneParser.Companion.Percent)
                                    setState(133)
                                    primaryExpr()
                                }
                                else -> throw NotImplementedError()
                            }
                        }
                    }
                    state = 138
                    _errHandler.sync(this)
                    _alt = interpreter.adaptivePredict(_input, 10, _ctx)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            unrollRecursionContexts(_parentctx)
        }
        return _localctx
    }

    class PrimaryExprContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun variableReference(): VariableReferenceContext {
            return getRuleContext(VariableReferenceContext::class.java, 0)
        }

        fun stringConstant(): StringConstantContext {
            return getRuleContext(StringConstantContext::class.java, 0)
        }

        fun OpenCurly(): TerminalNode {
            return getToken(OpenCurly, 0)
        }

        fun expression(): ExpressionContext {
            return getRuleContext(ExpressionContext::class.java, 0)
        }

        fun CloseCurly(): TerminalNode {
            return getToken(CloseCurly, 0)
        }

        fun stepConstant(): StepConstantContext {
            return getRuleContext(StepConstantContext::class.java, 0)
        }

        fun unaryExpr(): UnaryExprContext {
            return getRuleContext(UnaryExprContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_primaryExpr
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitPrimaryExpr(this) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun primaryExpr(): PrimaryExprContext {
        val _localctx = PrimaryExprContext(_ctx, state)
        enterRule(_localctx, 24, RULE_primaryExpr)
        try {
            state = 147
            _errHandler.sync(this)
            when (_input.LA(1)) {
                Dollar -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 139
                        variableReference()
                    }
                }
                StringLiteral -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 140
                        stringConstant()
                    }
                }
                OpenCurly -> {
                    enterOuterAlt(_localctx, 3)
                    run {
                        state = 141
                        match(OpenCurly)
                        state = 142
                        expression()
                        state = 143
                        match(CloseCurly)
                    }
                }
                Percent -> {
                    enterOuterAlt(_localctx, 4)
                    run {
                        state = 145
                        stepConstant()
                    }
                }
                Caret, Minus, Dot, NumberLiteral -> {
                    enterOuterAlt(_localctx, 5)
                    run {
                        state = 146
                        unaryExpr()
                    }
                }
                else -> throw NoViableAltException(this)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class UnaryExprContext(parent: ParserRuleContext?, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun numberOrLengthConstant(): NumberOrLengthConstantContext {
            return getRuleContext(NumberOrLengthConstantContext::class.java, 0)
        }

        fun Minus(): TerminalNode {
            return getToken(Minus, 0)
        }

        fun Caret(): TerminalNode {
            return getToken(Caret, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_unaryExpr
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitUnaryExpr(this) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun unaryExpr(): UnaryExprContext {
        val _localctx = UnaryExprContext(_ctx, state)
        enterRule(_localctx, 26, RULE_unaryExpr)
        try {
            state = 154
            _errHandler.sync(this)
            when (_input.LA(1)) {
                Dot, NumberLiteral -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 149
                        numberOrLengthConstant()
                    }
                }
                Minus -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 150
                        match(Minus)
                        state = 151
                        numberOrLengthConstant()
                    }
                }
                Caret -> {
                    enterOuterAlt(_localctx, 3)
                    run {
                        state = 152
                        match(Caret)
                        state = 153
                        numberOrLengthConstant()
                    }
                }
                else -> throw NoViableAltException(this)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class VariableReferenceContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun Dollar(): TerminalNode {
            return getToken(Dollar, 0)
        }

        fun canBeIdentifier(): CanBeIdentifierContext {
            return getRuleContext(CanBeIdentifierContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_variableReference
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitVariableReference(
                this
            ) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun variableReference(): VariableReferenceContext {
        val _localctx = VariableReferenceContext(_ctx, state)
        enterRule(_localctx, 28, RULE_variableReference)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 156
                match(Dollar)
                state = 157
                canBeIdentifier()
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class StringConstantContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun StringLiteral(): TerminalNode {
            return getToken(StringLiteral, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_stringConstant
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitStringConstant(
                this
            ) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun stringConstant(): StringConstantContext {
        val _localctx = StringConstantContext(_ctx, state)
        enterRule(_localctx, 30, RULE_stringConstant)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 159
                match(StringLiteral)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class StepConstantContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun Percent(): TerminalNode {
            return getToken(Percent, 0)
        }

        fun NumberLiteral(): TerminalNode {
            return getToken(NumberLiteral, 0)
        }

        fun Minus(): TerminalNode {
            return getToken(Minus, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_stepConstant
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitStepConstant(this) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun stepConstant(): StepConstantContext {
        val _localctx = StepConstantContext(_ctx, state)
        enterRule(_localctx, 32, RULE_stepConstant)
        try {
            state = 166
            _errHandler.sync(this)
            when (interpreter.adaptivePredict(_input, 13, _ctx)) {
                1 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 161
                        match(Percent)
                        state = 162
                        match(NumberLiteral)
                    }
                }
                2 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 163
                        match(Percent)
                        state = 164
                        match(Minus)
                        state = 165
                        match(NumberLiteral)
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class NumberOrLengthConstantContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun NumberLiteral(): TerminalNode {
            return getToken(NumberLiteral, 0)
        }

        fun dots(): DotsContext {
            return getRuleContext(DotsContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_numberOrLengthConstant
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitNumberOrLengthConstant(
                this
            ) else visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun numberOrLengthConstant(): NumberOrLengthConstantContext {
        val _localctx = NumberOrLengthConstantContext(_ctx, state)
        enterRule(_localctx, 34, RULE_numberOrLengthConstant)
        try {
            state = 172
            _errHandler.sync(this)
            when (interpreter.adaptivePredict(_input, 14, _ctx)) {
                1 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 168
                        match(NumberLiteral)
                    }
                }
                2 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 169
                        match(NumberLiteral)
                        state = 170
                        dots(0)
                    }
                }
                3 -> {
                    enterOuterAlt(_localctx, 3)
                    run {
                        state = 171
                        dots(0)
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class DotsContext(parent: ParserRuleContext?, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun Dot(): TerminalNode {
            return getToken(Dot, 0)
        }

        fun dots(): DotsContext {
            return getRuleContext(DotsContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_dots
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitDots(this) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun dots(): DotsContext {
        return dots(0)
    }

    @Throws(RecognitionException::class)
    private fun dots(_p: Int): DotsContext {
        val _parentctx = _ctx
        val _parentState = state
        var _localctx = DotsContext(_ctx, _parentState)
        var _prevctx = _localctx
        val _startState = 36
        enterRecursionRule(_localctx, 36, RULE_dots, _p)
        try {
            var _alt: Int
            enterOuterAlt(_localctx, 1)
            run {
                run {
                    state = 175
                    match(Dot)
                }
                _ctx.stop = _input.LT(-1)
                state = 181
                _errHandler.sync(this)
                _alt = interpreter.adaptivePredict(_input, 15, _ctx)
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent()
                        _prevctx = _localctx
                        run {
                            run {
                                _localctx = DotsContext(_parentctx, _parentState)
                                pushNewRecursionContext(_localctx, _startState, MugeneParser.Companion.RULE_dots)
                                setState(177)
                                if (!precpred(_ctx, 1)) throw FailedPredicateException(this, "precpred(_ctx, 1)")
                                setState(178)
                                match(MugeneParser.Companion.Dot)
                            }
                        }
                    }
                    state = 183
                    _errHandler.sync(this)
                    _alt = interpreter.adaptivePredict(_input, 15, _ctx)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            unrollRecursionContexts(_parentctx)
        }
        return _localctx
    }

    class CanBeIdentifierContext(parent: ParserRuleContext?, invokingState: Int) :
        ParserRuleContext(parent, invokingState) {
        fun Identifier(): TerminalNode {
            return getToken(Identifier, 0)
        }

        fun Colon(): TerminalNode {
            return getToken(Colon, 0)
        }

        fun Slash(): TerminalNode {
            return getToken(Slash, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_canBeIdentifier
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitCanBeIdentifier(
                this
            ) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun canBeIdentifier(): CanBeIdentifierContext {
        val _localctx = CanBeIdentifierContext(_ctx, state)
        enterRule(_localctx, 38, RULE_canBeIdentifier)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 184
                _la = _input.LA(1)
                if (!(_la and 0x3f.inv() == 0 && 1L shl _la and (1L shl Slash or (1L shl Colon) or (1L shl Identifier)) != 0L)) {
                    _errHandler.recoverInline(this)
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true
                    _errHandler.reportMatch(this)
                    consume()
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class CommasContext(parent: ParserRuleContext?, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun Comma(): TerminalNode {
            return getToken(Comma, 0)
        }

        fun commas(): CommasContext {
            return getRuleContext(CommasContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_commas
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is MugeneParserVisitor<*>) (visitor as MugeneParserVisitor<out T>).visitCommas(this) else visitor.visitChildren(
                this
            )
        }
    }

    @Throws(RecognitionException::class)
    fun commas(): CommasContext {
        return commas(0)
    }

    @Throws(RecognitionException::class)
    private fun commas(_p: Int): CommasContext {
        val _parentctx = _ctx
        val _parentState = state
        var _localctx = CommasContext(_ctx, _parentState)
        var _prevctx = _localctx
        val _startState = 40
        enterRecursionRule(_localctx, 40, RULE_commas, _p)
        try {
            var _alt: Int
            enterOuterAlt(_localctx, 1)
            run {
                run {
                    state = 187
                    match(Comma)
                }
                _ctx.stop = _input.LT(-1)
                state = 193
                _errHandler.sync(this)
                _alt = interpreter.adaptivePredict(_input, 16, _ctx)
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent()
                        _prevctx = _localctx
                        run {
                            run {
                                _localctx = CommasContext(_parentctx, _parentState)
                                pushNewRecursionContext(_localctx, _startState, MugeneParser.Companion.RULE_commas)
                                setState(189)
                                if (!precpred(_ctx, 1)) throw FailedPredicateException(this, "precpred(_ctx, 1)")
                                setState(190)
                                match(MugeneParser.Companion.Comma)
                            }
                        }
                    }
                    state = 195
                    _errHandler.sync(this)
                    _alt = interpreter.adaptivePredict(_input, 16, _ctx)
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            unrollRecursionContexts(_parentctx)
        }
        return _localctx
    }

    override fun sempred(_localctx: RuleContext, ruleIndex: Int, predIndex: Int): Boolean {
        when (ruleIndex) {
            1 -> return operationUses_sempred(_localctx as OperationUsesContext, predIndex)
            4 -> return arguments_sempred(_localctx as ArgumentsContext, predIndex)
            10 -> return addSubExpr_sempred(_localctx as AddSubExprContext, predIndex)
            11 -> return mulDivModExpr_sempred(_localctx as MulDivModExprContext, predIndex)
            18 -> return dots_sempred(_localctx as DotsContext, predIndex)
            20 -> return commas_sempred(_localctx as CommasContext, predIndex)
        }
        return true
    }

    private fun operationUses_sempred(_localctx: OperationUsesContext, predIndex: Int): Boolean {
        when (predIndex) {
            0 -> return precpred(_ctx, 1)
        }
        return true
    }

    private fun arguments_sempred(_localctx: ArgumentsContext, predIndex: Int): Boolean {
        when (predIndex) {
            1 -> return precpred(_ctx, 1)
        }
        return true
    }

    private fun addSubExpr_sempred(_localctx: AddSubExprContext, predIndex: Int): Boolean {
        when (predIndex) {
            2 -> return precpred(_ctx, 3)
            3 -> return precpred(_ctx, 2)
            4 -> return precpred(_ctx, 1)
        }
        return true
    }

    private fun mulDivModExpr_sempred(_localctx: MulDivModExprContext, predIndex: Int): Boolean {
        when (predIndex) {
            5 -> return precpred(_ctx, 3)
            6 -> return precpred(_ctx, 2)
            7 -> return precpred(_ctx, 1)
        }
        return true
    }

    private fun dots_sempred(_localctx: DotsContext, predIndex: Int): Boolean {
        when (predIndex) {
            8 -> return precpred(_ctx, 1)
        }
        return true
    }

    private fun commas_sempred(_localctx: CommasContext, predIndex: Int): Boolean {
        when (predIndex) {
            9 -> return precpred(_ctx, 1)
        }
        return true
    }

    init {
        _interp = ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache)
    }
}