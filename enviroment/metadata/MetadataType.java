package enviroment.metadata;

public enum MetadataType {
	TYPE("TYPE"), MAINCLASS("MAINCLASS"), PACKAGE("PACKAGE");
	
	private String name;
	
	private MetadataType(String name) {
		this.name=name;
	}
	
	public String toString() {
		return name;
	}
	public static MetadataType fromString(String s) {
		for(MetadataType type : MetadataType.values()) {
			if(type.toString().equals(s)) return type;
		}
		return null;
	}
}