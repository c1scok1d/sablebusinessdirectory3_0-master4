package com.sable.businesslistingapi.ui.city.cityList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.binding.FragmentDataBindingComponent;
import com.sable.businesslistingapi.databinding.FragmentItemLocationBinding;
import com.sable.businesslistingapi.ui.city.adapter.CitySelectionAdapter;
import com.sable.businesslistingapi.ui.common.DataBoundListAdapter;
import com.sable.businesslistingapi.ui.common.PSFragment;
import com.sable.businesslistingapi.utils.AutoClearedValue;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewmodel.city.CityViewModel;
import com.sable.businesslistingapi.viewobject.City;
import com.sable.businesslistingapi.viewobject.Image;
import com.sable.businesslistingapi.viewobject.Item;
import com.sable.businesslistingapi.viewobject.ItemCategory;
import com.sable.businesslistingapi.viewobject.ItemSubCategory;
import com.sable.businesslistingapi.viewobject.common.Status;
import com.sable.businesslistingapi.viewobject.holder.CityParameterHolder;

import java.util.Comparator;
import java.util.List;

public class ItemUploadCityListFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {



    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private CityViewModel cityViewModel;

    @VisibleForTesting
    private AutoClearedValue<FragmentItemLocationBinding> binding;
    private AutoClearedValue<CitySelectionAdapter> cityAdapter;
    Item item;
    City city;
    Image def_img;
    ItemCategory itemCategory;
    ItemSubCategory itemSubCategory;

    private CityParameterHolder cityParameterHolder = new CityParameterHolder().getRecentCities();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentItemLocationBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_location, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        binding.get().setLoadingMore(connectivity.isConnected());

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

        binding.get().cityList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();
                assert layoutManager != null;
                int lastPosition = layoutManager
                        .findLastVisibleItemPosition();
                if (lastPosition == cityAdapter.get().getItemCount() - 1) {

                    if (!binding.get().getLoadingMore() && !cityViewModel.forceEndLoading) {

                        if (connectivity.isConnected()) {

                            cityViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                            int limit = Config.LIST_CATEGORY_COUNT;
                            cityViewModel.offset = cityViewModel.offset + limit;

                            cityViewModel.setNextPageCityListObj(String.valueOf(Config.HOME_PRODUCT_COUNT),String.valueOf(cityViewModel.offset), cityParameterHolder);
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void initViewModels() {

        cityViewModel = new ViewModelProvider(this, viewModelFactory).get(CityViewModel.class);

    }

    @Override
    protected void initAdapters() {

        CitySelectionAdapter nvAdapter = new CitySelectionAdapter(dataBindingComponent, city -> navigationController.navigateBackToEntryItemFragment(ItemUploadCityListFragment.this.getActivity(), city.name, city.id,
                Constants.SELECT_CITY), this);

        this.cityAdapter = new AutoClearedValue<>(this, nvAdapter);
        binding.get().cityList.setAdapter(this.cityAdapter.get());

    }

    @Override
    protected void initData() {

//        getIntentData();

        cityViewModel.setCityListObj(String.valueOf(Config.HOME_PRODUCT_COUNT), Constants.ZERO, cityParameterHolder);

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

        });

    }

    private void getIntentData()
    {
        if (getActivity() != null)
        {
            cityViewModel.cityParameterHolder = (CityParameterHolder) getActivity().getIntent().getSerializableExtra(Constants.CITY_HOLDER);

        }
        if (getArguments() != null){

            //   cityViewModel.cityParameterHolder = (CityParameterHolder) getArguments().getSerializable(Constants.CITY_HOLDER);
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
