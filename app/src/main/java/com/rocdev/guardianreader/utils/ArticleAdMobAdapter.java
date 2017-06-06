package com.rocdev.guardianreader.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import static com.rocdev.guardianreader.R.id.dateTextView;
import static com.rocdev.guardianreader.R.id.titleTextView;

/**
 * Created by piet on 04-06-17.
 *
 */

public class ArticleAdMobAdapter extends BaseAdapter {
    private static final String EMPTY_STRING = "";
    private static final String DATE_FORMAT_IN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DATE_FORMAT_OUT = "d MMMM yyyy HH:mm";

    private Context context;
    private ArrayList<ItemWrapper> mWrappedItems;
    private AdView adView;
    private List<Article> articles;
    private boolean articleIsSaved;
    private ArticleAdMobAdapterListener mListener;
    private int currentAdPosition;

    public ArticleAdMobAdapter(Context context, List<Article> articles, AdView adView) {
        this.context = context;
        if (context instanceof ArticleAdMobAdapterListener) {
            mListener = (ArticleAdMobAdapterListener) context;
        } else {
            throw new IllegalArgumentException("Activity should implement ArticleAdMobAdapterListener");
        }
        this.adView = adView;
        this.articles = articles;
        mWrappedItems = new ArrayList<>();
        fillWrappedItems();
    }

    @Override
    public void notifyDataSetChanged() {
        mWrappedItems.clear();
        currentAdPosition = 0;
        fillWrappedItems();
        super.notifyDataSetChanged();
    }

    private void fillWrappedItems() {
        for (Article article : articles) {
            mWrappedItems.add(new ItemWrapper(article));
        }
        if (!mWrappedItems.isEmpty()) {
            if (currentAdPosition > 0) {
                mWrappedItems.remove(currentAdPosition);
            }
            currentAdPosition = mWrappedItems.size();
            mWrappedItems.add(currentAdPosition,new ItemWrapper(adView));

        }

    }

    @Override
    public int getCount() {
        return mWrappedItems.size();
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
            if (view != null) view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (type == ItemWrapper.TYPE_NORMAL) {
            Article article = item.article;
            setListItemData(holder, article);
        } else if (type == ItemWrapper.TYPE_AD) {
            AdRequest adRequest = new AdRequest.Builder()
                    //TODO remove before production
                    .addTestDevice("211FE69AEAB7D31887757EB42F4B4FE7")
                    .build();
            holder.adView.setAdListener(new AdLoadListener(holder));
            holder.adView.loadAd(adRequest);
        }
        return view;
    }

    private ViewHolder createArticleHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.container = view.findViewById(R.id.article_item_container);
        holder.imgView = (ImageView) view.findViewById(R.id.thumbnail);
        holder.title = (TextView) view.findViewById(titleTextView);
        holder.date = (TextView) view.findViewById(dateTextView);
        holder.section = (TextView) view.findViewById(R.id.sectionTextView);
        return holder;
    }

    private ViewHolder createAdViewHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.adView = (AdView) view.findViewById(R.id.adView);
        holder.placeholder = (ImageView) view.findViewById(R.id.adViewPlaceholder);
        return holder;
    }

    private void setListItemData(ViewHolder holder, final Article article) {
        //http://stackoverflow.com/questions/25429683/picasso-loads-pictures-to-the-wrong-imageview-in-a-list-adapter
        Picasso.with(context).cancelRequest(holder.imgView);
        if ((article != null ? article.getThumbUrl() : null) != null) {
            Picasso.with(context).load(article.getThumbUrl()).into(holder.imgView);
        }
        holder.title.setText(article != null ? article.getTitle() : EMPTY_STRING);
        holder.date.setText(formatDateTime(article != null ? article.getDate() : EMPTY_STRING));
        holder.section.setText(article != null ? article.getSection() : EMPTY_STRING);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (article != null) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
                }
            }
        });
        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    QueryUtils.insertArticle(article,context);
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        })
                        .setIcon(icon)
                        .show();
                return true;
            }
        });
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
        View container;
        ImageView imgView;
        TextView title;
        TextView date;
        TextView section;
        AdView adView;
        ImageView placeholder;
    }

    private class AdLoadListener extends AdListener {
        AdView adView;
        ImageView placeholder;

        AdLoadListener(ViewHolder viewHolder) {
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

    public interface ArticleAdMobAdapterListener {
        void removeSavedArticle(Article article);
    }
}
