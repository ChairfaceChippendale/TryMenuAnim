package com.ujujzk.trymenuanim;

import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.ujujzk.trymenuanim.customView.DropdownMenu;
import com.ujujzk.trymenuanim.customView.LambdableAnimation;
import com.ujujzk.trymenuanim.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    boolean menuIsShown = true;

    List<View> menuViews = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        menuViews.add(binding.view1);
        menuViews.add(binding.view2);
        menuViews.add(binding.view3);
        menuViews.add(binding.view4);
        menuViews.add(binding.view5);
        setMenuViewsParams();


        binding.startAnim.setOnClickListener(v -> animMenu());
    }


    void animMenu() {
        if (menuIsShown) {
            hideMenu();
        } else {
            showMenu();
        }
    }

    private void hideMenu() {

//        Collections.reverse(menuViews);
        liftMenuItem(menuViews.size() - 1, menuViews);
        menuIsShown = false;
    }

    private void liftMenuItem(final int position, final List<View> views) {

        if (position >= 0) {

            final View v = views.get(position);

            Animation liftAnim = new TranslateAnimation(0, 0, 0, -v.getHeight());
            liftAnim.setDuration(200);
            liftAnim.setFillAfter(true);
            v.startAnimation(liftAnim);
            new Handler().postDelayed(
                    () -> {
                        int newPos = position - 1;
                        liftMenuItem(newPos, views);
                    },
                    50
            );
        }
    }

    private void showMenu() {

        dropMenuItem(0, menuViews);
        menuIsShown = true;
    }


    private void dropMenuItem(final int position, final List<View> views) {

        if (position < views.size()) {
            final View v = views.get(position);
            Animation shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
            LambdableAnimation.with(shakeAnim).setOnAnimationListener(
                    animation -> increaseView(v),
                    animation -> decreaseView(v)
            );
            Animation dropDownAnim = new TranslateAnimation(0, 0, -v.getHeight(), 0);
            dropDownAnim.setDuration(200);
            dropDownAnim.setFillAfter(true);
            LambdableAnimation.with(dropDownAnim).setOnAnimationListener(
                    animation -> {
                    },
                    animation -> {
                        v.startAnimation(shakeAnim);
                        int newPos = position + 1;
                        dropMenuItem(newPos, views);
                    }
            );
            v.startAnimation(dropDownAnim);
        }
    }


    private void increaseView(View v) {
        //used only for shake animation
        //makes view bigger to hide space between view and parent when view shakes
        v.layout(
                -(int) this.getResources().getDimension(R.dimen.quickplay_offset),
                -(int) this.getResources().getDimension(R.dimen.quickplay_offset),
                v.getWidth() + (int) this.getResources().getDimension(R.dimen.quickplay_offset),
                v.getHeight());
    }

    private void decreaseView(View v) {
        //used only for shake animation
        //turns back view size after increaseView()
        //delay needs because otherwise it is executed when animation hasn't stop yet
        new Handler().postDelayed(
                () -> v.layout(
                        0,
                        0,
                        v.getWidth() - 2 * (int) this.getResources().getDimension(R.dimen.quickplay_offset),
                        v.getHeight() - (int) this.getResources().getDimension(R.dimen.quickplay_offset)),
                500
        );
    }

    View.OnClickListener cl = v -> {animMenu();};

    private void setMenuViewsParams() {
        int n = menuViews.size();
        int screenHeight = screenHeight();
        int itemHeight = screenHeight / n;

        Log.w("mytag", "screenHeight: " + screenHeight);
        Log.w("mytag", "itemHeight: " + itemHeight);


        int multiplaer = n;

        ViewGroup.LayoutParams parentPar;
        ViewGroup.LayoutParams childPar;

        ViewGroup parent;
        View child;

        //it needs to avoid space between last menu element and screen bottom
        parent = (ViewGroup) menuViews.get(0);
        child = parent.getChildAt(0);
        parent.setOnClickListener(cl);
        childPar = child.getLayoutParams();
        childPar.height = itemHeight;
        child.setLayoutParams(childPar);
        parentPar = parent.getLayoutParams();
        parentPar.height = screenHeight;
        parent.setLayoutParams(parentPar);


        for (int i = 1; i < n; i++) {
            parent = (ViewGroup) menuViews.get(i);
            child = parent.getChildAt(0);
            parent.setOnClickListener(cl);
            childPar = child.getLayoutParams();
            childPar.height = itemHeight;
            child.setLayoutParams(childPar);
            parentPar = parent.getLayoutParams();
            parentPar.height = itemHeight*(--multiplaer);
            parent.setLayoutParams(parentPar);
        }

    }

    private int screenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels - getStatusBarHeight();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


}
