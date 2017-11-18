package com.rocdev.guardianreader.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.SectionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WidgetConfigActivity extends Activity {

    public static final String PREFS_NAME = "com.rocdev.guardianreader.widgetprefsname";

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
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                        WidgetConfigActivity.this);
                prefs.edit().putInt(String.valueOf(mAppWidgetId), position).apply();
                AppWidgetManager appWidgetManager = AppWidgetManager
                        .getInstance(WidgetConfigActivity.this);
                ArticlesWidgetProvider.updateAppWidget(WidgetConfigActivity.this,
                        appWidgetManager, mAppWidgetId);
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

    }

}