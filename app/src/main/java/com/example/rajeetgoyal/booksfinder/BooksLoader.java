package com.example.rajeetgoyal.booksfinder;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

class BooksLoader extends AsyncTaskLoader<List<Book>> {

    private static String mUrl;

    private static final String LOG_TAG = BooksLoader.class.getSimpleName();

    BooksLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        Log.i(LOG_TAG,"loadInBackground is called");
        if (mUrl == null) {
            return null;
        }
        else{
            return(QueryUtils.fetchBooksData(mUrl));
        }

    }


}
