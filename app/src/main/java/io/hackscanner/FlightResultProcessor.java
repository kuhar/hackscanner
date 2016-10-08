package io.hackscanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.hackscanner.io.hackscanner.skyapi.FlightInfo;
import io.hackscanner.io.hackscanner.skyapi.SkyScannerBroker;

/**
 * Created by Marcin on 2016-10-08.
 */

public class FlightResultProcessor implements SkyScannerBroker.FlightsReceivedCallback {

    public interface FlightDataUpdateListener {
        void onFlightDataUpdated(String flightName, List<String> data);
    }

    private String flightName;
    private FlightInfo info;
    private FlightDataUpdateListener listener;

    public FlightResultProcessor(String flightName, FlightInfo info, FlightDataUpdateListener listener) {
        this.flightName = flightName;
        this.listener = listener;
        this.info = info;
    }

    @Override
    public void onFlightsReceived(JSONObject response) {
        List<String> data = new ArrayList<>();
        try {
            JSONArray quotes = response.getJSONArray("Quotes");

            List<JSONObject> quotesAsList = new ArrayList<JSONObject>();

            for (int i = 0; i < quotes.length(); i++) {
                quotesAsList.add(quotes.getJSONObject(i));
            }

            Collections.sort(quotesAsList, new ByPriceComparator());

            SimpleDateFormat originalDataFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat expectedDateFormat = new SimpleDateFormat("dd/MM 'at' HH:mm");

            if(quotesAsList.size() > 0) {
                String departureDateAsString = quotesAsList.get(0).getJSONObject("OutboundLeg").getString("DepartureDate");
                Date date = originalDataFormat.parse(departureDateAsString);
                String dateInExpectedFormat = expectedDateFormat.format(date);

                data.add("BCN -> " + info.destinationPlace + ", " + dateInExpectedFormat + " for " + quotesAsList.get(0).getDouble("MinPrice") + " EUR");
            } else {
                data.add("Unfortunately, no flights found at this time. Try again later!");
            }

            listener.onFlightDataUpdated(flightName, data);

        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFlightDataUpdated(flightName, Arrays.asList("Error retrieving flights for this hackaton (JSON)"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private class ByPriceComparator implements Comparator<JSONObject> {
        //You can change "Name" with "ID" if you want to sort by ID
        private static final String KEY_NAME = "MinPrice";

        @Override
        public int compare(JSONObject a, JSONObject b) {
            Double valA = null;
            Double valB = null;

            try {
                valA = (Double) a.get(KEY_NAME);
                valB = (Double) b.get(KEY_NAME);
            }
            catch (JSONException e) {
                //do something
            }

            return valA.compareTo(valB);
            //if you want to change the sort order, simply use the following:
            //return -valA.compareTo(valB);
        }
    }

    @Override
    public void onFlightsRequestError(String error) {
        listener.onFlightDataUpdated(flightName, Arrays.asList("BCN -> " + info.destinationPlace + ": Not found, sorry!"));
    }
}
