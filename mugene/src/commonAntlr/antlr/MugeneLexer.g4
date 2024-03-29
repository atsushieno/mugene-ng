lexer grammar MugeneLexer;

StringLiteral : '"' [.*] '"';
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

NumberLiteral : [0-9]+;
Identifier : [a-zA-Z_]+ ;
