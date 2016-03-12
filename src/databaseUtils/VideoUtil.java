package databaseUtils;


import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;

public class VideoUtil {
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
	
	public static String Save(JSONObject objJSON)
	{
		if(mongo==null)
			initDB();
		DBCollection userCollection = userdb.getCollection("video");
		try 
		{
			String VideoIDSeq = VideoIDSequence();
			objJSON.put("videoID", Integer.parseInt(VideoIDSeq));
			if(objJSON.has("comments")==false)
				objJSON.put("comments", new JSONArray());
			if(objJSON.has("commends")==false)
				objJSON.put("commends", new JSONArray());
			DBObject dbo = (DBObject) com.mongodb.util.JSON.parse(objJSON.toString());
			userCollection.insert(dbo);
			return VideoIDSeq;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}  
		return null;
	}
	public static void Update(JSONObject objJSON)
	{
		if(mongo==null)
			initDB();
		DBCollection userCollection = userdb.getCollection("video");
		try 
		{
			DBObject dbo = (DBObject) com.mongodb.util.JSON.parse(objJSON.toString());
			DBObject oriDbo = (DBObject) com.mongodb.util.JSON.parse(RetrieveInt(objJSON.getString("videoID")).toString());
			dbo.put("videoID", Integer.parseInt(dbo.get("videoID").toString()));
			userCollection.update(oriDbo,dbo);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
	}

	public static void UpdateComment(String videoID, String commentID)
	{
		if(mongo==null)
			initDB();
		DBCollection videoCollection = userdb.getCollection("video");
		try 
		{
			JSONObject dboJSON= Retrieve(videoID);
			dboJSON.getJSONArray("comments").put(commentID);
			DBObject dbo = (DBObject) com.mongodb.util.JSON.parse(dboJSON.toString());
			DBObject oriDbo = (DBObject) com.mongodb.util.JSON.parse(RetrieveInt(videoID).toString());
			dbo.put("videoID", Integer.parseInt(dbo.get("videoID").toString()));
			videoCollection.update(oriDbo,dbo);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
	}
	public static JSONObject RetrieveInt(String videoID)
	{	
		if(mongo==null)
			initDB();
		DBCollection videoCollection = userdb.getCollection("video"); 
		try 
		{ 
			DBObject object = videoCollection.findOne(new BasicDBObject("videoID",Integer.parseInt(videoID)));
			if(object==null)	
			{
				System.out.println("R int null");
				return null;
			}
			else
			{
				JSONObject dataJson=new JSONObject(object.toString());
				dataJson.remove("_id");
				System.out.println("R int "+dataJson.toString());
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
	public static JSONObject Retrieve(String videoID)
	{	
		if(mongo==null)
			initDB();
		DBCollection videoCollection = userdb.getCollection("video"); 
		try 
		{ 
			if(videoID==null)
				return null;
			DBObject object = videoCollection.findOne(new BasicDBObject("videoID",Integer.parseInt(videoID)));
			System.out.println(videoID+" retrieve id");
			if(object==null){
				System.out.println("nothing get "+videoID);
				return null;
			}
			else
			{
				JSONObject dataJson=new JSONObject(object.toString());
				dataJson.remove("_id");
				String videoIDString = dataJson.get("videoID").toString();
				dataJson.put("videoID", videoIDString);
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
	
	public static JSONObject RetrieveByConSeq(String seq)
	{	
		if(mongo==null){
			initDB();
		}
		DBCollection videoCollection = userdb.getCollection("video");
		try 
		{ 
			DBObject dbo = (DBObject) com.mongodb.util.JSON.parse("{videoID:1}");
			DBCursor cursor = videoCollection.find();
			cursor.sort(dbo);
			if(cursor.count()-1-Integer.parseInt(seq)<0)
				return null;
			cursor.skip(cursor.count()-Integer.parseInt(seq)-1);
			DBObject object = cursor.next();
			if(object==null)
				return null;
			else
			{
				JSONObject dataJson=new JSONObject(object.toString());
				dataJson.remove("_id");
				String videoIDString = dataJson.get("videoID").toString();
				dataJson.put("videoID", videoIDString);
				System.out.println("Video Return Str "+dataJson.toString());
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
	
	public static String RetrieveSeq()
	{
		try 
		{ 
			DBCollection userCollection = userdb.getCollection("video");
			DBObject rootObj = userCollection.findOne(new BasicDBObject("videoID",0));
			Integer orderInteger = Integer.parseInt(rootObj.get("sequence").toString())-1;
			return orderInteger.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	private static String VideoIDSequence()
	{
		try 
		{ 
			DBCollection userCollection = userdb.getCollection("video");
			DBObject rootObj = userCollection.findOne(new BasicDBObject("videoID",0));
			
			Integer sequence = Integer.parseInt(rootObj.get("sequence").toString());
			rootObj.put("sequence", (new Integer(sequence+1))+"");
			Update(new JSONObject(rootObj.toString()));
			System.out.println(sequence+" stored");
			return sequence.toString();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}

