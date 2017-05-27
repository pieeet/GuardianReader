package com.rocdev.guardianreader.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.rocdev.guardianreader.activities.MainActivity;
import com.rocdev.guardianreader.utils.ArticleAdapter;
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

    private View listContainer;
    private ListView listView;
    private View progressContainer;
    private View noSavedArticlesContainer;
    private Button moreButton;
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
            adapter = new ArticleAdapter(getActivity(), articles);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onArticleClicked(articles.get(i));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return onArticleLongClicked(articles.get(i));
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
    public void notifyArticlesChanged(boolean isNewList, boolean isEditorPicks) {
        hasMoreButton = !isEditorPicks;
        showMoreButton(false);
        adapter.notifyDataSetChanged();
        progressContainer.setVisibility(View.GONE);
        listContainer.setVisibility(View.VISIBLE);
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
     * Invokes going to article in browser or Guardian App (if installed)
     *
     * @param article the selected article
     */
    public void onArticleClicked(Article article) {
        if (null != mListener) {
            mListener.onArticleClicked(article);
        }
    }

    public boolean onArticleLongClicked(final Article article) {
        if (null != mListener) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            String title;
            String message;
            Drawable icon;
            if (article.get_ID() == -1) {
                title = "Save article";
                message = "Do you want to save this article?";
                icon = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_archive_black_18dp, null);

            } else {
                title = "Delete article";
                message = "Do you want to delete this article from your saved list? " +
                        "This cannot be undone.";
                icon = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_unarchive_black_18dp, null);
            }
            builder
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mListener.onArticleLongClicked(article);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // do nothing
                        }
                    })
                    .setIcon(icon)
                    .show();
        }
        return true;
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

        void onArticleLongClicked(Article article);

        void saveListPosition(int position);

        void onMoreArticles();
    }
}
