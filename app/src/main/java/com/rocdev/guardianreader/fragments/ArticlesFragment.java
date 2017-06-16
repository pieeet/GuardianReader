package com.rocdev.guardianreader.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.utils.ArticleAdMobRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticlesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ArticlesFragment extends Fragment {

//    private ListView listView;
    private RecyclerView mRecyclerView;
    private View progressContainer;
    private View noSavedArticlesContainer;
    private View listContainer;
    private List<Article> articles;
    private List<Object> listItems;
    private ArticleAdMobRecyclerAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private boolean hasMoreButton;
    private int listPosition;
    private LayoutInflater inflater;

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
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_articles_recycler_view, container, false);
        initViews(rootView);
        return rootView;
    }

    /**
     * initializes views
     *
     * @param view the root view
     */
    private void initViews(View view) {
        populateListItems();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new ArticleAdMobRecyclerAdapter(getContext(), listItems, hasMoreButton);
        mRecyclerView.setAdapter(adapter);
        listContainer = view.findViewById(R.id.listContainer);

        //scroll to correct listposition on screen rotation
        if (listPosition > 0) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.smoothScrollToPosition(listPosition);

                }
            });
        }
        progressContainer = view.findViewById(R.id.progressContainer);
        noSavedArticlesContainer = view.findViewById(R.id.noSavedArticlesContainer);
        if (!articles.isEmpty()) {
            showProgressContainer(false);
        }
    }

    @Override
    public void onPause() {
        mListener.saveListPosition(listPosition);
        super.onPause();
    }

    /*
    check https://github.com/googleads/googleads-mobile-android-examples
     */

    private void populateListItems() {
        listItems = new ArrayList<>();
        if (articles != null) {
            for (Article article: articles) {
                listItems.add(article);
            }
        }
        View adContainer = inflater.inflate(R.layout.ad_list_item, null);
        AdView adView = (AdView) adContainer.findViewById(R.id.adView);
        listItems.add(adView);
        loadAdView(listItems.size() - 1);
        if (hasMoreButton) {
            View buttonView = inflater.inflate(R.layout.more_button_list_item, null);
            listItems.add(buttonView);
        }
    }

    private void loadAdView(final int index) {
        if (index >= listItems.size()) {
            return;
        }
        Object item = listItems.get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be an AdView");
        }
        final AdView adView = (AdView) item;
        AdRequest.Builder builder = new AdRequest.Builder();
        //TODO disable/enable before/after production
        builder.addTestDevice(getString(R.string.test_device_code_nexus9));
        builder.addTestDevice(getString(R.string.test_device_code_nexus5x));
        adView.loadAd(builder.build());
    }


    /**
     * method called from Activity when articles has changed
     *
     * @param isNewList     if true scroll to top of list
     * @param isEditorPicks if true hide moreButton
     */
    public void notifyArticlesChanged(boolean isNewList, boolean isEditorPicks) {
        hasMoreButton = !isEditorPicks;
        populateListItems();

        adapter.notifyAdapterDataSetChanged(hasMoreButton, listItems);
        showProgressContainer(false);
        if (isNewList) {
            listPosition = 0;
            mRecyclerView.smoothScrollToPosition(listPosition);
        }
    }

    public void showListContainer(boolean show) {
        try {
            if (show) {
                listContainer.setVisibility(View.VISIBLE);
            } else {
                listContainer.setVisibility(View.INVISIBLE);
            }
        } catch (NullPointerException ignored) {}
    }

    public void showProgressContainer(boolean show) {
        try {
            if (show) {
                progressContainer.setVisibility(View.VISIBLE);
            } else {
                progressContainer.setVisibility(View.GONE);
            }
        } catch (NullPointerException ignored) {}
    }

    public void showNoSavedArticlesContainer(boolean show) {
        if (show) {
            noSavedArticlesContainer.setVisibility(View.VISIBLE);
        } else {
            noSavedArticlesContainer.setVisibility(View.GONE);
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
    }
}
