package com.example.andreas.sunshine;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment
{
    private static final String DEFAULT_SEARCH_STRING ="7871855";//"94043";
    private ArrayAdapter<String> forecastAdapter = null;

    public ForecastFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_forecast_fragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_action_refresh)
        {
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView forecastListView = (ListView) rootView.findViewById(R.id.fragment_main_listView_forecast);

        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "Clicked on item: " + forecastAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                Intent detailActivity = new Intent(getActivity(),DetailActivity.class);
                detailActivity.putExtra(Intent.EXTRA_TEXT,forecastAdapter.getItem(position));
                startActivity(detailActivity);
            }
        });

        forecastAdapter = new ArrayAdapter<String>(rootView.getContext(),R.layout.listview_item_forecast,R.id.textView_item_listView_forecast,new ArrayList<String>());
        forecastListView.setAdapter(forecastAdapter);

        return rootView;
    }

    private void updateWeather()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = preferences.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        String unitStr = preferences.getString(getString(R.string.pref_unit_key), getString(R.string.pref_unit_default));
        Unit unit = Unit.values()[Integer.parseInt(unitStr)];

        WeatherConfiguration config = new WeatherConfiguration();
        config.NumberOfDays = 10;
        config.SearchString = location;
        config.TemperatureUnit = unit;

        new FetchWeatherTask().execute(config);
    }


    private class FetchWeatherTask extends AsyncTask<WeatherConfiguration,Void,String[]>
    {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(WeatherConfiguration... configurations) {
            WeatherConfiguration config = configurations[0];
            JsonRequest jsonRequest = new JsonRequest();
            jsonRequest.setCount(config.NumberOfDays);
            jsonRequest.setSearchString(config.SearchString);
            String response =  jsonRequest.Response();

            WeatherDataParser parser = new WeatherDataParser();
            try
            {
                TemperatureResult[] results = parser.getWeatherDataFromJson(response,jsonRequest.getCount());
                String[] list = new String[results.length];
                for(int i=0;i<results.length;i++)
                {
                    list[i] = results[i].toString(config.TemperatureUnit);
                }
                return list;
            }
            catch(JSONException e)
            {
                Log.e(LOG_TAG, "Can not convert to json", e);
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] results)
        {
            if(results == null || results.length <= 0)
                return;

            forecastAdapter.setNotifyOnChange(false);
            forecastAdapter.clear();

            for(String result: results)
                forecastAdapter.add(result);

            forecastAdapter.setNotifyOnChange(true);
            forecastAdapter.notifyDataSetChanged();
        }
    }

    private class WeatherConfiguration
    {
        public int NumberOfDays=7;
        public String SearchString= DEFAULT_SEARCH_STRING;
        public Unit TemperatureUnit = Unit.Metric;
    }
}
