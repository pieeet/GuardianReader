package com.rocdev.guardianreader.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    private ArrayList<Section> sectionsList;
    private int defaultEdition;
    private SectionsAdapter adapter;
    private SectionsFragmentListener mListener;

    private static final String KEY_SECTIONS_LIST = "sectionsList";
    private static final String KEY_DEFAULT_EDITION = "defaultEdition";
//    private static final String LOG_TAG = SectionsFragment.class.getSimpleName();

    public SectionsFragment() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sectionsList = (ArrayList<Section>) getArguments().getSerializable(KEY_SECTIONS_LIST);
            this.defaultEdition = getArguments().getInt(KEY_DEFAULT_EDITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sections, null);
        ListView listView = (ListView) rootView.findViewById(R.id.sectionsListView);
        adapter = new SectionsAdapter(getContext(), sectionsList);
        adapter.setSelectedEdition(defaultEdition);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.onSectionClicked(sectionsList.get(i));
                adapter.setSelectedEdition(i);
            }
        });
        return rootView;
    }

    public static SectionsFragment newInstance(ArrayList<Section> sections, int defaultPosition) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_SECTIONS_LIST, sections);
        args.putInt(KEY_DEFAULT_EDITION, defaultPosition);
        SectionsFragment fragment = new SectionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setSelectedEdition(int position) {
        adapter.setSelectedEdition(position);
    }

    public void refreshListView(ArrayList<Section> sectionsList) {
        this.sectionsList = sectionsList;
        adapter.clear();
        adapter.addAll(sectionsList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SectionsFragmentListener) {
            mListener = (SectionsFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface SectionsFragmentListener {
        void onSectionClicked(Section section);
    }
}
