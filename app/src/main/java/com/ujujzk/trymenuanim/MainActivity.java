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
import android.widget.Toast;

import com.ujujzk.trymenuanim.customView.DropdownMenu;
import com.ujujzk.trymenuanim.customView.LambdableAnimation;
import com.ujujzk.trymenuanim.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rainbow Dash from Softensy Team
 */

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DropdownMenu ddm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ddm = DropdownMenu.getNewBuilder(this)
                .addItem("Item 1", R.color.menu_1, R.drawable.ic_cloud_upload_white_18dp)
                .addItem("Item 2", R.color.menu_2, R.drawable.ic_help_white_18dp)
                .addItem("Item 3", R.color.menu_3, R.drawable.ic_map_white_18dp)
                .addItem("Item 4", R.color.menu_4, R.drawable.ic_person_white_18dp)
                .addItem("Item 5", R.color.menu_5, R.drawable.ic_settings_white_18dp)
                .addListener(itemName -> {
                    ddm.hideMenu();
                    Toast.makeText(this,itemName, Toast.LENGTH_SHORT).show();})
                .build();


        binding.startAnim.setOnClickListener(v -> ddm.showMenu());
    }

}
