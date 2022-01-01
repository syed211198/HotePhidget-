package Constructors;

public class ReaderData {
	int ReaderSerialNo;
	String tagName;
	String LabRoomName;
	boolean valid;
	
	public ReaderData(int readerSerialNo, String tagName) {
		super();
		this.ReaderSerialNo = readerSerialNo;
		this.tagName = tagName;
		this.LabRoomName ="unknown";
		this.valid = false;
		
	}
	public int getReaderSerialNo() {
		return ReaderSerialNo;
	}

	public void setReaderSerialNo(int readerSerialNo) {
		ReaderSerialNo = readerSerialNo;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getLabRoomName() {
		return LabRoomName;
	}

	public void setLabRoomName(String labRoomName) {
		LabRoomName = labRoomName;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	@Override
	public String toString() {
		return "ReaderData [ReaderSerialNo=" + ReaderSerialNo + ", tagName=" + tagName + ", LabRoomName=" + LabRoomName
				+ ", valid=" + valid + "]";
	}

}
