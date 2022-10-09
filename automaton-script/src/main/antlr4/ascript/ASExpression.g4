grammar ASExpression;
import ASTokens;

prog: stat+;

stat
    : expression NEWLINE
    | NEWLINE
    ;

expression
    : string                                    # StringExpr
    | number                                    # NumberExpr
    | func                                      # FuncExpr
    | id                                        # IdExpr
    | (LPAREN expression RPAREN)                # ParenExpr
    | expression op=(MUL|DIV|MOD) expression    # MulDivExpr
    | expression op=(ADD|SUB) expression        # AddSubExpr
    | expression op=(GTE|GT|LTE|LT|EQ|NEQ) expression   # RelExpr
    | NOT expression                            # NotExpr
    | expression AND expression                 # AndExpr
    | expression OR expression                  # OrExpr
    | <assoc=right> expression EXP expression   # ExpExpr
    ;

func
    : lenfunc
    | valfunc
    | isnanfunc
    | lowerfunc
    | upperfunc
    | containsfunc
    | matchesfunc
    | leftfunc
    | rightfunc
    | substrfunc
    | startswithfunc
    | endswithfunc
    | replacewithfunc
    | concatfunc
    ;

string
    : STRINGLITERAL
    ;

number
    : NUMBER
    ;

id
    : ID
    ;

lenfunc
    : LEN LPAREN expression RPAREN
    ;

valfunc
    : VAL LPAREN expression RPAREN
    ;

isnanfunc
    : ISNAN LPAREN expression RPAREN
    ;

lowerfunc
    : LOWER LPAREN expression RPAREN
    ;

upperfunc
    : UPPER LPAREN expression RPAREN
    ;

containsfunc
    : CONTAINS LPAREN expression COMMA expression RPAREN
    ;

matchesfunc
    : MATCHES LPAREN expression COMMA expression RPAREN
    ;

leftfunc
    : LEFT LPAREN expression COMMA expression RPAREN
    ;

rightfunc
    : RIGHT LPAREN expression COMMA expression RPAREN
    ;

substrfunc
    : SUBSTR LPAREN expression COMMA expression COMMA expression RPAREN
    ;

startswithfunc
    : STARTSWITH LPAREN expression COMMA expression RPAREN
    ;

endswithfunc
    : ENDSWITH LPAREN expression COMMA expression RPAREN
    ;

replacewithfunc
    : REPLACEWITH LPAREN expression COMMA expression COMMA expression RPAREN
    ;

concatfunc
    : CONCAT LPAREN expression COMMA expression RPAREN
    ;

exprlist
    : expression (COMMA expression)*
    ;
