package com.sable.businesslistingapi.ui.item.history.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.databinding.ItemHorizontalHistoryAdapterBinding;
import com.sable.businesslistingapi.ui.common.DataBoundListAdapter;
import com.sable.businesslistingapi.utils.Objects;
import com.sable.businesslistingapi.viewobject.ItemHistory;

public class HistoryHorizontalListAdapter extends DataBoundListAdapter<ItemHistory, ItemHorizontalHistoryAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private HistoryClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface = null;

    public HistoryHorizontalListAdapter(androidx.databinding.DataBindingComponent dataBindingComponent, HistoryClickCallback historyClickCallback) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = historyClickCallback;
    }

    @Override
    protected ItemHorizontalHistoryAdapterBinding createBinding(ViewGroup parent) {
        ItemHorizontalHistoryAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_horizontal_history_adapter, parent, false,
                        dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {
            ItemHistory itemHistory = binding.getHistory();
            if (itemHistory != null && callback != null) {
                callback.onClick(itemHistory);
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
    protected void bind(ItemHorizontalHistoryAdapterBinding binding, ItemHistory item) {
        binding.setHistory(item);
        binding.historyNameTextView.setText(item.historyName);
    }

    @Override
    protected boolean areItemsTheSame(ItemHistory oldItem, ItemHistory newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.historyName.equals(newItem.historyName);
    }

    @Override
    protected boolean areContentsTheSame(ItemHistory oldItem, ItemHistory newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.historyName.equals(newItem.historyName);
    }

    public interface HistoryClickCallback {
        void onClick(ItemHistory itemHistory);
    }
}

