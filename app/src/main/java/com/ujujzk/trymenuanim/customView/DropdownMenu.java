package com.ujujzk.trymenuanim.customView;

import android.app.Activity;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rainbow Dash on 29.03.2017.
 */

public class DropdownMenu {

    private List<View> menuItems;
    private List<ItemClickListener> listeners;
    private ViewGroup rootView;
    private Activity activity;
    private boolean menuIsShown;

    private DropdownMenu() {
        //to avoid use empty constructor
        throw new AssertionError();
    }

    DropdownMenu(List<View> menuElements, List<ItemClickListener> listeners, Activity activity, ViewGroup rootView) {
        this.menuItems = menuElements;
        this.listeners = listeners;
        this.activity = activity;
        this.rootView = rootView;
        menuIsShown = false;
    }

    public void addListener(final ItemClickListener itemClickListener) {
        listeners.add(itemClickListener);
    }

    void showMenu() {
        //TODO
        menuIsShown = true;
    }

    void hideMenu() {
        //TODO
        menuIsShown = false;
    }

    public static Builder getNewBuilder(Activity activity) {
        return new Builder(activity);
    }



    public static class Builder {

        List<View> _menuItems = new ArrayList<>();
        List<ItemClickListener> _listeners = new ArrayList<>();
        ViewGroup _rootView;
        Activity _activity;

        private Builder(Activity activity) {
            _activity = activity;
            this._rootView = (ViewGroup) activity.findViewById(android.R.id.content);
        }


        public Builder addListener(final ItemClickListener itemClickListener) {
            _listeners.add(itemClickListener);
            return Builder.this;
        }


        public Builder addItem(final String itemName, @ColorRes final int itemColor, @DrawableRes final int itemDrawable) {

            //TODO inflate menu item

            return Builder.this;
        }

        public DropdownMenu build() {
            if (_activity == null) {
                throw new RuntimeException("Pass an activity");
            }
            if (_menuItems.size() < 1) {
                throw new RuntimeException("Add at least 2 items to menu");
            }
            return new DropdownMenu(_menuItems, _listeners, _activity, _rootView);
        }
    }

}

interface ItemClickListener {
    void onItemClicked (String itemName);
}

