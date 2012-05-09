/**
 * UDP Server
 * 
 * @author Sebastian Menski (734272) <menski@uni-potsdam.de>
 * @author Martin Ohmann (734801) <ohmann@uni-potsdam.de>
 */
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Random;

public class UDPServer {
	
	public static final int GROUP_NR = 16;
	public static final int UNICAST = 1;
	public static final int MULTICAST = 2;
	public static final int BROADCAST = 3; 
	public static final int SERVER_PORT = 10041;
	public static final String MULTICAST_ADDRESS = "235.0.0.1";
	public static final String BROADCAST_ADDRESS = "255.255.255.255";
	
	private int port;
	
	private DatagramSocket unicastSocket;
	private MulticastSocket multicastSocket;
	private DatagramSocket broadcastSocket;
	
	private Thread unicastThread;
	private Thread multicastThread;
	private Thread broadcastThread;
	
	private UDPListenerRunnable unicastRunnable;
	private UDPListenerRunnable multicastRunnable;
	private UDPListenerRunnable broadcastRunnable;
	
	public UDPServer(int port) {    
		this.port = port;  
	}
	
	public void run() {
		try {                    
			//First create the UDP Sockets
			unicastSocket = new DatagramSocket (port+UNICAST+GROUP_NR);
			multicastSocket = new MulticastSocket (port+MULTICAST+GROUP_NR);
			multicastSocket.joinGroup(InetAddress.getByName(MULTICAST_ADDRESS));
			broadcastSocket = new DatagramSocket (port+BROADCAST+GROUP_NR);
			
			//Then create Runnables for each Socket, which will be executed within the threads
			unicastRunnable = new UDPListenerRunnable(unicastSocket, this, UNICAST);
			multicastRunnable = new UDPListenerRunnable(multicastSocket, this, MULTICAST);
			broadcastRunnable = new UDPListenerRunnable(broadcastSocket, this, BROADCAST);
			
			//Now create the listening Threads
			unicastThread = new Thread(unicastRunnable);
			multicastThread = new Thread(multicastRunnable);
			broadcastThread = new Thread(broadcastRunnable); 
			
			//Starting the listening Threads
			unicastThread.start();
			multicastThread.start();
			broadcastThread.start();          
			
			System.out.println("Just waiting for messages");
			//Just wait until the Server is closed          
			while (true) {}
		} catch (Exception e) {
			e.printStackTrace();
			//In this case stop the Threads and close the Sockets
			unicastRunnable.stopRunning();
			multicastRunnable.stopRunning();
			broadcastRunnable.stopRunning();
			
			unicastSocket.close();
			multicastSocket.close();
			broadcastSocket.close();
			return;
		} finally {
			//Stop the Threads and close the Sockets
			unicastRunnable.stopRunning();
			multicastRunnable.stopRunning();
			broadcastRunnable.stopRunning();
			
			unicastSocket.close();
			multicastSocket.close();
			broadcastSocket.close();
		} 	  
	}
	
	//If a message was received over one Socket the Thread will call this function handling the connection type and the received packet
	public void receivedMessage(int type, DatagramPacket packet) {               
		String message = new String(packet.getData());
		System.out.println("received message:" + message);
		
		if (message.substring(0,1).equals("1") && message.length() >= 8) {
			//handle the Matrikelnummer
			String matrikelnummer = message.substring(2,8);  
			
			//Calculate the random value
			Random random = new Random();
			int ran = random.nextInt(9999999);
			String ranS = String.valueOf(ran);
			
			while (ranS.length() < 7){
				ranS = "0" + ranS;
			}
			//Send the message
			sendMessage("2 " + matrikelnummer + " " + ranS, type, packet);          
		}    
	}
	
	//Sends Messages to clients
	public void sendMessage(String msg, int type, DatagramPacket inPacket) {     
		try {
			byte[] buffer = msg.getBytes();
			System.out.println("Sending Message" + msg);
			
			//Prepare the DatagramPacket
			DatagramPacket packet;
			if (type == UNICAST) {
				packet = new DatagramPacket(buffer, buffer.length, inPacket.getAddress(), inPacket.getPort());
			} else if (type == MULTICAST) {
				packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(MULTICAST_ADDRESS), inPacket.getPort());
			} else {
				packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(BROADCAST_ADDRESS), inPacket.getPort());
			}
			
			(new DatagramSocket()).send(packet);
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java UDPServer <port>");
			System.exit(-1);
		}
		
		int serverPort = 0;
		
		try {
			serverPort = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {}
		
		if (serverPort <= 0 || serverPort > 65535) {
			System.out.println("Invalid port: using default port (" + SERVER_PORT + ")");
			serverPort = SERVER_PORT;
		}    
		
		UDPServer server = new UDPServer(serverPort);	 
		server.run();
	}
	
}
