import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


public class Client {
	final static int Port = 11234;
	private static DatagramSocket Socket;
	private static DatagramPacket Packet_Receive, Packet_Send;
	private static List<byte[]> Packets = new ArrayList<byte[]>();
	private static InetAddress inetAddress;
	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		
		 try {
		        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
		                .hasMoreElements();) {
		            NetworkInterface intf = en.nextElement();
		            if (intf.getName().contains("wlan")) {
		                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
		                        .hasMoreElements();) {
		                    inetAddress = enumIpAddr.nextElement();
		                    if (!inetAddress.isLoopbackAddress()
		                            && (inetAddress.getAddress().length == 4)) {
		                        System.out.println (inetAddress.getHostAddress());
		                    }
		                }
		            }
		        }
		    } catch (SocketException ex) {
		    	System.out.println (ex.toString());
		    }
		
		Socket = new DatagramSocket(); 
		InetAddress IPAddress = InetAddress.getByName(inetAddress.getHostAddress());
		byte[] sendData = new byte[100];       
		byte[] receiveData = new byte[2048];  
		
		//Check hand connection
		sendData = "C:/Users/mahmoud/Desktop/NetWork/a.txt".getBytes();
		Packet_Send = new DatagramPacket(sendData, sendData.length, IPAddress, 4444);       
		Socket.send(Packet_Send);       
		
		//get number of the file's packets
		Packet_Receive = new DatagramPacket(receiveData, receiveData.length);       
		Socket.receive(Packet_Receive);
		Checksum g = new CRC32();
			
		int Packet_number = Integer.parseInt(new String(Packet_Receive.getData()).trim());
		boolean[] arrivedPackets = new boolean[Packet_number];
		Arrays.fill(arrivedPackets, false);
		//get file packets
		for(int i = 0; i < Packet_number; i++) {
			Packet_Receive = new DatagramPacket(receiveData, receiveData.length);       
			Socket.receive(Packet_Receive);
			DatagramPacket Order_Packet_Receive = new DatagramPacket(new byte[2048], 2048);
			Socket.receive(Order_Packet_Receive);
			DatagramPacket CheckSumPacket = new DatagramPacket(new byte[2048], 2048);
			Socket.receive(CheckSumPacket);
			
			//checkSum
			long Checksum = Long.valueOf(new String(CheckSumPacket.getData()).trim());
			
			String order = new String(Order_Packet_Receive.getData()).trim();
			g.reset();
			g.update(Packet_Receive.getData(), 0, Packet_Receive.getLength());
			System.out.println((g.getValue() == Checksum) + "     " + g.getValue() + "     " + Checksum + "      " + Packet_Receive.getLength());
			
			if(    (Integer.valueOf(order) == 0 || arrivedPackets[Integer.valueOf(order) -1])
					&& g.getValue() == Checksum){
				arrivedPackets[Integer.valueOf(order)] = true;
				byte[] buf = new byte[Packet_Receive.getLength()];
				System.arraycopy(Packet_Receive.getData(), Packet_Receive.getOffset(), buf, 0, Packet_Receive.getLength());
				Packets.add(buf);
				Packet_Send = new DatagramPacket(sendData, sendData.length, IPAddress, 4444);
				Socket.send(Packet_Send);
				System.out.println(Packets.get(Integer.valueOf(order)).length);
				System.out.println("Packet: " + (Integer.valueOf(order) ) + " has been Receiced and ACK has been sent.");
			} else {
				System.out.println("Cant accept the Packet: " + (Integer.valueOf(order) ));
				i--;
			}
			
		}
		Socket.close();
		
		// create the file
		FileOperations fo = new FileOperations("C:/Users/mahmoud/Desktop/NetWork/Server_file.txt");
        fo.File_Create(Packets);
	}

}
