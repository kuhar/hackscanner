package io.hackscanner.io.hackscanner.skyapi;

/**
 * Created by Marcin on 2016-10-08.
 */
public class FlightInfo {
    public String originPlace;
    public String destinationPlace;
    public String outboundPartialDate;
    public String inboundPartialDate;

    public FlightInfo(String originPlace, String destinationPlace, String outboundPartialDate) {
        this.originPlace = originPlace;
        this.destinationPlace = destinationPlace;
        this.outboundPartialDate = outboundPartialDate;
    }

    public FlightInfo(String originPlace, String destinationPlace, String outboundPartialDate, String inboundPartialDate) {
        this.originPlace = originPlace;
        this.destinationPlace = destinationPlace;
        this.outboundPartialDate = outboundPartialDate;
        this.inboundPartialDate = inboundPartialDate;
    }
}
