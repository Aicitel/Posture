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
public class ProfileUtil {
	private static Mongo mongo = null;
	private static DB userdb=null;
	private static void initDB()
	{
		try 
		{  
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
	public static void Save(JSONObject objJSON)
	{
		if(mongo==null)
			initDB();
		DBCollection userCollection = userdb.getCollection("profile");
		try 
		{
			objJSON.put("userID", UserIDSequence());
			JSONArray followarray = new JSONArray();
			objJSON.put("follows",followarray);
			JSONArray videosarray = new JSONArray();
			objJSON.put("videos",videosarray);
			JSONArray tagsarray = new JSONArray();
			objJSON.put("tags",tagsarray);
			DBObject dbo = (DBObject) com.mongodb.util.JSON.parse(objJSON.toString());
			userCollection.insert(dbo);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}  
	}
	public static void UpdateVideo(String userID,String videoID)
	{
		if(mongo==null)
			initDB();
		DBCollection userCollection = userdb.getCollection("profile");
		try 
		{
			JSONObject dboJSON= RetrieveByID(userID);
			dboJSON.put("videos", dboJSON.getJSONArray("videos").put(videoID));
			DBObject dbo = (DBObject) com.mongodb.util.JSON.parse(dboJSON.toString());
			DBObject oriDbo = (DBObject) com.mongodb.util.JSON.parse(RetrieveByID(userID).toString());
			userCollection.update(oriDbo,dbo);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
	}
	
	public static void Update(JSONObject objJSON)
	{
		if(mongo==null)
			initDB();
		DBCollection userCollection = userdb.getCollection("profile");
		try 
		{
			DBObject dbo = (DBObject) com.mongodb.util.JSON.parse(objJSON.toString());
			DBObject oriDbo = (DBObject) com.mongodb.util.JSON.parse(Retrieve(objJSON.getString("username")).toString());
			userCollection.update(oriDbo,dbo);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
	}
	public static String RetVideoIDByUserIndex(JSONObject reqJSON)
	{
		if(mongo==null)
			initDB();
		DBCollection userCollection = userdb.getCollection("profile"); 
		try 
		{ 
			DBObject object = userCollection.findOne(new BasicDBObject("username",reqJSON.getString("username")));
			if(object==null)
				return null;
			else
			{
				JSONArray videoArray = new JSONArray(object.get("videos").toString());
				if(videoArray!=null)
					return videoArray.get(reqJSON.getInt("index")).toString();
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		finally{}
		return null;
	}
	public static String RetLatVideoIDByUserIndex(JSONObject reqJSON)
	{
		if(mongo==null)
			initDB();
		DBCollection userCollection = userdb.getCollection("profile"); 
		try 
		{ 
			DBObject object = userCollection.findOne(new BasicDBObject("username",reqJSON.getString("username")));
			if(object==null)
				return null;
			else
			{
				JSONArray videoArray = new JSONArray(object.get("videos").toString());
				System.out.println(videoArray.toString());
				if(videoArray!=null&&(videoArray.length()-1-reqJSON.getInt("index")>=0))
					return videoArray.get(videoArray.length()-1-reqJSON.getInt("index")).toString();
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		finally{}
		return null;
	}
	public static JSONObject RetrieveByID(String userID)
	{	
		if(mongo==null)
			initDB();
		//DB userdb = mongo.getDB("User");
		DBCollection userCollection = userdb.getCollection("profile"); 
		try 
		{ 
			DBObject object = userCollection.findOne(new BasicDBObject("userID",userID));
			if(object==null)
				return null;
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
	public static JSONObject Retrieve(String username)
	{	
		if(mongo==null)
			initDB();
		//DB userdb = mongo.getDB("User");
		DBCollection userCollection = userdb.getCollection("profile"); 
		try 
		{ 
			DBObject object = userCollection.findOne(new BasicDBObject("username",username));
			if(object==null)
				return null;
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
	private static String UserIDSequence()
	{
		try 
		{ 
			//DB userdb = mongo.getDB("User");
			DBCollection userCollection = userdb.getCollection("profile");
			DBObject rootObj = userCollection.findOne(new BasicDBObject("username","root"));
			
			Integer sequence = Integer.parseInt(userCollection.findOne(new BasicDBObject("username","root")).get("userID").toString());
			rootObj.put("userID", (new Integer(sequence+1)).toString());
			Update(new JSONObject(rootObj.toString()));
			return sequence.toString();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
