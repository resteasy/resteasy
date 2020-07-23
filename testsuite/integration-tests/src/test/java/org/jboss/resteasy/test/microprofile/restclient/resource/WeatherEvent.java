package org.jboss.resteasy.test.microprofile.restclient.resource;

import java.util.Date;

public class WeatherEvent {
    private Date date;
    private String description;

    WeatherEvent(final Date date, final String description) {
        this.date = date;
        this.description = description;
    }

    public Date getDate() {
        return this.date;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        // provided for text/plain data format
        return date.toString() + "\n" + description + "\n";
    }

    // Method for testcase use
    public boolean compare(WeatherEvent event) {
        if (date.toString().equals(event.getDate().toString())
                && description.equals(event.getDescription())) {
            return true;
        }
        return false;
    }
}
