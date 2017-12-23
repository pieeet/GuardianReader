package com.rocdev.guardianreader.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.models.Section;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.rocdev.guardianreader.R.id.moreButton;

/**
 * Created by piet on 08-06-17.
 *
 */

public class ArticleAdMobRecyclerAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String EMPTY_STRING = "";
    private static final int VIEW_TYPE_ARTICLE = 0;
    private static final int VIEW_TYPE_AD = 1;
    private static final int VIEW_TYPE_BUTTON = 2;

    private static final int HEADLINES_AUS = 0;
    private static final int HEADLINES_UK = 1;
    private static final int HEADLINES_US = 2;
    private static final int HEADLINES_INT = 3;


    private final List<Object> listItems;
    private int currentSection;

    private final Context context;
    private ArticleAdMobRecyclerAdapterListener mListener;
    private ButtonViewHolder buttonViewHolder;


    public ArticleAdMobRecyclerAdapter(Context context, List<Object> listItems, int section) {
        this.listItems = listItems;
        this.context = context;
        this.currentSection = section;

        if (context instanceof ArticleAdMobRecyclerAdapterListener) {
            mListener = (ArticleAdMobRecyclerAdapterListener) context;
        } else {
            throw new RuntimeException("Activity should implement " +
                    "ArticleAdMobRecyclerAdapterListener");
        }
    }

    public void notifyAdapterDataSetChanged(List<Object> items, int section) {
        currentSection = section;
        if (buttonViewHolder != null) {
            buttonViewHolder.button.setEnabled(true);
            buttonViewHolder.button.setText(R.string.more_button_text);
        }
        listItems.clear();
        listItems.addAll(items);
        super.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case VIEW_TYPE_ARTICLE:
                View article = inflater.inflate(R.layout.article_list_item_constraint_layout, parent, false);
                return new ItemViewHolder(article);
            case VIEW_TYPE_AD:
                View adView = inflater.inflate(R.layout.ad_list_item, parent, false);
                return new AdViewHolder(adView);
            case VIEW_TYPE_BUTTON:
                // fall through
            default:
                View button = inflater.inflate(R.layout.more_button_list_item, parent, false);
                return new ButtonViewHolder(button);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        Object item = listItems.get(position);
        if (item instanceof Article) {
            type = VIEW_TYPE_ARTICLE;
        } else if (item instanceof AdView) {
            type = VIEW_TYPE_AD;
        } else {
            type = VIEW_TYPE_BUTTON;
        }
        return type;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_ARTICLE:
                Article article = (Article) listItems.get(position);
                setArticleItemHolder((ItemViewHolder) holder, article);
                break;
            case VIEW_TYPE_AD:
                setAdItemHolder(holder, position);
                break;
            case VIEW_TYPE_BUTTON:
                // fall through
            default:
                setButtonItemHolder((ButtonViewHolder) holder);
        }
    }

    private void setArticleItemHolder(ItemViewHolder holder, final Article article) {
        Picasso.with(context).cancelRequest(holder.imgView);
        if ((article != null ? article.getThumbUrl() : null) != null) {
            Picasso.with(context).load(article.getThumbUrl()).into(holder.imgView);
        }
        holder.title.setText(article != null ? article.getTitle() : EMPTY_STRING);
        holder.date.setText(ArticleDateUtils.formatDateTime(article != null ? article.getDate() :
                EMPTY_STRING));
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

    private void setAdItemHolder(RecyclerView.ViewHolder holder, int position) {
        AdViewHolder adViewHolder = (AdViewHolder) holder;
        AdView adView = (AdView) listItems.get(position);
        ViewGroup adContainer = (ViewGroup) adViewHolder.itemView;
        adContainer.removeAllViews();
        if (adView.getParent() != null) {
            ((ViewGroup)adView.getParent()).removeView(adView);
        }
        adContainer.addView(adView);

    }

    private void setButtonItemHolder(ButtonViewHolder holder) {
        this.buttonViewHolder = holder;
        if (currentSection <= Section.HEADLINES_INT.ordinal()) {
            switch (currentSection) {
                case HEADLINES_AUS:
                    buttonViewHolder.button.setText(R.string.more_button_aus);
                    break;
                case HEADLINES_UK:
                    buttonViewHolder.button.setText(R.string.more_button_uk);
                    break;
                case HEADLINES_US:
                    buttonViewHolder.button.setText(R.string.more_button_us);
                    break;
                case HEADLINES_INT:
                    buttonViewHolder.button.setText(R.string.more_button_world);
                    break;
            }
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onMoreNews(currentSection);
                }
            });

        } else {
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttonViewHolder.button.setEnabled(false);
                    buttonViewHolder.button.setText(R.string.more_button_disabled);
                    mListener.onMoreArticles();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        final View container;
        final ImageView imgView;
        final TextView title;
        final TextView date;
        final TextView section;

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

        AdViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class ButtonViewHolder extends RecyclerView.ViewHolder {
        final Button button;

        ButtonViewHolder(View itemView) {
            super(itemView);
            this.button = (Button) itemView.findViewById(moreButton);
        }
    }

    public interface ArticleAdMobRecyclerAdapterListener {
        void onMoreArticles();
        void onMoreNews(int section);
        void onItemClicked(Article article);
        boolean onItemLongClicked(Article article);
    }
}
