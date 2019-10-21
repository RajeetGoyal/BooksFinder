package com.example.rajeetgoyal.booksfinder;

import android.graphics.Bitmap;

class Book {

    private final Bitmap mThumbnail;
    private final String mTitle;
    private final String mAuthors;
    private final String mPublisher;
    private final String mInfoLink;


    Book(Bitmap thumbnail, String title, String authors, String publisher, String infoLink) {
        mThumbnail = thumbnail;
        mTitle = title;
        mAuthors = authors;
        mPublisher = publisher;
        mInfoLink = infoLink;
    }

    Bitmap getThumbnail() { return mThumbnail; }

    String getBookTitle() {
        return mTitle;
    }

    String getAuthors() {
        return mAuthors;
    }

    String getPublisher() {
        return mPublisher;
    }

    String getInfoLink() {
        return mInfoLink;
    }

}
