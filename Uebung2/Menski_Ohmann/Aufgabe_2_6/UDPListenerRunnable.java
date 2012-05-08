package udpserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * This Thread listenes on a DatagramSocket and if a packet is received it is handed to the UDPServer.receiveMessage()
 * @author Sebastian
 */
public class UDPListenerRunnable implements Runnable {
  DatagramSocket socket;
  UDPServer parent;
  int type;
  boolean running = true;
  
  public UDPListenerRunnable(DatagramSocket socket, udpserver.UDPServer parent, int type){
    this.socket = socket;    
    this.type = type;
    this.parent = parent;
  }
  
  public void run() {
    while (running){
      try {
        byte[] buffer = new byte[65000];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        System.out.println("Listening for Messages of type: "+type);
        socket.receive(packet);
        parent.receivedMessage(type, packet);
      } catch (Exception e){
        e.printStackTrace();
      }
    }
  }
  
  public void stopRunning (){
    running = false;
  }
}
