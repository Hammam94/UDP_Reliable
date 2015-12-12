import java.io.*;
import java.util.*;

public class FileOperations {

	private String file_path;
	private List<byte[]> Chunks = new ArrayList<byte[]>();
	private InputStream is;

	public FileOperations(String Path) {
		this.file_path = Path;
	}

	// convert file into small chunks of bytes
	public List<byte[]> getChunks() throws IOException {

		byte packet[] = new byte[2048];
		File file = new File(file_path);
		is = new FileInputStream(file);
		int read;
		while ( (read = is.read(packet, 0, 2048)) != -1) {
			if(read < 2048){
				Chunks.add(Arrays.copyOfRange(packet, 0, read));
				
			} else {
				System.out.println(packet.length);
				Chunks.add(packet);
			}
			packet = new byte[2048];
		}
		return Chunks;
	}

	// convert the bytes into file
	public void File_Create(List<byte[]> Packets) {
		byte[] Files_bytes = new byte[(Packets.size() - 1) * Packets.get(0).length + Packets.get(Packets.size()-1).length];
		int byte_index = 0;
		for (int i = 0; i < Packets.size(); i++) {
			byte[] bytes = Packets.get(i);
			for (int j = 0; j < bytes.length; j++) {
				Files_bytes[byte_index] = bytes[j];
				byte_index++;
			}
		}
		try {
			System.out.println(Files_bytes.length);
			File output = new File(file_path);
			FileOutputStream fos = new FileOutputStream(output);
			fos.write(Files_bytes);
			fos.flush();
			fos.close();
		} catch (Exception e) {

		}
	}
}
