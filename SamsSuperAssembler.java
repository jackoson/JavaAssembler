import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class SamsSuperAssembler {

	private List<String> input;
	private List<String> output;
	private int address;
	private Hashtable<String, Integer> lables;
	
	public SamsSuperAssembler(String fileName) {
		try {
		      Path path = Paths.get(fileName);
		      input = Files.readAllLines(path, Charset.defaultCharset());
		    } catch (IOException e) {
		    	System.err.println(String.format("Your file %s cannot be found and/or read.", fileName));
		    }
	}

	
	public static void main(String[] args) {
		
		SamsSuperAssembler ssa = new SamsSuperAssembler(args[0]);
		System.out.println(ssa.assemble(args.length>1));
		
	}

	public String assemble(boolean lineNumbers) {
		
		output = new ArrayList<String>();
		//labeling
		address = 0;
		lables = new Hashtable<String, Integer>();
		for(String line: input){
			if(!line.equals(""))
	    		lineProccessLabels(line.trim());
	    }
		//instruction encoding
		for(String line: input){
			if(!line.equals(""))
				lineProccess(line.trim());
	    }
		return printOutLines(output, lineNumbers);
	}

	private void lineProccess(String line) {
		if(line.charAt(0) == ';'){
			return;
		}else if(line.charAt(0) == ':'){
			return;
		}else{
			output.add(getOppCode(line));
		}
	}


	private String getOppCode(String instruction) {
		String command = instruction.substring(0, 3);
		if(command.equals("INC")){
			return String.format("%s0", instruction.charAt(5));
		}else if(command.equals("DEC")){
			return String.format("%s0", (instruction.charAt(5)-'0')+2);
		}else if(command.equals("JNZ")){
			return String.format("4%s", getAddressHex(instruction.substring(4, instruction.length())));
		}else if(command.equals("JNEG")){
			return String.format("6%s", getAddressHex(instruction.substring(5, instruction.length())));
		}else if(command.equals("STR")){
			return String.format("8%s", getAddressHex(instruction.substring(4, instruction.length()))); 
		}else if(command.equals("LDR")){
			return String.format("A%s", getAddressHex(instruction.substring(4, instruction.length()))); 
		}
		return "";
	}


	private String getAddressHex(String text) {
		int address = getAddress(text);
		return Integer.toHexString(address);
	}


	public int getAddress(String address) {
		if(address.charAt(0) == ':'){
			return lables.get(address.substring(1));
		}else{
			return Integer.parseInt(address);
		}
	}


	private String toBinary(String address) {
		String one = Integer.toString(((address.charAt(0)-'0')/2));
		String two = Integer.toString(((address.charAt(0)-'0')%2));
		return one+two;
	}


	private String printOutLines(List<String> output, boolean lineNumbers) {
		int counter = 0;
		String out = "";
		if(lineNumbers){
			for(String line: output){
				out+=Integer.toHexString(counter)+": "+line+"\n";
				counter++;
			}
		}else{
			for(String line: output){
				out+=line+"\n";
			}
		}
		return out;
	}


	public void lineProccessLabels(String line) {
		if(line.charAt(0) == ';'){
			return;
		}else if(line.charAt(0) == ':'){
			lables.put(line.substring(1), address);
		}else{
			address++;
		}
		
	}

}
