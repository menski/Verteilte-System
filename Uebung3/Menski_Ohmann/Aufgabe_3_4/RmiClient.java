/**
 * RMI Client
 * 
 * @author Sebastian Menski (734272) <menski@uni-potsdam.de>
 * @author Martin Ohmann (734801) <ohmann@uni-potsdam.de>
 */
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
 
public class RmiClient { 

	private static final int MN_MIN = 1;
	private static final int MN_MAX = 999999;
	private static final int MN_DIGITS = 6;
	private static final int REGISTRY_PORT = 1099;
	
    private RmiServerIntf stub = null;
    private String matriculationNumber;
    private String serverAddress;
    private String response;
    private String request; 
	private int registryPort; 	
 
 	public RmiClient(String mn, String serverAddr, int port) {
		matriculationNumber = mn;
 		serverAddress = serverAddr;
 		registryPort = port;
 	}
 	
 	public void run() {
        try { 
            stub = (RmiServerIntf)Naming.lookup("//" + serverAddress + ":" + registryPort + "/RmiServerImpl");
            
            request = "1 " + matriculationNumber;
            System.out.println("client> Sending "+request);
            response = stub.processRequest(request);
            System.out.println("client> Received "+response);
            
            if (response.substring(0,1).equals("2")) {
            	request = "3" + response.substring(1);
            	System.out.println("client> Sending "+request);
            	response = stub.processRequest(request);
            	System.out.println("client> Received "+response);
            }
        } catch (Exception e) { 
            System.err.println("RmiClient exception: " + e); 
            e.printStackTrace(); 
 
            System.err.println(e.getMessage());
        }  	
 	}
 
    public static void main(String args[]) {
        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        
		// exit on missing arguments
		if (args.length < 2) {
			System.err.println("Usage: java RmiClient <host_address> <matriculation_number> [<port>]");
			System.exit(-1);
		}
		
		int matNum = -1;
		
		try {
			matNum = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {}
		
		// exit if matriculation number is out of allowed bounds
		if (matNum < MN_MIN || matNum > MN_MAX) {
			System.err.println("Error: Matriculation number has to be a positive integer in range [" + MN_MIN + "," + MN_MAX + "]");
			System.exit(-1);
		}
		
		int port = REGISTRY_PORT;

		// try to use custom port if given and valid, use default port otherwise
		if (args.length >= 3) {
			try {
				port = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.out.println("Invalid port: using default port (" + REGISTRY_PORT + ")");
			}
		}

		// init and run the client   
		RmiClient rmiClient = new RmiClient(String.format("%06d", matNum), args[0], port);
		rmiClient.run();
    }
}
