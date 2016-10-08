package io.hackscanner.io.hackscanner.skyapi;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by Marcin on 2016-10-08.
 */

public class SkyScannerBroker {
    private static final String apiKey = "ha678525358889723348948613218626";
    private UserLocaleSettings userLocaleSettings;

    public interface FlightsReceivedCallback {
        void onFlightsReceived(JSONObject response);
        void onFlightsRequestError(String error);
    }

    public SkyScannerBroker(UserLocaleSettings userLocaleSettings) {
        this.userLocaleSettings = userLocaleSettings;
    }

    public void searchFlights(FlightInfo flightInfo, final FlightsReceivedCallback flightsReceivedCallback, Context context) {

        String url = getFlightRequestUrl(flightInfo, false);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                flightsReceivedCallback.onFlightsReceived(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                flightsReceivedCallback.onFlightsRequestError(error.getMessage());
            }
        });

        VolleyNetworkAccessSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public String getFlightRequestUrl(FlightInfo flightInfo, boolean referral) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http").authority("partners.api.skyscanner.net")
                .appendPath("apiservices")
                .appendPath(referral ? "referral" : "browsequotes")
                .appendPath("v1.0")
                .appendPath(userLocaleSettings.market)
                .appendPath(userLocaleSettings.currency)
                .appendPath(userLocaleSettings.locale)
                .appendPath(flightInfo.originPlace)
                .appendPath(flightInfo.destinationPlace)
                .appendPath(flightInfo.outboundPartialDate);

        if(flightInfo.inboundPartialDate != null) {
            uriBuilder.appendPath(flightInfo.inboundPartialDate);
        }

        uriBuilder.appendQueryParameter("apiKey", apiKey);

        return uriBuilder.toString();
    }
}
