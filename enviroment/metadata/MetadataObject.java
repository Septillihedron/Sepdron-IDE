package enviroment.metadata;

import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;

public class MetadataObject {
	
	HashMap<MetadataType, String> typeval;
	
	public MetadataObject() {
		typeval=new HashMap<MetadataType, String>();
	}
	public MetadataObject(File file) {
		typeval=new HashMap<MetadataType, String>();
		
		StringBuilder sb=new StringBuilder();
		try(BufferedReader br=new BufferedReader(new FileReader(file))) {
			String line;
			while((line=br.readLine()) != null) sb.append(line+"\n");
		} catch (IOException e) {}
		
		fromString(sb.toString());
	}
	public MetadataObject(String text) {
		typeval=new HashMap<MetadataType, String>();
		fromString(text);
	}
	private void fromString(String text) {
		for(String line:text.split("\n")) {
			String[] props=line.split("[ ]?:[ ]?");
			String type=props[0];
			String val=props[1];
			typeval.put(MetadataType.fromString(type), val);
		}
	}
	
	public MetadataObject put(MetadataType type, String value) {
		typeval.put(type, value);
		return this;
	}
	public String get(MetadataType type) {
		return typeval.get(type);
	}
	public void save(File file) {
		try(PrintWriter pw=new PrintWriter(new FileWriter(file))) {
			for(HashMap.Entry<MetadataType, String> entry : typeval.entrySet()) {
				pw.println(entry.getKey().toString()+" : "+entry.getValue().toString());
			}
			pw.flush();
		} catch (IOException e) {}
	}
}