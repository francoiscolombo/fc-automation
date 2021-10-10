lexer grammar ASTokens; // note "lexer grammar"

// operators
MUL : '*' ;
DIV : '/' ;
ADD : '+' ;
SUB : '-' ;
EXP : '^' ;
MOD : 'MOD' ;

// logical
NEQ : '<>' ;
GTE : '>=' ;
LTE : '<=' ;
GT  : '>' ;
LT  : '<' ;
EQ  : '=' ;

// relational
AND : 'AND' | 'and' ;
OR  : 'OR' | 'or' ;
NOT : 'NOT' | 'not' ;

// other
COMMA  : ',' ;
LPAREN : '(' ;
RPAREN : ')' ;

// functions
LEN : 'LEN' | 'len' ;
VAL : 'VAL' | 'val' ;
ISNAN   : 'ISNAN' | 'isnan' ;

// keywords
PRINT    : 'PRINT' | 'print' ;
INPUT    : 'INPUT' | 'input' ;
LET      : 'LET' | 'let' ;
REM      : 'REM' | 'rem' ;
IF       : 'IF' | 'if' ;
THEN     : 'THEN' | 'then' ;
ELSE     : 'ELSE' | 'else' ;
ENDIF    : 'ENDIF' | 'endif';
FOR      : 'FOR' | 'for' ;
ENDFOR   : 'ENDFOR' | 'endfor';
WHILE    : 'WHILE' | 'while' ;
ENDWHILE : 'ENDWHILE' | 'endwhile';
REPEAT   : 'REPEAT' | 'repeat' ;
UNTIL    : 'UNTIL' | 'until' ;
STEP     : 'STEP' | 'step' ;
TO       : 'TO' | 'to' ;
CONTINUE : 'CONTINUE' | 'continue' ;
EXIT     : 'EXIT' | 'exit' ;
COMPRESS : 'COMPRESS' | 'compress' ;
EXTRACT  : 'EXTRACT' | 'extract' ;
COPY     : 'COPY' | 'copy' ;
FROM     : 'FROM' | 'from' ;
SHOW     : 'SHOW' | 'show' ;
FILE     : 'FILE' | 'file' ;
DIR      : 'DIR' | 'dir' ;
MODE     : 'MODE' | 'mode' ;
EXECUTE  : 'EXECUTE' | 'execute' ;
STATE    : 'STATE' | 'state' ;
OWNER    : 'OWNER' | 'owner' ;
LINES    : 'LINES' | 'lines' ;
SET      : 'SET' | 'set' ;
BY       : 'BY' | 'by' ;
REGEX    : 'REGEX' | 'regex' ;
BEFORE   : 'BEFORE' | 'before' ;
AFTER    : 'AFTER' | 'after';
REPLACE  : 'REPLACE' | 'replace' ;
PACKAGE  : 'PACKAGE' | 'package' ;
CALL     : 'CALL' | 'call' ;
SEND     : 'SEND' | 'send' ;
PORT     : 'PORT' | 'port' ;
ON       : 'ON' | 'on' ;
TEMPLATE : 'TEMPLATE' | 'template' ;
CONTENT  : 'CONTENT' | 'content' ;
UNZIP    : 'UNZIP' | 'unzip' ;
ZIP      : 'ZIP' | 'zip' ;
PING     : 'PING' | 'ping' ;
SCAN     : 'SCAN' | 'scan' ;
NETWORK  : 'NETWORK' | 'network' ;

// comments
COMMENT : REM ~[\r\n]* ;

// literals
ID              : [a-zA-Z]+ ;  // match identifiers
NUMBER          : [0-9]+ ('.' [0-9]+)?;   // match integers
STRINGLITERAL   : '"' ~ ["\r\n]* '"' ;
DOLLAR          : '$' ;
NEWLINE         :'\r'? '\n' ;  // return newlines to parser (end-statement signal)
WS              : [ \t]+ -> skip ; // toss out whitespace
//NUMBER
//    : ('0' .. '9') + (('e' | 'E') NUMBER)*
//    ;
