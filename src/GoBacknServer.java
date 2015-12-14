import java.io.IOException;		
import java.net.*;			
import java.util.*;	
import java.util.zip.*;
				
public class GoBacknServer {		
			
	private DatagramPacket Packet_Send, Packet_Receive;		
	private List<byte[]> Packets;		
	private DatagramSocket Socket;		
	private Random r = new Random();		
	public GoBacknServer(List<byte[]> Packets, DatagramSocket Socket) {
		this.Packets = Packets;		
		this.Socket  = Socket;		
	}		
					
	public void Start( InetAddress Client_address, int Client_port) throws InterruptedException, IOException {
		boolean dataSent[] = new boolean[Packets.size()];		
		boolean ACKreceived[] = new boolean[Packets.size()];		
		Arrays.fill(ACKreceived, false);		
		Arrays.fill(dataSent, false);		
		CRC32 g = new CRC32();
		int i = 0; 		
		while (i < Packets.size()) {		
			int maxSelect = 		
					((Packets.size() - i) % 4 == 0 && (Packets.size() - i) != 0 ) || Packets.size() - i > 4? 4 : Packets.size() - i ;		
			int displacement = 0;	
			
			for(int j = 0; j < maxSelect; j++){		
				byte[] a = new byte[2048];		
				a= Integer.toString(i+j).getBytes();		
				DatagramPacket OrderPacket = new DatagramPacket(a, a.length, Client_address, Client_port);		
				Packet_Send = new DatagramPacket(Packets.get(i+j), Packets.get(i+j).length, Client_address, Client_port);
				g.reset();
				g.update(Packet_Send.getData(), 0, Packet_Send.getLength());
				byte[] checksum = String.valueOf(g.getValue()).getBytes();
				DatagramPacket CheckSumPacket = new DatagramPacket(checksum, checksum.length, Client_address, Client_port);
				if(!ACKreceived[i+j] && !dataSent[i+j]) {		
					if(Send(Packet_Send, OrderPacket, CheckSumPacket)){		
						dataSent[i+j] = true;		
						if(Receive(new byte[100])){		
							ACKreceived[i+j] = true;		
							displacement++;		
						} else {		
							ACKreceived[i+j] = false;		
						}		
					} 		
				}		
			}		
					
			for(int j = 0; j < maxSelect; j++){		
				if (dataSent[i] && ACKreceived[i]) break;		
				byte[] a = new byte[2048];		
				a= Integer.toString(i+j).getBytes();		
				DatagramPacket OrderPacket = new DatagramPacket(a, a.length, Client_address, Client_port);					
				Packet_Send = new DatagramPacket(Packets.get(i+j), Packets.get(i+j).length, Client_address, Client_port);
				g.reset();
				g.update(Packet_Send.getData(), 0, Packet_Send.getLength());
				byte[] checksum = String.valueOf(g.getValue()).getBytes();
				DatagramPacket CheckSumPacket = new DatagramPacket(checksum, checksum.length, Client_address, Client_port);
				if(Send(Packet_Send, OrderPacket, CheckSumPacket)){		
					if(Receive(new byte[100])){		
						ACKreceived[i+j] = true;		
						displacement++;		
					} else {		
						ACKreceived[i+j] = false;		
					}		
				} 				
			}		
			i+=displacement;		
		}		
	}		
			
	private synchronized boolean Send(DatagramPacket packetSend, DatagramPacket orderOfPacket, DatagramPacket CheckSumPacket) throws IOException, InterruptedException{
		int probability;		
		if( (probability = r.nextInt(10)) >= 5){		
			Socket.send(packetSend);
			Socket.send(orderOfPacket);	
			Socket.send(CheckSumPacket);
			System.out.println("Probability: " +probability + ", Packet Sent");				
			return true;		
		} else {		
			System.out.println("Probability: " +probability  + ", Packet did not send");		
			return false;		
		}			
	}		
			
	private synchronized boolean Receive( byte[] buffer) throws IOException, InterruptedException{
		try {		
			Packet_Receive = new DatagramPacket(buffer,buffer.length);		
			Socket.setSoTimeout(1000);		
			Socket.receive(Packet_Receive);		
			System.out.println("ACK has been received000000000000");		
		} catch (java.net.SocketTimeoutException e) {		
			System.out.println("ACK has not been received");		
			return false;		
		}		
		return true;			
	}		
		
}