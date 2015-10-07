package com.appedidos.parseinnovaapp;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.squareup.picasso.Picasso;

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

        //initialize and create the image loader logic
        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx) {
                return null;
            }
        });


        ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
