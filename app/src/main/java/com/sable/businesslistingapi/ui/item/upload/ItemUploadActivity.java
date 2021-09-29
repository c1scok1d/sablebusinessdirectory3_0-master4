package com.sable.businesslistingapi.ui.item.upload;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.MainActivity;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.databinding.ActivityItemUploadBinding;
import com.sable.businesslistingapi.ui.common.PSAppCompactActivity;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.MyContextWrapper;
import com.sable.businesslistingapi.utils.Utils;

public class ItemUploadActivity extends PSAppCompactActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityItemUploadBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_item_upload);
        initUI(binding);

    }

    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String LANG_CURRENT = preferences.getString(Constants.LANGUAGE_CODE, Config.DEFAULT_LANGUAGE);

        String CURRENT_LANG_COUNTRY_CODE = preferences.getString(Constants.LANGUAGE_COUNTRY_CODE, Config.DEFAULT_LANGUAGE_COUNTRY_CODE);

        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT, CURRENT_LANG_COUNTRY_CODE, true));
    }

    private void initUI(ActivityItemUploadBinding binding) {


        initToolbar(binding.toolbar, getResources().getString(R.string.item_upload__item_upload));
       // if (getIntent().hasExtra(Constants.CITY_ID)){
            Fragment fragment=new ItemUploadFragment();
            Bundle bundle=new Bundle();
            bundle.putParcelable("ITEM",getIntent().getExtras().getParcelable("ITEM"));
            bundle.putParcelable("CITY",getIntent().getExtras().getParcelable("CITY"));
            bundle.putParcelable("CAT",getIntent().getExtras().getParcelable("CAT"));
            bundle.putParcelable("SUB_CAT",getIntent().getExtras().getParcelable("SUB_CAT"));
            bundle.putParcelable("IMG",getIntent().getExtras().getParcelable("IMG"));
            fragment.setArguments(bundle);
            setupFragment(fragment);
      //  }else{
        //    setupFragment(new ItemUploadFragment());
       // }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Utils.psLog("Inside Result MainActivity");

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        assert fragment != null;
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, MainActivity.class));
        finish();

    }
}
