package udpserver;
/**
 *
 * @author Sebastian
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Random;

public class UDPServer {
  public static int UNICAST = 1;
  public static int MULTICAST = 2;
  public static int BROADCAST = 3; 
  
  int portNumber = 10041;
  DatagramSocket unicastSocket;
  //Socket für Multicast und Broadcast definieren
  Thread unicastThread;
  Thread multicastThread;
  Thread broadcastThread;
  UDPListenerRunnable unicastRunnable;
  UDPListenerRunnable multicastRunnable;
  UDPListenerRunnable broadcastRunnable;
  
  private UDPServer(int port) {    
    portNumber = port;
    
    try {                    
          //First create the UDP Sockets
          unicastSocket = new DatagramSocket (portNumber+UNICAST+3);
          //Multicast und Broadcastsocket erzeugen
		  
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
          while (true){}
          
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
  void receivedMessage(int type, DatagramPacket packet) {               
      String message = new String(packet.getData());
      System.out.println("received message:" + message);
      if (message.substring(0,1).equals("1") && message.length()>=8){
          //handle the Matrikelnummer
          String matrikelnummer = message.substring(2,8);         
          //Calculate the random value
          Random random = new Random();
          int ran = random.nextInt(9999999);
          String ranS = String.valueOf(ran);
          while (ranS.length()<7){
            ranS = "0"+ranS;
          }
          //Send the message
          sendMessage("2 "+matrikelnummer+" "+ranS, type, packet);          
      }    
  }
  
  //Sends Messages to clients
  void sendMessage(String msg, int type, DatagramPacket inPacket) {     
      try {
        byte[] buffer = msg.getBytes();
      
        System.out.println("Sending Message" + msg);
      
        //Prepare the DatagramPacket
        DatagramPacket packet;
        if (type == UNICAST){
          //Paket erstellen
        } else if (type == MULTICAST){
          //Paket erstellen
        } else {
          //Paket erstellen
        }

        (new DatagramSocket()).send(packet);
        
      } catch (Exception e){
        e.printStackTrace();
      }
	}
  
  
  public static void main(String[] args) {
    if (args[0] == null) {
      System.out.println("Please start with a portnumber!");
      return;
    }
    int port = Integer.parseInt(args[0]);
    
    UDPServer server = new UDPServer(port);	 
    
  }

  
}