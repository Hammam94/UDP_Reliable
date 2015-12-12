import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	final static int Port = 11234;
	private static DatagramSocket Socket;
	private static DatagramPacket Packet_Receive, Packet_Send;
	private static List<byte[]> Packets = new ArrayList<byte[]>();

	public static void main(String[] args) throws IOException, InterruptedException {
		Socket = new DatagramSocket();       
		InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] sendData = new byte[100];       
		byte[] receiveData = new byte[2048];  
		
		//Check hand connection
		Packet_Send = new DatagramPacket(sendData, sendData.length, IPAddress, Port);       
		Socket.send(Packet_Send);       
		
		//get number of the file's packets
		Packet_Receive = new DatagramPacket(receiveData, receiveData.length);       
		Socket.receive(Packet_Receive);
		int Packet_number = Integer.parseInt(new String(Packet_Receive.getData()).trim());
		boolean[] arrivedPackets = new boolean[Packet_number];
		Arrays.fill(arrivedPackets, false);
		//get file packets
		for(int i = 0; i < Packet_number; i++) {
			Packet_Receive = new DatagramPacket(new byte[2048], 2048);       
			Socket.receive(Packet_Receive);
			DatagramPacket Order_Packet_Receive = new DatagramPacket(new byte[2048], 2048);       
			Socket.receive(Order_Packet_Receive);
			String order = new String(Order_Packet_Receive.getData()).trim();
			
			if(Integer.valueOf(order) == 0 
					|| arrivedPackets[Integer.valueOf(order) -1]){
				arrivedPackets[Integer.valueOf(order)] = true;
				Packets.add(Packet_Receive.getData());
				Packet_Send = new DatagramPacket(sendData, sendData.length, IPAddress, Port);
				Socket.send(Packet_Send);
				System.out.println("Packet: " + (Integer.valueOf(order) ) + " has been Receiced and ACK has been sent.");
			} else {
				System.out.println("Cant accept the Packet: " + (Integer.valueOf(order) ));
				i--;
			}
			
		}
		Socket.close();
		
		// create the file
		FileOperations cc = new FileOperations("C:/Users/mahmoud/Downloads/SUBJECT/NetWork/Server_file.txt");
        cc.File_Create(Packets);
	}

}
