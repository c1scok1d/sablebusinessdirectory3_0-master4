package com.sable.businesslistingapi.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.gson.Gson;
import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.MainActivity;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.binding.FragmentDataBindingComponent;
import com.sable.businesslistingapi.databinding.FragmentDashboardCityListBinding;
import com.sable.businesslistingapi.ui.city.adapter.FeaturedCitiesAdapter;
import com.sable.businesslistingapi.ui.city.adapter.PopularCitiesAdapter;
import com.sable.businesslistingapi.ui.city.adapter.RecentCitiesAdapter;
import com.sable.businesslistingapi.ui.common.DataBoundListAdapter;
import com.sable.businesslistingapi.ui.common.PSFragment;
import com.sable.businesslistingapi.ui.dashboard.adapter.DashBoardViewPagerAdapter;
import com.sable.businesslistingapi.ui.item.adapter.ItemListAdapter;
import com.sable.businesslistingapi.utils.AutoClearedValue;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.GPSTracker;
import com.sable.businesslistingapi.utils.GeolocationService;
import com.sable.businesslistingapi.utils.PSDialogMsg;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewmodel.blog.BlogViewModel;
import com.sable.businesslistingapi.viewmodel.city.CityViewModel;
import com.sable.businesslistingapi.viewmodel.city.FeaturedCitiesViewModel;
import com.sable.businesslistingapi.viewmodel.city.PopularCitiesViewModel;
import com.sable.businesslistingapi.viewmodel.city.RecentCitiesViewModel;
import com.sable.businesslistingapi.viewmodel.clearalldata.ClearAllDataViewModel;
import com.sable.businesslistingapi.viewmodel.item.DiscountItemViewModel;
import com.sable.businesslistingapi.viewmodel.item.FeaturedItemViewModel;
import com.sable.businesslistingapi.viewmodel.item.PopularItemViewModel;
import com.sable.businesslistingapi.viewmodel.item.RecentItemViewModel;
import com.sable.businesslistingapi.viewobject.City;
import com.sable.businesslistingapi.viewobject.Item;
import com.sable.businesslistingapi.viewobject.holder.ItemParameterHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class DashBoardCityListFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private FeaturedItemViewModel featuredItemViewModel;
    private DiscountItemViewModel discountItemViewModel;
    private PopularItemViewModel popularItemViewModel;
    private RecentItemViewModel recentItemViewModel;
    private PopularCitiesViewModel popularCitiesViewModel;
    private FeaturedCitiesViewModel featuredCitiesViewModel;
    private RecentCitiesViewModel recentCitiesViewModel;
    private BlogViewModel blogViewModel;
    private ClearAllDataViewModel clearAllDataViewModel;
    private CityViewModel cityViewModel;
    private static final int FRAME_TIME_MS = 8000;
    private PSDialogMsg psDialogMsg;

    private ImageView[] dots;
    private Handler handler = new Handler();
    private Runnable update;
    private int NUM_PAGES = 10;
    private int currentPage = 0;
    private boolean touched = false;
    private Timer unTouchedTimer;

    @Inject
    protected SharedPreferences pref;
    private String startDate = Constants.ZERO;
    private String endDate = Constants.ZERO;

    @VisibleForTesting
    private AutoClearedValue<FragmentDashboardCityListBinding> binding;
    private AutoClearedValue<ItemListAdapter> featuredItemListAdapter;
    private AutoClearedValue<ItemListAdapter> popularItemListAdapter;
    private AutoClearedValue<ItemListAdapter> discountItemListAdapter;
    private AutoClearedValue<ItemListAdapter> recentItemListAdapter;
    private AutoClearedValue<PopularCitiesAdapter> popularCitiesAdapter;
    private AutoClearedValue<FeaturedCitiesAdapter> featuredCitiesAdapter;
    private AutoClearedValue<RecentCitiesAdapter> recentCitiesAdapter;
    private AutoClearedValue<DashBoardViewPagerAdapter> dashBoardViewPagerAdapter;
    private AutoClearedValue<LinearLayout> pageIndicatorLayout;
    private AutoClearedValue<ViewPager> viewPager;
    private Handler imageSwitchHandler;
    public String selectedCityId;
    GPSTracker gpsTracker;
    SharedPreferences sharedPref;


    //SharedPreferences prefs;
    //final String GEO_PREFS = "geo_prefs";
    SharedPreferences geoprefs;
    //selectedCityId = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentDashboardCityListBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard_city_list, container, false, dataBindingComponent);

        //selectedCityId = "";
        binding = new AutoClearedValue<>(this, dataBinding);
        gpsTracker = new GPSTracker(getContext());
        //preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Config.CurrentLocation = gpsTracker.getLocation();


        loadDates();
        if ((this.getActivity()) != null) {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.md_white_1000));
        }
        return binding.get().getRoot();
    }

    private void loadDates() {
        try {

            if (getActivity() != null && getActivity().getBaseContext() != null) {
                startDate = pref.getString(Constants.CITY_START_DATE, Constants.ZERO);
                endDate = pref.getString(Constants.CITY_END_DATE, Constants.ZERO);
            }

        } catch (NullPointerException ne) {
            Utils.psErrorLog("Null Pointer Exception.", ne);
        } catch (Exception e) {
            Utils.psErrorLog("Error in getting notification flag data.", e);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initUIAndActions() {
        geoprefs = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

        binding.get().noListingsLayout.setVisibility(View.GONE);
        if (!Config.ENABLE_ITEM_UPLOAD) {
            binding.get().floatingActionButton.setVisibility(View.GONE);
        } else {
            binding.get().floatingActionButton.setVisibility(View.VISIBLE);
        }

        binding.get().headerText.setVisibility(View.GONE);
        binding.get().loadingLayout.setVisibility(View.GONE);
        psDialogMsg = new PSDialogMsg(getActivity(), false);

        binding.get().floatingActionButton.setOnClickListener(view ->
                Utils.navigateOnUserVerificationActivity(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController, new Utils.NavigateOnUserVerificationActivityCallback() {
                    @Override
                    public void onSuccess() {
                        navigationController.navigateToItemUploadActivity(DashBoardCityListFragment.this.getActivity(), null, "", "", null,
                                null, null, null);
                    }
                }));

        binding.get().ivSkip.setOnClickListener(new View.OnClickListener() {
            ///psDialogMsg.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.get().noListingsLayout.isShown()){
                    Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                    binding.get().noListingsLayout.setAnimation(fadeOut);
                    binding.get().noListingsLayout.setVisibility(View.GONE);
                    binding.get().ivSkip.setVisibility(View.GONE);
                }
            }
            //});
        });

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.layout__primary_background));
            ((MainActivity) getActivity()).updateToolbarIconColor(Color.GRAY);
            ((MainActivity) getActivity()).updateMenuIconGrey();
        }

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            binding.get().adView.loadAd(adRequest);

            AdRequest adRequest2 = new AdRequest.Builder()
                    .build();
            binding.get().adView2.loadAd(adRequest2);

        } else {
            binding.get().adView.setVisibility(View.GONE);
            binding.get().adView2.setVisibility(View.GONE);
        }

        viewPager = new AutoClearedValue<>(this, binding.get().blogViewPager);

        pageIndicatorLayout = new AutoClearedValue<>(this, binding.get().pagerIndicator);

        if (viewPager.get() != null && viewPager.get() != null && viewPager.get() != null) {
            viewPager.get().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    currentPage = position;

                    if (pageIndicatorLayout != null) {
                        // setupSliderPagination(binding.getRoot());
                        setupSliderPagination();
                    }

                    for (ImageView dot : dots) {
                        if (dots != null) {
                            dot.setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
                        }
                    }

                    if (dots != null && dots.length > position) {
                        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
                    }

                    touched = true;

                    handler.removeCallbacks(update);

                    setUnTouchedTimer();

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
            binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
            binding.get().swipeRefresh.setOnRefreshListener(() -> {

                cityViewModel.loadingDirection = Utils.LoadingDirection.top;

                // reset itemCategoryViewModel.offset
                cityViewModel.offset = 0;

                // reset itemCategoryViewModel.forceEndLoading
                cityViewModel.forceEndLoading = false;

                // update live data

                featuredCitiesViewModel.setFeaturedCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredCitiesViewModel.featuredCitiesParameterHolder);
                popularCitiesViewModel.setPopularCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularCitiesViewModel.popularCitiesParameterHolder);
                recentCitiesViewModel.setRecentCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, recentCitiesViewModel.recentCitiesParameterHolder);
                blogViewModel.setBlogByIdObj(String.valueOf(Config.LIST_NEW_FEED_COUNT_PAGER), String.valueOf(blogViewModel.offset));

                discountItemViewModel.discountItemParameterHolder.lat = Utils.getCurrentLat();
                discountItemViewModel.discountItemParameterHolder.lng = Utils.getCurrentLng();
                discountItemViewModel.discountItemParameterHolder.miles = "20";

                discountItemViewModel.setDiscountItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, discountItemViewModel.discountItemParameterHolder);

                featuredItemViewModel.featuredItemParameterHolder.lat = Utils.getCurrentLat();
                featuredItemViewModel.featuredItemParameterHolder.lng = Utils.getCurrentLng();
                featuredItemViewModel.featuredItemParameterHolder.miles = "20";

                featuredItemViewModel.setFeaturedItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredItemViewModel.featuredItemParameterHolder);

                popularItemViewModel.popularItemParameterHolder.lat = Utils.getCurrentLat();
                popularItemViewModel.popularItemParameterHolder.lng = Utils.getCurrentLng();
                popularItemViewModel.popularItemParameterHolder.miles = "20";

                popularItemViewModel.setPopularItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularItemViewModel.popularItemParameterHolder);
            });
        }

        startPagerAutoSwipe();

        binding.get().searchBoxEditText.setOnClickListener(v -> binding.get().searchBoxEditText.setFocusable(true));

        binding.get().searchImageButton.setOnClickListener(v -> {

            ItemParameterHolder itemParameterHolder = new ItemParameterHolder();
            itemParameterHolder.keyword = binding.get().searchBoxEditText.getText().toString();

            navigationController.navigateToHomeFilteringActivity(getActivity(), itemParameterHolder, itemParameterHolder.keyword, "", "", "");
        });

        binding.get().nestedScrollView.setOnTouchListener((v, event) -> {

            if (binding.get().searchBoxEditText.hasFocus()) {
                binding.get().searchBoxEditText.clearFocus();
            }

            return false;
        });

        binding.get().bestThingsViewAllTextView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(DashBoardCityListFragment.this.getActivity(), featuredItemViewModel.featuredItemParameterHolder, DashBoardCityListFragment.this.getString(R.string.dashboard_best_things), "", "", ""));

        binding.get().popularPlacesViewAllTextView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(DashBoardCityListFragment.this.getActivity(), popularItemViewModel.popularItemParameterHolder, DashBoardCityListFragment.this.getString(R.string.dashboard_popular_places), "", "", ""));

        binding.get().newPlacesViewAllTextView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(DashBoardCityListFragment.this.getActivity(), recentItemViewModel.recentItemParameterHolder, DashBoardCityListFragment.this.getString(R.string.dashboard_new_places), "", "", ""));

        binding.get().blogViewAllTextView.setOnClickListener(v -> navigationController.navigateToBlogList(getActivity()));

        binding.get().promoListViewAllTextView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(DashBoardCityListFragment.this.getActivity(), discountItemViewModel.discountItemParameterHolder, DashBoardCityListFragment.this.getString(R.string.dashboard_promo_list), "", "", ""));

        binding.get().popularCitiesViewAllTextView.setOnClickListener(v -> navigationController.navigateToCityList(getActivity(), popularCitiesViewModel.popularCitiesParameterHolder, getString(R.string.dashboard_popular_cities)));

        binding.get().featuredViewAllTextView.setOnClickListener(v -> navigationController.navigateToCityList(getActivity(), featuredCitiesViewModel.featuredCitiesParameterHolder, getString(R.string.dashboard_best_cities)));

        binding.get().newCitiesViewAllTextView.setOnClickListener(v -> navigationController.navigateToCityList(getActivity(), recentCitiesViewModel.recentCitiesParameterHolder, getString(R.string.dashboard_new_cities)));

        if (force_update) {
            navigationController.navigateToForceUpdateActivity(this.getActivity(), force_update_title, force_update_msg);
        }
    }

    private void setupSliderPagination() {

        int dotsCount = dashBoardViewPagerAdapter.get().getCount();


        if (dotsCount > 0) {

            dots = new ImageView[dotsCount];

            if (pageIndicatorLayout != null) {
                if (pageIndicatorLayout.get().getChildCount() > 0) {
                    pageIndicatorLayout.get().removeAllViewsInLayout();
                }
            }

            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(getContext());
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(4, 0, 4, 0);

                pageIndicatorLayout.get().addView(dots[i], params);
            }

            int currentItem = viewPager.get().getCurrentItem();
            if (currentItem > 0 && currentItem < dots.length) {
                dots[currentItem].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            } else {
                dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            }
        }
    }

    @Override
    protected void initViewModels() {
        blogViewModel = new ViewModelProvider(this, viewModelFactory).get(BlogViewModel.class);
        clearAllDataViewModel = new ViewModelProvider(this, viewModelFactory).get(ClearAllDataViewModel.class);
        featuredItemViewModel = new ViewModelProvider(this, viewModelFactory).get(FeaturedItemViewModel.class);
        discountItemViewModel = new ViewModelProvider(this, viewModelFactory).get(DiscountItemViewModel.class);
        popularItemViewModel = new ViewModelProvider(this, viewModelFactory).get(PopularItemViewModel.class);
        recentItemViewModel = new ViewModelProvider(this, viewModelFactory).get(RecentItemViewModel.class);
        popularCitiesViewModel = new ViewModelProvider(this, viewModelFactory).get(PopularCitiesViewModel.class);
        featuredCitiesViewModel = new ViewModelProvider(this, viewModelFactory).get(FeaturedCitiesViewModel.class);
        recentCitiesViewModel = new ViewModelProvider(this, viewModelFactory).get(RecentCitiesViewModel.class);
        cityViewModel = new ViewModelProvider(this, viewModelFactory).get(CityViewModel.class);
    }

    @Override
    protected void initAdapters() {


        DashBoardViewPagerAdapter nvAdapter3 = new DashBoardViewPagerAdapter(dataBindingComponent, blog ->
                navigationController.navigateToBlogDetailActivity(DashBoardCityListFragment.this.getActivity(), blog.id, blog.cityId));
        this.dashBoardViewPagerAdapter = new AutoClearedValue<>(this, nvAdapter3);
        viewPager.get().setAdapter(dashBoardViewPagerAdapter.get());


        ItemListAdapter featuredAdapter = new ItemListAdapter(dataBindingComponent, item ->
                navigationController.navigateToSelectedItemDetail(this.getActivity(), item.id, item.name, item.cityId), this);
        this.featuredItemListAdapter = new AutoClearedValue<>(this, featuredAdapter);
        binding.get().featuredItemRecyclerView.setAdapter(featuredAdapter);

        ItemListAdapter discountAdapter = new ItemListAdapter(dataBindingComponent, item ->
                navigationController.navigateToSelectedItemDetail(this.getActivity(), item.id, item.name, item.cityId), this);
        this.discountItemListAdapter = new AutoClearedValue<>(this, discountAdapter);
        binding.get().promoListRecyclerView.setAdapter(discountAdapter);

        ItemListAdapter popularAdapter = new ItemListAdapter(dataBindingComponent, item ->
                navigationController.navigateToSelectedItemDetail(this.getActivity(), item.id, item.name, item.cityId), this);
        this.popularItemListAdapter = new AutoClearedValue<>(this, popularAdapter);
        binding.get().popularPlacesRecyclerView.setAdapter(popularAdapter);

        ItemListAdapter recentAdapter = new ItemListAdapter(dataBindingComponent, item ->
                navigationController.navigateToSelectedItemDetail(this.getActivity(), item.id, item.name, item.cityId), this);
        this.recentItemListAdapter = new AutoClearedValue<>(this, recentAdapter);
        binding.get().newPlacesRecyclerView.setAdapter(recentAdapter);

        PopularCitiesAdapter popularCitiesAdapter1 = new PopularCitiesAdapter(dataBindingComponent, city ->
                navigationController.navigateToSelectedCityDetail(getActivity(), city.id, city.name, ""), this);
        this.popularCitiesAdapter = new AutoClearedValue<>(this, popularCitiesAdapter1);
        binding.get().popularCitiesRecyclerView.setAdapter(popularCitiesAdapter1);

        FeaturedCitiesAdapter featuredCitiesAdapter1 = new FeaturedCitiesAdapter(dataBindingComponent, city ->
                navigationController.navigateToSelectedCityDetail(getActivity(), city.id, city.name, ""), this);
        this.featuredCitiesAdapter = new AutoClearedValue<>(this, featuredCitiesAdapter1);
        binding.get().featuredCityRecyclerView.setAdapter(featuredCitiesAdapter1);

        RecentCitiesAdapter recentCitiesAdapter1 = new RecentCitiesAdapter(dataBindingComponent, city ->
                navigationController.navigateToSelectedCityDetail(getActivity(), city.id, city.name, ""), this);
        this.recentCitiesAdapter = new AutoClearedValue<>(this, recentCitiesAdapter1);
        binding.get().newCitiesRecyclerView.setAdapter(recentCitiesAdapter1);

    }

    @Override
    protected void initData() {
        if (connectivity.isConnected()) {
            if (startDate.equals(Constants.ZERO)) {

                startDate = getDateTime();
                Utils.setDatesToShared(startDate, endDate, pref);
            }

        }

        clearAllDataViewModel.getDeleteAllDataData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {

                    case ERROR:

                    case SUCCESS:
                        break;
                }
            }
        });

        blogViewModel.setNewsFeedObj(String.valueOf(Config.LIST_NEW_FEED_COUNT_PAGER), String.valueOf(blogViewModel.offset));
        blogViewModel.getNewsFeedData().observe(this, listResource -> {

            if (listResource != null) {

                switch (listResource.status) {

                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        if (listResource.data != null) {

                            // Update the data
                            dashBoardViewPagerAdapter.get().replaceNewsFeedList(listResource.data);

                        }

                        break;

                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {
                            // Update the data

                            dashBoardViewPagerAdapter.get().replaceNewsFeedList(listResource.data);

                        }

                        blogViewModel.setLoadingState(false);

                        break;

                    case ERROR:
                        // Error State

                        blogViewModel.setLoadingState(false);

                        break;
                    default:
                        // Default

                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

                if (blogViewModel.offset > 1) {
                    // No more data for this list
                    // So, Block all future loading
                    blogViewModel.forceEndLoading = true;
                }

            }

        });

        discountItemViewModel.discountItemParameterHolder.lat = Utils.getCurrentLat();
        discountItemViewModel.discountItemParameterHolder.lng = Utils.getCurrentLng();
        discountItemViewModel.discountItemParameterHolder.miles = "20";

        discountItemViewModel.setDiscountItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, discountItemViewModel.discountItemParameterHolder);
        //discountItemViewModel.setDiscountItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredItemViewModel.featuredItemParameterHolder);
        discountItemViewModel.getDiscountItemListByKeyData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {
                    case LOADING:
                        if (result.data != null) {
                            replaceDiscountItem(result.data);
                        }
                        break;
                    case SUCCESS:

                        if (result.data != null) {
                            replaceDiscountItem(result.data);
                        }
                        discountItemViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        discountItemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        featuredItemViewModel.featuredItemParameterHolder.lat = Utils.getCurrentLat();
        featuredItemViewModel.featuredItemParameterHolder.lng = Utils.getCurrentLng();
        featuredItemViewModel.featuredItemParameterHolder.miles = "20";

        //featuredItemViewModel.setFeaturedItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, discountItemViewModel.discountItemParameterHolder);

        featuredItemViewModel.setFeaturedItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredItemViewModel.featuredItemParameterHolder);
        featuredItemViewModel.getFeaturedItemListByKeyData().observe(this, result -> {

            if (result != null) {
                Animation fadeIn = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
                Animation fadeOut = AnimationUtils.loadAnimation(getContext(),R.anim.fade_out);
                switch (result.status) {
                    case LOADING:
                        if (result.data != null) {
                            if (geoprefs.getString(Constants.GEO_SERVICE_KEY, "false").equals("true")){
                                startGeofences(result.data);
                            }
                            replaceFeaturedItem(result.data);
                        }
                        break;

                    case SUCCESS:
                        binding.get().loadingLayout.setAnimation(fadeOut);
                        binding.get().loadingLayout.setVisibility(View.GONE);
                        if (result.data != null) {
                            if(binding.get().noListingsLayout.isShown()){
                                binding.get().noListingsLayout.setAnimation(fadeOut);
                                binding.get().noListingsLayout.setVisibility(View.GONE);
                                binding.get().ivSkip.setVisibility(View.GONE);
                            }

                            //String GEC_SERVICE_KEY = pref.getString(Constants.GEO_SERVICE_KEY,"");
                            if (geoprefs.getString(Constants.GEO_SERVICE_KEY, "false").equals("true")){
                                startGeofences(result.data);
                            }
                            replaceFeaturedItem(result.data);
                        }
                        featuredItemViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        if(binding.get().loadingLayout.isShown()){
                            binding.get().loadingLayout.setAnimation(fadeOut);
                            binding.get().loadingLayout.setVisibility(View.GONE);
                        }
                        binding.get().noListingsLayout.startAnimation(fadeIn);
                        binding.get().noListingsLayout.setVisibility(View.VISIBLE);
                        featuredItemViewModel.setLoadingState(false);
                        binding.get().ivSkip.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        popularItemViewModel.popularItemParameterHolder.lat = Utils.getCurrentLat();
        popularItemViewModel.popularItemParameterHolder.lng = Utils.getCurrentLng();
        popularItemViewModel.popularItemParameterHolder.miles = "20";

        popularItemViewModel.setPopularItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularItemViewModel.popularItemParameterHolder);
        popularItemViewModel.getPopularItemListByKeyData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {
                    case LOADING:

                        if (result.data != null) {
                            replacePopularPlacesList(result.data);
                        }

                        break;

                    case SUCCESS:

                        if (result.data != null) {
                            replacePopularPlacesList(result.data);
                        }
                        popularItemViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        popularItemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        recentItemViewModel.setRecentItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, recentItemViewModel.recentItemParameterHolder);
        recentItemViewModel.getRecentItemListByKeyData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {

                    case LOADING:
                        if (result.data != null) {
                            try {
                                replaceRecentItemList(result.data);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        break;

                    case SUCCESS:

                        if (result.data != null) {
                            try {
                                replaceRecentItemList(result.data);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        recentItemViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        recentItemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        popularCitiesViewModel.setPopularCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularCitiesViewModel.popularCitiesParameterHolder);
        popularCitiesViewModel.getPopularCityListData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {

                    case LOADING:

                        if (result.data != null) {
                            replacePopularCitiesList(result.data);
                        }
                        break;

                    case SUCCESS:

                        if (result.data != null) {
                            replacePopularCitiesList(result.data);
                        }
                        popularCitiesViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        popularCitiesViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        featuredCitiesViewModel.setFeaturedCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredCitiesViewModel.featuredCitiesParameterHolder);
        featuredCitiesViewModel.getFeaturedCityListData().observe(this, result -> {
            if (result != null) {

                switch (result.status) {
                    case LOADING:
                        if (result.data != null) {
                            replaceFeaturedCitiesList(result.data);
                        }

                        break;

                    case SUCCESS:

                        if (result.data != null) {
                            replaceFeaturedCitiesList(result.data);
                        }
                        featuredCitiesViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        featuredCitiesViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        recentCitiesViewModel.setRecentCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, recentCitiesViewModel.recentCitiesParameterHolder);
        recentCitiesViewModel.getRecentCityListData().observe(this, result -> {
            if (result != null) {

                switch (result.status) {
                    case LOADING:
                        if (result.data != null) {
                            replaceRecentCitiesList(result.data);
                        }

                        break;
                    case SUCCESS:
                        if (result.data != null) {
                            replaceRecentCitiesList(result.data);
                        }
                        recentCitiesViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        recentCitiesViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        recentCitiesViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(recentCitiesViewModel.isLoading);
            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }

        });
    }
    @SuppressLint("NewApi")
    private void replaceFeaturedItem(List<Item> itemList) {
        this.featuredItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceDiscountItem(List<Item> itemList) {
        this.discountItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replacePopularPlacesList(List<Item> itemList) {
        itemList.sort((o1, o2) -> Double.compare(Double.parseDouble(o2.overallRating), Double.parseDouble(o1.overallRating)));
        this.popularItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceRecentItemList(List<Item> itemList) throws ParseException {
        //itemList.sort((o1, o2) -> Double.compare(Double.parseDouble(o2.overallRating), Double.parseDouble(o1.overallRating)));
        this.recentItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replacePopularCitiesList(List<City> citiesList) {
        this.popularCitiesAdapter.get().replace(citiesList);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceFeaturedCitiesList(List<City> cities) {
        this.featuredCitiesAdapter.get().replace(cities);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceRecentCitiesList(List<City> cities) {
        this.recentCitiesAdapter.get().replace(cities);
        binding.get().executePendingBindings();
    }

    @Override
    public void onDispatched() {

    }


    private void startPagerAutoSwipe() {

        update = () -> {
            if (!touched) {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }

                if (viewPager.get() != null) {
                    viewPager.get().setCurrentItem(currentPage++, true);
                }

            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 1000, 3000);
    }

    private void setUnTouchedTimer() {

        if (unTouchedTimer != null) {
            unTouchedTimer.cancel();
            unTouchedTimer.purge();

        }
        unTouchedTimer = new Timer();
        unTouchedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                touched = false;

                handler.post(update);
            }
        }, 3000, 6000);
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onResume() {
        loadLoginUserId();
        super.onResume();
    }
    private void startGeofences(List<Item> itemList){
        Intent intent = new Intent(getContext(), GeolocationService.class);
        intent.putExtra("itemList", new Gson().toJson(itemList));
        getContext().startService(intent);
        Log.i("Geofence", "Building GoogleApiClient");
    }

}