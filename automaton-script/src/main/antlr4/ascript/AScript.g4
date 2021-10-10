grammar AScript;
import ASExpression, ASTokens;

prog: block EOF;

statement
    : letstmt
    | printstmt
    | inputstmt
    | ifstmt
    | forstmt
    | whilestmt
    | repeatstmt
    | continuestmt
    | exitstmt
    | compressstmt
    | extractstmt
    | copystmt
    | showstmt
    | downloadstmt
    | executestmt
    | filestmt
    | linesstmt1
    | linesstmt2
    | linesstmt3
    | packagestmt
    | scriptstmt
    | sendstmt
    | templatestmt1
    | templatestmt2
    | unzipstmt
    | zipstmt
    | pingstmt
    | scanstmt
    | COMMENT;

block
    : (statement (NEWLINE+ | EOF))*
    ;

letstmt
    : LET? vardecl EQ expression
    ;

vardecl
    : varname varsuffix?
    ;

varname
    : ID
    ;

varsuffix
    : DOLLAR
    ;

printstmt
    : PRINT expression
    ;

inputstmt
    : INPUT string TO vardecl
    ;

ifstmt
    : IF expression NEWLINE* THEN NEWLINE+ block elifstmt* elsestmt? ENDIF
    ;

elifstmt
    : ELSE IF expression NEWLINE* THEN NEWLINE+ block
    ;

elsestmt
    : ELSE NEWLINE+ block
    ;

forstmt
    : FOR vardecl EQ expression TO expression (STEP expression)? NEWLINE+ block ENDFOR
    ;

whilestmt
    : WHILE expression NEWLINE+ block ENDWHILE
    ;

repeatstmt
    : REPEAT NEWLINE+ block NEWLINE* UNTIL expression
    ;

continuestmt
    : CONTINUE
    ;

exitstmt
    : EXIT
    ;

compressstmt
    : COMPRESS expression TO expression
    ;

extractstmt
    : EXTRACT expression TO expression
    ;

copystmt
    : COPY FROM expression TO expression
    ;

showstmt
    : SHOW FILE? expression
    ;

downloadstmt
    : DOWNLOAD FROM expression TO expression
    ;

executestmt
    : EXECUTE expression FROM DIR expression TO vardecl
    ;

filestmt
    : FILE expression STATE expression (OWNER expression)? (MODE expression)?
    ;

linesstmt1
    : LINES expression BEFORE REGEX expression SET expression
    ;

linesstmt2
    : LINES expression AFTER REGEX expression SET expression
    ;

linesstmt3
    : LINES expression REPLACE REGEX expression BY expression
    ;

packagestmt
    : PACKAGE exprlist
    ;

scriptstmt
    : CALL expression OUTPUT vardecl
    ;

sendstmt
    : SEND FILE expression TO string PORT number ON expression
    ;

templatestmt1
    : TEMPLATE FROM expression TO expression
    ;

templatestmt2
    : TEMPLATE CONTENT expression TO expression
    ;

unzipstmt
    : UNZIP FROM expression TO expression
    ;

zipstmt
    : ZIP FROM expression TO expression
    ;

pingstmt
    : PING expression
    ;

scanstmt
    : SCAN NETWORK? expression
    ;
