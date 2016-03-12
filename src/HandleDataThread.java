import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.json.JSONObject;

import databaseUtils.LogicDBUtils;
/**
 * 
 *
 * 
 */
public class HandleDataThread implements Runnable {
	// request from client
	private Socket request;
	
	/*
	 * Request String, Define context of request  
	 */
	public static final String SERVICE_REGISTER		="10000001";
	public static final String SERVICE_LOGIN		="10000002";
	public static final String SERVICE_CHANGE_PRO	="10000004";
	public static final String SERVICE_SHARE		="10000005";
	public static final String SERVICE_RMSHARE		="10000006";
	public static final String SERVICE_COMMEND		="10000007";
	public static final String SERVICE_UNCOMMEND	="10000008";
	public static final String SERVICE_COMMENT		="10000009";
	public static final String SERVICE_UNCOMMENT	="1000000a";
	public static final String SERVICE_FAVOR		="1000000b";
	public static final String SERVICE_UNFAVOR		="1000000c";
	public static final String SERVICE_FOLLOW		="1000000d";
	public static final String SERVICE_UNFOLLOW		="1000000e";
	public static final String SERVICE_GMBU			="10000011";
	public static final String SERVICE_GMBF			="10000012";
	public static final String SERVICE_GMBT			="10000013";
	public static final String SERVICE_GMBV			="10000014";
	public static final String SERVICE_GPBU			="10000021";
	public static final String SERVICE_GFILE		="10000031";
	public static final String SERVICE_GIMAGE		="10000032";
	public static final String SERVICE_GOBJ			="10000033";
	public static final String SERVICE_GCARD		="10000034";
	public static final String SERVICE_CGMPRO		="10000043";


	public HandleDataThread(Socket request) {
		this.request = request;
	}

