package com.macinternetservices.sablebusinessdirectory.ui.city.cityList;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.MainActivity;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentCityListBinding;
import com.macinternetservices.sablebusinessdirectory.ui.city.adapter.CityAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.city.CityViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.City;
import com.macinternetservices.sablebusinessdirectory.viewobject.Image;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCategory;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemSubCategory;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Status;
import com.macinternetservices.sablebusinessdirectory.viewobject.holder.CityParameterHolder;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class CityListFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {



    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private CityViewModel cityViewModel;

    @VisibleForTesting
    private AutoClearedValue<FragmentCityListBinding> binding;
    private AutoClearedValue<CityAdapter> cityAdapter;
    Item item;
    City city;
    Image def_img;
    ItemCategory itemCategory;
    ItemSubCategory itemSubCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentCityListBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_city_list, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        binding.get().setLoadingMore(connectivity.isConnected());

//        item= getArguments().getParcelable("ITEM");
//        city=getArguments().getParcelable("CITY");
//        itemCategory=getArguments().getParcelable("CAT");
//        itemSubCategory=getArguments().getParcelable("SUB_CAT");
//        def_img=getArguments().getParcelable("IMG");

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

        if(getActivity() instanceof MainActivity)  {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            ((MainActivity)getActivity()).updateToolbarIconColor(Color.WHITE);
            ((MainActivity)getActivity()).updateMenuIconWhite();
        }

        binding.get().cityListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager layoutManager = (GridLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();

                    if (lastPosition == cityAdapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !cityViewModel.forceEndLoading) {

                            cityViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                            int limit = Config.ITEM_COUNT;

                            cityViewModel.offset = cityViewModel.offset + limit;

                            cityViewModel.setNextPageCityListObj(String.valueOf(Config.HOME_PRODUCT_COUNT), String.valueOf(cityViewModel.offset), cityViewModel.cityParameterHolder);

                        }
                    }
                }
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            cityViewModel.forceEndLoading = false;

            cityViewModel.loadingDirection = Utils.LoadingDirection.top;

            cityViewModel.setCityListObj(String.valueOf(Config.HOME_PRODUCT_COUNT), Constants.ZERO, cityViewModel.cityParameterHolder);

        });

    }

    @Override
    protected void initViewModels() {

        cityViewModel = new ViewModelProvider(this, viewModelFactory).get(CityViewModel.class);

    }

    @Override
    protected void initAdapters() {

        CityAdapter nvAdapter = new CityAdapter(dataBindingComponent, new CityAdapter.NewsClickCallback() {
            @Override
            public void onClick(City city) {
                navigationController.navigateToSelectedCityDetail(CityListFragment.this.getActivity(), city.id, city.name, "");
            }
        }, this);


        this.cityAdapter = new AutoClearedValue<>(this, nvAdapter);
        binding.get().cityListRecyclerView.setAdapter(this.cityAdapter.get());

    }

    @Override
    protected void initData() {

        getIntentData();

        cityViewModel.setCityListObj(String.valueOf(Config.HOME_PRODUCT_COUNT), Constants.ZERO, cityViewModel.cityParameterHolder);

        cityViewModel.getCityListData().observe(this, listResource -> {

            if(listResource != null)
            {
                switch (listResource.status){

                    case SUCCESS:

                        if(listResource.data != null)
                        {
                            if(listResource.data.size() > 0)
                            {
                                replaceCity(listResource.data);
                            }
                        }

                        cityViewModel.setLoadingState(false);

                        break;

                    case LOADING:

                        if(listResource.data != null)
                        {
                            if(listResource.data.size() > 0)
                            {
                                replaceCity(listResource.data);
                            }
                        }

                        break;

                    case ERROR:

                        cityViewModel.setLoadingState(false);

                        break;
                }
            }

        });

        cityViewModel.getNextPageCityListData().observe(this, state -> {

            if (state != null) {
                if (state.status == Status.ERROR) {

                    cityViewModel.setLoadingState(false);
                    cityViewModel.forceEndLoading = true;

                }
            }
        });

        cityViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(cityViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }
        });

    }

    private void getIntentData()
    {
        if (getActivity().getIntent().getSerializableExtra(Constants.CITY_HOLDER) != null)
        {
            cityViewModel.cityParameterHolder = (CityParameterHolder) getActivity().getIntent().getSerializableExtra(Constants.CITY_HOLDER);
        } else if (getArguments() != null){
            cityViewModel.cityParameterHolder = (CityParameterHolder) getArguments().getSerializable(Constants.CITY_HOLDER);
            Log.e("foo", cityViewModel.cityParameterHolder.toString());

        }
    }

    @SuppressLint("NewApi")
    private void replaceCity(List<City> cities)
    {

        cities.sort(new Comparator<City>() {
            @Override
            public int compare(City o1, City o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        this.cityAdapter.get().replace(cities);
        binding.get().executePendingBindings();
    }

    @Override
    public void onDispatched() {

    }


}
