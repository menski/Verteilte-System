/**
 * RMI Server Stub Implementation
 * 
 * @author Sebastian Menski (734272) <menski@uni-potsdam.de>
 * @author Martin Ohmann (734801) <ohmann@uni-potsdam.de>
 */
import java.rmi.*;
import java.rmi.server.*;
import java.util.Random;
 
public class RmiServerImpl extends UnicastRemoteObject implements RmiServerIntf {
 
    public RmiServerImpl() throws RemoteException {
    }
    
    public String processRequest(String request) throws RemoteException {
    	System.out.println("server> Received "+request);
    	String response = "done";
    	if (request.substring(0,1).equals("1")) {
    		response = "2" + request.substring(1) + " " + randomNumber();
    	}
    	System.out.println("server> Sending "+response);
    	return response;
    }
    
    public String randomNumber() throws RemoteException {
		// calculate the random value
		Random random = new Random();
		int ran = random.nextInt(9999999);
		String randomString = String.valueOf(ran);
		
		while (randomString.length() < 7){
			randomString = "0" + randomString;
		}
		return randomString;
    }
}
