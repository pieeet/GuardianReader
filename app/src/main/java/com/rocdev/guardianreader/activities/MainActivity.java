package com.rocdev.guardianreader.activities;

import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.rocdev.guardianreader.database.Contract;
import com.rocdev.guardianreader.utils.ArticleLoader;
import com.rocdev.guardianreader.fragments.ArticlesFragment;
import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.Secret;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<List<Article>>,
        ArticlesFragment.OnFragmentInteractionListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /*******************************
     * CONSTANTS
     *******************************/
    private static final int CONTENT_CONTAINER = R.id.content_container;
    private static final String API_KEY = Secret.getApiKey();
//    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /*******************************
     * INSTANCE VARIABLES
     *******************************/
    private int loaderId;
    private String[] titles;
    private Loader<List<Article>> mLoader;
    private int currentSection;
    private int currentPage;
    private boolean isNewList;
    private boolean isEditorsPicks;
    private ArrayList<Article> articles;
    private ArticlesFragment fragment;
    private int listPosition;
    private String searchQuery;
    private SharedPreferences mSharedPreferences;
    private int defaultEdition;
    private NavigationView navigationView;
    private Menu mMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titles = getResources().getStringArray(R.array.titles);
        setContentView(R.layout.activity_main);
        setPreferences();
        initNavigation();

        //retrieve data in case of screen rotation
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            initInstanceState();
        }
        initFragment();
        if (articles.isEmpty()) {
            refreshUI();
        }
    }

    private void setPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        defaultEdition = Integer.parseInt(mSharedPreferences.getString(
                getString(R.string.pref_key_default_edition), "3"));
    }

    private void initNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        for (Section section : Section.values()) {
            if (!section.equals(Section.SEARCH)) {
                setNavBarSection(section);
            }
        }
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        articles = savedInstanceState.getParcelableArrayList("articles");
        currentPage = savedInstanceState.getInt("currentPage");
        currentSection = savedInstanceState.getInt("currentSection");
        loaderId = savedInstanceState.getInt("loaderId");
        isEditorsPicks = savedInstanceState.getBoolean("isEditorPicks");
        listPosition = savedInstanceState.getInt("listPosition");
        onLoaderReset(mLoader);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titles[currentSection]);
        }
    }

    private void initInstanceState() {
        articles = new ArrayList<>();
        isEditorsPicks = true;
        currentPage = 1;
        loaderId = 1;
        listPosition = 0;
        // the section that is shown on app start
        currentSection = defaultEdition;
    }

    private void initFragment() {
        fragment = ArticlesFragment.newInstance(articles, listPosition, !isEditorsPicks);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(CONTENT_CONTAINER, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkConnection()) {
            refreshUI();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // invoke search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            currentSection = Section.SEARCH.ordinal();
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
            refreshUI();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("articles", articles);
        outState.putInt("currentSection", currentSection);
        outState.putInt("currentPage", currentPage);
        outState.putInt("loaderId", loaderId);
        outState.putBoolean("isEditorPicks", isEditorsPicks);
        outState.putInt("listPosition", listPosition);
        super.onSaveInstanceState(outState);
    }

    private void refreshUI() {
        showProgressAnimations();
        try {
            fragment.showNoSavedArticlesContainer(currentSection == Section.SAVED.ordinal()
                    && articles.isEmpty());
        } catch (NullPointerException ignored) {}
        String title = titles[currentSection];
        if (checkConnection()) {
            //noinspection ConstantConditions
            if (currentSection == Section.SEARCH.ordinal()) {
                title = searchQuery;
                if (searchQuery.length() > 12) {
                    title = searchQuery.substring(0, 12) + "...";
                }
            }
            loaderId++;
            getLoaderManager().initLoader(loaderId, null, this);
        } else {
            if (currentSection == Section.SAVED.ordinal()) {
                getLoaderManager().initLoader(loaderId, null, this);
            } else {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!checkConnection()) {
                            Toast.makeText(MainActivity.this, "No network. Try again later", Toast.LENGTH_LONG).show();
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(R.string.title_no_network);
                            }
                            articles.clear();
                            fragment.notifyArticlesChanged(true, false);
                            stopRefreshButtonAnimation();
                        } else {
                            refreshUI();
                        }
                    }
                }, 2000);
            }
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_refresh:
                isNewList = true;
                currentPage = 1;
                refreshUI();
                break;
            case R.id.action_rate:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(
                        getString(R.string.google_play_url)));
                startActivity(browserIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressAnimations() {
        fragment.showProgressContainer(true);
        startRefreshButtonAnimation();
    }

    private void startRefreshButtonAnimation() {
        MenuItem m = null;
        if (mMenu != null) {
            m = mMenu.findItem(R.id.action_refresh);
        }
        if (m != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
            rotation.setRepeatCount(Animation.INFINITE);
            iv.startAnimation(rotation);
            m.setActionView(iv);
        }
    }

    private void stopRefreshButtonAnimation() {
        // Get our refresh item from the menu
        try {
            MenuItem m = mMenu.findItem(R.id.action_refresh);
            // Remove the animation.
            m.getActionView().clearAnimation();
            m.setActionView(null);
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        isNewList = true;
        currentPage = 1;
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_settings:
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    }
                }, 300);
                break;
            default:
                for (Section section : Section.values()) {
                    if (section.getIdNav() == id) {
                        if (currentSection != section.ordinal()) {
                            currentSection = section.ordinal();
                            refreshUI();
                        }
                    }
                }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        //build URI with specified parameters
        isEditorsPicks = currentSection <= Section.HEADLINES_INT.ordinal() ||
                currentSection == Section.SAVED.ordinal();
        Uri baseUri = Uri.parse(Section.values()[currentSection].getUrl());
        String uriString = baseUri.toString();
        if (currentSection != Section.SAVED.ordinal()) {
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("api-key", API_KEY);
            uriBuilder.appendQueryParameter("show-fields", "thumbnail");
            if (isEditorsPicks) {
                uriBuilder.appendQueryParameter("show-editors-picks", "true");
            } else {
                uriBuilder.appendQueryParameter("page", String.valueOf(currentPage));
                if (currentSection == Section.SEARCH.ordinal()) {
                    uriBuilder.appendQueryParameter("q", searchQuery);
                }
            }
            uriString = uriBuilder.toString();
        }
        return new ArticleLoader(this, uriString, isEditorsPicks);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        mLoader = loader;
        stopRefreshButtonAnimation();
        if (isNewList) {
            articles.clear();
        }
        for (Article article : data) {
            articles.add(article);
        }
        fragment.notifyArticlesChanged(isNewList, isEditorsPicks);
        fragment.showNoSavedArticlesContainer(currentSection == Section.SAVED.ordinal()
                && articles.isEmpty());
        fragment.showProgressContainer(false);
        onLoaderReset(mLoader);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        getLoaderManager().destroyLoader(loaderId);
    }

    @Override
    public void onArticleClicked(Article article) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
    }

    @Override
    public void onArticleLongClicked(Article article) {
        if (article.get_ID() == -1) {
            long id = insertArticle(article);
            if (id < 1) {
                Toast.makeText(this, "Error with saving article", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Article saved succefully", Toast.LENGTH_SHORT).show();
                article.set_ID(id);
            }
        } else {
            if (deleteArticle(article) < 1) {
                Toast.makeText(this, "Error with deleting article", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Deleted article", Toast.LENGTH_SHORT).show();
                if (currentSection == Section.SAVED.ordinal()) {
                    articles.remove(article);
                    fragment.notifyArticlesChanged(false, isEditorsPicks /* no morebutton */);
                    if (articles.isEmpty()) {
                        fragment.showNoSavedArticlesContainer(true);
                    }
                }
            }
        }
    }

    private long insertArticle(Article article) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.ArticleEntry.COLUMN_ARTICLE_DATE, article.getDate());
        contentValues.put(Contract.ArticleEntry.COLUMN_ARTICLE_SECTION, article.getSection());
        contentValues.put(Contract.ArticleEntry.COLUMN_ARTICLE_TITLE, article.getTitle());
        contentValues.put(Contract.ArticleEntry.COLUMN_ARTICLE_URL, article.getUrl());
        contentValues.put(Contract.ArticleEntry.COLUMN_THUMB_URL, article.getThumbUrl());
        Uri uri = getContentResolver().insert(Contract.ArticleEntry.CONTENT_URI, contentValues);
        return ContentUris.parseId(uri);
    }

    private int deleteArticle(Article article) {
        Uri uri = Uri.withAppendedPath(Contract.ArticleEntry.CONTENT_URI,
                String.valueOf(article.get_ID()));
        return getContentResolver().delete(uri, null, null);
    }

    @Override
    public void saveListPosition(int position) {
        listPosition = position;
    }


    @Override
    public void onMoreArticles() {
        showProgressAnimations();
        if (checkConnection()) {
            currentPage++;
            loaderId++;
            isNewList = false;
            getLoaderManager().initLoader(loaderId, null, this);
        } else {
            refreshUI();
        }
    }

    private boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void setNavBarSection(Section section) {
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(section.getIdNav())
                .setVisible(mSharedPreferences
                        .getBoolean(section.getPrefKey(), true));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mSharedPreferences = sharedPreferences;
        for (Section section : Section.values()) {
            if (section.getPrefKey().equals(key)) {
                setNavBarSection(section);
            }
        }
    }
}
