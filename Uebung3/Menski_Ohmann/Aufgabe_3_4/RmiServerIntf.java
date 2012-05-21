/**
 * RMI Server Stub Interface
 * 
 * @author Sebastian Menski (734272) <menski@uni-potsdam.de>
 * @author Martin Ohmann (734801) <ohmann@uni-potsdam.de>
 */
import java.rmi.*;
 
public interface RmiServerIntf extends Remote {

    public String processRequest(String request) throws RemoteException;
    public String randomNumber() throws RemoteException;
    
}
