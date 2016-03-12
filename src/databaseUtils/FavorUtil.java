package databaseUtils;

import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;

public class FavorUtil {
	private static Mongo mongo = null;
	private static DB userdb=null;
	private static void initDB()
	{
		try 
		{  
			System.out.println("Mongo Init");
	        mongo = new Mongo("localhost", 27017);  
	        MongoOptions opt = mongo.getMongoOptions();  
	        opt.connectionsPerHost = 100;  
	        opt.threadsAllowedToBlockForConnectionMultiplier = 100;  
	        userdb = mongo.getDB("User");
	    } 
		catch (UnknownHostException e) 
		{  
	        
		} 
		catch (MongoException e) 
		{  
	  
	    }  
	}

	public static String Update(JSONObject objJSON)
	{
		return null;
	}
	public static String AddVideo(String videoID,String tagName)
	{
		if(mongo==null)
			initDB();
		DBCollection favorCollection = userdb.getCollection("tag");
		try 
		{	
			JSONObject dboJSON= RetrieveByName(tagName);
			dboJSON.getJSONArray("videos").put(videoID);
			DBObject dbo = (DBObject) com.mongodb.util.JSON.parse(dboJSON.toString());
			DBObject oriDbo = (DBObject) com.mongodb.util.JSON.parse(RetrieveByName(tagName).toString());
			favorCollection.update(oriDbo, dbo);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}  
		return null;
	}
	public static String RemoveVideo(String videoID,String tagName)
	{
		if(mongo==null)
			initDB();
		DBCollection favorCollection = userdb.getCollection("tag");
		try 
		{	
			JSONObject dboJSON= RetrieveByName(tagName);
			JSONArray videoArray = dboJSON.getJSONArray("videos");
			for(int index = 0; index<videoArray.length();index++)
				if((tagName.equals(videoArray.get(index).toString())))
					videoArray.remove(index);
			dboJSON.put("videos", videoArray);
			DBObject dbo = (DBObject) com.mongodb.util.JSON.parse(dboJSON.toString());
			DBObject oriDbo = (DBObject) com.mongodb.util.JSON.parse(RetrieveByName(tagName).toString());
			favorCollection.update(oriDbo, dbo);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}  
		return null;
	}
	
	private static JSONObject RetrieveByName(String tagName){
		if(mongo==null)
			initDB();
		DBCollection favorCollection = userdb.getCollection("tag"); 
		try 
		{ 
			if(tagName==null)
				return null;
			DBObject object = favorCollection.findOne(new BasicDBObject("tagname",tagName));
			if(object==null){
				System.out.println("nothing get "+tagName);
				return null;
			}
			else
			{
				JSONObject dataJson=new JSONObject(object.toString());
				dataJson.remove("_id");
				return dataJson;
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		finally{}
		return null;
	}
	
	public static String GetVideoIDByConSeq(String index,String tagName){
		try 
		{	
			JSONObject dboJSON= RetrieveByName(tagName);
			if(dboJSON == null)
				System.out.println("GetVideoIDByConSeq NULL");
			return dboJSON.getJSONArray("videos").getString(Integer.parseInt(index));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}  
		return null;
	}
}
