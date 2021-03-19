// Generated from src/antlr/MugeneLexer.g4 by ANTLR 4.7.2
package dev.atsushieno.mugene.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MugeneLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		StringLiteral=1, Comma=2, OpenParen=3, CloseParen=4, OpenCurly=5, CloseCurly=6, 
		Question=7, Caret=8, Plus=9, Minus=10, Asterisk=11, Slash=12, Percent=13, 
		Dollar=14, Colon=15, Dot=16, BackSlashLesser=17, BackSlashLesserEqual=18, 
		BackSlashGreater=19, BackSlashGreaterEqual=20, KeywordNumber=21, KeywordLength=22, 
		KeywordString=23, KeywordBuffer=24, NumberLiteral=25, Identifier=26;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"StringLiteral", "Comma", "OpenParen", "CloseParen", "OpenCurly", "CloseCurly", 
			"Question", "Caret", "Plus", "Minus", "Asterisk", "Slash", "Percent", 
			"Dollar", "Colon", "Dot", "BackSlashLesser", "BackSlashLesserEqual", 
			"BackSlashGreater", "BackSlashGreaterEqual", "KeywordNumber", "KeywordLength", 
			"KeywordString", "KeywordBuffer", "NumberLiteral", "Identifier"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "','", "'('", "')'", "'{'", "'}'", "'?'", "'^'", "'+'", "'-'", 
			"'*'", "'/'", "'%'", "'$'", "':'", "'.'", "'\\<'", "'\\<='", "'\\>'", 
			"'\\>='", "'number'", "'length'", "'string'", "'buffer'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "StringLiteral", "Comma", "OpenParen", "CloseParen", "OpenCurly", 
			"CloseCurly", "Question", "Caret", "Plus", "Minus", "Asterisk", "Slash", 
			"Percent", "Dollar", "Colon", "Dot", "BackSlashLesser", "BackSlashLesserEqual", 
			"BackSlashGreater", "BackSlashGreaterEqual", "KeywordNumber", "KeywordLength", 
			"KeywordString", "KeywordBuffer", "NumberLiteral", "Identifier"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public MugeneLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MugeneLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\34\u008f\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6"+
		"\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3"+
		"\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3"+
		"\24\3\24\3\24\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3"+
		"\27\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\32\6\32\u0085\n\32\r\32\16\32\u0086"+
		"\3\33\3\33\7\33\u008b\n\33\f\33\16\33\u008e\13\33\2\2\34\3\3\5\4\7\5\t"+
		"\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\3\2\6\4\2,,\60\60\3\2\62;"+
		"\5\2C\\aac|\6\2\62;C\\aac|\2\u0090\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2"+
		"\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3"+
		"\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2"+
		"\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2"+
		"\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2"+
		"\2\2\3\67\3\2\2\2\5;\3\2\2\2\7=\3\2\2\2\t?\3\2\2\2\13A\3\2\2\2\rC\3\2"+
		"\2\2\17E\3\2\2\2\21G\3\2\2\2\23I\3\2\2\2\25K\3\2\2\2\27M\3\2\2\2\31O\3"+
		"\2\2\2\33Q\3\2\2\2\35S\3\2\2\2\37U\3\2\2\2!W\3\2\2\2#Y\3\2\2\2%\\\3\2"+
		"\2\2\'`\3\2\2\2)c\3\2\2\2+g\3\2\2\2-n\3\2\2\2/u\3\2\2\2\61|\3\2\2\2\63"+
		"\u0084\3\2\2\2\65\u0088\3\2\2\2\678\7$\2\289\t\2\2\29:\7$\2\2:\4\3\2\2"+
		"\2;<\7.\2\2<\6\3\2\2\2=>\7*\2\2>\b\3\2\2\2?@\7+\2\2@\n\3\2\2\2AB\7}\2"+
		"\2B\f\3\2\2\2CD\7\177\2\2D\16\3\2\2\2EF\7A\2\2F\20\3\2\2\2GH\7`\2\2H\22"+
		"\3\2\2\2IJ\7-\2\2J\24\3\2\2\2KL\7/\2\2L\26\3\2\2\2MN\7,\2\2N\30\3\2\2"+
		"\2OP\7\61\2\2P\32\3\2\2\2QR\7\'\2\2R\34\3\2\2\2ST\7&\2\2T\36\3\2\2\2U"+
		"V\7<\2\2V \3\2\2\2WX\7\60\2\2X\"\3\2\2\2YZ\7^\2\2Z[\7>\2\2[$\3\2\2\2\\"+
		"]\7^\2\2]^\7>\2\2^_\7?\2\2_&\3\2\2\2`a\7^\2\2ab\7@\2\2b(\3\2\2\2cd\7^"+
		"\2\2de\7@\2\2ef\7?\2\2f*\3\2\2\2gh\7p\2\2hi\7w\2\2ij\7o\2\2jk\7d\2\2k"+
		"l\7g\2\2lm\7t\2\2m,\3\2\2\2no\7n\2\2op\7g\2\2pq\7p\2\2qr\7i\2\2rs\7v\2"+
		"\2st\7j\2\2t.\3\2\2\2uv\7u\2\2vw\7v\2\2wx\7t\2\2xy\7k\2\2yz\7p\2\2z{\7"+
		"i\2\2{\60\3\2\2\2|}\7d\2\2}~\7w\2\2~\177\7h\2\2\177\u0080\7h\2\2\u0080"+
		"\u0081\7g\2\2\u0081\u0082\7t\2\2\u0082\62\3\2\2\2\u0083\u0085\t\3\2\2"+
		"\u0084\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0084\3\2\2\2\u0086\u0087"+
		"\3\2\2\2\u0087\64\3\2\2\2\u0088\u008c\t\4\2\2\u0089\u008b\t\5\2\2\u008a"+
		"\u0089\3\2\2\2\u008b\u008e\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2"+
		"\2\2\u008d\66\3\2\2\2\u008e\u008c\3\2\2\2\5\2\u0086\u008c\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}