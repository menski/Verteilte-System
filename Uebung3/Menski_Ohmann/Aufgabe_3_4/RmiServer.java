/**
 * RMI Server
 * 
 * @author Sebastian Menski (734272) <menski@uni-potsdam.de>
 * @author Martin Ohmann (734801) <ohmann@uni-potsdam.de>
 */
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.registry.*; 
 
public class RmiServer {

	private static final int REGISTRY_PORT = 10041;
	
	private int registryPort;

	public RmiServer(int port) {
		registryPort = port;
	}
	
	public void run() {
        try { //special exception handler for registry creation
            LocateRegistry.createRegistry(registryPort); 
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
        }
 
        try {
            //Instantiate RmiServerImpl
            RmiServerImpl stub = new RmiServerImpl();

            // Bind this object instance to the name "RmiServerImpl"
            Naming.rebind("//localhost:" + registryPort + "/RmiServerImpl", stub);

	        System.out.println("RMI server started");
            System.out.println("PeerServer bound in registry");
        } catch (Exception e) {
            System.err.println("RMI server exception:" + e);
            e.printStackTrace();
        }	
	}
 
    public static void main(String args[]) {
        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        
    	int port = REGISTRY_PORT;
    	
		if (args.length >= 1) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("Invalid port: using default port (" + REGISTRY_PORT + ")");
			}
		}
		
		RmiServer rmiServer = new RmiServer(port);
		rmiServer.run();
    }
}
