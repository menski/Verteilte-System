package udpclient;

/**
 * @author Sebastian
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UDPClient {
  private int portNummer = 10041;
  private InetAddress serverIp;
  //  Hier die Sockets Eintragen
  ObjectOutputStream out;
  ObjectInputStream in;
  String message;
  
  public static int UNICAST = 1;
  public static int MULTICAST = 2;
  public static int BROADCAST = 3; 
  void run(String matrikelNR, int transmissionType, String ip, int port){
    portNummer = port;
    
    try {
      serverIp = InetAddress.getByName(ip);
    } catch (UnknownHostException ex) {
      Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    try {    
      if (transmissionType == UNICAST){
        //Hier die Netzwerk Verbindung einbauen
        System.out.println("SocketIP:"+serverIp.getHostAddress());
        //Socket erzeugen
      }

      else if (transmissionType == MULTICAST){
        //Socket erzeugen
      }

      else if (transmissionType == BROADCAST){
        //Socket erzeugen
	   }
    } catch (Exception e ){
        e.printStackTrace();
    }
    
    //Transmit messages
    try{      
        System.out.println("Sending Matrikelnumber");
        sendMessage("1 "+matrikelNR, transmissionType);
        String response = receiveMessage(transmissionType);
        System.out.println("Received Message: "+response);        
    } catch(IOException classNot){
        System.err.println("data received in unknown format");
    } 
    
    finally{
        //Verbindung(en) schlieﬂen              
    }
  }
    
  void sendMessage(String msg, int transmissionType) throws IOException {      
      byte[] buffer = msg.getBytes();
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverIp, portNummer+transmissionType+3);           
	  //Paket absenden
      System.out.println("Sending Message" + msg);
	}
    
    String receiveMessage(int transmissionType) throws IOException {     
      byte[] buffer = new byte[65000];
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      //Paket empfangen und daten auslesen
      return new String(buffer);
    }

  public static void main(String[] args) {    
    if (args[2]==null){
      System.out.println("Please start with Matrikelnummer Transmissiontype ip ports as parameter");
      return;
    }
    
    // First handle the input parameters
    String matrikelnummer = args[0];
    String transmissiontype = args[1];
    int transmission = -1;
    if (transmissiontype.equalsIgnoreCase("unicast"))   transmission = UDPClient.UNICAST;
    else if (transmissiontype.equalsIgnoreCase("multicast"))   transmission = UDPClient.MULTICAST;
    else if (transmissiontype.equalsIgnoreCase("broadcast"))   transmission = UDPClient.BROADCAST;
    else {
      System.out.println("Transmissiontype must be either UNICAST, MULTICAST or BROADCAST");
      return;
    }
    String ip;
    int port;
    //Parameter einlesen ggf. IP adressen setzen
    
	//Start the client
    UDPClient client = new UDPClient();	
    client.run(matrikelnummer, transmission, ip, port);
  }
}
