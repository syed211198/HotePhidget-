package Lock;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;


public class SubscriberCallBack implements MqttCallback {
	
	int motorPosition;
	
	static String LabRoomName;
	static String LabRoomStatus;
	
	static String lock_data_json;
	
	static Gson gson = new Gson();
	
	static LockData lockdata = new LockData(0,0);

	@Override
	
	public void connectionLost(Throwable cause) {
		
	}
	
	public void messageArrived(String topic, MqttMessage message) throws Exception{
		System.out.println("Message arrived " + message.toString()+ "  and Topic is : " + topic);
		
		motorPosition = LockMover.getLockPosition();
		
		if (motorPosition == 0) {
			
			lock_data_json = LockMover.moveServoTo(180.0);
			
			lockdata = gson.fromJson(lock_data_json, LockData.class);
    		
			LabRoomName = lockdata.getLabRoomName();
    		
			LabRoomStatus = lockdata.getLabRoomStatus();
    		
    		Subscriber.androidRoomStatus(LabRoomName, LabRoomStatus);
			
		}else {
			
			
			lock_data_json = LockMover.moveServoTo(0.0);
			
			lockdata = gson.fromJson(lock_data_json, LockData.class);
    		
			LabRoomName = lockdata.getLabRoomName();
    		
			LabRoomStatus = lockdata.getLabRoomStatus();
    		
    		Subscriber.androidRoomStatus(LabRoomName, LabRoomStatus);
			
		}
		
        
		
		
		
		if((Subscriber.LabRoomName+"/LWT").equals(topic)){
			
			System.err.println("Sensor has gone");
		}
	}
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
	}
}
