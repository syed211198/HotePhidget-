package Lock;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.Gson;
import com.phidget22.PhidgetException;
import com.phidget22.RCServo;

public class LockMover {

	static RCServo servo = null;
	
	private static String sensorServerURL = "http://localhost:8080/MobileApplictionserverside/Server";
	
	static int motorSerialNo;
	static int motorPosition;
	static String lockDataJson;
	static Gson gson = new Gson();
	
	static LockData lockdata = new LockData(0,0);
	
	public static RCServo getInstance() {
		
		if (servo == null) {
			
			servo = PhidgetMotorMover();
		}
			return servo;
		
	}
	
	private static RCServo PhidgetMotorMover() {
		 try {
			 
			servo = new RCServo();
			servo.open(2000);
			
		 }catch(PhidgetException e) {
			 e.printStackTrace();
		 }
		 return servo;
	}
	public static String moveServoTo(double position) throws MqttException {
		LockMover.getInstance();
		
		try {
			
			motorSerialNo = servo.getDeviceSerialNumber();
			
			motorPosition = (int) position;
			
			lockdata.setMotorSerialNo(motorSerialNo);
			
			lockdata.setMotorPosition(motorPosition);
			
			
			
			
			servo.setMaxPosition(180.0);
			servo.setTargetPosition(position);
			servo.setEngaged(true);
			
			lockDataJson = gson.toJson(lockdata);
			
			lockDataJson = sendToServer(lockDataJson);
			
			//Subscriber.androidRoomStatus();
			
		} catch (PhidgetException e) {
			e.printStackTrace();
		}
		return lockDataJson;
	}
	
	public static String sendToServer(String lockDataJson) {
	
	
    	
    	URL url;   	
    	HttpURLConnection conn;
		BufferedReader rd;
		
		try {
			lockDataJson = URLEncoder.encode(lockDataJson, "UTF-8");

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String fullURL = sensorServerURL + "?lockdata=" +lockDataJson;		
		String line;		
		String result = "";
		
		try {			
			url = new URL(fullURL);			
			conn = (HttpURLConnection) url.openConnection();			
			conn.setRequestMethod("GET");			
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			// Request response from server to enable URL to be opened
			while ((line = rd.readLine()) != null) {
				result += line;				
			}			
			rd.close();
			
		}catch (Exception e) {			
			e.printStackTrace();
			
		}
		return result;
    }

	public static int getLockPosition() {
		
		LockMover.getInstance();
		
		int motorPosition = 0;
		
		try {
			motorPosition = (int) servo.getPosition();
			
		} catch (PhidgetException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return motorPosition;
	}
}
