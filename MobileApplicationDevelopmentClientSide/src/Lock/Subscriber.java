package Lock;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import com.google.gson.Gson;

public class Subscriber {
	private static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
	
	static int  motorSerialNo;
	static String LabRoomName;
	static String LabRoomStatus;
	static String lockDataJson;
	
	String clientId = LabRoomName + "-sub";
	
	private static MqttClient mqttClient;
	
	static Gson gson = new Gson();
	
	public Subscriber() {

        try {
            mqttClient = new MqttClient(BROKER_URL, clientId);           
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }  
    }
	public static void main(String... args) {    	
		try {
			
			lockDataJson = LockMover.moveServoTo(0.0) ;
			
			LockData lockdata = new LockData(0,0);
			
			lockdata = gson.fromJson(lockDataJson, LockData.class);
			
			LabRoomName = lockdata.getLabRoomName();
			   	 	
		    
			if(lockdata.isValid()) {
				
				LabRoomName = lockdata.getLabRoomName();
				
				LabRoomStatus = lockdata.getLabRoomStatus();
				
				final Subscriber subscriber = new Subscriber();
				
				subscriber.start(LabRoomName, LabRoomStatus);
				
			}else {
				
				System.out.println("Servo " + lockdata.getMotorSerialNo() + " does not exist in Database.");
				
			}
			
		} catch (MqttException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		    
	}
	
	public void start(String LabRoomName, String LabRoomStatus) {
    	
        try {
            mqttClient.setCallback(new SubscriberCallBack());
            mqttClient.connect();
            //This will Subscribe to the  correct topic
            final String motortopic = LabRoomName + "/motor";           
            mqttClient.subscribe(motortopic);
            System.out.println("Subscriber is now listening to " + motortopic);
            androidRoomStatus(LabRoomName, LabRoomStatus);
            
        } catch (MqttException e) {        	
            e.printStackTrace();            
            System.exit(1);            
        }
        
    }
	
	public static void androidRoomStatus(String LabRoomName, String LabRoomStatus) throws MqttException {
		
		String TOPIC_ROOM_STATUS = LabRoomName + "/statusNotification";
		
		final MqttTopic topic_room_status = mqttClient.getTopic(TOPIC_ROOM_STATUS);
		
		final String Message = LabRoomStatus;
		
		topic_room_status.publish(new MqttMessage(Message.getBytes()));
	
	}


}
