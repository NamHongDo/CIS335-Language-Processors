package cis335project6;
//class that store the value of each register used to calculate object code
public class Registers {
	String registers;

	Registers(){

	}
	Registers(String registers){
		this.registers=registers;
	}

	public int getNumber(String registers){
		if(registers.equals("%EAX")){
			return 00;
		}else if(registers.equals("%ESX")){
			return 04;
		}else if(registers.equals("%ETX")){
			return 05;
		}else{
			return 01;
		}
	}
}
