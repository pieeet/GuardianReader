package com.rocdev.guardianreader.utils;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piet on 09-06-17.
 *
 */

public class AdViewPool {

    private LayoutInflater _layoutInflater;
    private int _adViewLayoutId;
    private ViewGroup _root;

    private List<View> _adViews = new ArrayList<>();

    public AdViewPool(LayoutInflater layoutInflater, int adViewLayoutId, ViewGroup root) {

        _layoutInflater = layoutInflater;
        _adViewLayoutId = adViewLayoutId;
        _root = root;

        addToPool();
    }

    private void addToPool() {

        View adLayout = _layoutInflater.inflate(_adViewLayoutId, _root, false);
        addBackToPool(adLayout);
    }

    public void addBackToPool(View adLayout) {

        _adViews.add(adLayout);

        // load the ad so that it will be ready when it is retrieved
        loadAd(adLayout);
    }

    private void loadAd(View view) {

        final AdView nativeAd = (AdView) ((ViewGroup) view).getChildAt(0);
        nativeAd.loadAd(Utils.createAdRequest());
    }

    /**
     * @return An adview from the pool.
     */
    public View getAdView() {

        int index = _adViews.size() - 1;
        View v = _adViews.get(index);

        _adViews.remove(index);

        if (_adViews.isEmpty())
            addToPool();

        return v;
    }
}




}
