// Generated from src/antlr/MugeneParser.g4 by ANTLR 4.7.2
package dev.atsushieno.mugene.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MugeneParser extends Parser {
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
	public static final int
		RULE_expressionOrOperationUses = 0, RULE_operationUses = 1, RULE_operationUse = 2, 
		RULE_argumentsOptCurly = 3, RULE_arguments = 4, RULE_argument = 5, RULE_expression = 6, 
		RULE_conditionalExpr = 7, RULE_comparisonExpr = 8, RULE_comparisonOperator = 9, 
		RULE_addSubExpr = 10, RULE_mulDivModExpr = 11, RULE_primaryExpr = 12, 
		RULE_unaryExpr = 13, RULE_variableReference = 14, RULE_stringConstant = 15, 
		RULE_stepConstant = 16, RULE_numberOrLengthConstant = 17, RULE_dots = 18, 
		RULE_canBeIdentifier = 19, RULE_commas = 20;
	private static String[] makeRuleNames() {
		return new String[] {
			"expressionOrOperationUses", "operationUses", "operationUse", "argumentsOptCurly", 
			"arguments", "argument", "expression", "conditionalExpr", "comparisonExpr", 
			"comparisonOperator", "addSubExpr", "mulDivModExpr", "primaryExpr", "unaryExpr", 
			"variableReference", "stringConstant", "stepConstant", "numberOrLengthConstant", 
			"dots", "canBeIdentifier", "commas"
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

	@Override
	public String getGrammarFileName() { return "MugeneParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MugeneParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ExpressionOrOperationUsesContext extends ParserRuleContext {
		public OperationUsesContext operationUses() {
			return getRuleContext(OperationUsesContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpressionOrOperationUsesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionOrOperationUses; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitExpressionOrOperationUses(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionOrOperationUsesContext expressionOrOperationUses() throws RecognitionException {
		ExpressionOrOperationUsesContext _localctx = new ExpressionOrOperationUsesContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expressionOrOperationUses);
		try {
			setState(44);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Slash:
			case Colon:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(42);
				operationUses(0);
				}
				break;
			case StringLiteral:
			case OpenCurly:
			case Caret:
			case Minus:
			case Percent:
			case Dollar:
			case Dot:
			case NumberLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(43);
				expression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OperationUsesContext extends ParserRuleContext {
		public OperationUseContext operationUse() {
			return getRuleContext(OperationUseContext.class,0);
		}
		public OperationUsesContext operationUses() {
			return getRuleContext(OperationUsesContext.class,0);
		}
		public OperationUsesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operationUses; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitOperationUses(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperationUsesContext operationUses() throws RecognitionException {
		return operationUses(0);
	}

	private OperationUsesContext operationUses(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		OperationUsesContext _localctx = new OperationUsesContext(_ctx, _parentState);
		OperationUsesContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_operationUses, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(47);
			operationUse();
			}
			_ctx.stop = _input.LT(-1);
			setState(53);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new OperationUsesContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_operationUses);
					setState(49);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(50);
					operationUse();
					}
					} 
				}
				setState(55);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class OperationUseContext extends ParserRuleContext {
		public CanBeIdentifierContext canBeIdentifier() {
			return getRuleContext(CanBeIdentifierContext.class,0);
		}
		public ArgumentsOptCurlyContext argumentsOptCurly() {
			return getRuleContext(ArgumentsOptCurlyContext.class,0);
		}
		public OperationUseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operationUse; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitOperationUse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperationUseContext operationUse() throws RecognitionException {
		OperationUseContext _localctx = new OperationUseContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_operationUse);
		try {
			setState(60);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(56);
				canBeIdentifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(57);
				canBeIdentifier();
				setState(58);
				argumentsOptCurly();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentsOptCurlyContext extends ParserRuleContext {
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public TerminalNode OpenCurly() { return getToken(MugeneParser.OpenCurly, 0); }
		public TerminalNode CloseCurly() { return getToken(MugeneParser.CloseCurly, 0); }
		public ArgumentsOptCurlyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentsOptCurly; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitArgumentsOptCurly(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentsOptCurlyContext argumentsOptCurly() throws RecognitionException {
		ArgumentsOptCurlyContext _localctx = new ArgumentsOptCurlyContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_argumentsOptCurly);
		try {
			setState(69);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(62);
				arguments(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(63);
				match(OpenCurly);
				setState(64);
				match(CloseCurly);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(65);
				match(OpenCurly);
				setState(66);
				arguments(0);
				setState(67);
				match(CloseCurly);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentsContext extends ParserRuleContext {
		public ArgumentContext argument() {
			return getRuleContext(ArgumentContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public CommasContext commas() {
			return getRuleContext(CommasContext.class,0);
		}
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitArguments(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		return arguments(0);
	}

	private ArgumentsContext arguments(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, _parentState);
		ArgumentsContext _prevctx = _localctx;
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_arguments, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(72);
			argument();
			}
			_ctx.stop = _input.LT(-1);
			setState(80);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ArgumentsContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_arguments);
					setState(74);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(75);
					commas(0);
					setState(76);
					argument();
					}
					} 
				}
				setState(82);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ArgumentContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ConditionalExprContext conditionalExpr() {
			return getRuleContext(ConditionalExprContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			conditionalExpr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConditionalExprContext extends ParserRuleContext {
		public ComparisonExprContext comparisonExpr() {
			return getRuleContext(ComparisonExprContext.class,0);
		}
		public TerminalNode Question() { return getToken(MugeneParser.Question, 0); }
		public List<ConditionalExprContext> conditionalExpr() {
			return getRuleContexts(ConditionalExprContext.class);
		}
		public ConditionalExprContext conditionalExpr(int i) {
			return getRuleContext(ConditionalExprContext.class,i);
		}
		public TerminalNode Comma() { return getToken(MugeneParser.Comma, 0); }
		public ConditionalExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionalExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitConditionalExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionalExprContext conditionalExpr() throws RecognitionException {
		ConditionalExprContext _localctx = new ConditionalExprContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_conditionalExpr);
		try {
			setState(94);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(87);
				comparisonExpr();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(88);
				comparisonExpr();
				setState(89);
				match(Question);
				setState(90);
				conditionalExpr();
				setState(91);
				match(Comma);
				setState(92);
				conditionalExpr();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparisonExprContext extends ParserRuleContext {
		public AddSubExprContext addSubExpr() {
			return getRuleContext(AddSubExprContext.class,0);
		}
		public ComparisonOperatorContext comparisonOperator() {
			return getRuleContext(ComparisonOperatorContext.class,0);
		}
		public ComparisonExprContext comparisonExpr() {
			return getRuleContext(ComparisonExprContext.class,0);
		}
		public ComparisonExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitComparisonExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisonExprContext comparisonExpr() throws RecognitionException {
		ComparisonExprContext _localctx = new ComparisonExprContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_comparisonExpr);
		try {
			setState(101);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(96);
				addSubExpr(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(97);
				addSubExpr(0);
				setState(98);
				comparisonOperator();
				setState(99);
				comparisonExpr();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparisonOperatorContext extends ParserRuleContext {
		public TerminalNode BackSlashLesser() { return getToken(MugeneParser.BackSlashLesser, 0); }
		public TerminalNode BackSlashLesserEqual() { return getToken(MugeneParser.BackSlashLesserEqual, 0); }
		public TerminalNode BackSlashGreater() { return getToken(MugeneParser.BackSlashGreater, 0); }
		public TerminalNode BackSlashGreaterEqual() { return getToken(MugeneParser.BackSlashGreaterEqual, 0); }
		public ComparisonOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonOperator; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitComparisonOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisonOperatorContext comparisonOperator() throws RecognitionException {
		ComparisonOperatorContext _localctx = new ComparisonOperatorContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_comparisonOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BackSlashLesser) | (1L << BackSlashLesserEqual) | (1L << BackSlashGreater) | (1L << BackSlashGreaterEqual))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AddSubExprContext extends ParserRuleContext {
		public MulDivModExprContext mulDivModExpr() {
			return getRuleContext(MulDivModExprContext.class,0);
		}
		public AddSubExprContext addSubExpr() {
			return getRuleContext(AddSubExprContext.class,0);
		}
		public TerminalNode Plus() { return getToken(MugeneParser.Plus, 0); }
		public TerminalNode Caret() { return getToken(MugeneParser.Caret, 0); }
		public TerminalNode Minus() { return getToken(MugeneParser.Minus, 0); }
		public AddSubExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_addSubExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitAddSubExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AddSubExprContext addSubExpr() throws RecognitionException {
		return addSubExpr(0);
	}

	private AddSubExprContext addSubExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AddSubExprContext _localctx = new AddSubExprContext(_ctx, _parentState);
		AddSubExprContext _prevctx = _localctx;
		int _startState = 20;
		enterRecursionRule(_localctx, 20, RULE_addSubExpr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(106);
			mulDivModExpr(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(119);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(117);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
					case 1:
						{
						_localctx = new AddSubExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_addSubExpr);
						setState(108);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(109);
						match(Plus);
						setState(110);
						mulDivModExpr(0);
						}
						break;
					case 2:
						{
						_localctx = new AddSubExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_addSubExpr);
						setState(111);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(112);
						match(Caret);
						setState(113);
						mulDivModExpr(0);
						}
						break;
					case 3:
						{
						_localctx = new AddSubExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_addSubExpr);
						setState(114);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(115);
						match(Minus);
						setState(116);
						mulDivModExpr(0);
						}
						break;
					}
					} 
				}
				setState(121);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class MulDivModExprContext extends ParserRuleContext {
		public PrimaryExprContext primaryExpr() {
			return getRuleContext(PrimaryExprContext.class,0);
		}
		public MulDivModExprContext mulDivModExpr() {
			return getRuleContext(MulDivModExprContext.class,0);
		}
		public TerminalNode Asterisk() { return getToken(MugeneParser.Asterisk, 0); }
		public TerminalNode Slash() { return getToken(MugeneParser.Slash, 0); }
		public TerminalNode Percent() { return getToken(MugeneParser.Percent, 0); }
		public MulDivModExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mulDivModExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitMulDivModExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MulDivModExprContext mulDivModExpr() throws RecognitionException {
		return mulDivModExpr(0);
	}

	private MulDivModExprContext mulDivModExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		MulDivModExprContext _localctx = new MulDivModExprContext(_ctx, _parentState);
		MulDivModExprContext _prevctx = _localctx;
		int _startState = 22;
		enterRecursionRule(_localctx, 22, RULE_mulDivModExpr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(123);
			primaryExpr();
			}
			_ctx.stop = _input.LT(-1);
			setState(136);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(134);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
					case 1:
						{
						_localctx = new MulDivModExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_mulDivModExpr);
						setState(125);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(126);
						match(Asterisk);
						setState(127);
						primaryExpr();
						}
						break;
					case 2:
						{
						_localctx = new MulDivModExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_mulDivModExpr);
						setState(128);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(129);
						match(Slash);
						setState(130);
						primaryExpr();
						}
						break;
					case 3:
						{
						_localctx = new MulDivModExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_mulDivModExpr);
						setState(131);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(132);
						match(Percent);
						setState(133);
						primaryExpr();
						}
						break;
					}
					} 
				}
				setState(138);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PrimaryExprContext extends ParserRuleContext {
		public VariableReferenceContext variableReference() {
			return getRuleContext(VariableReferenceContext.class,0);
		}
		public StringConstantContext stringConstant() {
			return getRuleContext(StringConstantContext.class,0);
		}
		public TerminalNode OpenCurly() { return getToken(MugeneParser.OpenCurly, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode CloseCurly() { return getToken(MugeneParser.CloseCurly, 0); }
		public StepConstantContext stepConstant() {
			return getRuleContext(StepConstantContext.class,0);
		}
		public UnaryExprContext unaryExpr() {
			return getRuleContext(UnaryExprContext.class,0);
		}
		public PrimaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitPrimaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryExprContext primaryExpr() throws RecognitionException {
		PrimaryExprContext _localctx = new PrimaryExprContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_primaryExpr);
		try {
			setState(147);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Dollar:
				enterOuterAlt(_localctx, 1);
				{
				setState(139);
				variableReference();
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(140);
				stringConstant();
				}
				break;
			case OpenCurly:
				enterOuterAlt(_localctx, 3);
				{
				setState(141);
				match(OpenCurly);
				setState(142);
				expression();
				setState(143);
				match(CloseCurly);
				}
				break;
			case Percent:
				enterOuterAlt(_localctx, 4);
				{
				setState(145);
				stepConstant();
				}
				break;
			case Caret:
			case Minus:
			case Dot:
			case NumberLiteral:
				enterOuterAlt(_localctx, 5);
				{
				setState(146);
				unaryExpr();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryExprContext extends ParserRuleContext {
		public NumberOrLengthConstantContext numberOrLengthConstant() {
			return getRuleContext(NumberOrLengthConstantContext.class,0);
		}
		public TerminalNode Minus() { return getToken(MugeneParser.Minus, 0); }
		public TerminalNode Caret() { return getToken(MugeneParser.Caret, 0); }
		public UnaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitUnaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryExprContext unaryExpr() throws RecognitionException {
		UnaryExprContext _localctx = new UnaryExprContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_unaryExpr);
		try {
			setState(154);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Dot:
			case NumberLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(149);
				numberOrLengthConstant();
				}
				break;
			case Minus:
				enterOuterAlt(_localctx, 2);
				{
				setState(150);
				match(Minus);
				setState(151);
				numberOrLengthConstant();
				}
				break;
			case Caret:
				enterOuterAlt(_localctx, 3);
				{
				setState(152);
				match(Caret);
				setState(153);
				numberOrLengthConstant();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableReferenceContext extends ParserRuleContext {
		public TerminalNode Dollar() { return getToken(MugeneParser.Dollar, 0); }
		public CanBeIdentifierContext canBeIdentifier() {
			return getRuleContext(CanBeIdentifierContext.class,0);
		}
		public VariableReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableReference; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitVariableReference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableReferenceContext variableReference() throws RecognitionException {
		VariableReferenceContext _localctx = new VariableReferenceContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_variableReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			match(Dollar);
			setState(157);
			canBeIdentifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringConstantContext extends ParserRuleContext {
		public TerminalNode StringLiteral() { return getToken(MugeneParser.StringLiteral, 0); }
		public StringConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringConstant; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitStringConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringConstantContext stringConstant() throws RecognitionException {
		StringConstantContext _localctx = new StringConstantContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_stringConstant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(StringLiteral);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StepConstantContext extends ParserRuleContext {
		public TerminalNode Percent() { return getToken(MugeneParser.Percent, 0); }
		public TerminalNode NumberLiteral() { return getToken(MugeneParser.NumberLiteral, 0); }
		public TerminalNode Minus() { return getToken(MugeneParser.Minus, 0); }
		public StepConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stepConstant; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitStepConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StepConstantContext stepConstant() throws RecognitionException {
		StepConstantContext _localctx = new StepConstantContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_stepConstant);
		try {
			setState(166);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(161);
				match(Percent);
				setState(162);
				match(NumberLiteral);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(163);
				match(Percent);
				setState(164);
				match(Minus);
				setState(165);
				match(NumberLiteral);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberOrLengthConstantContext extends ParserRuleContext {
		public TerminalNode NumberLiteral() { return getToken(MugeneParser.NumberLiteral, 0); }
		public DotsContext dots() {
			return getRuleContext(DotsContext.class,0);
		}
		public NumberOrLengthConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numberOrLengthConstant; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitNumberOrLengthConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberOrLengthConstantContext numberOrLengthConstant() throws RecognitionException {
		NumberOrLengthConstantContext _localctx = new NumberOrLengthConstantContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_numberOrLengthConstant);
		try {
			setState(172);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(168);
				match(NumberLiteral);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(169);
				match(NumberLiteral);
				setState(170);
				dots(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(171);
				dots(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DotsContext extends ParserRuleContext {
		public TerminalNode Dot() { return getToken(MugeneParser.Dot, 0); }
		public DotsContext dots() {
			return getRuleContext(DotsContext.class,0);
		}
		public DotsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dots; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitDots(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DotsContext dots() throws RecognitionException {
		return dots(0);
	}

	private DotsContext dots(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		DotsContext _localctx = new DotsContext(_ctx, _parentState);
		DotsContext _prevctx = _localctx;
		int _startState = 36;
		enterRecursionRule(_localctx, 36, RULE_dots, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(175);
			match(Dot);
			}
			_ctx.stop = _input.LT(-1);
			setState(181);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new DotsContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_dots);
					setState(177);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(178);
					match(Dot);
					}
					} 
				}
				setState(183);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class CanBeIdentifierContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(MugeneParser.Identifier, 0); }
		public TerminalNode Colon() { return getToken(MugeneParser.Colon, 0); }
		public TerminalNode Slash() { return getToken(MugeneParser.Slash, 0); }
		public CanBeIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_canBeIdentifier; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitCanBeIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CanBeIdentifierContext canBeIdentifier() throws RecognitionException {
		CanBeIdentifierContext _localctx = new CanBeIdentifierContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_canBeIdentifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(184);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Slash) | (1L << Colon) | (1L << Identifier))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommasContext extends ParserRuleContext {
		public TerminalNode Comma() { return getToken(MugeneParser.Comma, 0); }
		public CommasContext commas() {
			return getRuleContext(CommasContext.class,0);
		}
		public CommasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commas; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MugeneParserVisitor ) return ((MugeneParserVisitor<? extends T>)visitor).visitCommas(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommasContext commas() throws RecognitionException {
		return commas(0);
	}

	private CommasContext commas(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		CommasContext _localctx = new CommasContext(_ctx, _parentState);
		CommasContext _prevctx = _localctx;
		int _startState = 40;
		enterRecursionRule(_localctx, 40, RULE_commas, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(187);
			match(Comma);
			}
			_ctx.stop = _input.LT(-1);
			setState(193);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new CommasContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_commas);
					setState(189);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(190);
					match(Comma);
					}
					} 
				}
				setState(195);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return operationUses_sempred((OperationUsesContext)_localctx, predIndex);
		case 4:
			return arguments_sempred((ArgumentsContext)_localctx, predIndex);
		case 10:
			return addSubExpr_sempred((AddSubExprContext)_localctx, predIndex);
		case 11:
			return mulDivModExpr_sempred((MulDivModExprContext)_localctx, predIndex);
		case 18:
			return dots_sempred((DotsContext)_localctx, predIndex);
		case 20:
			return commas_sempred((CommasContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean operationUses_sempred(OperationUsesContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean arguments_sempred(ArgumentsContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean addSubExpr_sempred(AddSubExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 3);
		case 3:
			return precpred(_ctx, 2);
		case 4:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean mulDivModExpr_sempred(MulDivModExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 3);
		case 6:
			return precpred(_ctx, 2);
		case 7:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean dots_sempred(DotsContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean commas_sempred(CommasContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\34\u00c7\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2\3\2\5\2/\n\2\3\3\3\3\3\3"+
		"\3\3\3\3\7\3\66\n\3\f\3\16\39\13\3\3\4\3\4\3\4\3\4\5\4?\n\4\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\5\5H\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\7\6Q\n\6\f\6\16"+
		"\6T\13\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\ta\n\t\3\n\3\n"+
		"\3\n\3\n\3\n\5\nh\n\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\7\fx\n\f\f\f\16\f{\13\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\7\r\u0089\n\r\f\r\16\r\u008c\13\r\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\5\16\u0096\n\16\3\17\3\17\3\17\3\17\3\17\5\17\u009d\n"+
		"\17\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\22\3\22\5\22\u00a9\n\22"+
		"\3\23\3\23\3\23\3\23\5\23\u00af\n\23\3\24\3\24\3\24\3\24\3\24\7\24\u00b6"+
		"\n\24\f\24\16\24\u00b9\13\24\3\25\3\25\3\26\3\26\3\26\3\26\3\26\7\26\u00c2"+
		"\n\26\f\26\16\26\u00c5\13\26\3\26\2\b\4\n\26\30&*\27\2\4\6\b\n\f\16\20"+
		"\22\24\26\30\32\34\36 \"$&(*\2\4\3\2\23\26\5\2\16\16\21\21\34\34\2\u00ca"+
		"\2.\3\2\2\2\4\60\3\2\2\2\6>\3\2\2\2\bG\3\2\2\2\nI\3\2\2\2\fU\3\2\2\2\16"+
		"W\3\2\2\2\20`\3\2\2\2\22g\3\2\2\2\24i\3\2\2\2\26k\3\2\2\2\30|\3\2\2\2"+
		"\32\u0095\3\2\2\2\34\u009c\3\2\2\2\36\u009e\3\2\2\2 \u00a1\3\2\2\2\"\u00a8"+
		"\3\2\2\2$\u00ae\3\2\2\2&\u00b0\3\2\2\2(\u00ba\3\2\2\2*\u00bc\3\2\2\2,"+
		"/\5\4\3\2-/\5\16\b\2.,\3\2\2\2.-\3\2\2\2/\3\3\2\2\2\60\61\b\3\1\2\61\62"+
		"\5\6\4\2\62\67\3\2\2\2\63\64\f\3\2\2\64\66\5\6\4\2\65\63\3\2\2\2\669\3"+
		"\2\2\2\67\65\3\2\2\2\678\3\2\2\28\5\3\2\2\29\67\3\2\2\2:?\5(\25\2;<\5"+
		"(\25\2<=\5\b\5\2=?\3\2\2\2>:\3\2\2\2>;\3\2\2\2?\7\3\2\2\2@H\5\n\6\2AB"+
		"\7\7\2\2BH\7\b\2\2CD\7\7\2\2DE\5\n\6\2EF\7\b\2\2FH\3\2\2\2G@\3\2\2\2G"+
		"A\3\2\2\2GC\3\2\2\2H\t\3\2\2\2IJ\b\6\1\2JK\5\f\7\2KR\3\2\2\2LM\f\3\2\2"+
		"MN\5*\26\2NO\5\f\7\2OQ\3\2\2\2PL\3\2\2\2QT\3\2\2\2RP\3\2\2\2RS\3\2\2\2"+
		"S\13\3\2\2\2TR\3\2\2\2UV\5\16\b\2V\r\3\2\2\2WX\5\20\t\2X\17\3\2\2\2Ya"+
		"\5\22\n\2Z[\5\22\n\2[\\\7\t\2\2\\]\5\20\t\2]^\7\4\2\2^_\5\20\t\2_a\3\2"+
		"\2\2`Y\3\2\2\2`Z\3\2\2\2a\21\3\2\2\2bh\5\26\f\2cd\5\26\f\2de\5\24\13\2"+
		"ef\5\22\n\2fh\3\2\2\2gb\3\2\2\2gc\3\2\2\2h\23\3\2\2\2ij\t\2\2\2j\25\3"+
		"\2\2\2kl\b\f\1\2lm\5\30\r\2my\3\2\2\2no\f\5\2\2op\7\13\2\2px\5\30\r\2"+
		"qr\f\4\2\2rs\7\n\2\2sx\5\30\r\2tu\f\3\2\2uv\7\f\2\2vx\5\30\r\2wn\3\2\2"+
		"\2wq\3\2\2\2wt\3\2\2\2x{\3\2\2\2yw\3\2\2\2yz\3\2\2\2z\27\3\2\2\2{y\3\2"+
		"\2\2|}\b\r\1\2}~\5\32\16\2~\u008a\3\2\2\2\177\u0080\f\5\2\2\u0080\u0081"+
		"\7\r\2\2\u0081\u0089\5\32\16\2\u0082\u0083\f\4\2\2\u0083\u0084\7\16\2"+
		"\2\u0084\u0089\5\32\16\2\u0085\u0086\f\3\2\2\u0086\u0087\7\17\2\2\u0087"+
		"\u0089\5\32\16\2\u0088\177\3\2\2\2\u0088\u0082\3\2\2\2\u0088\u0085\3\2"+
		"\2\2\u0089\u008c\3\2\2\2\u008a\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b"+
		"\31\3\2\2\2\u008c\u008a\3\2\2\2\u008d\u0096\5\36\20\2\u008e\u0096\5 \21"+
		"\2\u008f\u0090\7\7\2\2\u0090\u0091\5\16\b\2\u0091\u0092\7\b\2\2\u0092"+
		"\u0096\3\2\2\2\u0093\u0096\5\"\22\2\u0094\u0096\5\34\17\2\u0095\u008d"+
		"\3\2\2\2\u0095\u008e\3\2\2\2\u0095\u008f\3\2\2\2\u0095\u0093\3\2\2\2\u0095"+
		"\u0094\3\2\2\2\u0096\33\3\2\2\2\u0097\u009d\5$\23\2\u0098\u0099\7\f\2"+
		"\2\u0099\u009d\5$\23\2\u009a\u009b\7\n\2\2\u009b\u009d\5$\23\2\u009c\u0097"+
		"\3\2\2\2\u009c\u0098\3\2\2\2\u009c\u009a\3\2\2\2\u009d\35\3\2\2\2\u009e"+
		"\u009f\7\20\2\2\u009f\u00a0\5(\25\2\u00a0\37\3\2\2\2\u00a1\u00a2\7\3\2"+
		"\2\u00a2!\3\2\2\2\u00a3\u00a4\7\17\2\2\u00a4\u00a9\7\33\2\2\u00a5\u00a6"+
		"\7\17\2\2\u00a6\u00a7\7\f\2\2\u00a7\u00a9\7\33\2\2\u00a8\u00a3\3\2\2\2"+
		"\u00a8\u00a5\3\2\2\2\u00a9#\3\2\2\2\u00aa\u00af\7\33\2\2\u00ab\u00ac\7"+
		"\33\2\2\u00ac\u00af\5&\24\2\u00ad\u00af\5&\24\2\u00ae\u00aa\3\2\2\2\u00ae"+
		"\u00ab\3\2\2\2\u00ae\u00ad\3\2\2\2\u00af%\3\2\2\2\u00b0\u00b1\b\24\1\2"+
		"\u00b1\u00b2\7\22\2\2\u00b2\u00b7\3\2\2\2\u00b3\u00b4\f\3\2\2\u00b4\u00b6"+
		"\7\22\2\2\u00b5\u00b3\3\2\2\2\u00b6\u00b9\3\2\2\2\u00b7\u00b5\3\2\2\2"+
		"\u00b7\u00b8\3\2\2\2\u00b8\'\3\2\2\2\u00b9\u00b7\3\2\2\2\u00ba\u00bb\t"+
		"\3\2\2\u00bb)\3\2\2\2\u00bc\u00bd\b\26\1\2\u00bd\u00be\7\4\2\2\u00be\u00c3"+
		"\3\2\2\2\u00bf\u00c0\f\3\2\2\u00c0\u00c2\7\4\2\2\u00c1\u00bf\3\2\2\2\u00c2"+
		"\u00c5\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4+\3\2\2\2"+
		"\u00c5\u00c3\3\2\2\2\23.\67>GR`gwy\u0088\u008a\u0095\u009c\u00a8\u00ae"+
		"\u00b7\u00c3";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}