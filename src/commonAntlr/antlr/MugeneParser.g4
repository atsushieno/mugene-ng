parser grammar MugeneParser;

options { tokenVocab=MugeneLexer; }

expressionOrOptOperationUses :
	/* empty */
	{
		//return MutableListList<MmlOperationUse> ()
	}
	| operationUses
	| expression
	;

operationUses :
	operationUse
	{
	/*
		var l = MutableList<MmlOperationUse>()
		l.addAll($operationUse.ctx.children as List<MmlOperationUse>)
		return l
	*/
	}
	| operationUses operationUse
	{
	/*
		var l = $1 as MutableList<MmlOperationUse>
		l.add($2 as MmlOperationUse)
		return l;
    */
	}
	;

operationUse :
	canBeIdentifier
	{
	//	current_location = ($1 as MmlToken).Location;
	}
	argumentsOptCurly
	{
	/*
		var i = $1 as MmlToken
		var o = MmlOperationUse (i.Value as String, i.Location);
		for(a in ($3 as List<MmlValueExpr>)
			o.arguments.add(if (a == skipped_argument) null else a);
		return o;
	*/
	}
	;

argumentsOptCurly :
	optArguments
	| OpenCurly optArguments CloseCurly
	{
	//	return $2;
	}
	;

optArguments :
	/* empty */
	{
	//	return MutableList<MmlValueExpr>()
	}
	| arguments
	;

arguments :
	argument
	{
	/*
		var l = MutableList<MmlValueExpr>()
		l.add($1 as MmlValueExpr)
		return l;
	*/
	}
	| optArgument Comma arguments
	{
	/*
		var a = (MmlValueExpr) $1;
		var l = (List<MmlValueExpr>) $3;
		l.Insert (0, a);
		$$ = l;
	*/
	}
	;

optArgument :
	/* empty */
	{
	//	return skipped_argument;
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
	//	return MmlConditionalExpr ($1 as MmlValueExpr, $3 as MmlValueExpr, $5 as MmlValueExpr);
	}
	;

comparisonExpr :
	addSubExpr
	| addSubExpr comparisonOperator comparisonExpr
	{
	//	return MmlComparisonExpr ($1 as MmlValueExpr, $3 as MmlValueExpr, $2 as ComparisonType);
	}
	;

comparisonOperator
	: BackSlashLesser
	{
	//	return ComparisonType.Lesser;
	}
	| BackSlashLesserEqual
	{
	//	return ComparisonType.LesserEqual;
	}
	| BackSlashGreater
	{
	//	return ComparisonType.Greater;
	}
	| BackSlashGreaterEqual
	{
	//	return ComparisonType.GreaterEqual;
	}
	;

addSubExpr :
	mulDivModExpr
	| addSubExpr Plus mulDivModExpr
	{
	//	$$ = new MmlAddExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	| addSubExpr Caret mulDivModExpr
	{
	//	$$ = new MmlAddExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	| addSubExpr Minus mulDivModExpr
	{
	//	$$ = new MmlSubtractExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	;

mulDivModExpr :
	primaryExpr
	| mulDivModExpr Asterisk primaryExpr
	{
	//	$$ = new MmlMultiplyExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	| mulDivModExpr Slash primaryExpr
	{
	//	$$ = new MmlDivideExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	| mulDivModExpr Percent primaryExpr
	{
	//	$$ = new MmlModuloExpr ((MmlValueExpr) $1, (MmlValueExpr) $3);
	}
	;

primaryExpr :
	variableReference
	| stringConstant
	| OpenCurly expression CloseCurly
	{
	//	$$ = new MmlParenthesizedExpr ((MmlValueExpr) $2);
	}
	| stepConstant
	| unaryExpr
	;

unaryExpr :
	numberOrLengthConstant
	| Minus numberOrLengthConstant
	{
	/*
        var expr = (MmlValueExpr) $2;
		$$ = new MmlMultiplyExpr (new MmlConstantExpr (expr.Location, MmlDataType.Number, -1), expr);
	*/
	}
	| Caret numberOrLengthConstant
	{
	/*
        var expr = (MmlValueExpr) $2;
		$$ = new MmlAddExpr (new MmlVariableReferenceExpr (expr.Location, "__length"), expr);
	*/
	}
	;

variableReference :
	Dollar canBeIdentifier
	{
	/*
		var i = (MmlToken) $2;
		$$ = new MmlVariableReferenceExpr (i.Location, (string) i.Value);
	*/
	}
	;

stringConstant :
	StringLiteral
	{
	/*
		var t = (MmlToken) $1;
		$$ = new MmlConstantExpr (t.Location, MmlDataType.String, (string) t.Value);
	*/
	}
	;

stepConstant :
	Percent NumberLiteral
	{
	/*
		var n = (MmlToken) $2;
		var l = new MmlLength ((int) (double) MmlValueExpr.GetTypedValue (compiler, n.Value, MmlDataType.Number, n.Location)) { IsValueByStep = true };
		$$ = new MmlConstantExpr (n.Location, MmlDataType.Length, l);
	*/
	}
	| Percent Minus NumberLiteral
	{
	/*
		var n = (MmlToken) $3;
		var l = new MmlLength (-1 * (int) (double) MmlValueExpr.GetTypedValue (compiler, n.Value, MmlDataType.Number, n.Location)) { IsValueByStep = true };
		$$ = new MmlConstantExpr (n.Location, MmlDataType.Length, l);
	*/
	}
	;

numberOrLengthConstant :
	NumberLiteral
	{
	//	var t = (MmlToken) $1;
	//	$$ = new MmlConstantExpr (t.Location, MmlDataType.Number, t.Value);
	}
	| NumberLiteral dots
	{
	/*
		var t = (MmlToken) $1;
		var d = (int) $2;
		$$ = new MmlConstantExpr (t.Location, MmlDataType.Length, new MmlLength ((int) (double) t.Value) { Dots = d });
	*/
	}
	| dots
	{
	//	var d = (int) $1;
	//	$$ = new MmlMultiplyExpr (new MmlConstantExpr (input.Location, MmlDataType.Number, MmlValueExpr.LengthDotsToMultiplier (d)), new MmlVariableReferenceExpr (input.Location, "__length"));
	}
	;

dots :
	Dot
	{
	//	$$ = 1;
	}
	| dots Dot
	{
	//	$$ = ((int) $1) + 1;
	}
	;

canBeIdentifier :
	Identifier
	| Colon
	| Slash
	;

