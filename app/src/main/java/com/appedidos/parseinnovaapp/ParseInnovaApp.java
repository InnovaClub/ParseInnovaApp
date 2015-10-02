package com.appedidos.parseinnovaapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by jvargas on 9/29/15.
 */
public class ParseInnovaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "Wjpf9K6FCcZFma68uNnVtH4rFsCkefe2iXK1oYzs", "GJ9aUc5Ndwu3w06LqkNzPHxtDO0NXJU3lwl2WsVW");

        
    }
}
