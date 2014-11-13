package se.teamgejm.safesend.enums;

public enum MessageType {
	
	TEXT("Text");
	
	private String niceName;
	
	private MessageType(String niceName) {
		this.niceName = niceName;
	}

	public String getNiceName() {
		return niceName;
	}

	public void setNiceName(String niceName) {
		this.niceName = niceName;
	}

}
