package io.hackscanner.io.hackscanner.skyapi;

/**
 * Created by Marcin on 2016-10-08.
 */

public class UserLocaleSettings {
    public String market;
    public String currency;
    public String locale;

    public UserLocaleSettings(String market, String currency, String locale) {
        this.market = market;
        this.currency = currency;
        this.locale = locale;
    }
}
