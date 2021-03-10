
lexer grammar DiceNotationLexer;

/**
 * Tokens.
 */

OPERATOR
:
	( ADD | SUB )
;

// Operators

ADD
:
	'+'
;

SUB
:
	'-'
;

// Dice markers

DSEPARATOR
:
	( 'd' | 'D' )
;

DIGIT
:
	('0'..'9')+
;

// Skippable tokens

WS  : [\t\r\n]+ -> skip ;