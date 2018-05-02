package com.example.fatoumeh.shumanatorguardianfootballfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fatoumeh on 01/03/2018.
 */

public class FootballFeedLoader extends AsyncTaskLoader<List<FootballFeed>> {
    private String [] url;
    public FootballFeedLoader(Context context, String...url) {
        super(context);
        this.url=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<FootballFeed> loadInBackground() {
        if (url.length<1 || url[0]==null) {
            return null;
        }
        ArrayList<FootballFeed> footballFeedList=QueryUtils.fetchNewsFromURL(url[0], getContext());
        return footballFeedList;
    }
}
