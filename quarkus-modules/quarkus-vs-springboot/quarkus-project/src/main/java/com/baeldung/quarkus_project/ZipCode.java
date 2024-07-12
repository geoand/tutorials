package com.baeldung.quarkus_project;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ZipCode {

    private final String zip;
    private final String type;
    private final String city;
    private final String state;
    private final String county;
    private final String timezone;

    public ZipCode(String type, String city, String state, String county, String timezone) {
        this(null, type, city, state, county, timezone);
    }

    public ZipCode(String zip, String type, String city, String state, String county, String timezone) {
        this.zip = zip;
        this.type = type;
        this.city = city;
        this.state = state;
        this.county = county;
        this.timezone = timezone;
    }

    public ZipCode withZip(String zip) {
        return new ZipCode(zip, type, city, state, county, timezone);
    }

    public String getZip() {
        return zip;
    }

    public String getType() {
        return type;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCounty() {
        return county;
    }

    public String getTimezone() {
        return timezone;
    }
}
