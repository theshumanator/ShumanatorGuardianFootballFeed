package com.example.fatoumeh.shumanatorguardianfootballfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FootballFeedActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<FootballFeed>> {

    private FootballFeedAdapter footballFeedAdapter;
    private TextView tvEmpty;
    private ProgressBar pbSpinner;
    private int minSetPageSize, maxSetPageSize;

    private String NEWS_URL;
    private String QUERY_Q;
    private String QUERY_FOOTBALL;
    private String QUERY_API_KEY;
    private String QUERY_API_KEY_VALUE;
    private String QUERY_SHOW_TAGS;
    private String QUERY_CONTRIBUTOR;
    private String QUERY_ORDER_BY;
    private String QUERY_PAGE_SIZE;
    private String QUERY_SECTION;
    private String QUERY_FORMAT;
    private String QUERY_JSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_feed);
        setUpStrings();
        ListView footballListView=findViewById(R.id.football_list_view);
        footballFeedAdapter=new FootballFeedAdapter(this, new ArrayList<FootballFeed>());
        tvEmpty=(TextView)findViewById(R.id.empty);
        pbSpinner=(ProgressBar)findViewById(R.id.loading_spinner);

        footballListView.setEmptyView(tvEmpty);
        footballListView.setAdapter(footballFeedAdapter);
        footballListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                FootballFeed selectedFootballFeed=footballFeedAdapter.getItem(position);
                String feedURL=selectedFootballFeed.getUrl();
                Intent openBrowser=new Intent(Intent.ACTION_VIEW);
                openBrowser.setData(Uri.parse(feedURL));
                startActivity(openBrowser);
            }
        });
        if (isConnected()){
            getLoaderManager().initLoader(1,null,this);
        } else {
            pbSpinner.setVisibility(View.GONE);
            tvEmpty.setText(R.string.no_internet);
        }
    }

    private void setUpStrings() {
        NEWS_URL=getString(R.string.base_url);
        QUERY_Q=getString(R.string.query);
        QUERY_FOOTBALL=getString(R.string.query_football);
        QUERY_API_KEY=getString(R.string.query_api_key);
        QUERY_API_KEY_VALUE=getString(R.string.query_api_key_values);
        QUERY_SHOW_TAGS=getString(R.string.query_show_tags);
        QUERY_CONTRIBUTOR=getString(R.string.query_contributor);
        QUERY_ORDER_BY=getString(R.string.query_order_by);
        QUERY_PAGE_SIZE=getString(R.string.query_page_size);
        QUERY_SECTION=getString(R.string.query_section);
        QUERY_FORMAT=getString(R.string.query_format);
        QUERY_JSON=getString(R.string.query_json);
        minSetPageSize=Integer.parseInt(getString(R.string.min_page_size));
        maxSetPageSize=Integer.parseInt(getString(R.string.max_page_size));
    }

    @Override
    public Loader<List<FootballFeed>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy=sharedPreferences.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        boolean filterByFootball=sharedPreferences.getBoolean(getString(R.string.settings_filter_by_football_key),
                true);
        int maxPageSize=Integer.parseInt(sharedPreferences.getString(
                getString(R.string.settings_max_news_key),
                getString(R.string.settings_max_news_default)));

        //if the user passes max size out of the 1-50 range then we set it to default
        String maxPageSizeToURI=getString(R.string.settings_max_news_default);
        if (minSetPageSize<=maxPageSize && maxPageSize<=maxSetPageSize) {
            maxPageSizeToURI=String.valueOf(maxPageSize);
        }

        //build the uri
        Uri rootUri=Uri.parse(NEWS_URL);
        Uri.Builder uriBuilder = rootUri.buildUpon();
        uriBuilder.appendQueryParameter(QUERY_Q, QUERY_FOOTBALL);
        uriBuilder.appendQueryParameter(QUERY_API_KEY, QUERY_API_KEY_VALUE);
        uriBuilder.appendQueryParameter(QUERY_SHOW_TAGS, QUERY_CONTRIBUTOR);
        uriBuilder.appendQueryParameter(QUERY_ORDER_BY, orderBy);
        uriBuilder.appendQueryParameter(QUERY_PAGE_SIZE, maxPageSizeToURI);
        if (filterByFootball) {
            uriBuilder.appendQueryParameter(QUERY_SECTION,QUERY_FOOTBALL);
        }
        uriBuilder.appendQueryParameter(QUERY_FORMAT, QUERY_JSON);
        return new FootballFeedLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<FootballFeed>> loader, List<FootballFeed> footballFeeds) {
        pbSpinner.setVisibility(View.GONE);
        //even though we are setting it here but it wont appear unless view is empty
        tvEmpty.setText(R.string.no_news);
        footballFeedAdapter.clear();
        if (footballFeeds!=null && !footballFeeds.isEmpty()) {
            footballFeedAdapter.addAll(footballFeeds);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<FootballFeed>> loader) {
        footballFeedAdapter.clear();
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        return isConnected;
    }

    @Override
    //without this, we wont see the option to select settings
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
