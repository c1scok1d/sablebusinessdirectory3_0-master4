package com.macinternetservices.sablebusinessdirectory.ui.imageupload;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.macinternetservices.sablebusinessdirectory.MainActivity;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.ui.item.upload.ItemUploadFragment;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.ItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;

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
                        //PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
                        /*psDialogMsg.showSuccessDialog(getString(R.string.message__image_upload_complete), getString(R.string.message__ok_close)); //would you like to add another image
                        psDialogMsg.show(); //show yes/no button */

                        if (listResource.data != null) {
                            img_id = listResource.data.imgId;
                        }

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        navigationController.navigateToImageUploadActivity(getActivity(), img_id, imagePath,
                                                img_desc, Constants.IMAGE_UPLOAD_ITEM, itemListViewModel.itemId);

                                        navigationController.navigateToImageUploadActivity(getActivity(), "", "",
                                                "", Constants.IMAGE_UPLOAD_ITEM, itemListViewModel.itemId);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
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

                      /*  psDialogMsg.okButton.setOnClickListener(v -> {
                            psDialogMsg.cancel();

                            navigationController.navigateToItemPromoteActivity(getActivity(),itemListViewModel.itemId);
//                            closeActivity();

                        }); */

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
                        itemListViewModel.savedIsPromotion = getActivity().getIntent().getExtras().getBoolean(Constants.CHECKED_PROMOTION);
                        Timber.e(itemListViewModel.savedIsPromotion.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

