CIS 335/535 - Language Processors
---------------------------------
A small compiler for a very simple C-like language using the top down recursive descent method. The simplified grammar rules are given as bellow:
<stmt-list> ::= <stmt> {<stmt>}
<stmt>      ::= id = <expr> ;
<expr>      ::= <term> { + <term> | - <term>}
<term>      ::= <factor> { * <factor> | / <factor>}
<factor>    ::= id | intnum | ( <expr> )

Through I/O re-direction, the compiler reads the standard input which contains a list of statements. The compiler needs to check the syntax of the source code and generates the corresponding modified SIC/XE assembly code to the standar output.

Sample:
X = 7 ;
Y = 3 * X ;
Z = X * 2 - ( Y + X ) / 5;

Should produces:

MOV   #7,%EAX
MOV   %EAX,X
MOV   #3,%EAX
MUL   X
MOV   %EAX,Y
MOV   X,%EAX
MUL  #2
MOV  %EAX,T1
MOV  Y,%EAX
ADD  X
DIV  #5
MOV  %EAX,T2
MOV  T1,%EAX
SUB  T2
MOV  %EAX,Z

X   RESW    1
Y   RESW    1
Z   RESW    1
T1  RESW    1
T2  RESW    1
