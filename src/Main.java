import java.net.*;
import java.util.List;


public class Main {
	private static List<byte[]> Packets;
	private static final int Port = 11234;
	private static DatagramSocket Socket;
	private static DatagramPacket Packet_Receive, Packet_Send;
	
	public static void main(String[] args) {
		
		try{
			// create Chunks and get ArrayList of the packets
			FileOperations cc = new FileOperations("C:/Users/mahmoud/Downloads/SUBJECT/NetWork/NetWORK.txt");
			Packets = cc.getChunks();
			
			Socket = new DatagramSocket(Port);
			
			byte buffer[] = new byte[4096];
			
			//Check hand connection
			Packet_Receive = new DatagramPacket(buffer, buffer.length);
			Socket.receive(Packet_Receive);
			
			//Client info
			int Client_port = Packet_Receive.getPort();
			InetAddress Client_address = Packet_Receive.getAddress();
			
			//send the number of packets
			Packet_Send = new DatagramPacket(String.valueOf(Packets.size()).getBytes(), String.valueOf(Packets.size()).getBytes().length, 
					                         Client_address, Client_port);
			Socket.send(Packet_Send);
			
			//Stop and wait implementation

			/*StopAndWaitServer saws = new StopAndWaitServer(Packets, Socket);
			saws.Start(Client_address, Client_port);
			
			//Selective Repeat
			SelectiverepeatServer srs = new SelectiverepeatServer(Packets, Socket);
			srs.Start(Client_address, Client_port);
			*/
			//Go Back N
			GoBacknServer gbns = new GoBacknServer(Packets, Socket);
			gbns.Start(Client_address, Client_port);
			
			Socket.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	
}