package com.rocdev.guardianreader.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private static final String TIME_ZONE_IN = "UTC";

    private ArrayList<ItemWrapper> wrappedItems;
    private List<Article> articles;
    private AdView adView;
    private Button moreButton;
    private Context context;
    private ArticleAdMobRecyclerAdapterListener mListener;
    private int currentAdPosition;
    private ButtonViewHolder buttonViewHolder;


    public ArticleAdMobRecyclerAdapter(Context context, List<Article> articles,
                                       AdView adView, boolean hasMoreButton, Button moreButton) {
        this.adView = adView;
        this.moreButton = moreButton;
        this.articles = articles;
        this.context = context;
        if (context instanceof ArticleAdMobRecyclerAdapterListener) {
            mListener = (ArticleAdMobRecyclerAdapterListener) context;
        } else {
            throw new RuntimeException("Activity should implement " +
                    "ArticleAdMobRecyclerAdapterListener");
        }
        wrappedItems = new ArrayList<>();
        populateWrappedItems(hasMoreButton);
    }

    public void notifyAdapterDataSetChanged(boolean hasMoreButton) {
        wrappedItems.clear();
        currentAdPosition = 0;
        populateWrappedItems(hasMoreButton);
        super.notifyDataSetChanged();

    }

    private void populateWrappedItems(boolean hasMoreButton) {
        for (Article article : articles) {
            wrappedItems.add(new ItemWrapper(article));
        }
        currentAdPosition = wrappedItems.size();
        wrappedItems.add(currentAdPosition, new ItemWrapper(adView));

        if (hasMoreButton) {
            ItemWrapper buttonWrapper = new ItemWrapper(moreButton);
            wrappedItems.add(currentAdPosition + 1, buttonWrapper);
            if (buttonViewHolder != null) {
                buttonViewHolder.button.setEnabled(true);
                buttonViewHolder.button.setText(R.string.more_button_text);
            }
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
                View ad = inflater.inflate(R.layout.ad_list_item, parent, false);
                return new AdViewHolder(ad);
            case ItemWrapper.TYPE_BUTTON:
                // fall through
            default:
                View button = inflater.inflate(R.layout.more_button_list_item, parent, false);
                return new ButtonViewHolder(button);
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
                final Article article = item.article;
                setArticleItemHolder((ItemViewHolder) holder, article);
                break;

            case ItemWrapper.TYPE_AD:
                setAdItemHolder((AdViewHolder) holder);
                break;
            case ItemWrapper.TYPE_BUTTON:
                setButtonViewHolder((ButtonViewHolder) holder);
        }
    }

    private void setArticleItemHolder(ItemViewHolder holder, final Article article) {
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
                mListener.onItemClicked(article);
            }
        });
        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return mListener.onItemLongClicked(article);
            }
        });
    }

    private void setAdItemHolder(AdViewHolder adViewHolder) {
        AdRequest adRequest = new AdRequest.Builder()
                //TODO remove before production
                .addTestDevice(context.getString(R.string.test_device_code))
                .build();
        adViewHolder.adView.setAdListener(new AdLoadListener(adViewHolder));
        adViewHolder.adView.loadAd(adRequest);
    }

    private void setButtonViewHolder(ButtonViewHolder holder) {
        this.buttonViewHolder = holder;
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonViewHolder.button.setEnabled(false);
                buttonViewHolder.button.setText("...");
                mListener.onMoreArticles();
            }
        });
    }

    private String formatDateTime(String input) {
        SimpleDateFormat sdfIn = new SimpleDateFormat(DATE_FORMAT_IN);
        sdfIn.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_IN));
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

    private static class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button button;

        ButtonViewHolder(View itemView) {
            super(itemView);
            this.button = (Button) itemView.findViewById(R.id.moreButton);
        }
    }

    private class ItemWrapper {
        static final int TYPE_NORMAL = 0;
        static final int TYPE_AD = 1;
        static final int TYPE_BUTTON = 2;

        Article article;
        AdView adItem;
        Button button;
        int type;

        ItemWrapper(Article article) {
            this.type = TYPE_NORMAL;
            this.article = article;
        }

        ItemWrapper(AdView adView) {
            this.type = TYPE_AD;
            this.adItem = adView;
        }

        ItemWrapper(Button button) {
            this.type = TYPE_BUTTON;
            this.button = button;
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
        void onMoreArticles();
        void onItemClicked(Article article);
        boolean onItemLongClicked(Article article);
    }
}
