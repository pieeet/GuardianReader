package com.rocdev.guardianreader.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.rocdev.guardianreader.R;

/**
 * Created by piet on 23-06-17.
 *
 */

public class BaseActivity extends AppCompatActivity {

    void startRefreshButtonAnimation(Menu menu) {
        MenuItem m = null;
        if (menu != null) {
            m = menu.findItem(R.id.action_refresh);
        }
        if (m != null) {
            if (m.getActionView() != null) {
                stopRefreshButtonAnimation(menu);
            }
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
            rotation.setRepeatCount(Animation.INFINITE);
            iv.startAnimation(rotation);
            m.setActionView(iv);
        }
    }

    void stopRefreshButtonAnimation(Menu menu) {
        try {
            MenuItem m = menu.findItem(R.id.action_refresh);
            // Remove the animation.
            m.getActionView().clearAnimation();
            m.setActionView(null);
        } catch (NullPointerException ignored) {
        }
    }


}
