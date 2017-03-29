package cis335project6;
//this class is used to contain instruction size for each instruction to calculate the address of each line
public class AddressCalculation {
	String mnemonic;
	int location;

	AddressCalculation(){
	}

	AddressCalculation(String mnemonic){
		this.mnemonic=mnemonic;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

	public int getInstructionSize(String mnemonic) {
		if(mnemonic!=null){
			if (mnemonic.charAt(0)=='+'){
				return 4;
			}else if((mnemonic.substring(mnemonic.length()-1).charAt(0))=='R'){
				return 2;
			}else if(mnemonic.equals("BASE")||mnemonic.equals("START")){
				return 0;
			}else if(mnemonic.equals("RESB")){
				return 4096;
			}else if(mnemonic.equals("BYTE")){
				return 1;
			}else{
				return 3;
			}
		}else{
			return 0;
		}
	}
}
