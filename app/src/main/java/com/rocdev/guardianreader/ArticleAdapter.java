package com.rocdev.guardianreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by piet on 27-12-16.
 *
 */

class ArticleAdapter extends ArrayAdapter<Article> {

    private Context context;


    ArticleAdapter(Context context, List<Article> articles) {
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
        assert article != null;
        if (article.getThumbUrl() != null) {
            Picasso.with(context).load(article.getThumbUrl()).into(imgView);
        }

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.titleTextView);
        titleTextView.setText(article.getTitle());
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.dateTextView);
        String dateStrOriginal = article.getDate();
        int indexT = dateStrOriginal.indexOf("T");
        String dateString = dateStrOriginal.substring(0, indexT);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dateString);
            sdf = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
            dateString = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String timeString = dateStrOriginal.substring(indexT + 1, indexT + 6);


        dateTextView.setText(dateString + " " + timeString);
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.sectionTextView);
        sectionTextView.setText(article.getSection());

        return  listItemView;
    }
}
