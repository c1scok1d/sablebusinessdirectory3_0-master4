package com.sable.businesslistingapi.ui.city.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.databinding.ItemCitySelectionBinding;
import com.sable.businesslistingapi.ui.common.DataBoundListAdapter;
import com.sable.businesslistingapi.utils.Objects;
import com.sable.businesslistingapi.viewobject.City;

public class CitySelectionAdapter extends DataBoundListAdapter<City, ItemCitySelectionBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    public CitySelectionAdapter.CityExpClickCallback callback;
    private DiffUtilDispatchedInterface diffUtilDispatchedInterface = null;
    private String selectId;

    public CitySelectionAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                CitySelectionAdapter.CityExpClickCallback callback, DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface=diffUtilDispatchedInterface;
    }

    @Override
    protected ItemCitySelectionBinding createBinding(ViewGroup parent) {
        ItemCitySelectionBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_city_selection, parent, false,
                        dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {
            City city = binding.getCity();
            if (city != null && callback != null) {
                binding.constraintLayout.setBackgroundResource(R.color.md_green_100);
                binding.clickImageView.setVisibility(View.VISIBLE);
                callback.onClick(city);
            }
        });

        return binding;
    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemCitySelectionBinding binding, City city) {
        binding.setCity(city);

//        binding.setImage(item.defaultPhoto.path);

        if(city.id.equals(selectId)){
            binding.constraintLayout.setBackgroundResource(R.color.md_green_100);
            binding.clickImageView.setVisibility(View.VISIBLE);
        }else {
            binding.constraintLayout.setBackgroundResource(R.color.md_white_1000);
            binding.clickImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected boolean areItemsTheSame(City oldItem, City newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name);
    }

    @Override
    protected boolean areContentsTheSame(City oldItem, City newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name);
    }

    public interface CityExpClickCallback {
        void onClick(City city);
    }
}

