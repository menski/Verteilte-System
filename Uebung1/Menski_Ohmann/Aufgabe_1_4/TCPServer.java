/**
 * TCP Server
 * 
 * @author Sebastian Menski (734272) <menski@uni-potsdam.de>
 * @author Martin Ohmann (734801) <ohmann@uni-potsdam.de>
 */
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.Arrays;

public class TCPServer {
	
	// define constants
	private static final int RANDOM_MIN = 0;
	private static final int RANDOM_MAX = 999999;
	private static final int RANDOM_NUM_DIGITS = 7;
	private static final int SERVER_PORT = 10041;
	
	private ServerSocket serverSocket;
	private Socket connection = null;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String message;
	private String[] messageParts;
	
	public TCPServer() {}
	
	public void run() {
		try{
			// create a server socket
			serverSocket = new ServerSocket(SERVER_PORT, 10);
			System.out.println("Opened socket on port " + SERVER_PORT);
			
			// wait for connection
			System.out.println("Waiting for connection");
			connection = serverSocket.accept();
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
			
			// get input and output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			
			// communicate with the client
			do {
				try {
					// read message from input stream
					message = (String)in.readObject();
					System.out.println("client>" + message);
					
					// split message
					messageParts = splitMessage(message);
					
					// generate random integer
					int randomInt = randomInt(RANDOM_MIN, RANDOM_MAX);
					
					// send answer on expected message
					if (messageParts[0].equals("1")) {
						sendMessage("2 " + messageParts[1] + " " + intToString(randomInt, RANDOM_NUM_DIGITS));
					}
				}
				catch(ClassNotFoundException e) {
					System.err.println("Data received in unknown format");
				}
				catch(ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			} while(!messageParts[0].equals("3"));
		}
		catch(IOException e) {
			e.printStackTrace();
		}	
		finally {
			// close connection once finished
			try {
				in.close();
				out.close();
				serverSocket.close();
				System.out.println("Connection closed");
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String intToString(int num, int digits) {
	    if (digits > 0) {
		    // create variable length array of zeros
		    char[] zeros = new char[digits];
		    Arrays.fill(zeros, '0');
		    
		    // format number as String
		    DecimalFormat df = new DecimalFormat(String.valueOf(zeros));
	
		    return df.format(num);
	    }
	    return null;
	}
	
	public static int randomInt(int min, int max) {
		// return random integer in range [min, max]
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	public static String[] splitMessage(String msg) {
		// split message on white spaces and return result as string array
		String[] tmp;
		tmp = msg.split(" ");
		
		return tmp;
		
	}
	
	private void sendMessage(String msg) {
		try{
			// try writing message to output stream
			out.writeObject(msg);
			out.flush();
			System.out.println("server>" + msg);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String args[]) {
		// init and run the server
		TCPServer server = new TCPServer();
		while(true){
			server.run();
		}
	}
}