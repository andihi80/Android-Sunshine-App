package com.example.andreas.sunshine;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;


public class DetailActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if( savedInstanceState == null )
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if( id == R.id.action_settings ) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment
    {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String SHARE_HASHTAG = " # SunshineApp";
        private String mForecastStr;
        private ShareActionProvider _actionProvider;


        public DetailFragment()
        {}



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


            Intent  intent = getActivity().getIntent();
            mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            ((TextView)rootView.findViewById(R.id.textView_fragment_detail)).setText(mForecastStr);

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.fragment_detail,menu);

            MenuItem item = menu.findItem(R.id.action_share);
            _actionProvider = (ShareActionProvider) item.getActionProvider();
            setShareIntent(createShareForecastIntent());
        }

        private Intent createShareForecastIntent()
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, mForecastStr + SHARE_HASHTAG);
            return intent;
        }

        private void setShareIntent(Intent intent)
        {
            if(_actionProvider != null)
                _actionProvider.setShareIntent(intent);
            else
                Log.d(LOG_TAG,"Share action provider is null!");
        }
    }
}
