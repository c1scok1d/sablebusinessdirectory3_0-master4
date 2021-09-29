package com.sable.businesslistingapi.ui.item.collection.header;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.databinding.ActivityCollectionHeaderListBinding;
import com.sable.businesslistingapi.ui.common.PSAppCompactActivity;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.MyContextWrapper;

public class CollectionHeaderListActivity extends PSAppCompactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCollectionHeaderListBinding activityFilteringBinding = DataBindingUtil.setContentView(this, R.layout.activity_collection_header_list);

        initUI(activityFilteringBinding);

    }

    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String LANG_CURRENT = preferences.getString(Constants.LANGUAGE_CODE, Config.DEFAULT_LANGUAGE);

        String CURRENT_LANG_COUNTRY_CODE = preferences.getString(Constants.LANGUAGE_COUNTRY_CODE, Config.DEFAULT_LANGUAGE_COUNTRY_CODE);

        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT, CURRENT_LANG_COUNTRY_CODE, true));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initUI(ActivityCollectionHeaderListBinding binding) {

        initToolbar(binding.toolbar, getString(R.string.collection__list_title));
        setupFragment(new CollectionHeaderListFragment());

    }

}
