package io.hackscanner;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainScreen extends AppCompatActivity {


    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    HashMap<String, List<String>> listDataChild = new HashMap<>();

    String url = "https://mlh.io/seasons/eu-2017/events";
    List<String> citiesArray = new ArrayList<>();
    List<String> countriesArray = new ArrayList<>();
    List<String> startDatesArray = new ArrayList<>();
    List<String> endDatesArray = new ArrayList<>();
    List<String> namesArray = new ArrayList<>();
    List<Airport> airports = new ArrayList<>();
    List<Drawable> imageArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);



        /*Button titlebutton = (Button) findViewById(R.id.button);

        titlebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Execute Title AsyncTask*/
                new Title().execute();
            /*}*/
       /* });*/





    }

    // Title AsyncTask
    private class Title extends AsyncTask<Void, Void, Void> {
        String title;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                InputStream csvStream = getAssets().open("airports.csv");
                InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
                CSVReader csvReader = new CSVReader(csvStreamReader);
                String[] line;

                while ((line = csvReader.readNext()) != null) {
                    Airport a = new Airport();
                    a.City = line[2];
                    a.Country = line[3];
                    a.Code = line[4];
                    airports.add(a);
                    Log.w("hackscanner", a.City + a.Country + a.Code);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                // Connect to the web site
                Document document = Jsoup.connect(url).get();
                // Get the html document title
                /*title = document.title();*/
                Elements cities = document.body().select("span[itemprop=addressLocality]");
                Elements countries = document.body().select("span[itemprop=addressRegion]");
                Elements startDates = document.body().select("meta[itemprop=startDate]");
                Elements endDates = document.body().select("meta[itemprop=endDate]");
                Elements names = document.body().select("h3[itemprop=name]");
                Elements images = document.body().select("div[class=event-logo] img");


                //data per type of data
                for(int i=0; i<cities.size();i++){
                    citiesArray.add(cities.get(i).ownText());
                    countriesArray.add(countries.get(i).ownText());
                    startDatesArray.add(startDates.get(i).attr("content"));
                    endDatesArray.add(endDates.get(i).attr("content"));
                    namesArray.add(names.get(i).ownText());

                    Drawable d;
                    try {
                        InputStream is = (InputStream) new URL(images.get(i).attr("src")).getContent();
                        d = Drawable.createFromStream(is, "src name");

                    } catch (Exception e) {
                        d = null;
                    }
                    imageArray.add(d);


                }

                //data per hackathon
                for(int i=0; i<cities.size();i++){
                    List<String> hackathonData = new ArrayList<String>();

                    hackathonData.add("City: "+citiesArray.get(i));
                    hackathonData.add("Country: "+countriesArray.get(i));
                    hackathonData.add("Start date: "+startDatesArray.get(i));
                    hackathonData.add("End date: " +endDatesArray.get(i));

                    for (Airport a : airports)
                        if (a.Country.equals(countriesArray.get(i)) && a.City.equals(citiesArray.get(i)) && !a.Code.isEmpty())
                            hackathonData.add("Airport code: " + a.Code);

                    listDataChild.put(namesArray.get(i), hackathonData);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            /*TextView txttitle = (TextView) findViewById(R.id.textView);*/
            expListView = (ExpandableListView) findViewById(R.id.lvExp);
            listAdapter = new ExpandableListAdapter(MainScreen.this, namesArray, listDataChild, imageArray);
            expListView.setAdapter(listAdapter);

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

class Airport {
    String Code;
    String Country;
    String City;
}




