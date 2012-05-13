/**
 * UDP Client
 * 
 * @author Sebastian Menski (734272) <menski@uni-potsdam.de>
 * @author Martin Ohmann (734801) <ohmann@uni-potsdam.de>
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UDPClient {

	public static final int GROUP_NR = 16;
	public static final int UNICAST = 1;
	public static final int MULTICAST = 2;
	public static final int BROADCAST = 3; 
	public static final int MN_MIN = 1;
	public static final int MN_MAX = 999999;
	public static final int CLIENT_PORT = 10041;
	public static final String DEFAULT_ADDRESS = "127.0.0.1";
	public static final String BROADCAST_ADDRESS = "255.255.255.255";
	
	private int port;
	private int transmissionType;
	
	private String matriculationNumber;
	private String message;
	
	private InetAddress serverIp;
	
	private DatagramSocket unicastSocket;
	private MulticastSocket multicastSocket;
	private DatagramSocket broadcastSocket;
	
	public UDPClient(String mn, int transmissionType, String ip, int port) {
		this.port = port;
		this.transmissionType = transmissionType;
		this.matriculationNumber = mn;
		
		try {
			this.serverIp = InetAddress.getByName(ip);
		} catch (UnknownHostException ex) {
			System.err.println("Error: unknown host");
			System.exit(-1);
		}
	}
	
	public void run() {
		try { 
			// init socket  
			if (transmissionType == UNICAST) {
				unicastSocket = new DatagramSocket();
			} else if (transmissionType == MULTICAST) {
				multicastSocket = new MulticastSocket(port+MULTICAST+GROUP_NR);
				multicastSocket.joinGroup(serverIp);
			} else if (transmissionType == BROADCAST) {
				broadcastSocket = new DatagramSocket();
			}
			// transmit message
			message = "1 " + matriculationNumber;
			sendMessage(message, transmissionType);
			
			// receive message
			receiveMessage(transmissionType);  
		} catch (Exception e) {
			System.err.println("Error: unable to open socket");
			e.printStackTrace();
		} finally {
			// close connection    
			try {
				if (transmissionType == UNICAST){
					unicastSocket.close();
				} else if (transmissionType == MULTICAST){
					multicastSocket.close();    		
					multicastSocket.leaveGroup(serverIp);
				} else if (transmissionType == BROADCAST){
					broadcastSocket.close();
				}
			} catch (Exception e) {}
		}
	}
	
	private void sendMessage(String msg, int type) {   
		try {   
			byte[] buffer = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverIp, port+type+GROUP_NR);           
		
			// send packet
			if (type == UNICAST) {
				unicastSocket.send(packet);
			} else if (type == MULTICAST) {
				multicastSocket.send(packet);
			} else if (type == BROADCAST) {
				// use broadcast address here
				packet.setAddress(InetAddress.getByName(BROADCAST_ADDRESS));
				broadcastSocket.send(packet);
			}
		
			System.out.println("client> Sending Message: " + msg);
		} catch (IOException e) {
			System.err.println("Error: unable to send message");
		}
	}
	
	private void receiveMessage(int type) {    
		try { 
			byte[] buffer = new byte[65000];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
			// receive packet and contents
			if (type == UNICAST) {
				unicastSocket.receive(packet);
			} else if (type == MULTICAST) {
				// wait for server response, omit "own" message
				String msg;
				do {
					multicastSocket.receive(packet);
					msg = new String(buffer);
				} while (!msg.substring(0,1).equals("2") && msg.length() >= 8);
			} else if (type == BROADCAST) {
				broadcastSocket.receive(packet);
			}
			String response = new String(buffer);
		
			System.out.println("client> Received Message: " + response); 
			System.out.println("client> == MATRICULATION NUMBER: " + response.substring(2,8));
			System.out.println("client> == RANDOM NUMBER: " + response.substring(9,16));
		} catch (IOException e) {
			System.err.println("Error: unable to read received message");
		}		
	}
	
	public static boolean validateIpAddress(final String ip) {
		// pattern to validate ip-addresses
		final String IP_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		
		Pattern pattern = Pattern.compile(IP_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		
		return matcher.matches();             
	} 
	
	public static void usageAndExit() {
		System.err.println("Usage: java UDPClient <matriculation_number> [unicast|multicast] <ip_address> <port>");
		System.err.println("   or  java UDPClient <matriculation_number> broadcast <port>");
		System.exit(-1);
	}
	
	public static void main(String[] args) {  
		// validate args
		if (args.length < 3) {
			usageAndExit();
		}
		
		String transmissionType = args[1];
		String ipAddress;
		String port;
		
		// since broadcast uses one arg less, we have to check for it
		if (transmissionType.equalsIgnoreCase("broadcast")) {
			ipAddress = BROADCAST_ADDRESS;
			port = args[2];
		} else {
			if (args.length < 4) {
				usageAndExit();
			}
			ipAddress = args[2];
			port = args[3];
		}
		
		int clientPort = -1;
		int transmission = -1;
		int matNum = -1;
		
		// validate matriculation number
		try {
			matNum = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {}
		
		if (matNum < MN_MIN || matNum > MN_MAX) {
			System.err.println("Error: Matriculation number has to be a positive integer in range [" + MN_MIN + "," + MN_MAX + "]");
			System.exit(-1);
		}    
		
		// validate transmission type
		if (transmissionType.equalsIgnoreCase("unicast")) {
			transmission = UDPClient.UNICAST;
		} else if (transmissionType.equalsIgnoreCase("multicast")) {
			transmission = UDPClient.MULTICAST;
		} else if (transmissionType.equalsIgnoreCase("broadcast")) {
			transmission = UDPClient.BROADCAST;
		} else {
			usageAndExit();
		}
		
		// validate ip-address
		if (validateIpAddress(ipAddress) == false) {
			System.out.println("Invalid IP-address: using default address (" + DEFAULT_ADDRESS + ")");
			ipAddress = DEFAULT_ADDRESS;
		}
		
		// validate port
		try {
			clientPort = Integer.parseInt(port);
		} catch (NumberFormatException e) {}
		
		if (clientPort <= 0 || clientPort > 65535) {
			System.out.println("Invalid port: using default port (" + CLIENT_PORT + ")");
			clientPort = CLIENT_PORT;
		}
		
		// start the client
		UDPClient client = new UDPClient(Integer.toString(matNum), transmission, ipAddress, clientPort);	
		client.run();
	}
}
