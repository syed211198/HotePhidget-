package Constructors;

public class ApplicationConstructor {
	String apiKey;
	String LabRoomName;
	String LabRoomStatus;
	boolean valid;
	
	public ApplicationConstructor(String apiKey) {
		super();
		this.apiKey = apiKey;
		LabRoomName = "Unknown";
		LabRoomStatus = "Unknown";
		this.valid = false;
	}
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getLabRoomName() {
		return LabRoomName;
	}

	public void setLabRoomName(String labRoomName) {
		LabRoomName = labRoomName;
	}

	public String getLabRoomStatus() {
		return LabRoomStatus;
	}

	public void setLabRoomStatus(String labRoomStatus) {
		LabRoomStatus = labRoomStatus;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	@Override
	public String toString() {
		return "ApplicationConstructor [apiKey=" + apiKey + ", LabRoomName=" + LabRoomName + ", LabRoomStatus="
				+ LabRoomStatus + ", valid=" + valid + "]";
	}
	
	

}
