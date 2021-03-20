parser grammar MugeneParser;

options { tokenVocab=MugeneLexer; }

expressionOrOperationUses :
    operationUses
	| expression
	;

operationUses :
	operationUse
	| operationUses operationUse
	;

operationUse :
	canBeIdentifier argumentsOptCurly?
	;

argumentsOptCurly :
	OpenCurly arguments? CloseCurly
	| arguments
	;

arguments :
	argument (commas arguments)?
	;

argument :
	expression
	;

expression :
	conditionalExpr
	;

conditionalExpr :
	comparisonExpr
	| comparisonExpr Question conditionalExpr Comma conditionalExpr
	;

comparisonExpr :
	addSubExpr
	| addSubExpr comparisonOperator comparisonExpr
	;

comparisonOperator
	: BackSlashLesser
	| BackSlashLesserEqual
	| BackSlashGreater
	| BackSlashGreaterEqual
	;

addSubExpr :
	mulDivModExpr
	| addSubExpr Plus mulDivModExpr
	| addSubExpr Caret mulDivModExpr
	| addSubExpr Minus mulDivModExpr
	;

mulDivModExpr :
	primaryExpr
	| mulDivModExpr Asterisk primaryExpr
	| mulDivModExpr Slash primaryExpr
	| mulDivModExpr Percent primaryExpr
	;

primaryExpr :
	variableReference
	| stringConstant
	| OpenCurly expression CloseCurly
	| stepConstant
	| unaryExpr
	;

unaryExpr :
	Minus numberOrLengthConstant
	| Caret numberOrLengthConstant
	| numberOrLengthConstant
	;

variableReference :
	Dollar canBeIdentifier
	;

stringConstant :
	StringLiteral
	;

stepConstant :
	Percent NumberLiteral
	| Percent Minus NumberLiteral
	;

numberOrLengthConstant :
	NumberLiteral
	| NumberLiteral dots
	| dots
	;

dots :
	Dot
	| dots Dot
	;

canBeIdentifier :
	Identifier
	| Colon
	| Slash
	;

commas :
    Comma
    | commas Comma
    ;
