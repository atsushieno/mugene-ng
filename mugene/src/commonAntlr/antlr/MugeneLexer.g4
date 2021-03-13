lexer grammar MugeneLexer;

Identifier : [a-zA-Z_][a-zA-Z0-9_]* ;

StringLiteral : '"' [.*] '"';
NumberLiteral : [0-9]+;
Comma : ',';
OpenParen : '(';
CloseParen : ')';
OpenCurly : '{';
CloseCurly : '}';
Question : '?';
Caret : '^';
Plus : '+';
Minus : '-';
Asterisk : '*';
Slash : '/';
Percent : '%';
Dollar : '$';
Colon : ':';
Dot : '.';
BackSlashLesser : '\\<' ;
BackSlashLesserEqual : '\\<=' ;
BackSlashGreater : '\\>' ;
BackSlashGreaterEqual : '\\>=' ;
KeywordNumber : 'number' ;
KeywordLength : 'length' ;
KeywordString : 'string' ;
KeywordBuffer : 'buffer' ;

