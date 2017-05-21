package com.rocdev.guardianreader;

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
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


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

    //urls for different news editions
    private static final String URL_HEADLINES_AUS = "https://content.guardianapis.com/au";
    private static final String URL_HEADLINES_UK = "https://content.guardianapis.com/uk";
    private static final String URL_HEADLINES_US = "https://content.guardianapis.com/us";
    private static final String URL_HEADLINES_INT = "https://content.guardianapis.com/international";
    //The urls for the different sections
    private static final String URL_ART_AND_DESIGN = "https://content.guardianapis.com/artanddesign";
    private static final String URL_BOOKS = "https://content.guardianapis.com/books";
    private static final String URL_BUSINESS = "https://content.guardianapis.com/business";
    private static final String URL_CULTURE = "https://content.guardianapis.com/culture";
    private static final String URL_EDUCATION = "https://content.guardianapis.com/education";
    private static final String URL_FILM = "https://content.guardianapis.com/film";
    private static final String URL_FOOTBALL = "https://content.guardianapis.com/football";
    private static final String URL_LAW = "https://content.guardianapis.com/law";
    private static final String URL_LIFE_AND_STYLE = "https://content.guardianapis.com/lifeandstyle";
    private static final String URL_MEDIA = "https://content.guardianapis.com/media";
    private static final String URL_MONEY = "https://content.guardianapis.com/money";
    private static final String URL_MUSIC = "https://content.guardianapis.com/music";
    private static final String URL_OPINION = "https://content.guardianapis.com/commentisfree";
    private static final String URL_POLITICS = "https://content.guardianapis.com/politics";
    private static final String URL_SCIENCE = "https://content.guardianapis.com/science";
    private static final String URL_SOCIETY = "https://content.guardianapis.com/society";
    private static final String URL_SPORT = "https://content.guardianapis.com/sport";
    private static final String URL_STAGE = "https://content.guardianapis.com/stage";
    private static final String URL_TECH = "https://content.guardianapis.com/technology";
    private static final String URL_TRAVEL = "https://content.guardianapis.com/travel";
    private static final String URL_TV_RADIO = "https://content.guardianapis.com/tv-and-radio";
    private static final String URL_WEATHER = "https://content.guardianapis.com/weather";
    private static final String URL_NEWS_WORLD = "https://content.guardianapis.com/world";
    private static final String URL_SEARCH = "https://content.guardianapis.com/search";
    private static final String URL_NEWS_AUS = "https://content.guardianapis.com/australia-news";
    private static final String URL_NEWS_UK = "https://content.guardianapis.com/uk-news";
    private static final String URL_NEWS_US = "https://content.guardianapis.com/us-news";

    private static final String API_KEY = Secret.getApiKey();

    private static final String[] SECTIONS = {
            //news headlines
            URL_HEADLINES_AUS,
            URL_HEADLINES_UK,
            URL_HEADLINES_US,
            URL_HEADLINES_INT,
            // sections
            URL_ART_AND_DESIGN,
            URL_BOOKS,
            URL_BUSINESS,
            URL_CULTURE,
            URL_EDUCATION,
            URL_FILM,
            URL_FOOTBALL,
            URL_LAW,
            URL_LIFE_AND_STYLE,
            URL_MEDIA,
            URL_MONEY,
            URL_MUSIC,
            URL_NEWS_AUS,
            URL_NEWS_UK,
            URL_NEWS_US,
            URL_NEWS_WORLD,
            URL_OPINION,
            URL_POLITICS,
            URL_SCIENCE,
            URL_SOCIETY,
            URL_SPORT,
            URL_STAGE,
            URL_TECH,
            URL_TRAVEL,
            URL_TV_RADIO,
            URL_WEATHER,
            URL_SEARCH
    };

    //editions headlines (Editor Picks)
    private static final int SECTION_HEADLINES_AUS = 0;
    private static final int SECTION_HEADLINES_UK = 1;
    private static final int SECTION_HEADLINES_US = 2;
    private static final int SECTION_HEADLINES_WORLD = 3;
    //sections
    private static final int SECTION_ART_AND_DESIGN = 4;
    private static final int SECTION_BOOKS = 5;
    private static final int SECTION_BUSINESS = 6;
    private static final int SECTION_CULTURE = 7;
    private static final int SECTION_EDUCATION = 8;
    private static final int SECTION_FILM = 9;
    private static final int SECTION_FOOTBALL = 10;
    private static final int SECTION_LAW = 11;
    private static final int SECTION_LIFE_AND_STYLE = 12;
    private static final int SECTION_MEDIA = 13;
    private static final int SECTION_MONEY = 14;
    private static final int SECTION_MUSIC = 15;
    private static final int SECTION_NEWS_AUS = 16;
    private static final int SECTION_NEWS_UK = 17;
    private static final int SECTION_NEWS_US = 18;
    private static final int SECTION_NEWS_WORLD = 19;
    private static final int SECTION_OPINION = 20;
    private static final int SECTION_POLITICS = 21;
    private static final int SECTION_SCIENCE = 22;
    private static final int SECTION_SOCIETY = 23;
    private static final int SECTION_SPORT = 24;
    private static final int SECTION_STAGE = 25;
    private static final int SECTION_TECH = 26;
    private static final int SECTION_TRAVEL = 27;
    private static final int SECTION_TV_AND_RADIO = 28;
    private static final int SECTION_WEATHER = 29;
    private static final int SECTION_SEARCH = 30;

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

