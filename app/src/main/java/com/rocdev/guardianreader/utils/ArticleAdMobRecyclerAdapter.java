package com.rocdev.guardianreader.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
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

/**
 * Created by piet on 08-06-17.
 *
 */

public class ArticleAdMobRecyclerAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String EMPTY_STRING = "";
    private static final String DATE_FORMAT_IN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DATE_FORMAT_OUT = "d MMMM yyyy HH:mm";

    private ArrayList<ItemWrapper> wrappedItems;
    private List<Article> articles;
    private AdView adView;
    private Context context;
    private boolean articleIsSaved;
    private ArticleAdMobRecyclerAdapterListener mListener;
    private int currentAdPosition;


    public ArticleAdMobRecyclerAdapter(Context context, List<Article> articles, AdView adView) {
        this.adView = adView;
        this.articles = articles;
        this.context = context;
        if (context instanceof ArticleAdMobRecyclerAdapterListener) {
            mListener = (ArticleAdMobRecyclerAdapterListener) context;
        } else {
            throw new RuntimeException("Activity should implement " +
                    "ArticleAdMobRecyclerAdapterListener");
        }
        wrappedItems = new ArrayList<>();
        fillWrappedItems();
        wrappedItems.add(new ItemWrapper(adView));
    }


    public void notifyAdapterDataSetChanged(List<Article> articles) {
        this.articles = articles;
        wrappedItems.clear();
        currentAdPosition = 0;
        fillWrappedItems();
        notifyDataSetChanged();
    }

    private void fillWrappedItems() {
        for (Article article : articles) {
            wrappedItems.add(new ItemWrapper(article));
        }
        if (!wrappedItems.isEmpty()) {

                wrappedItems.remove(currentAdPosition);

            currentAdPosition = wrappedItems.size();
            wrappedItems.add(currentAdPosition, new ItemWrapper(adView));

        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case ItemWrapper.TYPE_NORMAL:
                View article = inflater.inflate(R.layout.article_list_item, parent, false);
                return new ItemViewHolder(article);
            case ItemWrapper.TYPE_AD:
                //fall through
            default:
                View ad = inflater.inflate(R.layout.ad_list_item, parent, false);
                return new AdViewHolder(ad);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return wrappedItems.get(position).type;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemWrapper item = wrappedItems.get(position);
        switch (item.type) {
            case ItemWrapper.TYPE_NORMAL:
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                final Article article = item.article;
                Picasso.with(context).cancelRequest(itemViewHolder.imgView);
                if ((article != null ? article.getThumbUrl() : null) != null) {
                    Picasso.with(context).load(article.getThumbUrl()).into(itemViewHolder.imgView);
                }
                itemViewHolder.title.setText(article != null ? article.getTitle() : EMPTY_STRING);
                itemViewHolder.date.setText(formatDateTime(article != null ? article.getDate() : EMPTY_STRING));
                itemViewHolder.section.setText(article != null ? article.getSection() : EMPTY_STRING);
                itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (article != null) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
                        }
                    }
                });
                itemViewHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        String title = "";
                        String message = "";
                        Drawable icon = null;
                        if (article != null) {
                            if (article.get_ID() == -1) {
                                articleIsSaved = false;
                                title = "Save article";
                                message = "Do you want to save this article?";
                                icon = ResourcesCompat.getDrawable(context.getResources(),
                                        R.drawable.ic_archive_black_18dp, null);
                            } else {
                                articleIsSaved = true;
                                title = "Delete article";
                                message = "Do you want to delete this article from your saved articles list? " +
                                        "This cannot be undone.";
                                icon = ResourcesCompat.getDrawable(context.getResources(),
                                        R.drawable.ic_unarchive_black_18dp, null);
                            }
                        }
                        builder
                                .setTitle(title)
                                .setMessage(message)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (articleIsSaved) {
                                            QueryUtils.deleteArticle(article, context);
                                            mListener.removeSavedArticle(article);
                                        } else {
                                            QueryUtils.insertArticle(article, context);
                                        }

                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .setIcon(icon)
                                .show();
                        return true;
                    }
                });
                break;

            case ItemWrapper.TYPE_AD:
                AdViewHolder adViewHolder = (AdViewHolder) holder;
                AdRequest adRequest = new AdRequest.Builder()
                        //TODO remove before production
                        .addTestDevice("211FE69AEAB7D31887757EB42F4B4FE7")
                        .build();
                adViewHolder.adView.setAdListener(new AdLoadListener(adViewHolder));
                adViewHolder.adView.loadAd(adRequest);
        }
    }

    private String formatDateTime(String input) {
        SimpleDateFormat sdfIn = new SimpleDateFormat(DATE_FORMAT_IN);
        sdfIn.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateIn = new Date();
        try {
            dateIn = sdfIn.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdfOut = new SimpleDateFormat(DATE_FORMAT_OUT, Locale.getDefault());
        return sdfOut.format(dateIn);
    }

    @Override
    public int getItemCount() {
        return wrappedItems.size();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        View container;
        ImageView imgView;
        TextView title;
        TextView date;
        TextView section;


        ItemViewHolder(View itemView) {
            super(itemView);
            this.container = itemView;
            this.imgView = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.titleTextView);
            this.date = (TextView) itemView.findViewById(R.id.dateTextView);
            this.section = (TextView) itemView.findViewById(R.id.sectionTextView);
        }
    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {
        AdView adView;
        ImageView placeholder;
        AdViewHolder(View itemView) {
            super(itemView);
            this.adView = (AdView) itemView.findViewById(R.id.adView);
            this.placeholder = (ImageView) itemView.findViewById(R.id.adViewPlaceholder);
        }
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

    private class AdLoadListener extends AdListener {
        AdView adView;
        ImageView placeholder;

        AdLoadListener(AdViewHolder viewHolder) {
            adView = viewHolder.adView;
            placeholder = viewHolder.placeholder;
        }

        @Override
        public void onAdLoaded() {
            adView.setVisibility(View.VISIBLE);
            placeholder.setVisibility(View.GONE);
            super.onAdLoaded();
        }
    }

    public interface ArticleAdMobRecyclerAdapterListener {
        void removeSavedArticle(Article article);
    }


}
