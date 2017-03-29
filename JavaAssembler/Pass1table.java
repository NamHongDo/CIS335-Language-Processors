package cis335project6;
import java.util.HashMap;
/*This class is used to aid in PASS 1, also to store Labels and their Address into a Hash table*/
public class Pass1Table {
	private String label1;
	private  String location1;
	private int count;
	private  HashMap<String,String> pass1Table=new HashMap<>();
	private  HashMap<Integer,String> addressTable=new HashMap<>();
	Pass1Table(){
	}

	public String getLabel1() {
		return label1;
	}

	public void setLabel1(String label) {
		this.label1 = label;
	}

	public String getLocation1() {
		return location1;
	}

	public void setLocation1(String location) {
		this.location1 = location;
	}

	public String getAddress(String label1){
		return pass1Table.get(label1);
	}
	public String getAddress1(int count1){
		return addressTable.get(count1);
	}

	public void pass1(String label, String location){//method to add labels and their address into a hash table
		pass1Table.put(label,location);
	}

	public void instAddress(int count, String location) {//method to add instruction address into hash table
		addressTable.put(count,location);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean containsKey(String destination) {//method to check the key is in the hash table
		if( pass1Table.containsKey(destination)){
			return true;
		}else{
			return false;
		}
	}



}
