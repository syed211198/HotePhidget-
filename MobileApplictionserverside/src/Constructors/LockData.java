package Constructors;

public class LockData {
	String LabRoomName;
	String LabRoomStatus;
	int motorSerialNo;
	int MotorPosition;
	boolean valid;
	
	
	public LockData(int motorSerialNo, int motorPosition) {
		super();
		this.LabRoomName ="Unknown";
		this.LabRoomStatus ="Unknown";
		this.motorSerialNo = motorSerialNo;
		this.MotorPosition = motorPosition;
		this.valid = false;
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


	public int getMotorSerialNo() {
		return motorSerialNo;
	}


	public void setMotorSerialNo(int motorSerialNo) {
		this.motorSerialNo = motorSerialNo;
	}


	public int getMotorPosition() {
		return MotorPosition;
	}


	public void setMotorPosition(int motorPosition) {
		MotorPosition = motorPosition;
	}


	public boolean isValid() {
		return valid;
	}


	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	@Override
	public String toString() {
		return "LockData [LabRoomName=" + LabRoomName + ", LabRoomStatus=" + LabRoomStatus + ", motorSerialNo="
				+ motorSerialNo + ", MotorPosition=" + MotorPosition + ", valid=" + valid + "]";
	}
	
}
