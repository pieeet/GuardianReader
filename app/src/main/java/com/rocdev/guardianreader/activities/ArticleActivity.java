package com.rocdev.guardianreader.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.fragments.ArticleFragment;
import com.rocdev.guardianreader.models.Article;

public class ArticleActivity extends BaseActivity
        implements ArticleFragment.ArticleFragmentListener {

    private static final String INTENT_KEY_ARTICLE = "article";

    private ArticleFragment fragment;
    private Article article;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        article = intent.getParcelableExtra(INTENT_KEY_ARTICLE);
        fragment = ArticleFragment.newInstance(article);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.article_fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fragment.goPageBack()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article, menu);
        mMenu = menu;
        startRefreshButtonAnimation(mMenu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_refresh:
                startRefreshButtonAnimation(mMenu);
                fragment.reload();
                return true;
            case R.id.menu_item_share:
                Intent myShareIntent = new Intent();
                myShareIntent.setAction(Intent.ACTION_SEND);
                myShareIntent.putExtra(Intent.EXTRA_TEXT, article.getTitle() + "\n--------\n" +
                        article.getUrl());
                myShareIntent.setType("text/plain");
                startActivity(Intent.createChooser(myShareIntent, "Share URL to..."));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void startDownloadAnimation() {
        startRefreshButtonAnimation(mMenu);
        Log.i("Activity", "startDownloadAnimation triggered");
    }

    @Override
    public void stopDownLoadAnimation() {
        stopRefreshButtonAnimation(mMenu);
    }
}
