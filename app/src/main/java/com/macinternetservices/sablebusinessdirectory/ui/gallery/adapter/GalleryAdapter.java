package com.macinternetservices.sablebusinessdirectory.ui.gallery.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ItemGalleryAdapterBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundListAdapter;
import com.macinternetservices.sablebusinessdirectory.utils.Objects;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewobject.Image;

import androidx.databinding.DataBindingUtil;

/**
 * Sable Business Directory on 09/01/2021
 */


public class GalleryAdapter extends DataBoundListAdapter<Image, ItemGalleryAdapterBinding> {

    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;
    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final ImageClickCallback callback;

    public GalleryAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                          ImageClickCallback callback,
                          DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
    }

    @Override
    protected ItemGalleryAdapterBinding createBinding(ViewGroup parent) {
        ItemGalleryAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_gallery_adapter, parent, false,
                        dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {
            Image image = binding.getImage();
            if (image != null && callback != null) {
                Utils.psLog("Clicked Image" + image.imgDesc);

                callback.onClick(image);
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
    protected void bind(ItemGalleryAdapterBinding binding, Image item) {
        binding.setImage(item);
    }

    @Override
    protected boolean areItemsTheSame(Image oldItem, Image newItem) {
        return Objects.equals(oldItem.imgId, newItem.imgId);
    }

    @Override
    protected boolean areContentsTheSame(Image oldItem, Image newItem) {
        return Objects.equals(oldItem.imgId, newItem.imgId);
    }

    public interface ImageClickCallback {
        void onClick(Image item);
    }
}