	@Override
	public void run() {
		try 
		{
			// set timeout:20 seconds
			request.setSoTimeout(20000);
			while (true) {
				// get info from request when getting a socket request
				String reqStr = "";
				try {
					// if read() get a timeout exception
					request.sendUrgentData(0); 
					reqStr = SocketUtil.readMessageFromStream(request.getInputStream());
						
				} catch (SocketTimeoutException e) {
					// then break while loop, stop the service
					System.out.println("Time Out");
					break;
				} catch (IOException e) {
					System.out.println("Socket Closed");
					break;
				}  
				
				if(reqStr.length()==0)
				{
					Thread.sleep(1000);
					reqStr = SocketUtil.readMessageFromStream(request.getInputStream());
					if(reqStr.length()==0)
						break;
				}
				if(reqStr.length()!=0)
				{
					System.out.println("here req str is "+reqStr);
					
					if(reqStr.equals("FILE_POST"))
					{
						System.out.println("Here Handle File Post");
						Integer[] fileSizes = new Integer[2]; 
						fileSizes = SocketUtil.ReadBytes(request.getInputStream());
						System.out.println(fileSizes[0]+" "+fileSizes[1]);
						String uploadJSON = SocketUtil.ReadJSONBySize(request.getInputStream(),fileSizes[0]);
						LogicDBUtils.PutVideoMsg(uploadJSON);
						String msg = SocketUtil.readFileFromStream(
								fileSizes[1],
								LogicDBUtils.GetNextFileRoute(),
								LogicDBUtils.GetVideoModelMode(uploadJSON),
								request.getInputStream());
						System.out.println(msg);
						SocketUtil.writeMessage2Stream(msg, request.getOutputStream());
						break;
					}
					
					JSONObject dataJson=new JSONObject(reqStr);
					String stat = dataJson.getString("STATE");
					System.out.println("Stat is "+stat);
					if(stat.equals(SERVICE_REGISTER))
					{
						System.out.println("You are registering");
						JSONObject userValue=dataJson.getJSONObject("VALUE");
						String loginStat = LogicDBUtils.VerifyUsernameExist(userValue.getString("username"));
						if(loginStat.equals("userexist")||loginStat.equals("serverdown")){
							System.out.println(loginStat);
							SocketUtil.writeMessage2Stream(loginStat, request.getOutputStream());
						}
						else{
							LogicDBUtils.WriteRegisterUser(userValue);
							SocketUtil.writeMessage2Stream(loginStat, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_LOGIN))
					{
						System.out.println("You are logging");
						JSONObject userValue=dataJson.getJSONObject("VALUE");
						String message = LogicDBUtils.VerifyLogin(userValue.getString("username"),userValue.getString("password"));
						if(message.equals("fail")||message.equals("serverdown")){
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
						
					}
					else if(stat.equals(SERVICE_CHANGE_PRO))
					{
						System.out.println("You are changing profile");
						JSONObject userValue=dataJson.getJSONObject("VALUE");
						String message = LogicDBUtils.UserChangeProfile(userValue);
						
						System.out.println(message);
						SocketUtil.writeMessage2Stream(message, request.getOutputStream());
					}
					else if(stat.equals(SERVICE_SHARE))
					{
						System.out.println("You are sharing");
						String message = LogicDBUtils.ShareVideos(dataJson.getJSONObject("VALUE"));
						if(message.equals("fail")||message.equals("serverdown")){
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
						
					}
					else if(stat.equals(SERVICE_RMSHARE))
					{
						System.out.println("You are Removing sharing");
						String message = LogicDBUtils.UnShareVideos(dataJson.getJSONObject("VALUE"));
						if(message.equals("fail")||message.equals("serverdown")){
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
						
					}
					else if(stat.equals(SERVICE_COMMEND))
					{
						System.out.println("You are Commending");
						String message = LogicDBUtils.UserCmdVideo(dataJson.getJSONObject("VALUE"));
						if(message==null)
							SocketUtil.writeMessage2Stream("serverdown", request.getOutputStream());
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_UNCOMMEND))
					{
						System.out.println("You are unCommending");
						String message = LogicDBUtils.UserUnCmdVideo(dataJson.getJSONObject("VALUE"));
						if(message==null)
							SocketUtil.writeMessage2Stream("serverdown", request.getOutputStream());
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_COMMENT))
					{
						System.out.println("You are Commenting");
						String message = LogicDBUtils.UserCommentVideo(dataJson.getJSONObject("VALUE"));
						if(message==null)
							SocketUtil.writeMessage2Stream("serverdown", request.getOutputStream());
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_FAVOR))
					{
						System.out.println("You are Choosing Favor");
						String message = LogicDBUtils.UserFavorTag(dataJson.getJSONObject("VALUE"));
						if(message==null)
							SocketUtil.writeMessage2Stream("serverdown", request.getOutputStream());
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_UNFAVOR))
					{
						System.out.println("You are Removing Favor");
						String message = LogicDBUtils.UserRemoveTag(dataJson.getJSONObject("VALUE"));
						if(message==null)
							SocketUtil.writeMessage2Stream("serverdown", request.getOutputStream());
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_FOLLOW))
					{
						System.out.println("You are following");
						String message = LogicDBUtils.UserFollow(dataJson.getJSONObject("VALUE"));
						if(message==null)
							SocketUtil.writeMessage2Stream("serverdown", request.getOutputStream());
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_UNFOLLOW))
					{
						System.out.println("You are Removing follow");
						String message = LogicDBUtils.UserRemoveFollow(dataJson.getJSONObject("VALUE"));
						if(message==null)
							SocketUtil.writeMessage2Stream("serverdown", request.getOutputStream());
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_GMBU))
					{
						System.out.println("You are Getting VideoMsg By Username");
						String message = LogicDBUtils.GetVideoMsgByUsername(dataJson.getJSONObject("VALUE"));
						if(message==null)
							SocketUtil.writeMessage2Stream("serverdown", request.getOutputStream());
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_GMBF))
					{
						System.out.println("You are Getting VideoMsg By Tag");
						String message = LogicDBUtils.GetVideoMsgByTag(dataJson.getJSONObject("VALUE"));
						if(message==null)
							SocketUtil.writeMessage2Stream("serverdown", request.getOutputStream());
						else{
							System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_GMBT))
					{
						System.out.println("You are Getting VideoMsg By time");
						String message = LogicDBUtils.GetVideoMsgByTime(dataJson.getJSONObject("VALUE"));
						System.out.println("Get Stop");
						if(message==null)
						{
							System.out.println("serverdown 10000013");
							SocketUtil.writeMessage2Stream("serverdown", request.getOutputStream());
						}
						else{
							System.out.println(message+" 10000013");
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_GMBV))
					{
						System.out.println("You are Getting VideoMsg By VideoID");
						String message = LogicDBUtils.GetVideoMsgByVideoID(dataJson.getJSONObject("VALUE"));
						if(message==null)
						{
							System.out.println("serverdown 10000014");
							SocketUtil.writeMessage2Stream("serverdown", request.getOutputStream());
						}
						else{
							System.out.println(message+" 10000014");
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_GPBU))
					{
						System.out.println("You are Getting Profile");
						String message = LogicDBUtils.GetUserProfile(dataJson.getJSONObject("VALUE"));
						if(message==null){
							SocketUtil.writeMessage2Stream("fail", request.getOutputStream());
						}
						else{
							//System.out.println(message);
							SocketUtil.writeMessage2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_GFILE))
					{
						System.out.println("You are Getting File By Video ID");
						String message = LogicDBUtils.GetVideoFileRoute(dataJson.getJSONObject("VALUE"));
						if(message==null){
							SocketUtil.writeMessage2Stream("fail", request.getOutputStream());
						}
						else{
							System.out.println(message);
							SocketUtil.writeFile2Stream(message, request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_GIMAGE))
					{
						System.out.println("You are Getting File Icon By Video ID");
						String message = LogicDBUtils.GetVideoFileRoute(dataJson.getJSONObject("VALUE"));
						if(message==null){
							SocketUtil.writeMessage2Stream("fail", request.getOutputStream());
						}
						else{
							System.out.println(message+".jpg");
							SocketUtil.writeFile2Stream(message+".jpg", request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_GOBJ))
					{
						System.out.println("You are Getting Object By Video ID");
						String message = LogicDBUtils.GetVideoFileRoute(dataJson.getJSONObject("VALUE"));
						if(message==null){
							SocketUtil.writeMessage2Stream("fail", request.getOutputStream());
						}
						else{
							System.out.println(message+".obj");
							SocketUtil.writeFile2Stream(message+".obj", request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_GCARD))
					{
						System.out.println("You are Getting Card info");
						String message = LogicDBUtils.GetVideoFileRoute(dataJson.getJSONObject("VALUE"));
						if(message==null){
							SocketUtil.writeMessage2Stream("fail", request.getOutputStream());
						}
						else{
							System.out.println(message+".cif");
							SocketUtil.writeFile2Stream(message+".cif", request.getOutputStream());
						}
					}
					else if(stat.equals(SERVICE_CGMPRO))
					{
						System.out.println("You are Changing Model Profile");
						String message = LogicDBUtils.UserChangeVideoMsg(dataJson.getJSONObject("VALUE"));
						if(message==null){
							SocketUtil.writeMessage2Stream("fail", request.getOutputStream());
						}
						else{
							System.out.println(message+".obj");
							SocketUtil.writeMessage2Stream(message+".obj", request.getOutputStream());
						}
					}
					else
					{
						System.out.println("blank request");
					}
				}
				Thread.sleep(300);
			}
		if (request != null) 
			request.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (request != null) {
				try {
					request.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
