package com.rocdev.guardianreader.utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.models.Section;

import java.util.List;


/**
 * Created by piet on 26-05-17.
 *
 */

public class SectionsAdapter extends ArrayAdapter<Section> {

    Context context;


    public SectionsAdapter(@NonNull Context context, List<Section> sections) {
        super(context, 0, sections);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View sectionView = convertView;
        if (sectionView == null) {
            sectionView = LayoutInflater.from(getContext())
                    .inflate(R.layout.section_list_item, parent, false);
        }
        Section section = getItem(position);
        ImageView imageView = (ImageView) sectionView.findViewById(R.id.section_image_icon);
        imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), section.getIcon(), null));
        TextView textView = (TextView) sectionView.findViewById(R.id.section_title_textview);
        textView.setText(context.getResources().getString(section.getTitle()));
        return sectionView;
    }
}
