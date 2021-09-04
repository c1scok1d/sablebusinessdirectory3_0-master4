package com.macinternetservices.sablebusinessdirectory.ui.comment.detail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ActivityCommentDetailBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSAppCompactActivity;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.MyContextWrapper;

import androidx.databinding.DataBindingUtil;

/**
 * Sable Business Directory
 * Contact Email : admin@sablebusinessdirectory.com
 * Website : http://www.sablebusinessdirectory.com
 */
public class CommentDetailActivity extends PSAppCompactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCommentDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_comment_detail);

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


    //region Private Methods

    private void initUI(ActivityCommentDetailBinding binding) {

        // Toolbar
        initToolbar(binding.toolbar, getResources().getString(R.string.comment__detail_title));

        // setup Fragment
        CommentDetailFragment commentDetailFragment = new CommentDetailFragment();
        setupFragment(commentDetailFragment);
        // Or you can call like this
        //setupFragment(new NewsListFragment(), R.id.content_frame);

    }

    //endregion

}
