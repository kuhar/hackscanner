package io.hackscanner;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {

    String url = "https://mlh.io/seasons/eu-2017/events";
    List<String> citiesArray = new ArrayList<>();
    List<String> countriesArray = new ArrayList<>();
    List<String> startDatesArray = new ArrayList<>();
    List<String> endDatesArray = new ArrayList<>();
    List<String> namesArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Button titlebutton = (Button) findViewById(R.id.button);

        titlebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Execute Title AsyncTask
                new Title().execute();
            }
        });

    }

    // Title AsyncTask
    private class Title extends AsyncTask<Void, Void, Void> {
        String title;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(url).get();
                // Get the html document title
                /*title = document.title();*/
                Elements cities = document.body().select("span[itemprop=addressLocality]");
                Elements countries = document.body().select("span[itemprop=addressRegion]");
                Elements startDates = document.body().select("span[itemprop=startDate]");
                Elements endDates = document.body().select("span[itemprop=endDate]");
                Elements names = document.body().select("span[itemprop=name]");

                for(int i=0; i<cities.size();i++){
                    citiesArray.add(cities.get(i).ownText());
                    countriesArray.add(cities.get(i).ownText());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            AssetManager assetManager = getAssets();
            InputStream input;
            String text = "";

            try {
                input = assetManager.open("airports.csv");

                int size = input.available();
                byte[] buffer = new byte[size];
                input.read(buffer);
                input.close();

                // byte buffer into a string
                text = new String(buffer);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            TextView txttitle = (TextView) findViewById(R.id.textView);
            

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
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


}




