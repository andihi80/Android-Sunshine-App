package com.example.andreas.sunshine;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
* Created by Andreas on 22.11.2014.
*/
public class TemperatureResult
{
    public double minTemperature;
    public double maxTemperature;
    public String IconPath;
    public Date date;
    public String description;

    @Override
    public String toString()
    {
        return toString(Unit.Metric);
    }

    public String toString(Unit unit)
    {
        return getReadableDateString(date) + " - " + description + "    " + formatTemperature(maxTemperature,unit) + "/" + formatTemperature(minTemperature,unit);
    }

    private String formatTemperature(double temperature, Unit unit )
    {
        long rounded = unit == Unit.Metric ? Math.round(temperature) : Math.round(toFahrenheit(temperature));

        if(unit == Unit.Metric)
            return rounded +"°C";
        else
            return rounded + "°F";
    }

    private double toFahrenheit(double temperature)
    {
        return ((9.0/5.0)* temperature)+32.0;
    }

    private String getReadableDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }
}
