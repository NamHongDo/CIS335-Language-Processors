/*CIS 335 project 7: Write a simple compiler with the given grammar rules, it will accept statement input, check if it follows the grammar rules and output the corresponding SIC/XE codes for the statements*/
import java.util.ArrayList;
import java.util.Scanner;

public class csucc {
	static int idx=0;
	static int idi=1;
	static String []str1=new String[0];
	static String [] s = new String[64];
	static int a=-1;//array index of str1 (currentToken=a, nextToken=a+1)
	static String currentToken;
	static String nextToken;
	static int REGA=0;
	static int tCount=0;
	static ArrayList <String> vars=new ArrayList<String>();//array to store all the variables
	public static void main(String[]args) throws Exception{
		//recieve the input and put each token into a String array
		Scanner input=new Scanner(System.in);
		while(input.hasNextLine()){
			String line1 = input.nextLine();
			String[] str2 = line1.split(" ");
			str1=concat(str1,str2);//concatenate every new string input with str1, make all the inputs into one complete string to lex() through in the program
		}
		input.close();
		stmt_list();//invoke statementlist to go though the sample code and check for grammar
		System.out.println(" ");

		for(int i=0;i<vars.size();i++){//loop to print out all the variables used in the SIC/XE code
			System.out.println(vars.get(i)+"\tRESW  1");
		}

	}
	//lex method to go through the string array elements one by one
	public static void lex(){
		a++;//each time lex() is invoked, currentToken and nextToken will be incremented by one and move to the next element in the string array
		currentToken=str1[a];
		if(a+1==str1.length){
			nextToken="end";//condition to avoid the nextToken goes out of bound when lex() reach the end of string array
		}else{
			nextToken=str1[a+1];
		}
	}

	public static void stmt_list(){		//<stmt_list>::<stmt>{<stmt>}
		lex();
		stmt();
		while(!nextToken.equals("end")){ //{<stmt>}
			idx=0;//clean idx and REGA for every statement
			REGA=0;
			lex();
			stmt();
		}
	}


	public static void stmt(){		//<stmt>::id=<expr>;
		int a;
		if(isAlpha(currentToken)){ //check if the first string array element of a statement is an ID(which is a letter)
			vars.add(currentToken);		//adding ID  variables initinalized at the beggining of each statement to the vars String array
			s[++idx]=currentToken;		//put the fist ID at str1[1]
			lex();				//check the second token in each statement
			if(currentToken.equals("=")){
				lex();
				a=expr();
				if(currentToken.equals(";")){//condition for the end of a statement
					GETA(a);
					System.out.println("MOV\t%EAX,"+s[idi]);//generate SIC/XE code
				}
			}
			return;
		}
	}

	private static void GETA(int i) {//method to generate SIC/XE code when register A is not used or generate temp vars to hold the value and do the arthmetic calculation when register is being use
		if (REGA==0){
			System.out.println("MOV\t" +s[i]+",%EAX");
		}else if(!s[i].equals("rA")){
			tCount++;
			vars.add("T"+tCount);
			System.out.println("MOV\t%EAX,T"+tCount);
			s[REGA]="T"+tCount;
			System.out.println("MOV\t" +s[i]+",%EAX");//generate SIC/XE code
		}
		s[i]="rA";//mark the string array element as being in the register A
		REGA=i;
	}

	private static int expr(){//<expr>::<term>{+<term>|-<term>}
		int b,c;
		b=term();//<term>
		while(currentToken.equals("+")||currentToken.equals("-")){
			String op=currentToken;
			lex();
			c=term();//{+<term>|-<term>}
			switch(op){
			case "+":
				if (s[b].equals("rA")){//if 1st operand is in the register A,ADD 2nd operand
					System.out.println("ADD\t"+s[c]);
				}else if(s[c].equals("rA")){//if 2nd operand is in the register A,ADD 1st operand
					System.out.println("ADD\t"+s[b]);
				}else{//if neither operand is in the register A, first operand will be put into regA and ADD 2nd operand
					GETA(b);
					System.out.println("ADD\t"+s[c]);//generate SIC/XE code
				}
				break;

			case "-":
				if (s[b].equals("rA")){//unlike addition, no mirror case
					System.out.println("SUB\t"+s[c]);
				}else{
					GETA(b);
					System.out.println("SUB\t" +s[c]);//generate SIC/XE code
				}
				break;
			default:
				break;
			}
			s[b]="rA";
			REGA=b;
		}
		return b;
	}


	private static int term() {//<term>::<factor>{*<factor>|/<factor>}
		int d,e;
		d=factor();//<factor>
		while(currentToken.equals("*")||currentToken.equals("/")){
			String op=currentToken;
			lex();
			e=factor();//{*<factor>|/<factor>}
			switch(op){
			case "*":
				if (s[d].equals("rA")){
					System.out.println("MUL\t"+s[e]);//generate SIC/XE code
				}else if(s[e].equals("rA")){
					System.out.println("MUL\t"+s[d]);
				}else{
					GETA(d);
					System.out.println("MUL\t" +s[e]);
				}
				break;
			case "/":
				if (s[d].equals("rA")){
					System.out.println("DIV\t"+s[e]);//generate SIC/XE code
				}else{
					GETA(d);
					System.out.println("DIV\t" +s[e]);
				}
				break;
			}
			s[d]="rA";//adjust the current string element as is in regA
			REGA=d;
		}
		return d;
	}

	private static int factor() {
		int f;
		if(isAlpha(currentToken)){//check if the operand is an ID(letter)
			s[++idx]=currentToken;	//add the currentToken to the next available position in the string
			lex();
			return idx;
		}else if(isInteger(currentToken)){//check if the operand is a number
			s[++idx]="#"+currentToken;
			lex();
			return idx;
		}else if(currentToken.equals("(")){//check for (expr)
			lex();
			f=expr();
			if(currentToken.equals(")")){
				lex();
			}else{
				System.out.println("ERROR");
				System.exit(0);
			}
			if(s[f].equals("rA")){
				REGA=f;
				return f;
			}
		}else{//Error if operand is not ID|NUM|(expr)
			System.out.println("ERROR");
			System.exit(0);
		}
		return 0;
	}


	//method to concatenate two strings together
	public static final String[] concat(String[] s1, String[] s2) {
		String[] s3 = new String[s1.length + s2.length];
		System.arraycopy(s1, 0, s3, 0, s1.length);
		System.arraycopy(s2, 0, s3, s1.length, s2.length);
		return s3;
	}

	//method to check if a String is letter which is used as an ID
	public static boolean isAlpha(String name) {
		return name.matches("[a-zA-Z]+");
	}
	public static boolean isInteger(String s) {
		return isInteger(s,10);
	}

//method to check if a String is a number
	public static boolean isInteger(String s, int radix) {
		if(s.isEmpty()) return false;
		for(int i = 0; i < s.length(); i++) {
			if(i == 0 && s.charAt(i) == '-') {
				if(s.length() == 1) return false;
				else continue;
			}
			if(Character.digit(s.charAt(i),radix) < 0) return false;
		}
		return true;
	}
}
