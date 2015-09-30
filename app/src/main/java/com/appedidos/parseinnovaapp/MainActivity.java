package com.appedidos.parseinnovaapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Tasks.executeInBackground(this, new BackgroundWork<List<ParseObject>>() {
            @Override
            public List<ParseObject> doInBackground() throws Exception {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Alumnos");
                try {
                    return query.find();
                } catch (ParseException e) {
                    return null;
                } // expensive operation
            }
        }, new Completion<List<ParseObject>>() {
            @Override
            public void onSuccess(Context context, List<ParseObject> result) {
                LinearLayout container = (LinearLayout)findViewById(R.id.container);

                for(ParseObject alumno:result){
                    //infle vista
                    View child = getLayoutInflater().inflate(R.layout.alumnos, null);
                    //obtener label
                    TextView label = (TextView) child.findViewById(R.id.nombre);

                    String nombre = alumno.getString("nombre");

                    label.setText(nombre);

                    container.addView(child);
                }
            }

            @Override
            public void onError(Context context, Exception e) {
                Toast.makeText(context, "Se produjo un error en la llamada a parse", Toast.LENGTH_SHORT).show();
            }
        });

        //new GetUsersOnBackGround().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetUsersOnBackGround extends AsyncTask<Void, Integer,  List<ParseObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected List<ParseObject> doInBackground(Void... urls) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Alumnos");
            try {
                return query.find();
            } catch (ParseException e) {
                return null;
            }
        }

        protected void onPostExecute(List<ParseObject> result) {
            if (!MainActivity.this.isFinishing()){
                LinearLayout container = (LinearLayout)findViewById(R.id.container);

                for(ParseObject alumno:result){
                    //infle vista
                    View child = getLayoutInflater().inflate(R.layout.alumnos, null);
                    //obtener label
                    TextView label = (TextView) child.findViewById(R.id.nombre);

                    String nombre = alumno.getString("nombre");

                    label.setText(nombre);

                    container.addView(child);
                }
            }

        }
    }

}
