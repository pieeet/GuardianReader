package com.rocdev.guardianreader.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Section;
import com.rocdev.guardianreader.utils.SectionsAdapter;

import java.util.ArrayList;

/**
 * Created by piet on 26-05-17.
 *
 */

public class SectionsFragment extends Fragment {

    ListView listView;
    ArrayList<Section> sectionsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sections, null);
        listView = (ListView) rootView.findViewById(R.id.sectionsListView);
        Section[] sectionsArray = Section.values();
        sectionsList = new ArrayList<>();
        for (Section section: sectionsArray) {
            if (section.getIdNav() != -1) {
                sectionsList.add(section);
            }
        }
        SectionsAdapter adapter = new SectionsAdapter(getContext(), sectionsList);
        listView.setAdapter(adapter);
        return rootView;
    }
}
