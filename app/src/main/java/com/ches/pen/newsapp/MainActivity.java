package com.ches.pen.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    /**
     * URL for news data from the newsapi dataset
     */
    private static final String NEWS_REQUEST_URL =
            "https://content.guardianapis.com/search?";
    private static final int NEWS_LOADER_ID = 1;
    public static String TAG = "Main Activity: ";
    private TextView emptyView;
    private ProgressBar progress;
    private SwipeRefreshLayout swipeContainer;
    private boolean isConnected;
    /**
     * Adapter for the list of news stories
     */
    private NewsAdapter mAdapter;
    private ListView newsListView;
    private LoaderManager mLoaderManager;

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        //Build URL for our query
        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("page-size", "8");
        uriBuilder.appendQueryParameter("api-key", "4175f891-97ef-44ab-ba2c-e7ecd7bdbaa8");
        uriBuilder.appendQueryParameter("show-fields", "byline");
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItemList) {
        Log.i(TAG, "Load finished");
        swipeContainer.setRefreshing(false);
        // Clear the adapter of previous news data
        mAdapter.clear();
        //Notify the user if there's no data to populate the list
        emptyView.setText(getResources().getText(R.string.no_data));
        progress.setVisibility(View.GONE);

        // If there is a valid list of {@link News Item}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsItemList != null && !newsItemList.isEmpty()) {
            mAdapter.addAll(newsItemList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        Log.i(TAG, "Loader reset");
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getBaseContext();
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        //Wire up views
        progress = (ProgressBar) findViewById(R.id.progress_indicator);
        newsListView = (ListView) findViewById(R.id.list);
        emptyView = (TextView) findViewById(R.id.empty_data);
        //Set listview to display empty data when there is no data
        newsListView.setEmptyView(emptyView);
        // Create a new adapter that takes an empty list of news stories as input
        mAdapter = new NewsAdapter(this, new ArrayList<NewsItem>());
        //attach adapted to listview
        newsListView.setAdapter(mAdapter);
        //set up link to news stories with intent on click
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem currentNewsStory = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentNewsStory.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(intent);
            }
        });

        mLoaderManager = getLoaderManager();
        //pull-to-refresh functionality
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //this method is called on refresh
                triggerLoad();
            }
        });
        //inital query of data to set up first display of listview.
        triggerLoad();
    }

    private void triggerLoad() {
        //Method to query and load data into listview. Restart call is used to allow for refreshing.
        if (isConnected) {
            mLoaderManager = getLoaderManager();
            mLoaderManager.restartLoader(NEWS_LOADER_ID, null, this);
        } else {
            emptyView.setText(getResources().getText(R.string.connection_error));
        }
    }
}