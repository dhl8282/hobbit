package com.example.hobbit;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class Database {
    public static final String COLLECTION_USER = "user";
    public static final String COLLECTION_PARENT_MISSION = "parent_mission";
    private final String URI = "mongodb://admin:admin@ds029640.mongolab.com:29640/hobbitdb";
    private final BasicDBObject LOC = new BasicDBObject("loc","2d");
    private MongoClientURI mongo_client_uri;
    private MongoClient client;
    private DB db;

    public Database() {
        mongo_client_uri = new MongoClientURI(URI);
        try {
            client = new MongoClient(mongo_client_uri);
            db = client.getDB(mongo_client_uri.getDatabase());

//            getCollection(COLLECTION_PARENT_MISSION).ensureIndex(LOC);
        } catch (UnknownHostException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public DBCollection getCollection(String collectionName) {
        if (db != null) {
            DBCollection collection = db.getCollection(COLLECTION_PARENT_MISSION);
            collection.ensureIndex(LOC, "geospatialIdx");
            return collection;
        }
        return null;
    }

    public DB getDb() {
        return db;
    }

    public MongoClientURI getMongo_client_uri() {
        return mongo_client_uri;
    }

    public MongoClient getClient() {
        return client;
    }
}