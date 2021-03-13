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
	canBeIdentifier
	| canBeIdentifier argumentsOptCurly
	;

argumentsOptCurly :
	arguments
	| OpenCurly CloseCurly
	| OpenCurly arguments CloseCurly
	;

arguments :
	argument
	| arguments commas argument
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
	numberOrLengthConstant
	| Minus numberOrLengthConstant
	| Caret numberOrLengthConstant
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
