/*Name: Nam Do
 * CIS335 proj 6: write a simple assembler that read a sample sic/xe code and output 
 * an lst file and obj file*/

package cis335project6;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Assembler {
	static String mnemonic;
	static int op; static int first2;
	private static File sample,obj,lst;
	static int location=0000;static int nxtloc=0000;
	static Pass1Table pass1Table=new Pass1Table(); // hash table for storing label and corresponding address
	static Pass1Table addressTable=new Pass1Table();
	static OCCalculation ObjectCodeTable=new OCCalculation();
	static ArrayList<String[]> e = new ArrayList<String[]>(); //array list for storing lines from main.asm file 
	static ArrayList<String>d=new ArrayList<String>();
	static OCCalculation sol=new OCCalculation();
	static StringBuilder OBJcode = new StringBuilder();
	static Registers register=new Registers();
	public static void main(String[]args) throws Exception{
		ArrayList<String> list = new ArrayList<String>();
		sample = new File(args[0]);
		obj = new File(sample.getName().substring(0, sample.getName().lastIndexOf('.')) + ".obj");
		lst = new File(sample.getName().substring(0, sample.getName().lastIndexOf('.')) + ".lst");
		BufferedReader asmLines= new BufferedReader(new FileReader(new File(args[0])));	
		String stringsToBeChecked;	int count =1;
		while((stringsToBeChecked=asmLines.readLine())!=null){ //looping through each line in main.asm and add to the a String array until we reach the end of file
			list.add(stringsToBeChecked);
			String[] str1= stringsToBeChecked.trim().split("\\s+");
			e.add(str1);
			/*
			 * 				PASS 1					*
			 * */
			for(int i=0;i<str1.length;i++){
				if(str1[i].equals(";")){
					if (str1[0].equals(";")){
						str1=null;
						break;
					}
					else
					{
						str1=Arrays.copyOfRange(str1, 0, i);
						break;
					}
				}else if(str1[i].equals("MOV")||str1[i].equals("+MOV")){		//translating MOV instruction into appropriate SIC/XE instruction
					if((str1[i+1].charAt(0))==('%')){
						if(str1[i+1].charAt(2)=='L'){
							str1[i]="STL";
							str1[i+1]=str1[i+1].substring(5);
						}else if(str1[i+1].charAt(2)=='A'){
							str1[i]="STA";
							str1[i+1]=str1[i+1].substring(5);
						}else{
							str1[i]="STX";
							str1[i+1]=str1[i+1].substring(5);		
						}
					}else if(str1[i].equals("+MOV")){
						str1[i]="+STT";
						str1[i+1]=str1[i+1].substring(0,str1[i+1].indexOf(','));	
					}else{
						if((str1[i+1].substring(str1[i+1].length()-4)).charAt(2)=='B'){
							str1[i]="LDB";
							str1[i+1]=str1[i+1].substring(0,str1[i+1].indexOf(','));

						}else if((str1[i+1].substring(str1[i+1].length()-4)).charAt(2)=='A'){
							str1[i]="LDA";
							str1[i+1]=str1[i+1].substring(0,str1[i+1].indexOf(','));	
						}else if((str1[i+1].substring(str1[i+1].length()-4)).charAt(2)=='D'){
							str1[i]="LDX";
							str1[i+1]=str1[i+1].substring(0,str1[i+1].indexOf(','));
						}else{
							str1[i]="LDT";
							str1[i+1]=str1[i+1].substring(0,str1[i+1].indexOf(','));
						}
					}
				}	
			}
			if (str1!=null){
				if(str1.length==3){			//locate the Mnemonic in each line
					mnemonic=str1[1];
				}
				else if( str1.length==2)
				{
					mnemonic=str1[0];
				}
				else
				{
					mnemonic=str1[0];
				}
				AddressCalculation loc=new AddressCalculation(mnemonic);
				if(count == 5){	//Calculate address for each line
					location=6;
					nxtloc=location+4;
				}else{
					if(mnemonic.equals("BYTE") && str1[0].equals("EOF")){
						location=nxtloc;
						nxtloc=location+3;
					}else{
						location=nxtloc;
						nxtloc=location+loc.getInstructionSize(mnemonic);
					}
				}

				String loc1= (Integer.toHexString(location)).toUpperCase();
				if(!str1[0].equals("END"))
					addressTable.instAddress(count,loc1);	//storing location address for each line in the hash table
				count++;

				if(str1!=null){
					if(str1.length==3){						// all lines with length of 3 contain the label
						pass1Table.pass1(str1[0],loc1);		//storing Labels and their location address in the hash table
					}
				}
			}
		}
		asmLines.close();

		/**
		 *PASS 2*
		 **/
		int count1=2; int count2;
		for(int i=2; i<e.size()-1;i++){     
			String Mnemonic;
			String destination;
			if(e.get(i).length==3){				//identify which String array element is Mnemonic and argument
				Mnemonic=e.get(i)[1];
				destination=e.get(i)[2];
			}else if(e.get(i).length==2){	
				Mnemonic=e.get(i)[0];
				destination=e.get(i)[1];
			}else{
				Mnemonic=e.get(i)[0];
				destination=Mnemonic;
			}
			//1st 2 NUMBER (handling OP code and ni flags//
			if(Mnemonic.equals("CLEAR")||Mnemonic.equals("COMPR")||Mnemonic.equals("TIXR")){
				first2=sol.OPcode(Mnemonic);
			}else if(destination.charAt(0)=='#'){
				first2=sol.OPcode(Mnemonic)+0x01;	
			}else if(destination.charAt(0)=='@'){
				first2=sol.OPcode(Mnemonic)+0x02;	
			}else{
				first2=sol.OPcode(Mnemonic)+0x03;
			}

			///FLAGs////
			int flag;
			if(Mnemonic.charAt(0)=='+'){//extended mode
				flag=0x01;
			}else if(destination.equals("LENGTH")&&Long.parseLong(addressTable.getAddress1(count1),16)>=Long.parseLong(pass1Table.getAddress("LENGTH"),16)){
				flag=0x04;
			}else if(destination.equals("BUFFER[%EXX]")){
				flag=0x0C;
			}else if(destination.charAt(0)=='#'||Mnemonic.equals("RSUB")){
				flag=0x00;
			}else{
				flag=0x02;
			}

			count2=count1; //used as hashtable keys to keep track of object code values
			if(count1<e.size()-1){
				count1++;
			}else{
				count1=count2;
			}
			//Calculating Displacement
			long displacement;
			if(destination.charAt(0)==('#')||destination.charAt(0)=='@'){
				destination=destination.substring(1);}
			//get the address of Labels from Pass1 table to calculate displacement
			if(Character.isDigit((destination.charAt(0)))){	
				displacement=(Long.parseLong(destination));
			}else if(flag==0x04){	//extended mode
				displacement=000;
			}else if(Mnemonic.charAt(0)=='+'){		
				displacement=(Long.parseLong(pass1Table.getAddress(destination),16));
			}else if(Mnemonic.equals("STCH")||Mnemonic.equals("LDCH")){
				displacement=(Long.parseLong(pass1Table.getAddress("BUFFER"),16)-Long.parseLong(pass1Table.getAddress("LENGTH"),16));
			}else if(pass1Table.containsKey(destination)){
				displacement=(Long.parseLong(pass1Table.getAddress(destination),16)-Long.parseLong(addressTable.getAddress1(count1),16));			
			}else if(Mnemonic.equals("CLEAR")||Mnemonic.equals("TIXR")){
				displacement=register.getNumber(destination);
			}else{
				displacement=0;
			}	
			//Append op code, flag and displacement into Object code
			if(Mnemonic.equals("CLEAR")||Mnemonic.equals("TIXR")){
				OBJcode.append((Integer.toHexString(first2))).append(Long.toHexString(displacement));// use String builder to easily manipulate strings
			}else if(Mnemonic.equals("BYTE")&&destination.equals("C'EOF'")){	
				OBJcode.append("454F46");
			}else if(destination.equals("X'F3'")){
				OBJcode.append("F3");	
			}else if(destination.equals("X'05'")){
				OBJcode.append("05");
			}else if(destination.charAt(0)=='%'&&destination.length()>4){	//case when there are two registers
				String[] registers=destination.split(",");
				String register1=registers[0];
				String register2=registers[1];
				OBJcode.append((Integer.toHexString(first2))).append((Integer.toHexString(register.getNumber(register1)))).append((Integer.toHexString(register.getNumber(register2))));
			}else if(Mnemonic.charAt(0)=='+'){
				OBJcode.append((Integer.toHexString(first2))).append(Integer.toHexString(flag)).append('0').append(Long.toHexString(displacement));
			}else if(Long.toHexString(displacement).length()==1){
				OBJcode.append((Integer.toHexString(first2))).append(Integer.toHexString(flag)).append("00").append(Long.toHexString(displacement));
			}else if(Long.toHexString(displacement).charAt(0)=='f'){
				OBJcode.append((Integer.toHexString(first2))).append(Integer.toHexString(flag)).append((Long.toHexString(displacement).substring((Long.toHexString(displacement)).length()-3)));
			}else{
				OBJcode.append((Integer.toHexString(first2))).append(Integer.toHexString(flag)).append('0').append(Long.toHexString(displacement));
			}
			String objcode;		
			if(Mnemonic.equals("RESW")||Mnemonic.equals("RESB")){//case for directives
				objcode=null;
			}else{
				objcode=OBJcode.toString().toUpperCase();}	
			OBJcode.delete(0,OBJcode.length());
			ObjectCodeTable.OBJcodeinput(count2,objcode);
		}
		PrintWriter lstout = new PrintWriter(lst,"UTF-8");
		for(int i=0; i<list.size();i++){ //use loop to write line with address, instruction and object code in a lst file
			if(addressTable.getAddress1(i)==null&&ObjectCodeTable.getObjectCode(i)==null){
				lstout.write("          "+list.get(i)+"\n");
			}else if(i==4){
				lstout.write("       "+list.get(i)+"\n");
			}else if(ObjectCodeTable.getObjectCode(i)==null){
				lstout.write(addressTable.getAddress1(i)+"      "+list.get(i)+"\n");
			}else{
				lstout.write(addressTable.getAddress1(i)+"      "+list.get(i)+"         "+ObjectCodeTable.getObjectCode(i)+"\n");
			}
		}
		lstout.close();
//		PrintWriter objout = new PrintWriter(obj);
		//			lstout.write(s);
	}

}



