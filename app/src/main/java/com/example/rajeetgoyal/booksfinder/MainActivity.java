package com.example.rajeetgoyal.booksfinder;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final int BOOKS_LOADER_ID = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Member Variables
    private BooksAdapter mBooksAdapter;
    private TextView mEmptyView;
    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private ListView booksListView;
    private String mBooksUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mEmptyView = findViewById(R.id.empty_view);
        mProgressBar = findViewById(R.id.loading_spinner);
        mImageView = findViewById(R.id.books_search_image_view);
        booksListView = findViewById(R.id.list);

        mImageView.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mBooksUrl = "https://www.googleapis.com/books/v1/volumes?q=" + query;
            Log.i(LOG_TAG, mBooksUrl);
            displaySearchResults();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_menu).getActionView();
        searchView.setSearchableInfo(
                Objects.requireNonNull(searchManager).getSearchableInfo(getComponentName()));
        return true;
    }

    private void displaySearchResults() {
        mImageView.setVisibility(View.GONE);
        ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            mEmptyView.setText(R.string.no_internet);
        } else {
            LoaderManager loaderManager = getLoaderManager();
            mBooksAdapter = new BooksAdapter(this, new ArrayList<Book>());
            loaderManager.initLoader(BOOKS_LOADER_ID, null, booksLoaderCallbacks);
            booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Book currentBook = mBooksAdapter.getItem(position);
                    String url = Objects.requireNonNull(currentBook).getInfoLink();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    try {
                        startActivity(intent);
                        try {
                            intent.setPackage("com.android.vending");
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.e(LOG_TAG, "No Activity found in google play");
                        }
                    } catch (ActivityNotFoundException e) {
                        Log.e(LOG_TAG, "invalid url");
                    }
                }
            });
            booksListView.setAdapter(mBooksAdapter);
        }
    }

    private final LoaderManager.LoaderCallbacks<List<Book>> booksLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Book>>() {
        @Override
        public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
            mProgressBar.setVisibility(View.VISIBLE);
            return new BooksLoader(MainActivity.this, mBooksUrl);
        }

        @Override
        public void onLoadFinished(Loader<List<Book>> loader, List<Book> booksList) {
            Log.i(LOG_TAG, "loadInBackground Executed Successfully");
            mBooksAdapter.clear();
            mProgressBar.setVisibility(View.INVISIBLE);
            if (booksList != null && !booksList.isEmpty()) {
                mBooksAdapter.addAll(booksList);
            } else {
                mEmptyView.setText(R.string.no_books);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Book>> loader) {
            mBooksAdapter.clear();
        }
    };
}
