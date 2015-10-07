package com.appedidos.parseinnovaapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appedidos.parseinnovaapp.adapters.RSSLisxtAdapter;
import com.einmalfel.earl.AtomEntry;
import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.einmalfel.earl.RSSItem;
import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.SlideInEffect;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    private static final int LOGIN_REQUEST = 65525;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;

    //the only profile we have
    private ProfileDrawerItem profile;

    //the toolbar
    private Toolbar toolbar;

    //rss to read the news later the jobs
    private static final String RSS_URI = "http://www.prospects.ac.uk/rss/accountancy-_banking_and_finance.rss";

    //data to be shown
    private List<RSSItem> data;

    //the adapter for the list
    private RSSLisxtAdapter adapter;

    //the list
    private JazzyListView list;

    //to make request http
    private OkHttpClient client = new OkHttpClient();

    //simple pulltorefesheffect
    private PullToRefreshView mPullToRefreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buildDrawer();

        getRSSData();

    }


    /**
     * Construyo el drawer
     */
    private void buildDrawer() {
        String facebookId = getFacebookId();

        ArrayList<IDrawerItem> drawerItems = new ArrayList<>();

        if (facebookId == null){
            profile = new ProfileDrawerItem().withName(getString(R.string.no_user)).withEmail(getString(R.string.app_name)).withIcon(ContextCompat.getDrawable(this, R.drawable.user));
            //como el user no esta registrado muestro la salida
            drawerItems.add(new PrimaryDrawerItem().withName(R.string.login).withIcon(FontAwesome.Icon.faw_users).withIdentifier(1).withCheckable(false)  );
            drawerItems.add(new PrimaryDrawerItem().withName(R.string.home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(2).withCheckable(false)  );
        } else {
            profile = new ProfileDrawerItem().withEmail(getString(R.string.app_name)).withName(ParseUser.getCurrentUser().getString("name")).withIcon("https://graph.facebook.com/"+facebookId+"/picture?type=large");
            //como el usuario esta registrado solo agrego el home y el logout;
            drawerItems.add(new PrimaryDrawerItem().withName(R.string.home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(2).withCheckable(false)  );
            drawerItems.add(new PrimaryDrawerItem().withName(R.string.logout).withIcon(FontAwesome.Icon.faw_user).withIdentifier(3).withCheckable(false));
        }

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile)
                .build();


        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .withDrawerItems(drawerItems)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            Intent intent = null;
                            switch (drawerItem.getIdentifier()) {
                                case 1:
                                    ParseUser user = ParseUser.getCurrentUser();
                                    if (user != null && ParseFacebookUtils.isLinked(user)) {
                                        makeMeRequest();
                                    } else {
                                        logIn();
                                    }
                                    break;
                                case 2:
                                    //no hago nada ya que ya estoy en el home! :)
                                    break;
                                case 3:
                                    logOut();
                                    break;
                            }
                        }

                        return false;
                    }
                })
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }



    /**
     * Salgo de la app y finalizo la app
     */
    private void logOut() {
        if (ParseUser.getCurrentUser() != null){
            ParseUser.logOut();
        }
        this.finish();
    }

    /**
     * Estadisticas de facebook
     */
    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }


    /**
     * Estadisticas de facebook
     */
    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }


    /**
     * Traigo el id del facebook del user de parse
     */
    private String getFacebookId() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && currentUser.has("profile")) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            if (userProfile.has("facebookId")) {
                try {
                    return userProfile.getString("facebookId");
                } catch (JSONException e) {
                    return null;
                }
            }
        }
        return null;
    }


    /**
     * Guardo los datos de facebook en parse y reconstruyo el drawer para que muestre la foto
     */
    public void makeMeRequest() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseInstallation.getCurrentInstallation().put("user", currentUser);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        if (!currentUser.has("profile")) {
            Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                    new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null ) {
                                // Create a JSON object to hold the profile info
                                JSONObject userProfile = new JSONObject();
                                try {
                                    // Populate the JSON object
                                    userProfile.put("facebookId", user.getId());
                                    userProfile.put("name", user.getName());
                                    if (user.getProperty("gender") != null) {
                                        userProfile.put("gender",
                                                user.getProperty("gender"));
                                    }
                                    if (user.getProperty("email") != null) {
                                        userProfile.put("email",
                                                user.getProperty("email"));
                                    }
                                    // Save the user profile info in a user property
                                    ParseUser currentUser = ParseUser
                                            .getCurrentUser();
                                    currentUser.put("profile", userProfile);
                                    currentUser.saveInBackground();
                                    // Show the user info
                                    //aca llamemos a algo
                                } catch (JSONException e) {
                                    buildDrawer();
                                }
                            } else if (response.getError() != null) {
                                buildDrawer();
                            }
                            buildDrawer();
                        }
                    });
            request.executeAsync();
        } else {
            buildDrawer();
        }

    }


    /**
     * Login
     */
    public void logIn() {
        ParseLoginBuilder builder = new ParseLoginBuilder(this);
        Intent parseLoginIntent = builder
                .setParseLoginEnabled(false)
                .setParseLoginButtonText("Entrar")
                .setParseSignupButtonText("Registrarse")
                .setParseLoginHelpText("Te olvidaste de tu password?")
                .setParseLoginInvalidCredentialsToastText(
                        "El mail no es correcto")
                .setParseLoginEmailAsUsername(true)
                .setParseSignupSubmitButtonText("Enviar registracion")
                .setFacebookLoginEnabled(true)
                .setFacebookLoginButtonText("Ingresar con Facebook")
                .setTwitterLoginEnabled(false)
                .setTwitterLoginButtontext("Twitter").build();
        startActivityForResult(parseLoginIntent, LOGIN_REQUEST);
    }

    /**
     * Se llama a este metodo despues de venir de la pantalla de login
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST){
            try{
                ParseInstallation.getCurrentInstallation().saveInBackground();
                ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
                ParseUser user = ParseUser.getCurrentUser();
                buildDrawer();
                if (user != null && ParseFacebookUtils.isLinked(user)){
                    makeMeRequest();
                }
            } catch (Exception e){
                ParseUser user = ParseUser.getCurrentUser();
                if (user !=null){
                    ParseUser.logOut();
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                }
                Toast.makeText(this, "Se produjo un error al intentar acceder, intenta de nuevo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * traigo el detalle de los rrs usando las nanotask para no interrumpir el hilo principal
     */
    public void getNetwordData() {
        toolbar.setSubtitle("Obteniendo datos...");
        Tasks.executeInBackground(this, new BackgroundWork<String>() {
            @Override
            public String doInBackground() throws Exception {
                try {
                    com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                            .url(RSS_URI)
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
                    Feed feed = EarlParser.parseOrThrow(response.body().byteStream(), 0);
                    data = (List<RSSItem>) feed.getItems();
                } catch (Exception e) {
                    return e.getMessage();
                } finally {
                }
                return null;
            }
        }, new Completion<String>() {
            @Override
            public void onSuccess(Context context, String result) {
                toolbar.setSubtitle("Ultimos trabajos ofrecidos");
                if (result == null) {
                    refresh();
                }
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 5);
            }

            @Override
            public void onError(Context context, Exception e) {
                Toast.makeText(context, "Se produjo un error en la llamada al RSS", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * actualizo el adapter de mi lista
     */
    private void refresh() {
        if (data != null) {
            adapter = new RSSLisxtAdapter(this, data);
            list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Obtengo la lista y llamo al rss
     */
    public void getRSSData() {
        list = (JazzyListView) findViewById(R.id.list);
        list.setTransitionEffect(new SlideInEffect());

        list.setSelector(android.R.color.transparent);

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNetwordData();
            }
        });
        getNetwordData();
        mPullToRefreshView.setRefreshing(true);
    }
}
