package com.sable.businesslistingapi.ui.city.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.databinding.ItemCityAdapterBinding;
import com.sable.businesslistingapi.ui.common.DataBoundListAdapter;
import com.sable.businesslistingapi.ui.common.DataBoundViewHolder;
import com.sable.businesslistingapi.utils.Objects;
import com.sable.businesslistingapi.viewobject.City;

public class CityAdapter extends DataBoundListAdapter<City, ItemCityAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final CityAdapter.NewsClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;

    public CityAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                 CityAdapter.NewsClickCallback callback,
                                 DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
    }


    @Override
    protected ItemCityAdapterBinding createBinding(ViewGroup parent) {
        ItemCityAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_city_adapter, parent, false,
                        dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            City city = binding.getCity();
            if (city != null && callback != null) {
                callback.onClick(city);
            }
        });
        return binding;
    }

    @Override
    public void bindView(DataBoundViewHolder<ItemCityAdapterBinding> holder, int position) {
        super.bindView(holder, position);

    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemCityAdapterBinding binding, City city) {

        binding.setCity(city);

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

    public interface NewsClickCallback {
        void onClick(City city);
    }

//    private void setAnimation(View viewToAnimate, int position) {
//        if (position > lastPosition) {
//            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_in_bottom);
//            viewToAnimate.startAnimation(animation);
//            lastPosition = position;
//        } else {
//            lastPosition = position;
//        }
//    }
}





