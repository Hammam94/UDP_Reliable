import java.io.*;	
import java.net.*;	
import java.util.*;
		
public class StopAndWaitServer {		
	private DatagramPacket Packet_Send, Packet_Receive, Packet_Order_Send;		
	private List<byte[]> Packets;		
	private DatagramSocket Socket;		
	private Random r = new Random();	
	
	public StopAndWaitServer(List <byte[]> Packets, DatagramSocket Socket) {
		this.Packets = Packets;		
		this.Socket  = Socket;						
	}
			
	public void Start(InetAddress Client_address, int Client_port) throws IOException {
		byte[] buffer = new byte[500];		
	    byte[] order = new byte[100];		
		for(int i = 0; i < Packets.size(); i++){		
			order = Integer.toString(i).getBytes();		
			Packet_Send = new DatagramPacket(Packets.get(i), Packets.get(i).length, Client_address, Client_port);		
			Packet_Order_Send = new DatagramPacket(order, order.length, Client_address, Client_port);		
			int probability;		
			if((probability = r.nextInt(10)) >= 5){		
				Socket.send(Packet_Send);		
				Socket.send(Packet_Order_Send);		
			}		
			System.out.println(probability);		
			Packet_Receive = new DatagramPacket(buffer, buffer.length);		
			try{		
				Socket.setSoTimeout(1000);		
				Socket.receive(Packet_Receive);		
				System.out.println("acknowlagment has been Received");		
			} catch (SocketTimeoutException e) {		
				  System.out.println("No acknowlagment has been Recevied from the client.");		
				  System.out.println("Trying to send the packet again.............");		
				  Packet_Send = new DatagramPacket(Packets.get(i), Packets.get(i).length, Client_address, Client_port);		
				  Socket.send(Packet_Send);		
				  Socket.send(Packet_Order_Send);		
				  Socket.receive(Packet_Receive);		
				  System.out.println("acknowlagment has been Received");		
			}			
		}			
	}		
		
}