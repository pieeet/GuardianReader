package com.rocdev.guardianreader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<List<Article>>,
        ArticlesFragment.OnFragmentInteractionListener {

    protected static final int CONTENT_CONTAINER = R.id.content_container;

    //The urls for the different sections
    private static final String URL_NEWS = "https://content.guardianapis.com/search";
    private static final String URL_EDITORS_PICKS = "http://content.guardianapis.com/uk";
    private static final String URL_ART_AND_DESIGN = "https://content.guardianapis.com/artanddesign";
    private static final String URL_BOOKS = "https://content.guardianapis.com/books";
    private static final String URL_BUSINESS = "https://content.guardianapis.com/business";
    private static final String URL_CULTURE = "https://content.guardianapis.com/culture";
    private static final String URL_EDUCATION = "https://content.guardianapis.com/education";
    private static final String URL_FILM = "https://content.guardianapis.com/film";
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
    private static final String URL_WORLD = "https://content.guardianapis.com/world";

    //TODO Search
    //private static final String URL_SEARCH = "https://content.guardianapis.com/search";

    private static final String API_KEY = "test";

    private static final String[] SECTIONS = {
            //news sections
            URL_NEWS, URL_WORLD, URL_EDITORS_PICKS,
            // sections
            URL_ART_AND_DESIGN,
            URL_BOOKS, URL_BUSINESS,
            URL_CULTURE,
            URL_EDUCATION,
            URL_FILM,
            URL_LAW, URL_LIFE_AND_STYLE,
            URL_MEDIA, URL_MONEY, URL_MUSIC,
            URL_OPINION,
            URL_POLITICS,
            URL_SCIENCE, URL_SOCIETY, URL_SPORT, URL_STAGE,
            URL_TECH, URL_TRAVEL, URL_TV_RADIO,
            URL_WEATHER
    };

    // should be in strings.xml for multi language (TODO)
    private static final String[] TITLES = {
            "Latest news",
            "World News",
            "Editors Picks",
            "Art & Design",
            "Books",
            "Business",
            "Culture",
            "Education",
            "Film",
            "Law",
            "Life & Style",
            "Media",
            "Money",
            "Music",
            "Opinion",
            "Politics",
            "Science",
            "Society",
            "Sport",
            "Stage",
            "Technology",
            "Travel",
            "TV & Radio",
            "Weather"
    };

    private static final int SECTION_NEWS = 0;
    private static final int SECTION_WORLD_NEWS = 1;
    private static final int SECTION_EDITORS_PICKS = 2;
    private static final int SECTION_ART_AND_DESIGN = 3;
    private static final int SECTION_BOOKS = 4;
    private static final int SECTION_BUSINESS = 5;
    private static final int SECTION_CULTURE = 6;
    private static final int SECTION_EDUCATION = 7;
    private static final int SECTION_FILM = 8;
    private static final int SECTION_LAW = 9;
    private static final int SECTION_LIFE_AND_STYLE = 10;
    private static final int SECTION_MEDIA = 11;
    private static final int SECTION_MONEY = 12;
    private static final int SECTION_MUSIC = 13;
    private static final int SECTION_OPINION = 14;
    private static final int SECTION_POLITICS = 15;
    private static final int SECTION_SCIENCE = 16;
    private static final int SECTION_SOCIETY = 17;
    private static final int SECTION_SPORT = 18;
    private static final int SECTION_STAGE = 19;
    private static final int SECTION_TECH = 20;
    private static final int SECTION_TRAVEL = 21;
    private static final int SECTION_TV_AND_RADIO = 22;
    private static final int SECTION_WEATHER = 23;

    /*********
     * INSTANCE VARIABLES
     ******/
    private int loaderId;
    private Loader<List<Article>> mLoader;
    private int currentSection;
    private int currentPage;
    private boolean isNewList;
    private boolean isEditorsPicks;
    private ArrayList<Article> articles;
    private ArticlesFragment fragment;
    private int listPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loaderId = 1;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //screen rotation
        if (savedInstanceState != null) {
            articles = savedInstanceState.getParcelableArrayList("articles");
            currentPage = savedInstanceState.getInt("currentPage");
            currentSection = savedInstanceState.getInt("currentSection");
            loaderId = savedInstanceState.getInt("loaderId");
            onLoaderReset(mLoader);
            isEditorsPicks = savedInstanceState.getBoolean("isEditorPicks");
            listPosition = savedInstanceState.getInt("listPosition");
        } else {
            articles = new ArrayList<>();
            isEditorsPicks = false;
            currentSection = SECTION_NEWS;
            currentPage = 1;
            loaderId = 1;
            listPosition = 0;
        }

        fragment = ArticlesFragment.newInstance(articles, listPosition);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(CONTENT_CONTAINER, fragment)
                .commit();
        if (articles.isEmpty()) {
            selectSection(currentSection);
        }
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(TITLES[currentSection]);
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
        isNewList = true;
        loaderId++;
        if (checkConnection()) {
            fragment.showProgressBar();
            currentPage = 1;
            currentSection = section;
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(TITLES[currentSection]);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            if (checkConnection()) {
                fragment.showProgressBar();
                currentPage = 1;
                loaderId++;
                isNewList = true;
                getLoaderManager().initLoader(loaderId, null, this);
            } else {
                fragment.showNoNetworkWarning();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_news:
                if (currentSection != SECTION_NEWS) {
                    isEditorsPicks = false;
                    selectSection(SECTION_NEWS);
                }
                break;
            case R.id.nav_world_news:
                if (currentSection != SECTION_WORLD_NEWS) {
                    isEditorsPicks = false;
                    selectSection(SECTION_WORLD_NEWS);
                }
                break;
            case R.id.nav_editors_picks:
                if (currentSection != SECTION_EDITORS_PICKS) {
                    isEditorsPicks = true;
                    selectSection(SECTION_EDITORS_PICKS);
                }
                break;
            case R.id.nav_art_and_design:
                if (currentSection != SECTION_ART_AND_DESIGN) {
                    isEditorsPicks = false;
                    selectSection(SECTION_ART_AND_DESIGN);
                }
                break;
            case R.id.nav_books:
                if (currentSection != SECTION_BOOKS) {
                    isEditorsPicks = false;
                    selectSection(SECTION_BOOKS);
                }
                break;
            case R.id.nav_business:
                if (currentSection != SECTION_BUSINESS) {
                    isEditorsPicks = false;
                    selectSection(SECTION_BUSINESS);
                }
                break;
            case R.id.nav_culture:
                if (currentSection != SECTION_CULTURE) {
                    isEditorsPicks = false;
                    selectSection(SECTION_CULTURE);
                }
                break;
            case R.id.nav_education:
                if (currentSection != SECTION_EDUCATION) {
                    isEditorsPicks = false;
                    selectSection(SECTION_EDUCATION);
                }
                break;
            case R.id.nav_film:
                if (currentSection != SECTION_FILM) {
                    isEditorsPicks = false;
                    selectSection(SECTION_FILM);
                }
                break;
            case R.id.nav_law:
                if (currentSection != SECTION_LAW) {
                    isEditorsPicks = false;
                    selectSection(SECTION_LAW);
                }
                break;
            case R.id.nav_life_and_style:
                if (currentSection != SECTION_LIFE_AND_STYLE) {
                    isEditorsPicks = false;
                    selectSection(SECTION_LIFE_AND_STYLE);
                }
                break;
            case R.id.nav_media:
                if (currentSection != SECTION_MEDIA) {
                    isEditorsPicks = false;
                    selectSection(SECTION_MEDIA);
                }
                break;
            case R.id.nav_money:
                if (currentSection != SECTION_MONEY) {
                    isEditorsPicks = false;
                    selectSection(SECTION_MONEY);
                }
                break;
            case R.id.nav_music:
                if (currentSection != SECTION_MUSIC) {
                    isEditorsPicks = false;
                    selectSection(SECTION_MUSIC);
                }
                break;
            case R.id.nav_opinion:
                if (currentSection != SECTION_OPINION) {
                    isEditorsPicks = false;
                    selectSection(SECTION_OPINION);
                }
            case R.id.nav_politics:
                if (currentSection != SECTION_POLITICS) {
                    isEditorsPicks = false;
                    selectSection(SECTION_POLITICS);
                }
                break;
            case R.id.nav_science:
                if (currentSection != SECTION_SCIENCE) {
                    isEditorsPicks = false;
                    selectSection(SECTION_SCIENCE);
                }
                break;
            case R.id.nav_society:
                if (currentSection != SECTION_SOCIETY) {
                    isEditorsPicks = false;
                    selectSection(SECTION_SOCIETY);
                }
                break;
            case R.id.nav_sport:
                if (currentSection != SECTION_SPORT) {
                    isEditorsPicks = false;
                    selectSection(SECTION_SPORT);
                }
                break;
            case R.id.nav_stage:
                if (currentSection != SECTION_STAGE) {
                    isEditorsPicks = false;
                    selectSection(SECTION_STAGE);
                }
                break;
            case R.id.nav_tech:
                if (currentSection != SECTION_TECH) {
                    isEditorsPicks = false;
                    selectSection(SECTION_TECH);
                }
                break;
            case R.id.nav_travel:
                if (currentSection != SECTION_TRAVEL) {
                    isEditorsPicks = false;
                    selectSection(SECTION_TRAVEL);
                }
                break;
            case R.id.nav_tv_and_radio:
                if (currentSection != SECTION_TV_AND_RADIO) {
                    isEditorsPicks = false;
                    selectSection(SECTION_TV_AND_RADIO);
                }
                break;
            case R.id.nav_weather:
                if (currentSection != SECTION_WEATHER) {
                    isEditorsPicks = false;
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
        Uri baseUri = Uri.parse(SECTIONS[currentSection]);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if (isEditorsPicks) {
            uriBuilder.appendQueryParameter("show-editors-picks", "true");
        } else {
            uriBuilder.appendQueryParameter("page", String.valueOf(currentPage));
        }
        uriBuilder.appendQueryParameter("api-key", API_KEY);
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");

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
        fragment.hideWarningContainer();
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
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
