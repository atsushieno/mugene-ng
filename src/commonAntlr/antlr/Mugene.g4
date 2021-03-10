parser grammar Mugene;

options { tokenVocab=MugeneLexer; }

expressionOrOptOperationUses :
	/* empty */
	{
		$$ = new List<MmlOperationUse> ();
	}
	| operationUses
	| expression
	;

operationUses :
	operationUse
	{
		var l = new List<MmlOperationUse> ();
		l.Add ((MmlOperationUse) $1);
		$$ = l;
	}
	| operationUses operationUse
	{
		var l = (List<MmlOperationUse>) $1;
		l.Add ((MmlOperationUse) $2);
		$$ = l;
	}
	;

operationUse :
	canBeIdentifier
	{
		current_location = ((MmlToken) $1).Location;
	}
	argumentsOptCurly
	{
		var i = (MmlToken) $1;
		var o = new MmlOperationUse ((string) i.Value, i.Location);
		foreach (MmlValueExpr a in (IEnumerable<MmlValueExpr>) $3)
			o.Arguments.Add (a == skipped_argument ? null : a);
		$$ = o;
	}
	;

argumentsOptCurly :
	optArguments
	| OpenCurly optArguments CloseCurly
	{
		$$ = $2;
	}
	;

optArguments :
	/* empty */
	{
		$$ = new List<MmlValueExpr> ();
	}
	| arguments
	;

arguments :
	argument
	{
		var l = new List<MmlValueExpr> ();
		l.Add ((MmlValueExpr) $1);
		$$ = l;
	}
	| optArgument Comma arguments
	{
		var a = (MmlValueExpr) $1;
		var l = (List<MmlValueExpr>) $3;
		l.Insert (0, a);
		$$ = l;
	}
	;

optArgument :
	/* empty */
	{
		$$ = skipped_argument;
	}
	| argument
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
	{
		$$ = new MmlConditionalExpr ((MmlValueExpr) $1, (MmlValueExpr) $3, (MmlValueExpr) $5);
	}
	;

comparisonExpr :
	addSubExpr
	| addSubExpr comparisonOperator comparisonExpr
	{
		$$ = new MmlComparisonExpr ((MmlValueExpr) $1, (MmlValueExpr) $3, (ComparisonType) $2);
	}
	;

comparisonOperator
	: BackSlashLesser
	{
		$$ = ComparisonType.Lesser;
	}
	| BackSlashLesserEqual
	{
		$$ = ComparisonType.LesserEqual;
	}
	| BackSlashGreater
	{
		$$ = ComparisonType.Greater;
	}
	| BackSlashGreaterEqual
	{
		$$ = ComparisonType.GreaterEqual;
	}
	;

addSubExpr :
	mulDivModExpr
	| addSubExpr Plus mulDivModExpr
	{
		$$ = new MmlAddExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	| addSubExpr Caret mulDivModExpr
	{
		$$ = new MmlAddExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	| addSubExpr Minus mulDivModExpr
	{
		$$ = new MmlSubtractExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	;

mulDivModExpr :
	primaryExpr
	| mulDivModExpr Asterisk primaryExpr
	{
		$$ = new MmlMultiplyExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	| mulDivModExpr Slash primaryExpr
	{
		$$ = new MmlDivideExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	| mulDivModExpr Percent primaryExpr
	{
		$$ = new MmlModuloExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	;

primaryExpr :
	variableReference
	| stringConstant
	| OpenCurly expression CloseCurly
	{
		$$ = new MmlParenthesizedExpr ((MmlValueExpr) $2);
	}
	| stepConstant
	| unaryExpr
	;

unaryExpr :
	numberOrLengthConstant
	| Minus numberOrLengthConstant
	{
        var expr = (MmlValueExpr) $2;
		$$ = new MmlMultiplyExpr (new MmlConstantExpr (expr.Location, MmlDataType.Number, -1), expr);
	}
	| Caret numberOrLengthConstant
	{
        var expr = (MmlValueExpr) $2;
		$$ = new MmlAddExpr (new MmlVariableReferenceExpr (expr.Location, "__length"), expr);
	}
	;

variableReference :
	Dollar canBeIdentifier
	{
		var i = (MmlToken) $2;
		$$ = new MmlVariableReferenceExpr (i.Location, (string) i.Value);
	}
	;

stringConstant :
	StringLiteral
	{
		var t = (MmlToken) $1;
		$$ = new MmlConstantExpr (t.Location, MmlDataType.String, (string) t.Value);
	}
	;

stepConstant :
	Percent NumberLiteral
	{
		var n = (MmlToken) $2;
		var l = new MmlLength ((int) (double) MmlValueExpr.GetTypedValue (compiler, n.Value, MmlDataType.Number, n.Location)) { IsValueByStep = true };
		$$ = new MmlConstantExpr (n.Location, MmlDataType.Length, l);
	}
	| Percent Minus NumberLiteral
	{
		var n = (MmlToken) $3;
		var l = new MmlLength (-1 * (int) (double) MmlValueExpr.GetTypedValue (compiler, n.Value, MmlDataType.Number, n.Location)) { IsValueByStep = true };
		$$ = new MmlConstantExpr (n.Location, MmlDataType.Length, l);
	}
	;

numberOrLengthConstant :
	NumberLiteral
	{
		var t = (MmlToken) $1;
		$$ = new MmlConstantExpr (t.Location, MmlDataType.Number, t.Value);
	}
	| NumberLiteral dots
	{
		var t = (MmlToken) $1;
		var d = (int) $2;
		$$ = new MmlConstantExpr (t.Location, MmlDataType.Length, new MmlLength ((int) (double) t.Value) { Dots = d });
	}
	| dots
	{
		var d = (int) $1;
		$$ = new MmlMultiplyExpr (new MmlConstantExpr (input.Location, MmlDataType.Number, MmlValueExpr.LengthDotsToMultiplier (d)), new MmlVariableReferenceExpr (input.Location, "__length"));
	}
	;

dots :
	Dot
	{
		$$ = 1;
	}
	| dots Dot
	{
		$$ = ((int) $1) + 1;
	}
	;

canBeIdentifier :
	Identifier
	| Colon
	| Slash
	;

