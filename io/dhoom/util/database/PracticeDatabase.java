package io.dhoom.util.database;

import com.mongodb.client.*;
import io.dhoom.*;
import com.mongodb.*;
import java.util.*;

public class PracticeDatabase
{
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection profiles;
    private final MongoCollection kits;
    
    public PracticeDatabase(final kPractice main) {
        this.client = (main.getConfig().getBoolean("DATABASE.MONGO.AUTHENTICATION.ENABLED") ? new MongoClient(new ServerAddress(main.getConfig().getString("DATABASE.MONGO.HOST"), main.getConfig().getInt("DATABASE.MONGO.PORT")), (List)Arrays.asList(MongoCredential.createCredential(main.getConfig().getString("DATABASE.MONGO.AUTHENTICATION.USER"), main.getConfig().getString("DATABASE.MONGO.AUTHENTICATION.DATABASE"), main.getConfig().getString("DATABASE.MONGO.AUTHENTICATION.PASSWORD").toCharArray()))) : new MongoClient(new ServerAddress(main.getConfig().getString("DATABASE.MONGO.HOST"), main.getConfig().getInt("DATABASE.MONGO.PORT"))));
        this.database = this.client.getDatabase("practice");
        this.profiles = this.database.getCollection("profiles");
        this.kits = this.database.getCollection("kits");
    }
    
    public MongoClient getClient() {
        return this.client;
    }
    
    public MongoDatabase getDatabase() {
        return this.database;
    }
    
    public MongoCollection getProfiles() {
        return this.profiles;
    }
    
    public MongoCollection getKits() {
        return this.kits;
    }
}
