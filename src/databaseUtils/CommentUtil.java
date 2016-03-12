package databaseUtils;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;

public class CommentUtil {
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
	
	public static String Save(JSONObject objJSON)
	{
		if(mongo==null)
			initDB();
		DBCollection userCollection = userdb.getCollection("comment");
		try 
		{
			String seqString =  CommentIDSequence();
			objJSON.put("commentID",seqString);
			
			objJSON.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			DBObject dbo = (DBObject) com.mongodb.util.JSON.parse(objJSON.toString());
			userCollection.insert(dbo);
			return seqString;
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
		DBCollection userCollection = userdb.getCollection("comment");
		try 
		{
			System.out.println("Update Comment");
			DBObject dbo = (DBObject) com.mongodb.util.JSON.parse(objJSON.toString());
			System.out.println(dbo.toString());
			DBObject oriDbo = (DBObject) com.mongodb.util.JSON.parse(RetrieveByCmtID(objJSON.getString("commentID")).toString());
			userCollection.update(oriDbo,dbo);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
	}
	public static JSONObject RetrieveByCmtID(String commentID)
	{	
		if(mongo==null)
			initDB();
		DBCollection userCollection = userdb.getCollection("comment"); 
		try 
		{ 
			DBObject object = userCollection.findOne(new BasicDBObject("commentID",commentID));
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
	
	private static String CommentIDSequence()
	{
		try 
		{ 
			DBCollection userCollection = userdb.getCollection("comment");
			DBObject rootObj = userCollection.findOne(new BasicDBObject("commentID","-1"));
			Integer sequence = Integer.parseInt(rootObj.get("sequence").toString());
			sequence = sequence+1;
			rootObj.put("sequence",sequence.toString());
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
