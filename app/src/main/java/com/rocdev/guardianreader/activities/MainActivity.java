package com.rocdev.guardianreader.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Handler;
//import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rocdev.guardianreader.fragments.SectionsFragment;
import com.rocdev.guardianreader.utils.ArticleAdMobRecyclerAdapter;
import com.rocdev.guardianreader.utils.ArticleLoader;
import com.rocdev.guardianreader.fragments.ArticlesFragment;
import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.ArticlesUriBuilder;
import com.rocdev.guardianreader.utils.QueryUtils;
import com.rocdev.guardianreader.widget.ArticlesWidgetProvider;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<List<Article>>,
        ArticlesFragment.OnFragmentInteractionListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        SectionsFragment.SectionsFragmentListener,
        ArticleAdMobRecyclerAdapter.ArticleAdMobRecyclerAdapterListener {

    /*******************************
     * STATIC
     *******************************/
    private static final int CONTENT_CONTAINER = R.id.content_container;
    private static final String KEY_ARTICLES = "articles";
    private static final String KEY_CURRENT_SECTION = "currentSection";
    private static final String KEY_CURRENT_PAGE = "currentPage";
    private static final String KEY_LOADER_ID = "loaderId";
    private static final String KEY_IS_EDITOR_PICKS = "isEditorPicks";
    private static final String KEY_LIST_POSITION = "listPosition";
    private static final String PREF_DEFAULT_EDITION_IF_UNSET = "3";
    private static final String KEY_PAUSE_TIME = "pauseTime";
    private static final int TIME_REFRESH_INTERVAL = 1000 * 60 * 15;
    private static final int TIME_POST_DELAYED = 2000;
    private static final int CLOSE_DRAWER_DELAY = 300;
    public static final String EXTRA_SECTION_INDEX = "com.rocdev.guardianreader.extra.SECTION_INDEX";
    public static final String EXTRA_ARTICLE = "com.rocdev.guardianreader.extra.ARTICLE";
    private static final int IN_APP_BROWSER = 0;
    private static final int SYSTEM_BROWSER = 1;

    public static final String ACTION_OPEN_ARTICLE_FROM_WIDGET =
            "com.rocdev-guardian_reader_action_open_article_from_widget";
    public static final String ACTION_OPEN_SECTION_FROM_WIDGET =
            "com.rocdev-guardian_reader.action_open_section_from_widget";
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
    private ArrayList<Section> sections;
    private ArticlesFragment articlesFragment;
    private SectionsFragment sectionsFragment;
    private int listPosition;
    private String searchQuery;
    private SharedPreferences mSharedPreferences;
    private NavigationView navigationView;
    private Menu mMenu;
    private boolean isTwoPane;
    private boolean onPaused;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Article mArticleFromWidget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, getString(R.string.app_id));
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        onPaused = false;
        titles = getResources().getStringArray(R.array.titles);
        setContentView(R.layout.activity_main);
        isTwoPane = findViewById(R.id.fragment_container) != null;
        setPreferences();
        initNavigation();

        //retrieve data in case of screen rotation
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            initInstanceState();
        }
        initFragments();
    }

    private void setPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void initNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // NO DRAWER IN TWO PANE LAYOUT
        if (!isTwoPane) {
            setUpDrawer(toolbar);
        } else {
            setUpOrRefreshSelectedSections();
        }
    }

    private void setUpDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setUpNavBarSections();
    }

    private void setUpNavBarSections() {
        for (Section section : Section.values()) {
            if (section != Section.SEARCH) {
                Menu navMenu = navigationView.getMenu();
                navMenu.findItem(section.getIdNav())
                        .setVisible(mSharedPreferences.getBoolean(section.getPrefKey(),
                                true /* default */));
            }
        }
    }

    private void setUpOrRefreshSelectedSections() {
        sections = new ArrayList<>();
        for (Section section : Section.values()) {
            if (mSharedPreferences.getBoolean(section.getPrefKey(), true)
                    && section != Section.SEARCH) {
                sections.add(section);
            }
        }
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        articles = savedInstanceState.getParcelableArrayList(KEY_ARTICLES);
        currentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);
        currentSection = savedInstanceState.getInt(KEY_CURRENT_SECTION);
        loaderId = savedInstanceState.getInt(KEY_LOADER_ID);
        isEditorsPicks = savedInstanceState.getBoolean(KEY_IS_EDITOR_PICKS);
        listPosition = savedInstanceState.getInt(KEY_LIST_POSITION);
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
        currentSection = Integer.parseInt(mSharedPreferences.getString(
                getString(R.string.pref_key_default_edition), PREF_DEFAULT_EDITION_IF_UNSET));
        isNewList = true;
    }

    private void initFragments() {
        articlesFragment = ArticlesFragment.newInstance(articles, currentSection);
        FragmentManager fm = getSupportFragmentManager();
        if (isTwoPane) {
            sectionsFragment = SectionsFragment.newInstance(sections, currentSection);
            fm.beginTransaction()
                    .replace(R.id.content_pane_left, sectionsFragment)
                    .commit();
            fm.beginTransaction()
                    .replace(R.id.content_pane_right, articlesFragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(CONTENT_CONTAINER, articlesFragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null && (ACTION_OPEN_ARTICLE_FROM_WIDGET.equals(action) ||
                action.startsWith(ACTION_OPEN_SECTION_FROM_WIDGET))) {
            if (ACTION_OPEN_ARTICLE_FROM_WIDGET.equals(action)) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    currentSection = extras.getInt(EXTRA_SECTION_INDEX);
                    mArticleFromWidget = extras.getParcelable(EXTRA_ARTICLE);
                }
            } else if (action.startsWith(ACTION_OPEN_SECTION_FROM_WIDGET)) {
                currentSection = Integer.parseInt(action
                        .substring(ACTION_OPEN_SECTION_FROM_WIDGET.length()));
            }
            isNewList = true;
            currentPage = 1;
            articles.clear();
            refreshUI();
            intent.setAction(null);
        } else if (onPaused) {
            long pauseTime = mSharedPreferences.getLong(KEY_PAUSE_TIME, -1);
            long currentTime = new GregorianCalendar().getTimeInMillis();
            long timePassed = currentTime - pauseTime;
            // refresh if pause > 15 minutes
            if (!checkConnection() || timePassed > TIME_REFRESH_INTERVAL) {
                Toast.makeText(this, R.string.toast_refreshing_articles, Toast.LENGTH_SHORT).show();
                isNewList = true;
                currentPage = 1;
                articles.clear();
                refreshUI();
            }
        } else {
            if (articles.isEmpty()) {
                isNewList = true;
                refreshUI();
            }
        }
        setSelectedEdition();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(KEY_PAUSE_TIME, new GregorianCalendar().getTimeInMillis()).apply();
        onPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // invoke search intent
        if (intent != null && intent.getAction() != null) {
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                currentSection = Section.SEARCH.ordinal();
                searchQuery = intent.getStringExtra(SearchManager.QUERY);
                refreshUI();
                searchQuery = null;
            } else if (ACTION_OPEN_ARTICLE_FROM_WIDGET.equals(intent.getAction()) ||
                    intent.getAction().startsWith(ACTION_OPEN_SECTION_FROM_WIDGET)) {
                setIntent(intent);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_ARTICLES, articles);
        outState.putInt(KEY_CURRENT_SECTION, currentSection);
        outState.putInt(KEY_CURRENT_PAGE, currentPage);
        outState.putInt(KEY_LOADER_ID, loaderId);
        outState.putBoolean(KEY_IS_EDITOR_PICKS, isEditorsPicks);
        outState.putInt(KEY_LIST_POSITION, listPosition);
        super.onSaveInstanceState(outState);
    }

    private void refreshUI() {
        showProgressAnimations();
        try {
            articlesFragment.showNoSavedArticlesContainer(currentSection == Section.SAVED.ordinal()
                    && articles.isEmpty());
        } catch (NullPointerException ignored) {
        }
        String title = titles[currentSection];
        if (checkConnection()) {
            if (currentSection == Section.SEARCH.ordinal()) title = searchQuery;
            logFirebaseLoadingEvent();
            getLoaderManager().restartLoader(loaderId, null, this);
        } else {
            if (currentSection == Section.SAVED.ordinal()) {
                getLoaderManager().restartLoader(loaderId, null, this);
            } else {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!checkConnection()) {
                            setNoNetWorkStateWarning();
                        } else {
                            stopRefreshButtonAnimation(mMenu);
                            refreshUI();
                        }
                    }
                }, TIME_POST_DELAYED);
            }
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void logFirebaseLoadingEvent() {
        if (currentSection != Section.SAVED.ordinal()) {
            //TODO uncomment for production
            mFirebaseAnalytics.logEvent("api_call", null);
        }
    }

    private void setNoNetWorkStateWarning() {
        Toast.makeText(MainActivity.this, R.string.toast_no_network_alert,
                Toast.LENGTH_LONG).show();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_no_network);
        }
        articles.clear();
        articlesFragment.notifyArticlesChanged(true, currentSection);
        stopRefreshButtonAnimation(mMenu);
    }


    @Override
    public void onBackPressed() {
        if (!isTwoPane) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
            else closeAppWithConfirm();
        } else closeAppWithConfirm();
    }

    private void closeAppWithConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(R.string.dialog_close_app_title)
                .setMessage(R.string.dialog_close_app_message)
                .setIcon(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_warning_black_18dp, null))
                .setPositiveButton(R.string.dialog_confirm_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.dialog_confirm_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
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
        if (searchManager != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, Settings2Activity.class));
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
        articlesFragment.showProgressContainer(true);
        if (isNewList) {
            articlesFragment.showListContainer(false);
        }
        startRefreshButtonAnimation(mMenu);
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
                        startActivity(new Intent(MainActivity.this, Settings2Activity.class));
                    }
                }, CLOSE_DRAWER_DELAY);
                break;
            default:
                for (Section section : Section.values()) {
                    if (section.getIdNav() == id && currentSection != section.ordinal()) {
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
        isEditorsPicks = currentSection <= Section.HEADLINES_INT.ordinal() ||
                currentSection == Section.SAVED.ordinal();
        Uri baseUri = Uri.parse(Section.values()[currentSection].getUrl());
        String uriString = baseUri.toString();
        if (currentSection != Section.SAVED.ordinal()) {
            uriString = ArticlesUriBuilder.buildUriWithParams(currentPage,
                    currentSection, searchQuery).toString();
        }

        return new ArticleLoader(this, uriString, isEditorsPicks);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        mLoader = loader;
        stopRefreshButtonAnimation(mMenu);
        if (isNewList) articles.clear();
        articles.addAll(data);
        articlesFragment.notifyArticlesChanged(isNewList, currentSection);
        articlesFragment.showNoSavedArticlesContainer(currentSection == Section.SAVED.ordinal()
                && articles.isEmpty());
        articlesFragment.showProgressContainer(false);
        articlesFragment.showListContainer(true);
        if (mArticleFromWidget != null) {
            openArticleFromWidget();
        }
    }

    private void openArticleFromWidget() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onItemClicked(mArticleFromWidget);
                mArticleFromWidget = null;
            }
        }, 1000);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        getLoaderManager().destroyLoader(loaderId);
    }

    @Override
    public void onMoreArticles() {
        isNewList = false;
        showProgressAnimations();
        if (checkConnection()) {
            logFirebaseLoadingEvent();
            currentPage++;
            getLoaderManager().restartLoader(loaderId, null, this);
        } else {
            refreshUI();
        }
    }

    //More button on headlines
    @Override
    public void onMoreNews(int section) {
        isNewList = true;
        switch (section) {
            case 0:
                currentSection = Section.NEWS_AUS.ordinal();
                break;
            case 1:
                currentSection = Section.NEWS_UK.ordinal();
                break;
            case 2:
                currentSection = Section.NEWS_US.ordinal();
                break;
            case 3:
                currentSection = Section.NEWS_WORLD.ordinal();
                break;
        }
        setSelectedEdition();
        showProgressAnimations();
        if (checkConnection()) {
            currentPage = 1;
            refreshUI();
        }
    }

    private void setSelectedEdition() {
        if (isTwoPane) {
            int position = -1;
            for (int i = 0; i < sections.size(); i++) {
                Section section = sections.get(i);
                if (currentSection == section.ordinal()) {
                    position = i;
                    break;
                }
            }
            sectionsFragment.setSelectedEdition(position);
        } else {
            if (currentSection >= 0) navigationView.getMenu()
                    .getItem(currentSection).setChecked(true);
            else {
                Menu menu = navigationView.getMenu();
                for (int i = 0; i < menu.size(); i++) {
                    menu.getItem(i).setChecked(false);
                }
            }
        }
    }

    private boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mSharedPreferences = sharedPreferences;

        if (key.equals(getString(R.string.pref_key_default_edition)) ||
                key.equals(getString(R.string.pref_key_default_browser)))
            // do nothing
            return;
        // send widget refresh rate broadcast to widget provider
        if (key.equals(getString(R.string.pref_key_widget_refresh_rate))) {
            Intent intent = new Intent(this, ArticlesWidgetProvider.class);
            intent.setAction(ArticlesWidgetProvider.ACTION_SET_REFRESH_RATE_TIMER);
            sendBroadcast(intent);
            return;
        }
        // all is left is show sections has changed
        if (isTwoPane) {
            setUpOrRefreshSelectedSections();
            sectionsFragment.refreshListView(sections);
            setSelectedEdition();
        } else {
            setUpNavBarSections();
        }
    }

    @Override
    public void onSectionClicked(Section section) {
        if (currentSection != section.ordinal()) {
            isNewList = true;
            currentPage = 1;
            articles.clear();
            currentSection = section.ordinal();
            refreshUI();
        }
    }

    public void removeSavedArticle(Article article) {
        if (currentSection == Section.SAVED.ordinal()) {
            for (Article a : articles) {
                if (a.equals(article)) {
                    articles.remove(a);
                    break;
                }
            }
            if (articles.isEmpty()) {
                articlesFragment.showNoSavedArticlesContainer(true);
            }
            articlesFragment.notifyArticlesChanged(true, currentSection);
        }
    }


    @Override
    public void onItemClicked(Article article) {
        if (article != null) {
            int browserValue = Integer.parseInt(mSharedPreferences.getString(getString(
                    R.string.pref_key_default_browser), "0"));
            if (browserValue == IN_APP_BROWSER) {
                Intent intent = new Intent(this, ArticleActivity.class);
                intent.putExtra("article", article);
                startActivity(intent);
            } else if (browserValue == SYSTEM_BROWSER) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
            }
        }
    }

    //Needs to be global
    private boolean articleIsSaved;

    @Override
    public boolean onItemLongClicked(final Article article) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getString(R.string.empty_string);
        String message = getString(R.string.empty_string);
        Drawable icon = null;
        if (article != null) {
            if (article.get_ID() == Article.NO_ID) {
                articleIsSaved = false;
                title = getString(R.string.dialog_save_article_title);
                message = getString(R.string.dialog_save_article_message);
                icon = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_archive_black_18dp, null);
            } else {
                articleIsSaved = true;
                title = getString(R.string.dialog_delete_article_title);
                message = getString(R.string.dialog_delete_article_message);
                icon = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_unarchive_black_18dp, null);
            }
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_confirm_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (articleIsSaved) {
                            QueryUtils.deleteArticle(article, MainActivity.this);
                            removeSavedArticle(article);
                        } else {
                            QueryUtils.insertArticle(article, MainActivity.this);
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_confirm_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setIcon(icon)
                .show();
        return true;
    }


}