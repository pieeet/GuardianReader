package com.rocdev.guardianreader.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by piet on 27-12-16.
 *
 */

public class ArticleAdapter extends ArrayAdapter<Article> {

    private Context context;

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.article_list_item, parent, false);
        }
        Article article = getItem(position);
        ImageView imgView = (ImageView) listItemView.findViewById(R.id.thumbnail);
        // anders laadt verkeerde plaatje
        //http://stackoverflow.com/questions/25429683/picasso-loads-pictures-to-the-wrong-imageview-in-a-list-adapter
        Picasso.with(context).cancelRequest(imgView);
        if ((article != null ? article.getThumbUrl() : null) != null) {
            Picasso.with(context).load(article.getThumbUrl()).into(imgView);
        }
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.titleTextView);
        titleTextView.setText(article != null ? article.getTitle() : "");
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.dateTextView);
        dateTextView.setText(formatDateTime(article != null ? article.getDate() : ""));
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.sectionTextView);
        sectionTextView.setText(article != null ? article.getSection() : "");
        return listItemView;
    }

    private String formatDateTime(String input) {
        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdfIn.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateIn = new Date();
        try {
            dateIn = sdfIn.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdfOut = new SimpleDateFormat("d MMMM yyyy HH:mm", Locale.getDefault());
        return sdfOut.format(dateIn);
    }
}
