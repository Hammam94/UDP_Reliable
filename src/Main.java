import java.net.*;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFileChooser;


public class Main {
	private static List<byte[]> Packets;
	private static final int Port = 11234;
	private static DatagramSocket Socket;
	private static DatagramPacket Packet_Receive, Packet_Send;
	
	public static void main(String[] args) throws SocketException, UnknownHostException {
		try {
	        InetAddress candidateAddress = null;
	        // Iterate all NICs (network interface cards)...
	        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            // Iterate all IP addresses assigned to each card...
	            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {

	                    if (inetAddr.isSiteLocalAddress()) {
	                        // Found non-loopback site-local address. Return it immediately...
	                       // System.out.println(inetAddr);
	                    }
	                    else if (candidateAddress == null) {
	                        // Found non-loopback address, but not necessarily site-local.
	                        // Store it as a candidate to be returned if site-local address is not subsequently found...
	                        candidateAddress = inetAddr;
	                        // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
	                        // only the first. For subsequent iterations, candidate will be non-null.
	                    }
	                }
	            }
	        }
	        if (candidateAddress != null) {
	            // We did not find a site-local address, but we found some other non-loopback address.
	            // Server might have a non-site-local address assigned to its NIC (or it might be running
	            // IPv6 which deprecates the "site-local" concept).
	            // Return this non-loopback candidate address...
	        	System.out.println(candidateAddress);
	        }
	        // At this point, we did not find a non-loopback address.
	        // Fall back to returning whatever InetAddress.getLocalHost() returns...
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        if (jdkSuppliedAddress == null) {
	            throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
	        }
	        //System.out.println( jdkSuppliedAddress);
	    }
	    catch (Exception e) {
	        UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
	        unknownHostException.initCause(e);
	        throw unknownHostException;
	    }
		try{
			System.out.println(InetAddress.getLocalHost());
			// create Chunks and get ArrayList of the packets
			FileOperations cc = new FileOperations("C:/Users/mahmoud/Desktop/NetWork/a.txt");
			Packets = cc.getChunks();
			System.out.println(Packets.get(Packets.size() - 1) .length);
			
			Socket= new DatagramSocket(4444);
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