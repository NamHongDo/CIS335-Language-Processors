MOV	#7,%EAX
MOV	%EAX,X
MOV	#3,%EAX
MUL	X
MOV	%EAX,Y
MOV	X,%EAX
MUL	#2
MOV	%EAX,T1
MOV	Y,%EAX
ADD	X
DIV	#5
MOV	%EAX,T2
MOV	T1,%EAX
SUB	T2
MOV	%EAX,Z
 
X	RESW  1
Y	RESW  1
Z	RESW  1
T1	RESW  1
T2	RESW  1
