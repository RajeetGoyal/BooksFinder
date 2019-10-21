package com.example.rajeetgoyal.booksfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

class BooksAdapter extends ArrayAdapter<Book>  {

    // Constructor
    BooksAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.books_list_item, parent, false);
        }
        Book currentBook = getItem(position);
        ImageView imageView = listItemView.findViewById(R.id.thumbnail);
        Bitmap bitmap = Objects.requireNonNull(currentBook).getThumbnail();
        if (bitmap != null) {
            imageView.setImageBitmap(currentBook.getThumbnail());
        }
        TextView title = listItemView.findViewById(R.id.title);
        title.setText(currentBook.getBookTitle());
        TextView authors = listItemView.findViewById(R.id.authors);
        authors.setText(currentBook.getAuthors());
        TextView publisher = listItemView.findViewById(R.id.publisher);
        publisher.setText(currentBook.getPublisher());
        return listItemView;
    }
}
