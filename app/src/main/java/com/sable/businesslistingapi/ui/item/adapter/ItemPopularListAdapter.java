package com.sable.businesslistingapi.ui.item.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.databinding.ItemItemVerticalWithCityNameBinding;
import com.sable.businesslistingapi.ui.common.DataBoundListAdapter;
import com.sable.businesslistingapi.ui.common.DataBoundViewHolder;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.Objects;
import com.sable.businesslistingapi.viewobject.Item;

public class ItemPopularListAdapter extends DataBoundListAdapter<Item, ItemItemVerticalWithCityNameBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final ItemPopularListAdapter.NewsClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;

    public ItemPopularListAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                           ItemPopularListAdapter.NewsClickCallback callback,
                           DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
    }

    @Override
    protected ItemItemVerticalWithCityNameBinding createBinding(ViewGroup parent) {
        ItemItemVerticalWithCityNameBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_item_vertical_with_city_name, parent, false,
                        dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            Item item = binding.getItem();
            if (item != null && callback != null) {
                callback.onClick(item);
            }
        });
        return binding;
        
    }


    @Override
    public void bindView(DataBoundViewHolder<ItemItemVerticalWithCityNameBinding> holder, int position) {
        super.bindView(holder, position);
    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemItemVerticalWithCityNameBinding binding, Item item) {

        binding.setItem(item);

        binding.ratingValueTextView.setText(String.valueOf(item.ratingDetails.totalRatingValue));
        binding.reviewValueTextView.setText(String.format("( %s %s )", String.valueOf(item.ratingDetails.totalRatingCount), binding.getRoot().getResources().getString(R.string.dashboard_review)));

        if (item.paidStatus.equals(Constants.ADSPROGRESS)){
            binding.sponsorCardView.setVisibility(View.VISIBLE);
            binding.addedDateStrTextView.setText(R.string.paid__sponsored);
        } else{
            binding.sponsorCardView.setVisibility(View.GONE);
        }
    }

    @Override
    protected boolean areItemsTheSame(Item oldItem, Item newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name)
                && oldItem.ratingDetails.totalRatingValue == newItem.ratingDetails.totalRatingValue
                && oldItem.ratingDetails.totalRatingCount == newItem.ratingDetails.totalRatingCount;

    }

    @Override
    protected boolean areContentsTheSame(Item oldItem, Item newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name)
                && oldItem.ratingDetails.totalRatingValue == newItem.ratingDetails.totalRatingValue
                && oldItem.ratingDetails.totalRatingCount == newItem.ratingDetails.totalRatingCount;
    }

    public interface NewsClickCallback {
        void onClick(Item item);
    }


}


