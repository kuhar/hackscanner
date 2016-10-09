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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.hackscanner.io.hackscanner.skyapi.FlightInfo;
import io.hackscanner.io.hackscanner.skyapi.SkyScannerBroker;

/**
 * Created by Marcin on 2016-10-08.
 */

public class FlightResultProcessor implements SkyScannerBroker.FlightsReceivedCallback {

    public interface FlightDataUpdateListener {
        void onFlightDataUpdated(String flightName, List<String> data, Map<String, String> uris);
        void updateLowestPrice(String flightName, Double value);
    }

    private String flightName;
    private FlightInfo info;
    private FlightDataUpdateListener listener;
    private SkyScannerBroker broker;

    public FlightResultProcessor(String flightName, FlightInfo info, FlightDataUpdateListener listener, SkyScannerBroker broker) {
        this.flightName = flightName;
        this.listener = listener;
        this.info = info;
        this.broker = broker;
    }

    @Override
    public void onFlightsReceived(JSONObject response) {
        Map<String, String> uris = new HashMap<>();
        List<String> data = new ArrayList<>();
        try {
            JSONArray quotes = response.getJSONArray("Quotes");

            List<JSONObject> quotesAsList = new ArrayList<>();

            for (int i = 0; i < quotes.length(); i++) {
                quotesAsList.add(quotes.getJSONObject(i));
            }

            Collections.sort(quotesAsList, new ByPriceComparator());

            //SimpleDateFormat originalDataFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            //SimpleDateFormat expectedDateFormat = new SimpleDateFormat("dd/MM");

            if(quotesAsList.size() > 0) {
                //String departureDateAsString = quotesAsList.get(0).getJSONObject("OutboundLeg").getString("DepartureDate");
                //Date date = originalDataFormat.parse(departureDateAsString);
                //String dateInExpectedFormat = expectedDateFormat.format(date);


                data.add("Round trip BCN -> " + info.destinationPlace + " for " + quotesAsList.get(0).getDouble("MinPrice") + " EUR");
                uris.put(flightName, broker.getFlightRequestUrl(info, true));
                listener.updateLowestPrice(flightName, quotesAsList.get(0).getDouble("MinPrice"));
                //data.add(quotesAsList.get(0).toString());
            } else {
                //data.add("Unfortunately, no flights found at this time. Try again later!");
            }

            listener.onFlightDataUpdated(flightName, data, uris);

        } catch (JSONException e) {
            e.printStackTrace();
            //listener.onFlightDataUpdated(flightName, Arrays.asList("Error retrieving flights for this hackaton (JSON)"));
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

            }

            return valA.compareTo(valB);
        }
    }

    @Override
    public void onFlightsRequestError(String error) {
        //listener.onFlightDataUpdated(flightName, Arrays.asList("Unfortunately, no flights found at this time. Try again later!"));
    }
}
