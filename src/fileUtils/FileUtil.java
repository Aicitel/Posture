package fileUtils;

public class FileUtil {
	final public String dirRoute="/tmp/";
	static public int Byte2Int(byte[] maskBytes){
		int i = 0;
		
		i+=((maskBytes[3]&0xff)<<24);  
	    i+=((maskBytes[2]&0xff)<<16);  
	    i+=((maskBytes[1]&0xff)<<8);  
	    i+=((maskBytes[0]&0xff));  
	    System.out.println("Here int parse is "+i);
		
	    return i;
	}
	static public byte[] Int2Byte(int fileLength){
		byte[] a = new byte[4];
	    a[0] = (byte) (0xff & fileLength);
	    a[1] = (byte) ((0xff00 & fileLength) >> 8);
	    a[2] = (byte) ((0xff0000 & fileLength) >> 16);
	    a[3] = (byte) ((0xff000000 & fileLength) >> 24);

	    System.out.println("Here 2int is "+a[0]+" "+a[1]+" "+a[2]+" "+a[3] );
	    return a;
	}
	
}
