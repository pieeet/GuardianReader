package com.rocdev.guardianreader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticlesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ArticlesFragment extends Fragment implements AbsListView.OnScrollListener {

    private View listContainer;
    private ListView listView;
    private View warningContainer;
    private Button moreButton;
    private View progressView;
    private TextView noNetworkTextView;
    private List<Article> articles;
    private ArticleAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private boolean hasMoreButton;
    private int listPosition;


    /**
     * required (Framework) empty constructor
     */
    public ArticlesFragment() {
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param articles reference to articles in Activity to be shown. Can be empty, not null.
     * @return A new instance of fragment.
     */
    public static ArticlesFragment newInstance(@NonNull ArrayList<Article> articles, int listPosition) {
        ArticlesFragment fragment = new ArticlesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("articles", articles);
        args.putInt("listPosition", listPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articles = getArguments().getParcelableArrayList("articles");
            listPosition = getArguments().getInt("listPosition");
            adapter = new ArticleAdapter(getActivity(), articles);
            hasMoreButton = true;
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
     * @param view the root view
     */
    private void initViews(View view) {
        listContainer = view.findViewById(R.id.listContainer);
        listView = (ListView) view.findViewById(R.id.listView);
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
        warningContainer = view.findViewById(R.id.warningcontainer);
        moreButton = (Button) view.findViewById(R.id.moreButton);
        moreButton.setVisibility(View.GONE);
        progressView = view.findViewById(R.id.progressBar);
        noNetworkTextView = (TextView) view.findViewById(R.id.noNetworkTextView);
        if (!articles.isEmpty()) {
            hideWarningContainer();
        }
    }

    /**
     * initializes listeners
     */
    private void initListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onArticleClicked(articles.get(i));
            }
        });
        listView.setOnScrollListener(this);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    protected void notifyArticlesChanged(boolean isNewList, boolean isEditorPicks) {
        hasMoreButton = !isEditorPicks;
        showMoreButton(false);
        adapter.notifyDataSetChanged();
        progressView.setVisibility(View.GONE);
        warningContainer.setVisibility(View.GONE);
        listContainer.setVisibility(View.VISIBLE);
        if (isNewList) {
            listPosition = 0;
            listView.smoothScrollToPosition(listPosition);
        }
    }


    /**
     * toggles progressBar
     */
    protected void showProgressBar() {
        try {
            warningContainer.setVisibility(View.VISIBLE);
            noNetworkTextView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * sets no network warning
     */
    protected void showNoNetworkWarning() {
        try {
            progressView.setVisibility(View.GONE);
            noNetworkTextView.setVisibility(View.VISIBLE);
            warningContainer.setVisibility(View.VISIBLE);
        } catch (NullPointerException ignored) {
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
        } catch (NullPointerException ignored) {
        }
    }


    protected void hideWarningContainer() {
        try {
            warningContainer.setVisibility(View.GONE);
        } catch (NullPointerException ignored) {}
    }


    /**
     * Invokes going to article in browser or Guardian App (if installed)
     *
     * @param article the selected article
     */
    public void onArticleClicked(Article article) {
        if (mListener != null) {
            mListener.onArticleClicked(article);
        }
    }

    /**
     * invokes loading more articles
     */
    public void onMoreArticles() {
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


    /****************
     * OnScrollListener methods
     ***************/

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
                showMoreButton(true);
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
        // DONE: Update argument type and name
        void onArticleClicked(Article article);
        void saveListPosition(int position);
        void onMoreArticles();
    }
}
