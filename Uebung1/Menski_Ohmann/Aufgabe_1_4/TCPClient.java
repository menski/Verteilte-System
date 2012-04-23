/**
 * TCP Client
 * 
 * @author Sebastian Menski (734272) <menski@uni-potsdam.de>
 * @author Martin Ohmann (734801) <ohmann@uni-potsdam.de>
 */
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.Arrays;

public class TCPClient {
	
	// define constants
	private static final int MN_MIN = 1;
	private static final int MN_MAX = 999999;
	private static final int MN_DIGITS = 6;
	private static final int SERVER_PORT = 10041;
	
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String message;
	private String[] messageParts;
	private String host;
	private String matriculationNumber;
	private int port;
	
 	public TCPClient(String host, String mn, int port) {
 		this.host = host;
 		this.matriculationNumber = mn;
 		this.port = port;
 	}
	
	public void run() {
		try {
			// create a socket to connect to the server
			clientSocket = new Socket(host, port);
			System.out.println("Connected to " + host + " on port " + port);
			
			// get input and output streams
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(clientSocket.getInputStream());
			
			// communicate with the server, send initial message
			sendMessage("1 "+ matriculationNumber);
			
			do {
				try {
					// read message from input stream
					message = (String)in.readObject();
					System.out.println("server>" + message);
					
					// split message
					messageParts = splitMessage(message);
					
					// send answer on expected message
					if (messageParts[0].equals("2")) {
						sendMessage("3 "+ messageParts[1] + " " + messageParts[2]);
					}
				}
				catch(ClassNotFoundException e) {
					System.err.println("Data received in unknown format");
				}
				catch(ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			} while(!messageParts[0].equals("2"));
		}
		catch(UnknownHostException e) {
			System.err.println("Connection failed: unknown host");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			// close connection once finished
			try {
				in.close();
				out.close();
				clientSocket.close();
				System.out.println("Connection closed");
			}
			catch(NullPointerException e) {
				e.printStackTrace();
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
	
	public static String[] splitMessage(String msg) {
		// split message on white spaces and return result as string array
		String[] tmp;
		tmp = msg.split(" ");
		
		return tmp;
		
	}
	
	private void sendMessage(String msg) {
		try {
			// try writing message to output stream
			out.writeObject(msg);
			out.flush();
			System.out.println("client>" + msg);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		// exit on missing arguments
		if (args.length < 2) {
			System.err.println("Usage: java TCPClient <host_address> <matriculation_number>");
            System.exit(-1);
		}
		
		int matNum = -1;
		
		try {
			matNum = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {}
		
		// exit if matriculation number is out of allowed bounds
		if (matNum < MN_MIN || matNum > MN_MAX) {
			System.err.println("Matriculation number has to be a positive integer in range [" + MN_MIN + "," + MN_MAX + "]");
			System.exit(-1);
		}
		
		int port = SERVER_PORT;
		
		// try to use custom port if given and valid, use default port otherwise
		if (args.length >= 3) {
			try {
				port = Integer.parseInt(args[2]);
			} catch(NumberFormatException e) {
				System.out.println("Invalid port: using default port (" + SERVER_PORT + ")");
			}
		}
		
		// init and run the client
		TCPClient client = new TCPClient(args[0], intToString(matNum, MN_DIGITS), port);
		client.run();
	}
}
