package com.ches.pen.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Custom arraylist
 */

public class NewsAdapter extends ArrayAdapter<NewsItem> {

    public NewsAdapter(Context context, List<NewsItem> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);

        }
        //wire up textviews
        TextView title = (TextView) listItemView.findViewById(R.id.title);
        TextView author = (TextView) listItemView.findViewById(R.id.author);
        TextView category = (TextView) listItemView.findViewById(R.id.category);
        TextView date = (TextView) listItemView.findViewById(R.id.date);


        //get current
        NewsItem currentStory = getItem(position);

        //populate tvs
        title.setText(currentStory.getHeadline());
        //check this field exists
        if (currentStory.getAuthor() != null) {
            author.setText(currentStory.getAuthor());
        }
        category.setText(currentStory.getCategory());
        //check date is there
        if (currentStory.getDateTime() != null) {
            //set the date pattern in the String
            String datePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.US);
            ///Extract the date from the String as recived from json results
            String rawDate = currentStory.getDateTime();
            try {
                //parse and convert it to new format
                Date dateTime = dateFormat.parse(rawDate);
                SimpleDateFormat dateFormatDisplay = new SimpleDateFormat("dd, MMM, yyyy", Locale.US);
                //print the date in the date textview
                String publishedOn = listItemView.getResources().getString(R.string.publish_date);
                date.setText(publishedOn);
                date.append(" " + dateFormatDisplay.format(dateTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return listItemView;
    }
}