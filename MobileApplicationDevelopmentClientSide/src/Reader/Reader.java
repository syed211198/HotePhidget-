package Reader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.Gson;
import com.phidget22.PhidgetException;
import com.phidget22.RFID;
import com.phidget22.RFIDTagEvent;
import com.phidget22.RFIDTagListener;
import com.phidget22.RFIDTagLostEvent;
import com.phidget22.RFIDTagLostListener;

public class Reader {

	RFID rfid = new RFID();
	
	ReaderData readerdata = new ReaderData(0,"Unknown");
	
	Gson gson = new Gson();
	
	String readerDataJson;
	
	private static String sensorServerURL = "http://localhost:8080/MobileApplictionserverside/Server";
	
	public static Publisher publisher;
	
	public static String client_id;
	
	public static void main(String[] args) throws PhidgetException {
		
		new Reader();
	
	}
	
	public Reader() throws PhidgetException {
		
		rfid.addTagListener(new RFIDTagListener() {
			
			public void onTag(RFIDTagEvent e) {

				try {
					
					String tagName = e.getTag();
					
					int ReaderSerialNumber = rfid.getDeviceSerialNumber();
					
					client_id = Integer.toString(rfid.getDeviceSerialNumber());
					
					publisher = new Publisher(client_id);
					
					readerdata.setTagName(tagName);
					
					readerdata.setReaderSerialNo(ReaderSerialNumber);

				} catch (PhidgetException e1) {
					
					// TODO Auto-generated catch block
					
					e1.printStackTrace();
				
				}
				
				readerDataJson = gson.toJson(readerdata);
				
				sendToServer(readerDataJson);
			
			}

		});
		
		rfid.addTagLostListener(new RFIDTagLostListener() {
			
			public void onTagLost(RFIDTagLostEvent e) {
				
				System.out.println("Tag lost: " + e.getTag());
				
			}
			
		});
		
		rfid.open(5000);
		
		try {
			
			System.out.println("\n\nGathering data for 30 seconds\n\n");
			
			pause(30);
			
			rfid.close();
			
			System.out.println("\nClosed RFID Reader");
			
		} catch (PhidgetException ex) {
			
			System.out.println(ex.getDescription());
			
		}
		
	}
	
	public String sendToServer(String readerDataJson) {

		URL url;
		
		HttpURLConnection conn;
		
		BufferedReader rd;

		try {
		
			readerDataJson = URLEncoder.encode(readerDataJson, "UTF-8");
		
		} catch (UnsupportedEncodingException e1) {
			
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
	
		}

		String fullURL = sensorServerURL + "?RFIDConstructor=" + readerDataJson;
		
		System.out.println("Sending data to: " + fullURL);
		
		String line;
		
		String result = "";

		try {
		
			url = new URL(fullURL);
			
			conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("GET");
			
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			while ((line = rd.readLine()) != null) {
				
				result += line;
				
				readerdata = gson.fromJson(result, ReaderData.class);
				

				if (readerdata.isValid()) {

					try {
						
						String LabRoomName = readerdata.getLabRoomName();
						
						String tagNotification = "Valid attempt made using Tag: " + readerdata.getTagName();
						
						publisher.publishMotor(LabRoomName);
						
						publisher.publishRFIDdetails(LabRoomName, tagNotification);
						

						try {
							
							Thread.sleep(5000);
							
						} catch (InterruptedException e) {
							
							e.printStackTrace();
							
						}
						
					} catch (MqttException e) {
						
						System.out.println("Caller publish error");
						
						e.printStackTrace();
					}
					
				} else {
					
					String LabRoomName = readerdata.getLabRoomName();
					
					String tagNotification = "Invalid attempt made using Tag:" + readerdata.getTagName();
					
					publisher.publishRFIDdetails(LabRoomName, tagNotification);
					
				}

			}
			
			rd.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		return result;
		
	}
	
	private void pause(int secs) {

		try {
			
			Thread.sleep(secs * 1000);
		
		} catch (InterruptedException e1) {
			
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
		
		}
		
	}
	
}