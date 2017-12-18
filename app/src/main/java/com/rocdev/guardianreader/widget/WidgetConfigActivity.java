package com.rocdev.guardianreader.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.SectionsAdapter;

import java.util.ArrayList;
import java.util.List;

public class WidgetConfigActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "com.rocdev.guardianreader.widgetprefsname";
    private static final String TAG = WidgetConfigActivity.class.getSimpleName();

    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        ListView listView = (ListView) findViewById(R.id.listview_widget_config_sections);

        /*
        Tip: When your configuration Activity first opens, set the Activity result to
        RESULT_CANCELED, along with EXTRA_APPWIDGET_ID, as shown in step 5 above.
        This way, if the user backs-out of the Activity before reaching the end,
        the App Widget host is notified that the configuration was cancelled and the App Widget
        will not be added.
        https://developer.android.com/guide/topics/appwidgets/index.html
        */

        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        List<Section> sections = new ArrayList<>();
        for (Section section: Section.values()) {
            // exclude saved and search sections
            if (section.ordinal() < Section.SAVED.ordinal()) {
                sections.add(section);
            }
        }
        SectionsAdapter adapter = new SectionsAdapter(this, sections);
        adapter.setSelectedEdition(SectionsAdapter.NO_SELECTION);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
                prefs.edit().putInt(String.valueOf(mAppWidgetId), position).apply();
                ArticlesWidgetProvider.startService(WidgetConfigActivity.this, mAppWidgetId);
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.articles_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(mAppWidgetId, rv);
        if (getSupportActionBar() != null)
        getSupportActionBar().setTitle("Pick a section");

    }

}