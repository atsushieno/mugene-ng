grammar DiceNotation;

options { tokenVocab=DiceNotationLexer; }

/**
 * Rules.
 */

parse
:
	function
;

function
:
	dice
	| binaryOp
	| DIGIT
;

binaryOp
:
	dice OPERATOR function
	| DIGIT OPERATOR function
;

dice
:
