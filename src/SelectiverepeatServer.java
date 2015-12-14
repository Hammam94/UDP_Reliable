import java.io.IOException;		
import java.net.*;				
import java.util.*;		
import java.util.zip.CRC32;
import java.util.zip.Checksum;
				
public class SelectiverepeatServer {		
			
	private DatagramPacket Packet_Send, Packet_Receive, CheckSumPacket, OrderPacket;		
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
		Checksum g = new CRC32();
		while (i < Packets.size()) {		
			int maxSelect = 		
					((Packets.size() - i) % 4 == 0 && (Packets.size() - i) != 0 ) || Packets.size() - i > 4 ? 4 : Packets.size() - i ;	
			int displacement = 0;
			for(int j = 0; j < maxSelect; j++){		
				byte[] a = new byte[2048];		
				a= Integer.toString(i+j).getBytes();		
				OrderPacket = new DatagramPacket(a, a.length, client_address, client_port);		
				Packet_Send = new DatagramPacket(Packets.get(i+j), Packets.get(i+j).length, client_address, client_port);
				g.reset();
				g.update(Packet_Send.getData(), 0, Packet_Send.getLength());
				System.out.println(g.getValue());
				byte[] checksum = String.valueOf((int)(g.getValue())).getBytes();
				CheckSumPacket = new DatagramPacket(checksum, checksum.length, client_address, client_port);
				if(!ACKreceived[i+j] && !dataSent[i+j]) {		
					if(Send(Packet_Send, OrderPacket, CheckSumPacket, r.nextInt(10))){		
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
				OrderPacket = new DatagramPacket(a, a.length, client_address, client_port);					
				Packet_Send = new DatagramPacket(Packets.get(i+j), Packets.get(i+j).length, client_address, client_port);
				g.reset();
				g.update(Packet_Send.getData(), 0, Packet_Send.getLength());
				byte[] checksum = String.valueOf(g.getValue()).getBytes();
				CheckSumPacket = new DatagramPacket(checksum, checksum.length, client_address, client_port);
				if(!ACKreceived[i+j]) {		
					if(Send(Packet_Send, OrderPacket, CheckSumPacket)){		
						if(Receive(new byte[100])){		
							ACKreceived[i+j] = true;
							displacement++;
						} else {		
							ACKreceived[i+j] = false;		
						}		
					} 		
				}		
			}		
					
					
			i+=displacement;		
		}		
	}		
			
	private synchronized boolean Send(DatagramPacket packetSend, DatagramPacket orderOfPacket, DatagramPacket CheckSumPacket, int probability) throws IOException, InterruptedException{
		if( probability >= 5){
			Socket.send(packetSend);
			Socket.send(orderOfPacket);
			System.out.println(new String(CheckSumPacket.getData()).trim());
			Socket.send(CheckSumPacket);
			System.out.println("Probability: "+probability + " , Packet sent and it's Order is: " + new String(orderOfPacket.getData()).trim() );					
			return true;
		} else {
			System.out.println("Probability: "+probability + " , Packet did not send");
			return false;
		}
	}		
			
	private synchronized boolean Send(DatagramPacket packetSend, DatagramPacket orderOfPacket, DatagramPacket CheckSumPacket) throws IOException, InterruptedException{
			Socket.send(packetSend);
			Socket.send(orderOfPacket);
			System.out.println(new String(CheckSumPacket.getData()).trim());
			Socket.send(CheckSumPacket);
			System.out.println("Packet Sent and it's order is: "+ new String(orderOfPacket.getData()).trim());
			return true;
	}
		
	private synchronized boolean Receive( byte[] buffer) throws IOException{
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