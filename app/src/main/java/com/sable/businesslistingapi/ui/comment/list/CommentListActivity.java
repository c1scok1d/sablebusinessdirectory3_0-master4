package com.sable.businesslistingapi.ui.comment.list;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.databinding.ActivityCommentListBinding;
import com.sable.businesslistingapi.ui.common.PSAppCompactActivity;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.MyContextWrapper;
import com.sable.businesslistingapi.utils.Utils;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * Sable Business Directory
 * Contact Email : admin@sablebusinessdirectory.com
 * Website : http://www.sablebusinessdirectory.com
 */
public class CommentListActivity extends PSAppCompactActivity {


    //region Override Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCommentListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_comment_list);

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
    private void initUI(ActivityCommentListBinding binding) {

        // Toolbar
        initToolbar(binding.toolbar, getResources().getString(R.string.comment__title));

        // setup Fragment
        setupFragment(new CommentListFragment());
        // Or you can call like this
        //setupFragment(new NewsListFragment(), R.id.content_frame);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.psLog("Inside Result MainActivity");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    //endregion


}
