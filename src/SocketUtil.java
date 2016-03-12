import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.concurrent.Executors;

import fileUtils.*;

/**
 * @author martin
 * @modify Aicitel
 * 
 */
public class SocketUtil {
	
	public static String ReadJSONBySize(InputStream in, Integer size) throws IOException
	{
		StringBuffer result = new StringBuffer("");
		//BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				
		try 
		{   
			for(int cntByte=0;cntByte<size;cntByte++)
				result.append((char)in.read());
		}
		catch (IOException e) 
		{
			System.out.println(e);
			throw e;
		}
		System.out.println("end reading string from stream "+result.toString());
		return result.toString();
	}
	
	public static Integer[] ReadBytes(InputStream in) throws IOException
	{
		Integer[] sizes = new Integer[2];
		byte[] maskBytes = new byte[4];
		try 
		{
			for(int cntInt=0; cntInt<2;cntInt++)
			{
				for(int cntByte=0; cntByte<4;cntByte++){					
					maskBytes[cntByte] = (byte)in.read();
					//System.out.println(maskBytes[cntByte]);
				}
				sizes[cntInt]=FileUtil.Byte2Int(maskBytes);
			}
			return sizes;
		}
		catch (IOException e) 
		{
			System.out.println(e);
			throw e;
		}
	}
	/**
	 * write message(JSON in this context)to a outputstream
	 * 
	 * @param String, @param Socket Outputstream
	 *            to write string
	 */
	public static void writeMessage2Stream(String str, OutputStream out) throws IOException {
		try 
		{
			BufferedOutputStream writer = new BufferedOutputStream(out);
			byte[] bLocalArr = new byte[4];
			for (int i = 0; i < 4; i++)
				bLocalArr[i] = (byte) (str.length() >> 8 * i & 0xFF);
			writer.write(bLocalArr);
			// write as bytes
			writer.write(str.getBytes());
			writer.flush();
			System.out.println(str);
			/*writer.write(str.getBytes());
			writer.flush();*/
			
		} 
		catch (IOException ex) 
		{
			System.out.println(ex);
			System.out.println("??????????????????????????????");
			throw ex;
		}
	}
	
	/**
	 * read message(JSON in this context)from a inputstream
	 * 
	 * @param Socket Inputstream
	 * @return String
	 *            to read string
	 */
	public static String readMessageFromStream(InputStream in) throws IOException
	{
		StringBuffer result = new StringBuffer("");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				
		char[] chars = new char[2048];
		byte[] maskBytes = new byte[12];
		StringBuffer maskString = new StringBuffer();
		int len;
		try 
		{	
			for(int cntByte=0;cntByte<4;cntByte++){
				maskBytes[cntByte] = (byte)in.read();
				System.out.println((byte)maskBytes[cntByte]);
				maskString.append((char)maskBytes[cntByte]);
			}
			if(FileUtil.Byte2Int(maskBytes)==268435521)
			{
				System.out.println("Here File Post");
				return "FILE_POST";
			}
			while ((len = reader.read(chars)) != -1) 
			{	
				if (len == 2048)
					result.append(chars);
				else 
				{			
					for (int i = 0; i < len; i++)	
						result.append(chars[i]);
					break;
				}
			}
		}
		catch (IOException e) 
		{
			System.out.println(e);
			throw e;
		}
		System.out.println("end reading string from stream "+maskString.toString()+result.toString());
		return maskString.toString()+result.toString();
	}


	/**
	 * @@remain to modify
	 * read file from a Inputstream
	 * 
	 * @param in
	 * @throws IOException 
	 */
	public static String readFileFromStream(int fileSize, String filerouteString, int ModelMode, InputStream in) throws IOException {
		DataInputStream reader = new DataInputStream(in);
		String modeString=null;
		char cnt_byte;
		int total=0;
		FileOutputStream picstream = null;
		try 
		{
			picstream = new FileOutputStream(new File(filerouteString+".txt"));
			while (true)
			{
				cnt_byte = (char)reader.read();
				picstream.write(cnt_byte);
				picstream.flush();
				total = total + 1;
				if(total==fileSize){
					System.out.println("receive end");
					break;
				}
			}
			if(ModelMode == 2)
				modeString = new String("personzombie.ms3d");
			if(ModelMode == 0 || ModelMode == 1)
				modeString = new String("hands.ms3d");
			Executors.newCachedThreadPool().execute(
					new ModelCalThread(//"E:/javaIO/"+modelString,
										"video/"+modeString,
										filerouteString+".txt",
										//"E:/javaIO/oritest.txt",
										//"E:/javaIO/server/"+filerouteString,
										filerouteString,
										42,
										ModelMode	
								)
					);
			
			System.out.println("New Thread End "+filerouteString);
			
			return "REV";
		} 
		catch (IOException e) 
		{
			System.out.println(e);
			throw e;
		}
		finally
		{
			if (picstream != null)  
				picstream.close();
		}
	}
	
	/**
	 * @@remain to modify
	 * write file to a Outputstream
	 * 
	 * @param in
	 * @return String
	 * @throws IOException 
	 */
	public static void writeFile2Stream(String filerouteString,OutputStream out) throws IOException {
		BufferedOutputStream writer = new BufferedOutputStream(out);
		byte[] chars = new byte[2048];
		int len;
		FileInputStream picstream = null;
		int total=0;
		try 
		{
			File writeFile = new File("video/"+filerouteString);
			int fileLength = (int)writeFile.length();
			
			picstream = new FileInputStream(writeFile);
			writer.write(FileUtil.Int2Byte(fileLength));
			
            while ((len = picstream.read(chars)) != -1) 
            {
            	total+=len;
            	writer.write(chars,0,len);
    			writer.flush();
            }
            System.out.println(total);
			System.out.println("print end");
		} 
		catch (IOException e) 
		{
			System.out.println(e);
			throw e;
		}
		finally
		{
			if (picstream != null)  
				picstream.close();
		}
	}
	
	public static String getNowTime()
	{
		return new Date().toString();
	}

}
