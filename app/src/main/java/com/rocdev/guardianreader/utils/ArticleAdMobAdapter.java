package com.rocdev.guardianreader.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.rocdev.guardianreader.R.id.dateTextView;
import static com.rocdev.guardianreader.R.id.titleTextView;

/**
 * Created by piet on 04-06-17.
 *
 */

public class ArticleAdMobAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<ItemWrapper> mWrappedItems;
    private AdView adView;
    private List<Article> articles;

    private static final int FIRST_ADD_POSITION = 9;
    private static final int ADD_INTERVAL = 10;

    public ArticleAdMobAdapter(Context context, List<Article> articles, AdView adView) {
        this.context = context;
        this.adView = adView;
        this.articles = articles;
        mWrappedItems = new ArrayList<>();
        fillWrappedItems();

    }

    @Override
    public void notifyDataSetChanged() {
        mWrappedItems.clear();
        fillWrappedItems();
        super.notifyDataSetChanged();
    }

    private void fillWrappedItems() {
        for (Article article: articles) {
            mWrappedItems.add(new ItemWrapper(article));
        }
        if (!mWrappedItems.isEmpty()) {
            for (int i = FIRST_ADD_POSITION; i < mWrappedItems.size(); i += ADD_INTERVAL) {
                mWrappedItems.add(i, new ItemWrapper(adView));
            }
        }
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public ItemWrapper getItem(int position) {
        if (mWrappedItems.isEmpty()) {
            return null;
        }
        return mWrappedItems == null ? null : mWrappedItems.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) != null) {
            return getItem(position).type;
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return ItemWrapper.TYPE_COUNT;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ItemWrapper item = getItem(position);
        final int type = item.type;
        ViewHolder holder = null;
        if (view == null) {
            if (type == ItemWrapper.TYPE_NORMAL) {
                view = LayoutInflater.from(context)
                        .inflate(R.layout.article_list_item, parent, false);
                holder = createArticleHolder(view);
            } else if (type == ItemWrapper.TYPE_AD) {
                view = LayoutInflater.from(context).inflate(R.layout.ad_list_item, parent, false);
                holder = createAdViewHolder(view);
            }
            if (view != null) {
                view.setTag(holder);
            }
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (type == ItemWrapper.TYPE_NORMAL) {
            Article article = item.article;
            setListItemData(holder, article);
        }
        else if (type == ItemWrapper.TYPE_AD) {
            AdRequest adRequest = new AdRequest.Builder().build();
            holder.adView.loadAd(adRequest);
        }
        return view;
    }

    private void setListItemData(ViewHolder holder, Article article) {
        //http://stackoverflow.com/questions/25429683/picasso-loads-pictures-to-the-wrong-imageview-in-a-list-adapter
        Picasso.with(context).cancelRequest(holder.imgView);
        if ((article != null ? article.getThumbUrl() : null) != null) {
            Picasso.with(context).load(article.getThumbUrl()).into(holder.imgView);
        }
        holder.title.setText(article != null ? article.getTitle() : "");
        holder.date.setText(formatDateTime(article != null ? article.getDate() : ""));
        holder.section.setText(article != null ? article.getSection() : "");
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


    private ViewHolder createArticleHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.imgView = (ImageView) view.findViewById(R.id.thumbnail);
        holder.title = (TextView) view.findViewById(titleTextView);
        holder.date = (TextView) view.findViewById(dateTextView);
        holder.section = (TextView) view.findViewById(R.id.sectionTextView);
        return holder;
    }

    private ViewHolder createAdViewHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.adView = (AdView) view.findViewById(R.id.adView);
        return holder;
    }

    private class ItemWrapper {
        static final int TYPE_NORMAL = 0;
        static final int TYPE_AD = 1;
        static final int TYPE_COUNT = 2;

        Article article;
        AdView adItem;
        int type;

        ItemWrapper(Article article) {
            this.type = TYPE_NORMAL;
            this.article = article;
        }
        ItemWrapper(AdView adView) {
            this.type = TYPE_AD;
            this.adItem = adView;
        }
    }

    private class ViewHolder {
        ImageView imgView;
        TextView title;
        TextView date;
        TextView section;
        AdView adView;
    }
}
