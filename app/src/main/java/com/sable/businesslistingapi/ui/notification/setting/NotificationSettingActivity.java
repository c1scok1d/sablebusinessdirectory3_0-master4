package com.sable.businesslistingapi.ui.notification.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.databinding.ActivityNotificationSettingBinding;
import com.sable.businesslistingapi.ui.common.PSAppCompactActivity;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.MyContextWrapper;

import androidx.databinding.DataBindingUtil;

public class NotificationSettingActivity extends PSAppCompactActivity {

    //region Override Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityNotificationSettingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_setting);

        // Init all UI
        initUI(binding);

    }

    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String LANG_CURRENT = preferences.getString(Constants.LANGUAGE_CODE, Config.DEFAULT_LANGUAGE);

        String CURRENT_LANG_COUNTRY_CODE = preferences.getString(Constants.LANGUAGE_COUNTRY_CODE, Config.DEFAULT_LANGUAGE_COUNTRY_CODE);

        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT, CURRENT_LANG_COUNTRY_CODE, true));

    }
    //endregion


    //region Private Methods

    private void initUI(ActivityNotificationSettingBinding binding) {

        // Toolbar
        initToolbar(binding.toolbar, getResources().getString(R.string.notification_setting__title));

        // setup Fragment
        setupFragment(new NotificationSettingFragment());

    }

    //endregion

}