//    private static final String LOG_TAG = MainActivity.class.getSimpleName();


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
            selectSection(currentSection);
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
        if(!checkConnection()) {
            fragment.showNoNetworkWarning();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // invoke search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            currentSection = SECTION_SEARCH;
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
            selectSection(currentSection);
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

    /**
     * set news section and update articles list
     * in onLoadFinished the list will be passed to fragment
     *
     * @param section the news section
     */
    private void selectSection(int section) {
        currentSection = section;
        isNewList = true;
        // avoid using same loaderId
        loaderId++;
        if (checkConnection()) {
            fragment.showProgressBar();
            currentPage = 1;
            //noinspection ConstantConditions
            if (currentSection == SECTION_SEARCH) {
                String title = searchQuery;
                if (searchQuery.length() > 12) {
                    title = searchQuery.substring(0, 12) + "...";
                }
                //noinspection ConstantConditions
                getSupportActionBar().setTitle(title);
            } else {
                //noinspection ConstantConditions
                getSupportActionBar().setTitle(titles[currentSection]);
            }
            // initialize Loader
            getLoaderManager().initLoader(loaderId, null, this);
        } else {
            fragment.showNoNetworkWarning();
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
                if (checkConnection()) {
                    fragment.showProgressBar();
                    currentPage = 1;
                    loaderId++;
                    isNewList = true;
                    getLoaderManager().initLoader(loaderId, null, this);
                } else {
                    fragment.showNoNetworkWarning();
                }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_headlines_aus:
                if (currentSection != SECTION_HEADLINES_AUS) {
                    selectSection(SECTION_HEADLINES_AUS);
                }
                break;
            case R.id.nav_headlines_uk:
                if (currentSection != SECTION_HEADLINES_UK) {
                    selectSection(SECTION_HEADLINES_UK);
                }
                break;
            case R.id.nav_headlines_us:
                if (currentSection != SECTION_HEADLINES_US) {
                    selectSection(SECTION_HEADLINES_US);
                }
                break;
            case R.id.nav_headlines_int:
                if (currentSection != SECTION_HEADLINES_WORLD) {
                    selectSection(SECTION_HEADLINES_WORLD);
                }
                break;
            case R.id.nav_art_and_design:
                if (currentSection != SECTION_ART_AND_DESIGN) {
                    selectSection(SECTION_ART_AND_DESIGN);
                }
                break;
            case R.id.nav_books:
                if (currentSection != SECTION_BOOKS) {
                    selectSection(SECTION_BOOKS);
                }
                break;
            case R.id.nav_business:
                if (currentSection != SECTION_BUSINESS) {
                    selectSection(SECTION_BUSINESS);
                }
                break;
            case R.id.nav_culture:
                if (currentSection != SECTION_CULTURE) {
                    selectSection(SECTION_CULTURE);
                }
                break;
            case R.id.nav_education:
                if (currentSection != SECTION_EDUCATION) {
                    selectSection(SECTION_EDUCATION);
                }
                break;
            case R.id.nav_film:
                if (currentSection != SECTION_FILM) {
                    selectSection(SECTION_FILM);
                }
                break;
            case R.id.nav_football:
                if (currentSection != SECTION_FOOTBALL) {
                    selectSection(SECTION_FOOTBALL);
                }
                break;
            case R.id.nav_law:
                if (currentSection != SECTION_LAW) {
                    selectSection(SECTION_LAW);
                }
                break;
            case R.id.nav_life_and_style:
                if (currentSection != SECTION_LIFE_AND_STYLE) {
                    selectSection(SECTION_LIFE_AND_STYLE);
                }
                break;
            case R.id.nav_media:
                if (currentSection != SECTION_MEDIA) {
                    selectSection(SECTION_MEDIA);
                }
                break;
            case R.id.nav_money:
                if (currentSection != SECTION_MONEY) {
                    selectSection(SECTION_MONEY);
                }
                break;
            case R.id.nav_music:
                if (currentSection != SECTION_MUSIC) {
                    selectSection(SECTION_MUSIC);
                }
                break;
            case R.id.nav_news_australia:
                if (currentSection != SECTION_NEWS_AUS) {
                    selectSection(SECTION_NEWS_AUS);
                }
                break;
            case R.id.nav_news_uk:
                if (currentSection != SECTION_NEWS_UK) {
                    selectSection(SECTION_NEWS_UK);
                }
                break;
            case R.id.nav_news_us:
                if (currentSection != SECTION_NEWS_US) {
                    selectSection(SECTION_NEWS_US);
                }
                break;
            case R.id.nav_news_world:
                if (currentSection != SECTION_NEWS_WORLD) {
                    selectSection(SECTION_NEWS_WORLD);
                }
                break;
            case R.id.nav_opinion:
                if (currentSection != SECTION_OPINION) {
                    selectSection(SECTION_OPINION);
                }
                break;
            case R.id.nav_politics:
                if (currentSection != SECTION_POLITICS) {
                    selectSection(SECTION_POLITICS);
                }
                break;
            case R.id.nav_science:
                if (currentSection != SECTION_SCIENCE) {
                    selectSection(SECTION_SCIENCE);
                }
                break;
            case R.id.nav_society:
                if (currentSection != SECTION_SOCIETY) {
                    selectSection(SECTION_SOCIETY);
                }
                break;
            case R.id.nav_sport:
                if (currentSection != SECTION_SPORT) {
                    selectSection(SECTION_SPORT);
                }
                break;
            case R.id.nav_stage:
                if (currentSection != SECTION_STAGE) {
                    selectSection(SECTION_STAGE);
                }
                break;
            case R.id.nav_tech:
                if (currentSection != SECTION_TECH) {
                    selectSection(SECTION_TECH);
                }
                break;
            case R.id.nav_travel:
                if (currentSection != SECTION_TRAVEL) {
                    selectSection(SECTION_TRAVEL);
                }
                break;
            case R.id.nav_tv_and_radio:
                if (currentSection != SECTION_TV_AND_RADIO) {
                    selectSection(SECTION_TV_AND_RADIO);
                }
                break;
            case R.id.nav_weather:
                if (currentSection != SECTION_WEATHER) {
                    selectSection(SECTION_WEATHER);
                }
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        //build URI with specified parameters
        isEditorsPicks = currentSection <= SECTION_HEADLINES_WORLD;
        Uri baseUri = Uri.parse(SECTIONS[currentSection]);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("api-key", API_KEY);
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        if (isEditorsPicks) {
            uriBuilder.appendQueryParameter("show-editors-picks", "true");
        } else {
            uriBuilder.appendQueryParameter("page", String.valueOf(currentPage));
            if (currentSection == SECTION_SEARCH) {
                uriBuilder.appendQueryParameter("q", searchQuery);
            }
        }
        return new ArticleLoader(this, uriBuilder.toString(), isEditorsPicks);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        mLoader = loader;
        if (isNewList) {
            articles.clear();
        }
        for (Article article : data) {
            articles.add(article);
        }
        fragment.notifyArticlesChanged(isNewList, isEditorsPicks);
        fragment.hideProgressContainer();
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
        if (checkConnection()) {
            //fragment.showMoreButton(false);
            fragment.showProgressBar();
            currentPage++;
            loaderId++;
            isNewList = false;
            getLoaderManager().initLoader(loaderId, null, this);
        } else {
            fragment.showNoNetworkWarning();
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
