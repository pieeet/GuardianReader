package com.rocdev.guardianreader.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Section;

import java.util.List;



/**
 * Created by piet on 26-05-17.
 *
 */

public class SectionsAdapter extends ArrayAdapter<Section> {

    private final Context context;
    private int selectedPosition;

    public static final int NO_SELECTION = -1;

    public SectionsAdapter(@NonNull Context context, List<Section> sections) {
        super(context, 0, sections);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View sectionView = convertView;
        ViewHolder holder;
        if (sectionView == null) {
            sectionView = LayoutInflater.from(getContext())
                    .inflate(R.layout.section_list_item, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) sectionView.findViewById(R.id.section_image_icon);
            holder.title = (TextView) sectionView.findViewById(R.id.section_title_textview);
            sectionView.setTag(holder);
        } else {
            holder = (ViewHolder) sectionView.getTag();
        }
        Section section = getItem(position);
        if (section != null) {
            holder.icon.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), section.getIcon(), null));
            holder.title.setText(context.getResources().getString(section.getTitle()));
            if (position == selectedPosition) {
                holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorHoloRed));
                holder.icon.setColorFilter(ContextCompat.getColor(context, R.color.colorHoloRed));
            } else {
                holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorTextListitemTitle));
                holder.icon.setColorFilter(ContextCompat.getColor(context, R.color.colorTextListitemTitle));
            }
        }
        return sectionView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView title;
    }

    public void setSelectedEdition(int position) {
        if (position == Section.SEARCH.ordinal()) selectedPosition = NO_SELECTION;
        else selectedPosition = position;
        notifyDataSetChanged();
    }
}
