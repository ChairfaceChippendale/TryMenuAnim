package com.ujujzk.trymenuanim.customView;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.ujujzk.trymenuanim.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rainbow Dash from Softensy Team
 */

public class DropdownMenu {

    private List<View> menuItems;
    private List<ItemClickListener> listeners;
    private ViewGroup rootView;
    private Activity activity;
    private boolean isShown;
    private int shakeExtraSpace;

    private DropdownMenu() {
        //to avoid use empty constructor
        throw new AssertionError("Don't use empty constructor of DropdownMenu");
    }

    private DropdownMenu(Activity activity) {
        this.activity = activity;
        this.isShown = true;
        this.shakeExtraSpace = (int) activity.getResources().getDimension(R.dimen.extra_space);
    }

    public void addListener(final ItemClickListener itemClickListener) {
        listeners.add(itemClickListener);
    }

    public boolean isShown() {
        return isShown;
    }

    public void hideMenu() {
        liftMenuItem(menuItems.size() - 1, menuItems);
        isShown = false;
    }

    private void liftMenuItem(final int position, final List<View> views) {
        int nexPosition = position - 1;
        if (position >= 0) {
            final View v = views.get(position);
            v.setClickable(false); //without that the view remains clickable after hiding
            Animation liftAnim = new TranslateAnimation(0, 0, 0, -v.getHeight());
            liftAnim.setDuration(200);
            liftAnim.setFillAfter(true);
            v.startAnimation(liftAnim);
            new Handler().postDelayed(
                    () -> liftMenuItem(nexPosition, views),
                    50
            );
        }
    }

    public void showMenu() {
        dropMenuItem(0, menuItems);
        isShown = true;
    }

    private void dropMenuItem(final int position, final List<View> views) {
        int nextPosition = position + 1;
        if (position < views.size()) {
            final View v = views.get(position);
            v.setClickable(true);
            Animation shakeAnim = AnimationUtils.loadAnimation(activity, R.anim.shake);
            LambdableAnimation.with(shakeAnim).setOnAnimationListener(
                    animation -> increaseView(v, position),
                    animation -> decreaseView(v, position)
            );
            Animation dropDownAnim = new TranslateAnimation(0, 0, -v.getHeight(), 0);
            dropDownAnim.setDuration(200);
            dropDownAnim.setFillAfter(true);
            LambdableAnimation.with(dropDownAnim).setOnAnimationListener(
                    animation -> {
                    },
                    animation -> {
                        if (position == 0) {
                            //when shakes whole last item it looks bad
                            v.findViewById(R.id.item_name).startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));
                            v.findViewById(R.id.item_drawable).startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));
                        } else {
                            v.startAnimation(shakeAnim);
                        }
                        dropMenuItem(nextPosition, views);
                    }
            );
            v.startAnimation(dropDownAnim);
        }
    }


    private void increaseView(View v, int position) {
        //used only for shake animation
        //makes view bigger to hide space between view and parent when view shakes
        v.layout(
                -shakeExtraSpace,
                -shakeExtraSpace,
                v.getWidth() + shakeExtraSpace,
                v.getHeight());
    }

    private void decreaseView(View v, int position) {
        //used only for shake animation
        //turns back view size after increaseView()
        //delay needs because otherwise it is executed when animation hasn't stop yet
        new Handler().postDelayed(
                () -> v.layout(0, 0,
                        v.getWidth() - 2 * shakeExtraSpace,
                        v.getHeight() - shakeExtraSpace),
                500
        );
    }


    public static Builder getNewBuilder(Activity activity) {
        return new Builder(activity);
    }


    public static class Builder {

        DropdownMenu dropdownMenu;

//        List<View> _menuItems = new ArrayList<>();
//        List<ItemClickListener> _listeners = new ArrayList<>();
//        ViewGroup _rootView;
//        Activity _activity;

        private Builder(Activity activity) {
            dropdownMenu = new DropdownMenu(activity);
            dropdownMenu.rootView = (ViewGroup) activity.findViewById(android.R.id.content);
            dropdownMenu.listeners = new ArrayList<>();
            dropdownMenu.menuItems = new ArrayList<>();
        }

        private Builder() {
            //to avoid use empty constructor
            throw new AssertionError("Don't use empty constructor of DropdownMenu#Builder ");
        }

        public Builder addListener(final ItemClickListener itemClickListener) {
            dropdownMenu.listeners.add(itemClickListener);
            return Builder.this;
        }

        public Builder addItem(@NonNull final String itemName, @ColorRes final int itemColor, @DrawableRes final int itemDrawable) {

            //TODO inflate menu item

            View menuItem = dropdownMenu.activity.getLayoutInflater().inflate(R.layout.item_dropdow_menu, null);
            menuItem.setBackgroundResource(itemColor);
            ((TextView) menuItem.findViewById(R.id.item_name)).setText(itemName);
            ((ImageView) menuItem.findViewById(R.id.item_drawable)).setImageResource(itemDrawable);
            menuItem.setOnClickListener(v -> {
                for (ItemClickListener cl : dropdownMenu.listeners) {
                    cl.onItemClicked(((TextView) menuItem.findViewById(R.id.item_name)).getText().toString());
                }
            });

            dropdownMenu.rootView.addView(menuItem);

            dropdownMenu.menuItems.add(menuItem);

            return Builder.this;
        }

        public DropdownMenu build() {
            if (dropdownMenu.menuItems.size() < 1) {
                throw new RuntimeException("Add at least one items to menu");
            }

            setMenuViewsParams();

            new Handler().postDelayed(() -> dropdownMenu.hideMenu(), 100);
            return dropdownMenu;
        }

        private void setMenuViewsParams() {
            int n = dropdownMenu.menuItems.size();
            int screenHeight = screenHeight();
            int itemHeight = screenHeight / n;

            int multiplier = n;

            ViewGroup.LayoutParams parentPar;
            ViewGroup.LayoutParams childPar;
            ViewGroup parent;
            View child;
            for (int i = 0; i < n; i++) {
                parent = (ViewGroup) dropdownMenu.menuItems.get(i);
                parentPar = parent.getLayoutParams();
                if (i == 0) {
                    //it needs to avoid space between last menu element and screen bottom
                    parentPar.height = screenHeight;
                } else {
                    parentPar.height = itemHeight * (--multiplier);
                }
                parent.setLayoutParams(parentPar);

                child = parent.getChildAt(0);
                childPar = child.getLayoutParams();
                childPar.height = itemHeight;
                child.setLayoutParams(childPar);

                child = parent.getChildAt(1);
                childPar = child.getLayoutParams();
                childPar.height = itemHeight;
                child.setLayoutParams(childPar);
            }

        }

        private int screenHeight() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            dropdownMenu.activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels - getStatusBarHeight();
        }

        private int getStatusBarHeight() {
            int result = 0;
            int resourceId = dropdownMenu.activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = dropdownMenu.activity.getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        }
    }


    public interface ItemClickListener {
        void onItemClicked(String itemName);
    }


}


