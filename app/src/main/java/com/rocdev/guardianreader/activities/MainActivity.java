package com.rocdev.guardianreader.activities;

import android.app.SearchManager;
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


import com.rocdev.guardianreader.utils.ArticleLoader;
import com.rocdev.guardianreader.fragments.ArticlesFragment;
import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.models.Section;

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
        loaderId = 1;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setNavBarSections();
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
        if (checkConnection()) {
            String title = titles[currentSection];
            //noinspection ConstantConditions
            if (currentSection == Section.SEARCH.ordinal()) {
                title = searchQuery;
                if (searchQuery.length() > 12) {
                    title = searchQuery.substring(0, 12) + "...";
                }
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
            loaderId++;
            getLoaderManager().initLoader(loaderId, null, this);
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    Toast.makeText(MainActivity.this, "No network. Try again later", Toast.LENGTH_LONG).show();
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(R.string.title_no_network);
                    }
                    articles.clear();
                    fragment.notifyArticlesChanged(true, false);
                    stopRefreshButtonAnimation();
                }
            }, 2000);
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
        int id = item.getItemId();
        switch (id) {
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
        // Handle navigation view item clicks here.
        isNewList = true;
        currentPage = 1;
        int id = item.getItemId();
        for (Section section : Section.values()) {
            if (section.getIdNav() == id) {
                if (currentSection != section.ordinal()) {
                    currentSection = section.ordinal();
                    refreshUI();
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
        isEditorsPicks = currentSection <= Section.HEADLINES_INT.ordinal();
        Uri baseUri = Uri.parse(Section.values()[currentSection].getUrl());
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
        return new ArticleLoader(this, uriBuilder.toString(), isEditorsPicks);
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
    public void saveListPosition(int position) {
        listPosition = position;
    }


    @Override
    public void onMoreArticles() {
        showProgressAnimations();
        if (checkConnection()) {
            //fragment.showMoreButton(false);
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


    private void setNavBarSections() {
        Menu navMenu = navigationView.getMenu();

        //Headlines
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_aus_headlines), true)) {
            navMenu.findItem(R.id.nav_headlines_aus).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_headlines_aus).setVisible(true);
        }

        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_uk_headlines), true)) {
            navMenu.findItem(R.id.nav_headlines_uk).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_headlines_uk).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_us_headlines), true)) {
            navMenu.findItem(R.id.nav_headlines_us).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_headlines_us).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_int_headlines), true)) {
            navMenu.findItem(R.id.nav_headlines_int).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_headlines_int).setVisible(true);
        }

        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_art_design), true)) {
            navMenu.findItem(R.id.nav_art_and_design).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_art_and_design).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_books), true)) {
            navMenu.findItem(R.id.nav_books).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_books).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_business), true)) {
            navMenu.findItem(R.id.nav_business).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_business).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_culture), true)) {
            navMenu.findItem(R.id.nav_culture).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_culture).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_education), true)) {
            navMenu.findItem(R.id.nav_education).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_education).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_film), true)) {
            navMenu.findItem(R.id.nav_film).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_film).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_football), true)) {
            navMenu.findItem(R.id.nav_football).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_football).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_law), true)) {
            navMenu.findItem(R.id.nav_law).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_law).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_life_style), true)) {
            navMenu.findItem(R.id.nav_life_and_style).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_life_and_style).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_media), true)) {
            navMenu.findItem(R.id.nav_media).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_media).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_money), true)) {
            navMenu.findItem(R.id.nav_money).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_money).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_music), true)) {
            navMenu.findItem(R.id.nav_music).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_music).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_news_australia), true)) {
            navMenu.findItem(R.id.nav_news_australia).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_news_australia).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_news_uk), true)) {
            navMenu.findItem(R.id.nav_news_uk).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_news_uk).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_news_us), true)) {
            navMenu.findItem(R.id.nav_news_us).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_news_us).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_news_world), true)) {
            navMenu.findItem(R.id.nav_news_world).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_news_world).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_opinion), true)) {
            navMenu.findItem(R.id.nav_opinion).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_opinion).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_politics), true)) {
            navMenu.findItem(R.id.nav_politics).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_politics).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_science), true)) {
            navMenu.findItem(R.id.nav_science).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_science).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_society), true)) {
            navMenu.findItem(R.id.nav_society).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_society).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_sport), true)) {
            navMenu.findItem(R.id.nav_sport).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_sport).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_stage), true)) {
            navMenu.findItem(R.id.nav_stage).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_stage).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_tech), true)) {
            navMenu.findItem(R.id.nav_tech).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_tech).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_travel), true)) {
            navMenu.findItem(R.id.nav_travel).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_travel).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_tv_radio), true)) {
            navMenu.findItem(R.id.nav_tv_and_radio).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_tv_and_radio).setVisible(true);
        }
        if (!mSharedPreferences.getBoolean(getString(R.string.pref_key_weather), true)) {
            navMenu.findItem(R.id.nav_weather).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_weather).setVisible(true);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mSharedPreferences = sharedPreferences;
        String[] keyParts = key.split("_");
        if (keyParts.length > 1) {
            if (keyParts[1].equals("section")) {
                setNavBarSections();
            }
        }
    }
}
