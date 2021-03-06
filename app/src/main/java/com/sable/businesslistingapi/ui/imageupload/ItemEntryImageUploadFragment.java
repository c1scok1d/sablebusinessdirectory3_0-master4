package com.sable.businesslistingapi.ui.imageupload;

import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.lifecycle.ViewModelProvider;

import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.viewmodel.item.ItemViewModel;

import timber.log.Timber;

public class ItemEntryImageUploadFragment extends ImageUploadFragment {

    ItemViewModel itemListViewModel;

    @Override
    protected void initViewModels() {
        itemListViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemViewModel.class);
    }

    @Override
    protected void initData() {
        getIntentData();

    }

    @Override
    protected void uploadImage() {
        if(!binding.get().descEditText.getText().toString().isEmpty())
        {
            img_desc = binding.get().descEditText.getText().toString();
        }

        if(getActivity() != null)
        itemListViewModel.uploadImage(imagePath, selectedImage,selectedId,img_id, img_desc,
                (getActivity()).getContentResolver()).observe(this, listResource -> {
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        progressDialog.get().cancel();

                        if (listResource.data != null) {
                            img_id = listResource.data.imgId;
                        }

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        navigationController.navigateToImageUploadActivity(getActivity(), img_id, imagePath,
                                                img_desc, Constants.IMAGE_UPLOAD_ITEM, itemListViewModel.itemId, itemListViewModel.savedIsPromotion);

                                        navigationController.navigateToImageUploadActivity(getActivity(), "", "",
                                                "", Constants.IMAGE_UPLOAD_ITEM, itemListViewModel.itemId, itemListViewModel.savedIsPromotion);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        if(itemListViewModel.savedIsPromotion){
                                            navigationController.navigateToItemPromoteActivity(getActivity(),itemListViewModel.itemId);
                                        } else {
                                            navigationController.navigateToMainActivity(ItemEntryImageUploadFragment.this.getActivity());
                                        }
                                        break;
                                }
                            }
                        };


                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Would you like to add additional photos?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                        break;

                    case LOADING:
                        break;


                    case ERROR:
                        progressDialog.get().cancel();

                }

            }

        });
    }


    private void getIntentData() {
        try {
            if (getActivity() != null) {
                if (getActivity().getIntent() != null) {
                    if (getActivity().getIntent().getExtras() != null) {
                        itemListViewModel.itemId = getActivity().getIntent().getExtras().getString(Constants.SELECTEDID);
                        itemListViewModel.savedIsPromotion = getActivity().getIntent().getExtras().getBoolean(String.valueOf(Constants.isPromotion));
                        Timber.e(itemListViewModel.savedIsPromotion.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

