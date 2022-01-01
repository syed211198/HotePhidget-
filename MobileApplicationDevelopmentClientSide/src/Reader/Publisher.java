package Reader;


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class Publisher {
	
	public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
	
	public static String client_id;
	
	private static MqttClient client;

	public Publisher(String readerID) {

		client_id = readerID;
		
		System.out.println("Client ID is: " + client_id);

		try {
			
			client = new MqttClient(BROKER_URL, client_id);
			
			// Create MQTT Session
			
			MqttConnectOptions options = new MqttConnectOptions();
			
			options.setCleanSession(false);
			
			options.setWill(client.getTopic(client_id + "/LWT"), "I'm gone :(".getBytes(), 0, false);
			
			client.connect(options);
		
		} catch (MqttException e) {
			
			e.printStackTrace();
			
			System.exit(1);
		
		}
	
	}

	public void publishMotor(String LabRoomNumber) throws MqttException {

		String TOPIC_MOTOR = LabRoomNumber + "/motor";
		
		System.out.println("Published Topic: " + TOPIC_MOTOR);
		
		final MqttTopic motorTopic = client.getTopic(TOPIC_MOTOR);
		
		System.out.println("Publishing message : TESTER to topic: " + motorTopic.getName());
		
		final String motorMessage = " from Reader ";
		
		motorTopic.publish(new MqttMessage(motorMessage.getBytes()));
	
	}

	public void publishRFIDdetails(String LabRoomName, String notificationMessage) throws MqttException {

		String TOPIC_TAG = LabRoomName + "/tagNotification";
		
		System.out.println("Published Topic: " + TOPIC_TAG);
		
		final MqttTopic RFIDTopic = client.getTopic(TOPIC_TAG);
		
		System.out.println("Publishing message : TESTER to topic: " + RFIDTopic.getName());
		
		final String tagNotification = notificationMessage;
		
		RFIDTopic.publish(new MqttMessage(tagNotification.getBytes()));
	
	}
	
}