		
import java.io.IOException;		
import java.net.*;				
import java.util.*;		
				
public class SelectiverepeatServer {		
			
	private DatagramPacket Packet_Send, Packet_Receive;		
	private List<byte[]> Packets;		
	private DatagramSocket Socket;		
	private Random r = new Random();		
			
	public SelectiverepeatServer(List<byte[]> Packets, DatagramSocket Socket){		
		this.Packets = Packets;		
		this.Socket  = Socket;		
	}		
			
	public void Start(InetAddress client_address, int client_port) throws InterruptedException, IOException {		
		boolean dataSent[] = new boolean[Packets.size()];		
		boolean ACKreceived[] = new boolean[Packets.size()];		
		Arrays.fill(ACKreceived, false);		
		Arrays.fill(dataSent, false);		
				
		int i = 0; 		
		while (i < Packets.size()) {		
			int maxSelect = 		
					((Packets.size() - i) % 4 == 0 && (Packets.size() - i) != 0 ) || Packets.size() - i > 4 ? 4 : Packets.size() - i ;		
			for(int j = 0; j < maxSelect; j++){		
		
				byte[] a = new byte[2048];		
				a= Integer.toString(i+j).getBytes();		
				DatagramPacket OrderPacket = new DatagramPacket(a, a.length, client_address, client_port);		
				Packet_Send = new DatagramPacket(Packets.get(i+j), Packets.get(i+j).length, client_address, client_port);		
				if(!ACKreceived[i+j] && !dataSent[i+j]) {		
					if(Send(Packet_Send, OrderPacket, r.nextInt(10))){		
						dataSent[i+j] = true;		
						if(Receive(new byte[100])){		
							ACKreceived[i+j] = true;		
						} else {		
							ACKreceived[i+j] = false;		
						}		
					} 		
				}		
			}		
					
			for(int j = 0; j < maxSelect; j++){		
				byte[] a = new byte[2048];		
				a= Integer.toString(i+j).getBytes();		
				DatagramPacket OrderPacket = new DatagramPacket(a, a.length, client_address, client_port);					
				Packet_Send = new DatagramPacket(Packets.get(i+j), Packets.get(i+j).length, client_address, client_port);		
				if(!ACKreceived[i+j]) {		
					if(Send(Packet_Send, OrderPacket)){		
						if(Receive(new byte[100])){		
							ACKreceived[i+j] = true;		
						} else {		
							ACKreceived[i+j] = false;		
						}		
					} 		
				}		
			}		
					
					
			i+=maxSelect;		
		}		
	}		
			
	private boolean Send(DatagramPacket packetSend, DatagramPacket orderOfPacket, int probability) throws IOException, InterruptedException{		
		if( probability >= 5){		
			Socket.send(packetSend);		
			System.out.println(new String(orderOfPacket.getData()).trim());		
			Socket.send(orderOfPacket);		
			System.out.println("Probability: "+probability + " , Packet sent and it's Order is: " + new String(orderOfPacket.getData()).trim() );		
					
			return true;		
		} else {		
			System.out.println("Probability: "+probability + " , Packet did not send");		
			return false;		
		}			
	}		
			
	private boolean Send(DatagramPacket packetSend, DatagramPacket orderOfPacket) throws IOException, InterruptedException{		
			Socket.send(packetSend);		
			Socket.send(orderOfPacket);		
			System.out.println("Packet Sent and it's order is: "+ new String(orderOfPacket.getData()).trim());		
			return true;		
					
	}		
		
	private boolean Receive( byte[] buffer) throws IOException{		
		try {		
			Packet_Receive = new DatagramPacket(buffer,buffer.length);		
			Socket.setSoTimeout(1000);		
			Socket.receive(Packet_Receive);		
			System.out.println("ACK has been received");		
		} catch (java.net.SocketTimeoutException e) {		
			System.out.println("ACK has not been received");		
			return false;		
		}		
		return true;			
	}		
		
			
}