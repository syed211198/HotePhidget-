package ClientDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.Gson;

import Constructors.ApplicationConstructor;
import Constructors.LockData;
import Constructors.ReaderData;

public class DAO {
	
	Connection conn = null;
	Statement statement;
	Gson gson = new Gson();
	
	ResultSet resultSet;
	
	LockData lockdata = new LockData(0,0);
	
	ReaderData lastReaderData = new ReaderData(0,"Unknown");
	public void getConnection() {
		// so this will load the driver and establish the connection

		String user = "amjads";
		String password = "wrequinF9";
		String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/" + user;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.out.println(e);
		}
		// this gets a connection with the  users Username  and password

		try {
			conn = DriverManager.getConnection(url, user, password);
			// System.out.println("DEBUG: Connection to database successful.");
			statement = conn.createStatement();
		} catch (SQLException se) {
			System.out.println(se);
			//System.out.println("\nAlter the lines to set user/password in the sensor server code");
		}
	}

	private void closeConnection() {
		try {
			conn.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void destroy() {

		try {
			// add anything extra to do when servlet closes
		} catch (Exception e) {
			System.out.println(e);
		}

	}
	
	public String selectLabRoom(String lockDataJson) {
		
		lockdata = gson.fromJson(lockDataJson, LockData.class);
		
		String selectLabRoom = "SELECT LabRoomName, LabRoomStatus From ServoMotor " + 
				"INNER JOIN LabRoom ON ServoMotor.servoRoomID = LabRoom.LabRoomID " + 
				"WHERE ServoMotor.motorSerialNo = "+ lockdata.getMotorSerialNo() +";";
		
		try {
			
			getConnection();
			
			resultSet = statement.executeQuery(selectLabRoom);
			
			if(resultSet.next()) {
				
				String LabRoomName  = resultSet.getString("LabRoomName");
				
				String LabRoomStatus = updateLabRoomStatus(LabRoomName, lockdata.getMotorPosition());
				
				lockdata.setLabRoomName(LabRoomName);
				
				lockdata.setLabRoomStatus(LabRoomStatus);
				
				lockdata.setValid(true);
				
				lockDataJson = gson.toJson(lockdata);
				
			}else {
				
				lockdata.setValid(false);
				
				lockDataJson = gson.toJson(lockdata);
				
			}
			
		} catch (SQLException e) {
			
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			
		}
		
		closeConnection();
		
		return lockDataJson;
		
	}
	
	private String updateLabRoomStatus(String LabRoomName, int motorPosition) {
		
		String LabRoomStatus = " ";
		
		String updateLabRoomStatus = " ";
		
		if(motorPosition == 0) {
			
			LabRoomStatus = "LOCKED";
			
			updateLabRoomStatus = "UPDATE LabRoom SET LabRoomStatus = '" + LabRoomStatus + "', dateAttempted = now() WHERE LabRoomName = '" + LabRoomName + "';";
			
		} else {
			
			LabRoomStatus = "UNLOCKED";
			
			updateLabRoomStatus = "UPDATE LabRoom SET LabRoomStatus = '" + LabRoomStatus + "', dateAttempted= now() WHERE LabRoomName = '" + LabRoomName + "';";
			
		}
		
		return LabRoomStatus;
		
	}
public String validateTag(String ReaderDataJson) {
		
		ReaderData readerdata = new ReaderData(0, "Unknown");
		
		readerdata = gson.fromJson(ReaderDataJson, ReaderData.class);
		
		int readerSerialNumber = readerdata.getReaderSerialNo();
		
		String tagName = readerdata.getTagName();
		
		String validateSQL = "SELECT LabRoomName, tagID, ReaderID FROM RFIDTag " + 
							 "INNER JOIN ValidTags ON RFIDTag.tagID = ValidTags.ValidTagID " + 
							 "INNER JOIN LabRoom ON ValidTags.ValidRoomID = LabRoom.LabRoomID " + 
							 "INNER JOIN RFIDReader ON LabRoom.LabRoomID = RFIDReader.ReaderRoomID " + 
							 "WHERE RFIDTag.tagName = '" + tagName + "' AND RFIDReader.ReaderSerialNo = " + readerSerialNumber + ";";
		try {

			getConnection();
			
			resultSet = statement.executeQuery(validateSQL);

			if (resultSet.next()) {
				
				String LabRoomName = resultSet.getString("LabRoomName");
				
				int tagID = resultSet.getInt("tagID");
				
				int rfidReaderID = resultSet.getInt("ReaderID");
				
				readerdata.setLabRoomName(LabRoomName);
				
				readerdata.setValid(true);
				
				updateAtempts(rfidReaderID, tagID, readerdata.isValid());

				ReaderDataJson = updateSensorValues(readerdata);
				
				closeConnection();
				
			} else {
				
				readerdata.setValid(false);
				
				int tagID = selectTagID(readerdata.getTagName());
				
				int rfidReaderID = selectReader(readerdata.getReaderSerialNo());
				
				
				updateAtempts(rfidReaderID, tagID, readerdata.isValid());

				ReaderDataJson = updateSensorValues(readerdata);
				
				closeConnection();
			}

		} catch (SQLException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}
		
		return ReaderDataJson;
		
	}

	
	private void updateAtempts(int rfidReaderID, int tagID, boolean valid) {

		String updateAtempts = "";

		if (valid) {

			updateAtempts = "INSERT INTO Attempts(rfidReaderID, rfidTagID, tagStatus) "
						  + "VALUES (" + rfidReaderID + ", " + tagID + ", 'Valid Tag');";
					
					System.out.println("Attempt Updated successfully in the database!");

		} else {

			updateAtempts = "INSERT INTO Attempts(rfidReaderID, rfidTagID, tagStatus) "
						  + "VALUES (" + rfidReaderID + ", " + tagID + ", 'InValid Tag');";
			
					System.out.println("Attempt Updated unsuccessfully in the database!");

		}

		try {

			statement.executeUpdate(updateAtempts);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void insertTag(String tagName) {

		String insertTagNameSQL = "INSERT INTO RFIDTag(tagName) VALUES ('" + tagName + "');";

		try {
			
			statement.executeUpdate(insertTagNameSQL);
			
			System.out.println("Inserted successfully in the database! " + tagName);

		} catch (SQLException e) {
			
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			
		}
		
	}
	
	private String updateSensorValues(ReaderData readerdata) {

		lastReaderData = readerdata;
		
		String ReaderDataJson = gson.toJson(readerdata);
		
		return ReaderDataJson;
		
	}

	private int selectTagID(String tagName) {

		String validateTagName = "SELECT tagID FROM RFIDTag WHERE tagName = '" + tagName + "';";
		
		int tagID = 0;

		try {
			resultSet = statement.executeQuery(validateTagName);

			if (resultSet.next()) {

				tagID = resultSet.getInt("tagID");

			} else {

				insertTag(tagName);
				
				resultSet = statement.executeQuery(validateTagName);
				
				if (resultSet.next()) {

					tagID = resultSet.getInt("tagID");

				}
			}

		} catch (SQLException e) {
			
			// TODO Auto-generated catch block
			
			e.printStackTrace();

		}

		return tagID;
	}

	private int selectReader(int ReaderSerialNo) {

		String validateReaderSQL = "SELECT ReaderID FROM RFIDReader WHERE ReaderSerialNo = " + ReaderSerialNo + ";";
		
		int rfidReaderID = 0;
		
		try {

			resultSet = statement.executeQuery(validateReaderSQL);

			if (resultSet.next()) {

				rfidReaderID = resultSet.getInt("ReaderID");

			} else {

				insertReader(ReaderSerialNo);
				
				resultSet = statement.executeQuery(validateReaderSQL);
				
				if (resultSet.next()) {

					rfidReaderID = resultSet.getInt("ReaderID");

				}
			}

		} catch (SQLException e) {
			
			// TODO Auto-generated catch block
			
			e.printStackTrace();

		}

		return rfidReaderID;
	}
	
	private void insertReader(int ReaderSerialNo) {

		String insertReaderSQL = "INSERT INTO RFIDTag(ReaderSerialNo) VALUES (" + ReaderSerialNo + ");";

		try {

			statement.executeUpdate(insertReaderSQL);
			
			System.out.println("Inserted successfully in the database! " + ReaderSerialNo);

		} catch (SQLException e) {
			
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			
		}
		
	}
	
	public String getApplicationRoomStatus(String ApplicationJSON) {

		ApplicationConstructor appConstruct = new ApplicationConstructor("Unknown");
		appConstruct = gson.fromJson(ApplicationJSON, ApplicationConstructor.class);

		String applicationSQL = "SELECT LabRoomName, LabRoomStatus FROM Application "
				+ "INNER JOIN LabRoom ON Application.appID = LabRoom.RoomAppID " + "WHERE apiKey = '"
				+ appConstruct.getApiKey() + "';";
		String result = "";
		boolean valid = false;
		ResultSet resultSet;
		//"UPDATE LabRoom SET LabRoomStatus = 'Unlocked' WHERE LabRoomName = '" + lastSensor.getDoorID() + "';";
		try {
			getConnection();
			resultSet = statement.executeQuery(applicationSQL);

			if (resultSet.next()) {
				// this is used to check that the api key is correct
				result = appConstruct.getApiKey() + "Verified Api key";
				valid = true;
				appConstruct.setLabRoomName(resultSet.getString("LabRoomName"));
				appConstruct.setLabRoomStatus(resultSet.getString("LabRoomStatus"));
				appConstruct.setValid(valid);
				ApplicationJSON = gson.toJson(appConstruct);
				System.out.println("Room To Unlock: " + result);

			} else {
				// this else statement will tell you that you have the incorrect api key to open the door
				result = appConstruct.getApiKey() + "Unverified Api key";
				valid = false;
				appConstruct.setLabRoomName("");
				appConstruct.setLabRoomStatus("");
				appConstruct.setValid(valid);
				ApplicationJSON = gson.toJson(appConstruct);

				System.out.println("No Room has been Associated with this Lock: ");
				closeConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ApplicationJSON;
	}

}
