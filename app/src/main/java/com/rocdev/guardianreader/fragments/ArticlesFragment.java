package com.rocdev.guardianreader.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.rocdev.guardianreader.utils.ArticleAdMobAdapter;
//import com.rocdev.guardianreader.utils.ArticleAdapter;
import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticlesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ArticlesFragment extends Fragment implements AbsListView.OnScrollListener {

    private ListView listView;
    private View progressContainer;
    private View noSavedArticlesContainer;
    private Button moreButton;
    private List<Article> articles;
    private ArticleAdMobAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private boolean hasMoreButton;
    private int listPosition;
    private boolean isLoading; /*prevent more button appearing while loading due to scroll listener*/

    /**
     * required (Framework) empty constructor
     */
    public ArticlesFragment() {}


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param articles reference to articles in Activity to be shown. Can be empty, not null.
     * @return A new instance of fragment.
     */
    public static ArticlesFragment newInstance(@NonNull ArrayList<Article> articles,
                                               int listPosition, boolean hasMoreButton) {
        ArticlesFragment fragment = new ArticlesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("articles", articles);
        args.putInt("listPosition", listPosition);
        args.putBoolean("hasMoreButton", hasMoreButton);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articles = getArguments().getParcelableArrayList("articles");
            listPosition = getArguments().getInt("listPosition");
            hasMoreButton = getArguments().getBoolean("hasMoreButton");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_articles, container, false);
        initViews(rootView);
        initListeners();
        return rootView;
    }

    /**
     * initializes views
     *
     * @param view the root view
     */
    private void initViews(View view) {
        AdView adView = new AdView(getContext());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new ArticleAdMobAdapter(getContext(), articles, adView);
        listView.setAdapter(adapter);

        //scroll to correct listposition on screen rotation
        if (listPosition > 0) {
            listView.post(new Runnable() {
                @Override
                public void run() {
                    listView.smoothScrollToPosition(listPosition);

                }
            });
        }
        progressContainer = view.findViewById(R.id.progressContainer);
        noSavedArticlesContainer = view.findViewById(R.id.noSavedArticlesContainer);
        moreButton = (Button) view.findViewById(R.id.moreButton);
        moreButton.setVisibility(View.GONE);
        if (!articles.isEmpty()) {
            showProgressContainer(false);
        }
    }

    /**
     * initializes listeners
     */
    private void initListeners() {
        listView.setOnScrollListener(this);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreButton(false);
                onMoreArticles();
            }
        });
    }


    @Override
    public void onPause() {
        mListener.saveListPosition(listPosition);
        super.onPause();
    }


    /**
     * method called from Activity when articles has changed
     *
     * @param isNewList     if true scroll to top of list
     * @param isEditorPicks if true hide moreButton
     */
    public void notifyArticlesChanged(boolean isNewList, boolean isEditorPicks) {
        isLoading = false;
        hasMoreButton = !isEditorPicks;
        showMoreButton(false);
        adapter.notifyDataSetChanged();
        progressContainer.setVisibility(View.GONE);
        if (isNewList) {
            listPosition = 0;
            listView.smoothScrollToPosition(listPosition);
        }
    }

    public void showProgressContainer(boolean show) {
        try {
            if (show) {
                progressContainer.setVisibility(View.VISIBLE);
            } else {
                progressContainer.setVisibility(View.GONE);
            }
        } catch (NullPointerException ignored) {
        }
    }

    public void showNoSavedArticlesContainer(boolean show) {
        if (show) {
            noSavedArticlesContainer.setVisibility(View.VISIBLE);
        } else {
            noSavedArticlesContainer.setVisibility(View.GONE);
        }
    }

    /**
     * toggles more articles button
     *
     * @param show true show, false hide
     */
    protected void showMoreButton(boolean show) {
        try {
            if (show) {
                moreButton.setVisibility(View.VISIBLE);
            } else {
                moreButton.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * invokes loading more articles
     */
    public void onMoreArticles() {
        isLoading = true;
        if (mListener != null) {
            mListener.onMoreArticles();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /*******************************
     * OnScrollListener methods
     *******************************/

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
    }

    /**
     * Shows or hides the more articles button
     *
     * @param absListView      the listview
     * @param firstVisibleItem first visible listitem
     * @param visibleItemCount number of visible items
     * @param totalItemCount   total number of items
     */
    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem != 0) {
            listPosition = firstVisibleItem + visibleItemCount;
        }

        if (absListView.getId() == listView.getId() && hasMoreButton && !articles.isEmpty()) {
            int lastItem = firstVisibleItem + visibleItemCount;
            if (lastItem >= totalItemCount) {
                if (!isLoading) showMoreButton(true);
            } else {
                showMoreButton(false);
            }
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void saveListPosition(int position);

        void onMoreArticles();
    }
}
