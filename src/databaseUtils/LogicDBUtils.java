package databaseUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class LogicDBUtils {
	public static String VerifyUsernameExist(String username) 
	{
		if(ProfileUtil.Retrieve(username)==null)
			return "success";
		else 
			return "userexist";
    } 
	public static String VerifyLogin(String username,String password)
	{
		try
		{
			JSONObject userJSON = ProfileUtil.Retrieve(username);
			if(userJSON != null && userJSON.get("password").equals(password))
				return userJSON.toString();
			else 
				return "fail";
		}
		catch(JSONException e)
		{
			System.out.print(e);
		}
		return "serverdown";
	}
	public static String UserChangeProfile(JSONObject registerJSON){
		try
		{
			JSONObject userJSON = ProfileUtil.Retrieve(registerJSON.getString("username"));
			userJSON.put("password", registerJSON.getString("password"));
			userJSON.put("email", registerJSON.getString("email"));
			ProfileUtil.Update(userJSON);
			System.out.println("After change profile is "+userJSON.toString());
			return userJSON.toString();
		}
		catch(JSONException e)
		{
			System.out.print(e);
		}
		return "serverdown";
	}
	public static void WriteRegisterUser(JSONObject registerJSON)
	{
		try 
		{
			registerJSON.put("tags", "[]");
			ProfileUtil.Save(registerJSON);
		} 
		catch (Exception e) 
		{
			System.out.print(e);
		}
	}
	public static String GetVideoMsgByUsername(JSONObject reqJSON)
	{
		try
		{
			JSONObject videoJSON = VideoUtil.Retrieve(ProfileUtil.RetLatVideoIDByUserIndex(reqJSON));
			if(videoJSON==null)
				return null;
			return videoJSON.put("comments", RetCommentsInVideo(videoJSON)).toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static String GetVideoMsgByTime(JSONObject reqJSON)
	{
		try
		{
			JSONObject videoJSON = VideoUtil.RetrieveByConSeq(reqJSON.getString("index"));
			if(videoJSON==null)
				return null;
			return videoJSON.put("comments", RetCommentsInVideo(videoJSON)).toString();
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		return null;
	}
	public static String GetVideoMsgByTag(JSONObject reqJSON)
	{
		try
		{
			String videoID = FavorUtil.GetVideoIDByConSeq(reqJSON.getString("index"),reqJSON.getString("tag"));
			JSONObject videoJSON = VideoUtil.Retrieve(videoID);
			if(videoJSON==null)
				return null;
			return videoJSON.put("comments", RetCommentsInVideo(videoJSON)).toString();
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		return null;
	}
	public static String GetVideoMsgByVideoID(JSONObject reqJSON)
	{
		try
		{
			JSONObject videoJSON = VideoUtil.Retrieve(reqJSON.getString("videoID"));
			if(videoJSON==null)
				return null;
			return videoJSON.put("comments", RetCommentsInVideo(videoJSON)).toString();
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		return null;
	}
	public static String UserCmdVideo(JSONObject reqJSON)
	{
		try
		{
			JSONObject videoJSON = VideoUtil.Retrieve(reqJSON.getString("videoID"));
			videoJSON.getJSONArray("commends").put(reqJSON.getString("username"));
			VideoUtil.Update(videoJSON);
			return videoJSON.put("comments", RetCommentsInVideo(videoJSON)).toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static synchronized String UserUnCmdVideo(JSONObject reqJSON)
	{
		try
		{
			JSONObject videoJSON = VideoUtil.Retrieve(reqJSON.getString("videoID"));
			JSONArray cmdArray = videoJSON.getJSONArray("commends");
			for(int index = 0; index<cmdArray.length();index++)
				if((reqJSON.getString("username").equals(cmdArray.get(index).toString())))
					cmdArray.remove(index);
			videoJSON.put("commends", cmdArray);
			VideoUtil.Update(videoJSON);
			return videoJSON.put("comments", RetCommentsInVideo(videoJSON)).toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static synchronized JSONObject UserAddVideoTag(JSONObject reqJSON)
	{
		try
		{
			JSONObject videoJSON = VideoUtil.Retrieve(reqJSON.getString("videoID"));
			if(!videoJSON.getString("tag").equals(""))
				FavorUtil.RemoveVideo(reqJSON.getString("videoID"), reqJSON.getString("tag"));
			FavorUtil.AddVideo(reqJSON.getString("videoID"), reqJSON.getString("tag"));
			videoJSON.put("tag", reqJSON.getString("tag"));
			VideoUtil.Update(videoJSON);
			return videoJSON;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static String UserChangeVideoMsg(JSONObject reqJSON)
	{
		try
		{
			JSONObject videoJSON = VideoUtil.Retrieve(reqJSON.getString("videoID"));
			
			if(!videoJSON.getString("tag").equals(reqJSON.getString("tag")))
				videoJSON = UserAddVideoTag(reqJSON);
			videoJSON.put("describe", reqJSON.getString("reqJSON"));
			VideoUtil.Update(videoJSON);
			return videoJSON.put("comments", RetCommentsInVideo(videoJSON)).toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static String ShareVideos(JSONObject reqJSON)
	{
		try
		{
			JSONObject profileJSON = ProfileUtil.RetrieveByID(reqJSON.getString("userID"));
			profileJSON.getJSONArray("videos").put(reqJSON.getString("videoID"));
			ProfileUtil.Update(profileJSON);
			return profileJSON.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static String UnShareVideos(JSONObject reqJSON)
	{
		try
		{
			JSONObject profileJSON = ProfileUtil.RetrieveByID(reqJSON.getString("userID"));
			profileJSON.getJSONArray("videos").remove(Integer.parseInt(reqJSON.getString("index")));
			ProfileUtil.Update(profileJSON);
			return profileJSON.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static String UserFavorTag(JSONObject reqJSON)
	{
		try
		{
			JSONObject profileJSON = ProfileUtil.RetrieveByID(reqJSON.getString("userID"));
			profileJSON.getJSONArray("tags").put(reqJSON.getString("tag"));
			ProfileUtil.Update(profileJSON);
			return profileJSON.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static String UserRemoveTag(JSONObject reqJSON)
	{
		try
		{
			JSONObject profileJSON = ProfileUtil.RetrieveByID(reqJSON.getString("userID"));
			profileJSON.getJSONArray("tags").remove(Integer.parseInt(reqJSON.getString("index")));
			ProfileUtil.Update(profileJSON);
			return profileJSON.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static String UserFollow(JSONObject reqJSON)
	{
		try
		{
			JSONObject profileJSON = ProfileUtil.RetrieveByID(reqJSON.getString("userID"));
			profileJSON.getJSONArray("follows").put(reqJSON.getString("name"));
			ProfileUtil.Update(profileJSON);
			return profileJSON.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static String UserRemoveFollow(JSONObject reqJSON)
	{
		try
		{
			JSONObject profileJSON = ProfileUtil.RetrieveByID(reqJSON.getString("userID"));
			profileJSON.getJSONArray("follows").remove(Integer.parseInt(reqJSON.getString("index")));
			ProfileUtil.Update(profileJSON);
			return profileJSON.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static String GetNextFileRoute()
	{
		try
		{
			Integer seq = Integer.parseInt(VideoUtil.RetrieveSeq());
			return "video/"+seq;
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		return null;
	}
	public static String GetVideoModelMsg(String reqString)
	{
		try
		{
			JSONObject videoValue=new JSONObject(reqString);
			return videoValue.getString("model");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static int GetVideoModelMode(String reqString)
	{
		try
		{
			JSONObject videoValue=new JSONObject(reqString);
			return Integer.parseInt(videoValue.getString("mode"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	public static int PutVideoMsg(String reqString)
	{
		try 
		{
			JSONObject videoValue=new JSONObject(reqString);
			System.out.println(videoValue.toString());
			String updateSeq = VideoUtil.Save(videoValue);
			ProfileUtil.UpdateVideo(videoValue.getString("userID"),updateSeq);
			FavorUtil.AddVideo(updateSeq, videoValue.getString("tag"));
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static String GetVideoFileRoute(JSONObject reqJSON)
	{
		try
		{
			JSONObject videoJSON = VideoUtil.Retrieve(reqJSON.getString("videoID"));
			if(videoJSON==null)
				return null;
			//return (videoJSON.getString("directory")+reqJSON.getString("videoID"));
			return reqJSON.getString("videoID");
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		return null;
	}
	public static String GetUserProfile(JSONObject reqJSON)
	{
		try
		{	
			JSONObject userJSON = ProfileUtil.Retrieve(reqJSON.getString("username"));
			if(userJSON==null)
				return null;
			return userJSON.toString();
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		return "fail";
	}
	public static String UserCommentVideo(JSONObject reqJSON)
	{
		try
		{
			String commentSeq = CommentUtil.Save(reqJSON);			
			JSONObject videoJSON = VideoUtil.Retrieve(reqJSON.getString("videoID"));
			if(videoJSON==null)
				return null;
			VideoUtil.UpdateComment(videoJSON.getString("videoID"),commentSeq);
			videoJSON = VideoUtil.Retrieve(videoJSON.getString("videoID"));
			return videoJSON.put("comments", RetCommentsInVideo(videoJSON)).toString();
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		return "fail";
	}
	
	private static JSONArray RetCommentsInVideo(JSONObject videoJSON)
	{
		try
		{
			JSONArray commentsIDArray = videoJSON.getJSONArray("comments");
			StringBuilder commentsString = new StringBuilder("[{}");
			System.out.println(commentsIDArray.length());
			for(int index = 0; index<commentsIDArray.length(); index++)
				if(Character.isDigit(commentsIDArray.getString(index).charAt(0))){
					System.out.println("RetCom index is "+commentsIDArray.getString(index));
					commentsString.append(","+CommentUtil.RetrieveByCmtID(commentsIDArray.getString(index)).toString());
				}
			return new JSONArray(commentsString+"]");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
