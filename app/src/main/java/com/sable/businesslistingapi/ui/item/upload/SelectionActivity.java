package com.sable.businesslistingapi.ui.item.upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.databinding.ActivitySelectionBinding;
import com.sable.businesslistingapi.ui.category.categoryselection.CategorySelectionFragment;
import com.sable.businesslistingapi.ui.city.cityList.ItemUploadCityListFragment;
import com.sable.businesslistingapi.ui.common.PSAppCompactActivity;
import com.sable.businesslistingapi.ui.status.StatusSelectionFragment;
import com.sable.businesslistingapi.ui.subcategory.subcategoryselection.SubCategorySelectionFragment;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.MyContextWrapper;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewmodel.city.PopularCitiesViewModel;
import com.sable.businesslistingapi.viewobject.City;
import com.sable.businesslistingapi.viewobject.Image;
import com.sable.businesslistingapi.viewobject.Item;
import com.sable.businesslistingapi.viewobject.ItemCategory;
import com.sable.businesslistingapi.viewobject.ItemSubCategory;

import java.util.Objects;

public class SelectionActivity extends PSAppCompactActivity {

    public int flagType;
    private PopularCitiesViewModel popularCitiesViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySelectionBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_selection);

        flagType = Objects.requireNonNull(getIntent().getIntExtra((Constants.FLAG), 1));

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1212){
            if (resultCode== Activity.RESULT_OK){
                String name = data.getStringExtra(Constants.CITY_NAME);
                Toast.makeText(this, ""+name, Toast.LENGTH_SHORT).show();
                finish();
            }else{
                onBackPressed();
            }
        }else {
            Utils.psLog("Inside Result MainActivity");
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            assert fragment != null;
            fragment.onActivityResult(requestCode, resultCode, data);

        }
    }


    //region Private Methods

    private void initUI(ActivitySelectionBinding binding) {
        popularCitiesViewModel = new ViewModelProvider(this, viewModelFactory).get(PopularCitiesViewModel.class);
        popularCitiesViewModel.setPopularCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularCitiesViewModel.popularCitiesParameterHolder);
        // setup Fragment
        if (flagType == Constants.SELECT_CATEGORY) {
            CategorySelectionFragment categoryExpFragment = new CategorySelectionFragment();
            setupFragment(categoryExpFragment);
            initToolbar(binding.toolbar, "Category List");

        } else if (flagType == Constants.SELECT_SUBCATEGORY) {
            SubCategorySelectionFragment subCategoryExpFragment = new SubCategorySelectionFragment();
            setupFragment(subCategoryExpFragment);
            initToolbar(binding.toolbar, "Sub Category List");

        } else if (flagType == Constants.SELECT_STATUS) {
            StatusSelectionFragment categoryExpFragment = new StatusSelectionFragment();
            setupFragment(categoryExpFragment);
            initToolbar(binding.toolbar, "Status List");
        } else if (flagType == Constants.SELECT_CITY) {

          ItemUploadCityListFragment itemUploadCityListFragment = new ItemUploadCityListFragment();
            setupFragment(itemUploadCityListFragment);
            initToolbar(binding.toolbar, "City List");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*  if (flagType == Constants.SELECT_CITY){*/
        navigationController.navigateToItemUploadActivity(this,(Item)getIntent().getExtras().getParcelable("ITEM"),"",""
                ,(City)getIntent().getExtras().getParcelable("CITY"),(ItemCategory)getIntent().getExtras().getParcelable("CAT"),
                (ItemSubCategory)getIntent().getExtras().getParcelable("SUB_CAT"),(Image) getIntent().getExtras().getParcelable("IMG"));
        //    }
    }
}

