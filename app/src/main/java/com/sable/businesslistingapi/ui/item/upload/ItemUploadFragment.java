package com.sable.businesslistingapi.ui.item.upload;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.sable.businesslistingapi.MainActivity;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.binding.FragmentDataBindingComponent;
import com.sable.businesslistingapi.binding.PlaceArrayAdapter;
import com.sable.businesslistingapi.databinding.FragmentItemUploadBinding;
import com.sable.businesslistingapi.ui.common.DataBoundListAdapter;
import com.sable.businesslistingapi.ui.common.PSFragment;
import com.sable.businesslistingapi.utils.AutoClearedValue;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.GeoCodeLocation;
import com.sable.businesslistingapi.utils.PSDialogMsg;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewmodel.image.ImageViewModel;
import com.sable.businesslistingapi.viewmodel.item.ItemViewModel;
import com.sable.businesslistingapi.viewobject.Image;
import com.sable.businesslistingapi.viewobject.common.Resource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ItemUploadFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface, OnMapReadyCallback {

    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);


    private ItemViewModel itemViewModel;
    private ImageViewModel imageViewModel;
    private PSDialogMsg psDialogMsg;
    private GeoCodeLocation locationAddress;
    public String itemName;
    private GoogleMap map;
    private Marker marker;
    //private AutoCompleteTextView search_tx_loc;
    private Double latitude, longitude;
    private Boolean promoChanged = false;

    @VisibleForTesting
    private AutoClearedValue<FragmentItemUploadBinding> binding;
    private AutoClearedValue<ProgressDialog> progressDialog;
    private AutoClearedValue<ItemEntryImageAdapter> itemEntryImageAdapter;
    private PlaceArrayAdapter mAutoCompleteAdapter;


    private Calendar dateTime = Calendar.getInstance();
    //endregion

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.get().mapView.onResume();
        binding.get().mapView.getMapAsync(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentItemUploadBinding dataBinding = (FragmentItemUploadBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_item_upload, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        binding.get().mapView.onCreate(savedInstanceState);

        return binding.get().getRoot();
    }

    @Override
    public void onDispatched() {

    }

    @Override
    protected void initUIAndActions() {

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            ((MainActivity) getActivity()).updateToolbarIconColor(Color.WHITE);
            ((MainActivity) getActivity()).updateMenuIconWhite();
        }

        binding.get().itemImageRecyclerView.setNestedScrollingEnabled(false);


        psDialogMsg = new PSDialogMsg(getActivity(), false);

        // for alert box
        progressDialog = new AutoClearedValue<>(this, new ProgressDialog(getActivity()));
        progressDialog.get().setMessage(getString(R.string.message__please_wait));
        progressDialog.get().setCancelable(false);
        binding.get().isPromotion.setVisibility(View.GONE);
        binding.get().searchTextView22.setVisibility(View.GONE);


        // click save button
        binding.get().saveButton.setOnClickListener(v -> {

            if (checkNameTheSame()) {
                if (checkCondition()) {
                    saveItem();
                    progressDialog.get().show();
                }
            } else {
                    Toast.makeText(getContext(),R.string.item_upload__already_saved,Toast.LENGTH_LONG).show();
                psDialogMsg.showErrorDialog(getString(R.string.item_upload__already_saved), getString(R.string.app__ok));
                psDialogMsg.show();
            }
        });

        // click save button
        binding.get().cancelButtonUpload.setOnClickListener(v -> {

            // @Override
            //public void onClick(View view) {
            psDialogMsg.cancel();

//                            if (Config.CLOSE_ENTRY_AFTER_SUBMIT) {
//                                if (getActivity() != null) {
//                                    getActivity().finish();
//                                }
//                            }

            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                getActivity().finish();
            }
            //}

        });


        binding.get().mapViewButton.setOnClickListener(v -> navigationController.navigateToLists(getActivity(), Constants.MAP, Utils.getCurrentLat(), Utils.getCurrentLng()));

        // for city
 /*       if(!selectedCityId.isEmpty()) {
            binding.get().cityTextView1.setVisibility(View.VISIBLE);
            binding.get().cityNameTextView1.setVisibility(View.VISIBLE);
            binding.get().cityImageView2.setVisibility(View.VISIBLE);
            binding.get().businessNameTextView.setVisibility(View.VISIBLE);
        } else {

        }*/
        binding.get().cityTextView1.setOnClickListener(v -> {

            navigationController.navigateToExpandActivity(getActivity(), Constants.SELECT_CITY, itemViewModel.cityId, "");


        });
        // for category
        binding.get().categoryTextView.setOnClickListener(v -> {
            if (itemViewModel.cityId.isEmpty()/* && selectedCityId.isEmpty()*/) {
                psDialogMsg.showWarningDialog("Select City", getString(R.string.app__ok));
                psDialogMsg.show();
            } else {

                navigationController.navigateToExpandActivity(getActivity(), Constants.SELECT_CATEGORY, itemViewModel.catSelectId, itemViewModel.cityId);

            }
        });

        // region for sub_category
        binding.get().subCatTextView.setOnClickListener(v -> {
            if (itemViewModel.catSelectId.isEmpty()) {
                psDialogMsg.showWarningDialog(getString(R.string.error_message__choose_category), getString(R.string.app__ok));
                psDialogMsg.show();
            } else {

                navigationController.navigateToExpandActivity(getActivity(), Constants.SELECT_SUBCATEGORY, itemViewModel.catSelectId, itemViewModel.cityId);

            }
        });

        //region for status

        binding.get().statusTextView.setOnClickListener(v -> navigationController.navigateToExpandActivity(getActivity(), Constants.SELECT_STATUS, itemViewModel.statusSelectId, ""));


        // for openTime
        binding.get().openTimeTextView.setOnClickListener(v -> openTimePicker(binding.get().openTimeTextView));

        // for closeTime
        binding.get().closeTimeTextView.setOnClickListener(v -> openTimePicker(binding.get().closeTimeTextView));

        // for latitude
        //binding.get().latitudeTextView.setVisibility(View.GONE);

        // for longitude
        //binding.get().longitudeTextView.setVisibility(View.GONE);

        binding.get().attributeHeaderButton.setOnClickListener(v -> navigationController.navigateToSpecificationListActivity(getActivity(), itemViewModel.itemSelectId, itemName));

        binding.get().uploadImageButton.setOnClickListener(v -> navigationController.navigateToImageUploadActivity(getActivity(), itemViewModel.img_id, itemViewModel.img_path,
                itemViewModel.img_desc, Constants.IMAGE_UPLOAD_ITEM, itemViewModel.img_id, itemViewModel.savedIsPromotion));

        binding.get().uploadImageButton.setOnClickListener(v -> navigationController.navigateToImageUploadActivity(getActivity(), "", "",
                "", Constants.IMAGE_UPLOAD_ITEM, itemViewModel.itemSelectId, itemViewModel.savedIsPromotion));

        binding.get().viewAllTextView.setOnClickListener(v -> navigationController.navigateToImageList(getActivity(), itemViewModel.itemSelectId));


        Places.initialize(getContext(), getResources().getString(R.string.google_map_api_key));
        mAutoCompleteAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item);
        binding.get().txtAutocomplete.setAdapter(mAutoCompleteAdapter);
        binding.get().txtAutocomplete.setThreshold(3);//start searching from 3 characters
        binding.get().txtAutocomplete.setAdapter(mAutoCompleteAdapter);

        binding.get().txtAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!binding.get().txtAutocomplete.getText().toString().isEmpty()) {
                    LatLng latLng = getLatLngFromAddress(binding.get().txtAutocomplete.getText().toString());
                    if (latLng != null) {
                        changeCamera(String.valueOf(latLng.latitude),String.valueOf(latLng.longitude));
                        latitude = latLng.latitude;
                        longitude = latLng.longitude;
                        String address = getAddressFromLatLng(latLng);
                        if(!binding.get().isPromotion.isChecked()) {
                            binding.get().searchTextView22.setVisibility(View.VISIBLE);
                            binding.get().isPromotion.setVisibility(View.VISIBLE);
                        }
                        if (address != null) {
                            Log.d("Adddress", address);
                        } else {
                            Log.d("Adddress", "Address Not Found");
                        }
                    } else {
                        Log.d("Lat Lng", "Lat Lng Not Found");
                    }
                } else {
                    binding.get().searchTextView22.setVisibility(View.GONE);
                    binding.get().isPromotion.setVisibility(View.GONE);
                }
            }
        });


        binding.get().btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latitude = locationGPS.getLatitude();
                longitude = locationGPS.getLongitude();
                //String address = getAddressFromLatLng();
                changeCamera(String.valueOf(locationGPS.getLatitude()), String.valueOf(locationGPS.getLongitude()));
                binding.get().txtAutocomplete.setText(getAddressFromLatLng(new LatLng(locationGPS.getLatitude(), locationGPS.getLongitude())));
                if(!binding.get().isPromotion.isChecked()) {
                    binding.get().searchTextView22.setVisibility(View.VISIBLE);
                    binding.get().isPromotion.setVisibility(View.VISIBLE);
                }
            }
        });
    }



    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if (addresses != null) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()/*String city = addresses.get(0).getLocality();
                /*bldgNo = addresses.get(0).getSubThoroughfare();
                street = addresses.get(0).getThoroughfare();
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                zipcode = addresses.get(0).getPostalCode();
                tvCurrentAddress.setText(address); */
                //Address address = addresses.get(0).getAddressLine();
                return address;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private LatLng getLatLngFromAddress(String address) {

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null) {
                Address singleaddress = addressList.get(0);
                LatLng latLng = new LatLng(singleaddress.getLatitude(), singleaddress.getLongitude());
                return latLng;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void initViewModels() {
        itemViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemViewModel.class);
        imageViewModel = new ViewModelProvider(this, viewModelFactory).get(ImageViewModel.class);
    }

    @Override
    protected void initAdapters() {
        ItemEntryImageAdapter nvAdapter = new ItemEntryImageAdapter(dataBindingComponent, new ItemEntryImageAdapter.ItemImageViewClickCallback() {
            @Override
            public void onClick(Image image) {
//                Toast.makeText(getContext(),"On click",Toast.LENGTH_SHORT).show();
                navigationController.navigateToImageUploadActivity(getActivity(), image.imgId, image.imgPath, image.imgDesc, Constants.IMAGE_UPLOAD_ITEM, itemViewModel.itemSelectId, itemViewModel.savedIsPromotion);

            }

            @Override
            public void onDeleteClick(Image deleteImage) {
                imageViewModel.setDeleteImageObj(itemViewModel.itemSelectId, deleteImage.imgId);

            }
        }, this);

        itemEntryImageAdapter = new AutoClearedValue<>(this, nvAdapter);
        binding.get().itemImageRecyclerView.setAdapter(itemEntryImageAdapter.get());
    }

    @Override
    protected void initData() {

        try {
            if (getActivity() != null) {
                if (getActivity().getIntent().getExtras() != null) {
                    itemViewModel.itemSelectId = getActivity().getIntent().getStringExtra(Constants.ITEM_ID);
                    itemName = getActivity().getIntent().getStringExtra(Constants.ITEM_NAME);

                    if (!itemName.isEmpty()) {
                        itemViewModel.edit_mode = true;
                    }

                    editMode(itemViewModel.itemSelectId);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("", e);
        }

        //region Getting ImageList
        imageViewModel.getImageListByIdLiveData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {

                    case LOADING:
                        if (result.data != null) {
                            replaceImages(result.data);
                        }
                        break;

                    case SUCCESS:
                        if (result.data != null) {
                            replaceImages(result.data);
                        }
                        imageViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        imageViewModel.setLoadingState(false);
                        break;

                }
            }
        });
        //endregion

        //region image delete
        imageViewModel.getDeleteImageData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {
                    case SUCCESS:
                        Toast.makeText(ItemUploadFragment.this.getContext(), "Succeed", Toast.LENGTH_SHORT).show();

                        break;

                    case ERROR:
                        Toast.makeText(ItemUploadFragment.this.getActivity(), result.message, Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        //endregion


        //Getting One Item

        itemViewModel.getItemDetailData().observe(this, result -> {
            if (result != null) {
                switch (result.status) {
                    case LOADING:

                        if (result.data != null) {
                            binding.get().itemNameEditText.setText(result.data.name);
                            binding.get().categoryTextView.setText(result.data.category.name);
                            binding.get().subCatTextView.setText(result.data.subCategory.name);
                            binding.get().cityTextView1.setText(result.data.city.name);
                            binding.get().itemDescriptionTextView.setText(result.data.description);
                            binding.get().searchTagEditText.setText(result.data.searchTag);
                            binding.get().itemHighlightInformationTextView.setText(result.data.highlightInformation);
                            //binding.get().latitudeTextView.setText(result.data.lat);
                            //binding.get().longitudeTextView.setText(result.data.lng);

                            binding.get().openTimeTextView.setText(result.data.openingHour);
                            binding.get().closeTimeTextView.setText(result.data.closingHour);
                            binding.get().phoneOneTextView.setText((PhoneNumberUtils.formatNumber(result.data.phone1,Locale.getDefault().getCountry())));
                            binding.get().phoneTwoTextView.setText(result.data.phone2);
                            binding.get().phoneThreeTextView.setText(result.data.phone3);
                            binding.get().emailTextView.setText(result.data.email);
                            binding.get().txtAutocomplete.setText(result.data.address);
                            binding.get().facebookTextView.setText(result.data.facebook);
                            binding.get().googlePlusTextView.setText(result.data.google_plus);
                            binding.get().twitterTextView.setText(result.data.twitter);
                            binding.get().youtubeTextView.setText(result.data.youtube);
                            binding.get().instagrmTextView.setText(result.data.instagram);
                            binding.get().pinterestTextView.setText(result.data.pinterest);
                            binding.get().websiteTextView.setText(result.data.website);
                            binding.get().whatappsTextView.setText(result.data.whatsapp);
                            binding.get().messangerTextView2.setText(result.data.messenger);
                            binding.get().timeRemarkTextView.setText(result.data.time_remark);
                            binding.get().termsAndConditionTextView.setText(result.data.terms);
                            binding.get().cancelationTextView.setText(result.data.cancelation_policy);
                            binding.get().additionalTextView.setText(result.data.additional_info);
                            binding.get().statusTextView.setText(result.data.itemStatusId);

                            itemViewModel.savedItemName = result.data.name;
                            itemViewModel.savedCategoryName = result.data.category.name;
                            itemViewModel.savedSubCategoryName = result.data.subCategory.name;
                            itemViewModel.savedDescription = result.data.description;
                            itemViewModel.savedSearchTag = result.data.searchTag;
                            itemViewModel.savedHighLightInformation = result.data.highlightInformation;
                            itemViewModel.savedLatitude = result.data.lat;
                            itemViewModel.savedLongitude = result.data.lng;
                            itemViewModel.savedOpeningHour = result.data.openingHour;
                            itemViewModel.savedClosingHour = result.data.closingHour;
                            itemViewModel.savedPhoneOne = result.data.phone1;
                            itemViewModel.savedPhoneTwo = result.data.phone2;
                            itemViewModel.savedPhoneThree = result.data.phone3;
                            itemViewModel.savedEmail = result.data.email;
                            itemViewModel.savedAddress = result.data.address;
                            itemViewModel.savedFacebook = result.data.facebook;
                            itemViewModel.savedGooglePlus = result.data.google_plus;
                            itemViewModel.savedTwitter = result.data.twitter;
                            itemViewModel.savedYoutube = result.data.youtube;
                            itemViewModel.savedInstagram = result.data.instagram;
                            itemViewModel.savedPinterest = result.data.pinterest;
                            itemViewModel.savedWebsite = result.data.website;
                            itemViewModel.savedWhatsapp = result.data.whatsapp;
                            itemViewModel.savedMessenger = result.data.messenger;
                            itemViewModel.savedTimeRemark = result.data.time_remark;
                            itemViewModel.savedTerms = result.data.terms;
                            itemViewModel.savedCancelationPolicy = result.data.cancelation_policy;
                            itemViewModel.savedAdditionalInfo = result.data.additional_info;

                            itemViewModel.cityId = result.data.cityId;
                            itemViewModel.catSelectId = result.data.catId;
                            itemViewModel.subCatSelectId = result.data.subCatId;
                            itemViewModel.img_desc = result.data.defaultPhoto.imgDesc;
                            itemViewModel.img_id = result.data.defaultPhoto.imgId;
                            itemViewModel.img_path = result.data.defaultPhoto.imgPath;

                            if (result.data.isFeatured.equals("1")) {
                                binding.get().isFeature.setChecked(true);
                                itemViewModel.savedIsFeatured = true;
                            } else if (result.data.isFeatured.equals("0")) {
                                binding.get().isFeature.setChecked(false);
                                itemViewModel.savedIsFeatured = false;
                            }
                            if (result.data.paidStatus.equals("Progress")) {
                                binding.get().searchTextView22.setVisibility(View.GONE);
                                binding.get().isPromotion.setVisibility(View.GONE);
                            } else { //if (result.data.isPromotion.equals("0") || !result.data.paidStatus.equals("In Progress")) {
                                binding.get().isPromotion.setVisibility(View.VISIBLE);
                                binding.get().searchTextView22.setVisibility(View.VISIBLE);
                                binding.get().isPromotion.setChecked(false);
                                itemViewModel.savedIsPromotion = false;
                                binding.get().isPromotion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                                    {
                                        if ( isChecked ) {
                                            binding.get().isPromotion.setChecked(true);
                                            itemViewModel.savedIsPromotion = true;
                                            promoChanged = true;
                                        }
                                    }
                                });
                            }

                            switch (result.data.itemStatusId) {
                                case "1":
                                    binding.get().statusTextView.setText(Constants.CHECKED_PUBLISH);
                                    itemViewModel.savedStatusSelectedId = Constants.CHECKED_PUBLISH;
                                    break;
                                case "0":
                                    binding.get().statusTextView.setText(Constants.CHECKED_UNPUBLISH);
                                    itemViewModel.savedStatusSelectedId = Constants.CHECKED_UNPUBLISH;
                                    break;
                                case "2":
                                case "3":
                                    pendingOrRejectItem(result.data.itemStatusId);
                                    binding.get().statusTextView.setText("");
//                                    itemViewModel.savedStatusSelectedId = result.data.itemStatusId;
                                    break;
                            }
                            if (!binding.get().txtAutocomplete.getText().toString().isEmpty()) {
                                LatLng latLng = getLatLngFromAddress(binding.get().txtAutocomplete.getText().toString());
                                if (latLng != null) {
                                    latitude = latLng.latitude;
                                    longitude = latLng.longitude;
                                    changeCamera(String.valueOf(latLng.latitude),String.valueOf(latLng.longitude));
                                    String address = getAddressFromLatLng(latLng);
                                    if (address != null) {
                                        Log.d("Adddress", address);

                                    } else {
                                        Log.d("Adddress", "Address Not Found");
                                    }
                                } else {
                                    Log.d("Lat Lng", "Lat Lng Not Found");
                                }
                            } else {
                                binding.get().searchTextView22.setVisibility(View.GONE);
                                binding.get().isPromotion.setVisibility(View.GONE);
                            }

                        }
                        break;

                    case SUCCESS:
                        if (result.data != null) {
                            binding.get().itemNameEditText.setText(result.data.name);
                            binding.get().categoryTextView.setText(result.data.category.name);
                            binding.get().subCatTextView.setText(result.data.subCategory.name);
                            binding.get().itemDescriptionTextView.setText(result.data.description);
                            binding.get().searchTagEditText.setText(result.data.searchTag);
                            binding.get().itemHighlightInformationTextView.setText(result.data.highlightInformation);
//                            binding.get().latitudeTextView.setText(result.data.lat);
//                            binding.get().longitudeTextView.setText(result.data.lng);
                            binding.get().openTimeTextView.setText(result.data.openingHour);
                            binding.get().closeTimeTextView.setText(result.data.closingHour);
                            binding.get().phoneOneTextView.setText(PhoneNumberUtils.formatNumber(result.data.phone1,Locale.getDefault().getCountry()));
                            binding.get().phoneTwoTextView.setText(result.data.phone2);
                            binding.get().phoneThreeTextView.setText(result.data.phone3);
                            binding.get().emailTextView.setText(result.data.email);
                            binding.get().txtAutocomplete.setText(result.data.address);
                            binding.get().facebookTextView.setText(result.data.facebook);
                            binding.get().googlePlusTextView.setText(result.data.google_plus);
                            binding.get().twitterTextView.setText(result.data.twitter);
                            binding.get().youtubeTextView.setText(result.data.youtube);
                            binding.get().instagrmTextView.setText(result.data.instagram);
                            binding.get().pinterestTextView.setText(result.data.pinterest);
                            binding.get().websiteTextView.setText(result.data.website);
                            binding.get().whatappsTextView.setText(result.data.whatsapp);
                            binding.get().messangerTextView2.setText(result.data.messenger);
                            binding.get().timeRemarkTextView.setText(result.data.time_remark);
                            binding.get().termsAndConditionTextView.setText(result.data.terms);
                            binding.get().cancelationTextView.setText(result.data.cancelation_policy);
                            binding.get().additionalTextView.setText(result.data.additional_info);
                            //String foo = (result.data.paidStatus);

                            if (result.data.itemStatusId.equals("1")) {
                                binding.get().statusTextView.setText(Constants.CHECKED_PUBLISH);

                            } else if (result.data.itemStatusId.equals("0")) {
                                binding.get().statusTextView.setText(Constants.CHECKED_UNPUBLISH);

                            }

                            itemViewModel.cityId = result.data.cityId;
                            itemViewModel.catSelectId = result.data.catId;
                            itemViewModel.subCatSelectId = result.data.subCatId;
                            itemViewModel.img_desc = result.data.defaultPhoto.imgDesc;
                            itemViewModel.img_id = result.data.defaultPhoto.imgId;
                            itemViewModel.img_path = result.data.defaultPhoto.imgPath;
                            itemViewModel.savedItemName = result.data.name;
                            itemViewModel.savedCategoryName = result.data.category.name;
                            itemViewModel.savedSubCategoryName = result.data.subCategory.name;
                            itemViewModel.savedDescription = result.data.description;
                            itemViewModel.savedSearchTag = result.data.searchTag;
                            itemViewModel.savedHighLightInformation = result.data.highlightInformation;
                            itemViewModel.savedLatitude = result.data.lat;
                            itemViewModel.savedLongitude = result.data.lng;
                            itemViewModel.savedOpeningHour = result.data.openingHour;
                            itemViewModel.savedClosingHour = result.data.closingHour;
                            itemViewModel.savedPhoneOne = result.data.phone1;
                            itemViewModel.savedPhoneTwo = result.data.phone2;
                            itemViewModel.savedPhoneThree = result.data.phone3;
                            itemViewModel.savedEmail = result.data.email;
                            itemViewModel.savedAddress = result.data.address;
                            itemViewModel.savedFacebook = result.data.facebook;
                            itemViewModel.savedGooglePlus = result.data.google_plus;
                            itemViewModel.savedTwitter = result.data.twitter;
                            itemViewModel.savedYoutube = result.data.youtube;
                            itemViewModel.savedInstagram = result.data.instagram;
                            itemViewModel.savedPinterest = result.data.pinterest;
                            itemViewModel.savedWebsite = result.data.website;
                            itemViewModel.savedWhatsapp = result.data.whatsapp;
                            itemViewModel.savedMessenger = result.data.messenger;
                            itemViewModel.savedTimeRemark = result.data.time_remark;
                            itemViewModel.savedTerms = result.data.terms;
                            itemViewModel.savedCancelationPolicy = result.data.cancelation_policy;
                            itemViewModel.savedAdditionalInfo = result.data.additional_info;
//                            itemViewModel.savedStatusSelectedId = result.data.itemStatusId;

                            if (result.data.isFeatured.equals("1")) {
                                binding.get().isFeature.setChecked(true);
                                itemViewModel.savedIsFeatured = true;
                            } else if (result.data.isFeatured.equals("0")) {
                                binding.get().isFeature.setChecked(false);
                                itemViewModel.savedIsFeatured = false;
                            }

                            if (result.data.paidStatus.equals("Progress")) {
                                binding.get().isPromotion.setVisibility(View.GONE);
                                binding.get().searchTextView22.setVisibility(View.GONE);

                            } else {
                                binding.get().isPromotion.setVisibility(View.VISIBLE);
                                binding.get().searchTextView22.setVisibility(View.VISIBLE);
                                binding.get().isPromotion.setChecked(false);
                                itemViewModel.savedIsPromotion = false;
                                binding.get().isPromotion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                                    {
                                        if ( isChecked ) {
                                            binding.get().isPromotion.setChecked(true);
                                            itemViewModel.savedIsPromotion = true;
                                            promoChanged = true;
                                        }
                                    }
                                });
                            }

                            switch (result.data.itemStatusId) {
                                case "1":
                                    binding.get().statusTextView.setText(Constants.CHECKED_PUBLISH);
                                    itemViewModel.savedStatusSelectedId = Constants.CHECKED_PUBLISH;
                                    break;
                                case "0":
                                    binding.get().statusTextView.setText(Constants.CHECKED_UNPUBLISH);
                                    itemViewModel.savedStatusSelectedId = Constants.CHECKED_UNPUBLISH;
                                    break;
                                case "2":
                                case "3":
                                    pendingOrRejectItem(result.data.itemStatusId);
                                    break;
                            }
                            itemViewModel.setLoadingState(false);
                        }
                        if (!binding.get().txtAutocomplete.getText().toString().isEmpty()) {
                            LatLng latLng = getLatLngFromAddress(binding.get().txtAutocomplete.getText().toString());
                            if (latLng != null) {
                                latitude = latLng.latitude;
                                longitude = latLng.longitude;
                                changeCamera(String.valueOf(latLng.latitude),String.valueOf(latLng.longitude));
                                String address = getAddressFromLatLng(latLng);
                                if (address != null) {
                                    Log.d("Adddress", address);

                                } else {
                                    Log.d("Adddress", "Address Not Found");
                                }
                            } else {
                                Log.d("Lat Lng", "Lat Lng Not Found");
                            }
                        } else{
                            binding.get().searchTextView22.setVisibility(View.GONE);
                            binding.get().isPromotion.setVisibility(View.GONE);
                        }

                        break;

                    case ERROR:
                        itemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        //for saveItem
        progressDialog.get().cancel();

        PSDialogMsg psDialogMsgerror = new PSDialogMsg(getActivity(), false);

        PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
        psDialogMsg.showSuccessDialog(getString(R.string.item_upload__item_saved), getString(R.string.app__ok));

        itemViewModel.getSaveOneItemData().observe(this, itemResource -> {

            if (itemResource != null) {

                switch (itemResource.status) {
                    case LOADING:
                        break;

                    case ERROR:
                        progressDialog.get().cancel();

                        //psDialogMsgerror.showErrorDialog();
                        psDialogMsgerror.showErrorDialog(itemResource.message, getString(R.string.app__ok));
                        psDialogMsgerror.show();

                        psDialogMsgerror.okButton.setOnClickListener(v -> psDialogMsgerror.cancel());
                        break;

                    case SUCCESS:

                        if (itemResource.data != null) {

                            itemViewModel.savedItemName = binding.get().itemNameEditText.getText().toString();
                            itemViewModel.savedCategoryName = binding.get().categoryTextView.getText().toString();
                            itemViewModel.savedSubCategoryName = binding.get().subCatTextView.getText().toString();
                            itemViewModel.savedCityName = binding.get().cityTextView1.getText().toString();
                            itemViewModel.savedDescription = binding.get().itemDescriptionTextView.getText().toString();
                            itemViewModel.savedSearchTag = binding.get().searchTagEditText.getText().toString();
                            itemViewModel.savedHighLightInformation = binding.get().itemHighlightInformationTextView.getText().toString();
                            itemViewModel.savedIsFeatured = binding.get().isFeature.isChecked();
                            itemViewModel.savedOpeningHour = binding.get().openTimeTextView.getText().toString();
                            itemViewModel.savedClosingHour = binding.get().closeTimeTextView.getText().toString();
                            itemViewModel.savedIsPromotion = binding.get().isPromotion.isChecked();
                            itemViewModel.savedPhoneOne = binding.get().phoneOneTextView.getText().toString();
                            itemViewModel.savedPhoneTwo = binding.get().phoneTwoTextView.getText().toString();
                            itemViewModel.savedPhoneThree = binding.get().phoneThreeTextView.getText().toString();
                            itemViewModel.savedEmail = binding.get().emailTextView.getText().toString();
                            itemViewModel.savedAddress = binding.get().txtAutocomplete.getText().toString();
                            itemViewModel.savedFacebook = binding.get().facebookTextView.getText().toString();
                            itemViewModel.savedGooglePlus = binding.get().googlePlusTextView.getText().toString();
                            itemViewModel.savedTwitter = binding.get().twitterTextView.getText().toString();
                            itemViewModel.savedYoutube = binding.get().youtubeTextView.getText().toString();
                            itemViewModel.savedInstagram = binding.get().instagrmTextView.getText().toString();
                            itemViewModel.savedPinterest = binding.get().pinterestTextView.getText().toString();
                            itemViewModel.savedWebsite = binding.get().websiteTextView.getText().toString();
                            itemViewModel.savedWhatsapp = binding.get().whatappsTextView.getText().toString();
                            itemViewModel.savedMessenger = binding.get().messangerTextView2.getText().toString();
                            itemViewModel.savedTimeRemark = binding.get().timeRemarkTextView.getText().toString();
                            itemViewModel.savedTerms = binding.get().termsAndConditionTextView.getText().toString();
                            itemViewModel.savedCancelationPolicy = binding.get().cancelationTextView.getText().toString();
                            itemViewModel.savedAdditionalInfo = binding.get().additionalTextView.getText().toString();
                            itemViewModel.savedStatusSelectedId = binding.get().statusTextView.getText().toString();

                            itemViewModel.saved = true;

                            progressDialog.get().cancel();

                            if (!psDialogMsg.isShowing()) {
                                psDialogMsg.show();
                            }
                            psDialogMsg.okButton.setOnClickListener(v -> {
                                psDialogMsg.cancel();

                                if (!itemViewModel.edit_mode) {
                                    itemViewModel.itemSelectId = itemResource.data.id;
                                    navigationController.navigateToImageUploadActivity(getActivity(), "", "", "", Constants.IMAGE_UPLOAD_ITEM, itemViewModel.itemSelectId, itemViewModel.savedIsPromotion);
                                }  else if (promoChanged) {
                                    navigationController.navigateToItemPromoteActivity(getActivity(),itemViewModel.itemSelectId);
                                   // Log.e("foo", "foo");
                                }
                            });
                        }
                        break;
                }
            }
        });
        //Save Item
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.psLog("Request code " + requestCode);
        Utils.psLog("Result code " + resultCode);

        if (requestCode == 1) {
            if (resultCode == Constants.SELECT_CATEGORY) {
                itemViewModel.catSelectId = data.getStringExtra(Constants.CATEGORY_ID);
                binding.get().categoryTextView.setText(data.getStringExtra(Constants.CATEGORY_NAME));
            } else if (resultCode == Constants.SELECT_SUBCATEGORY) {
                itemViewModel.subCatSelectId = data.getStringExtra(Constants.SUBCATEGORY_ID);
                binding.get().subCatTextView.setText(data.getStringExtra(Constants.SUBCATEGORY_NAME));
            } else if (resultCode == Constants.SELECT_STATUS) {
                itemViewModel.statusSelectId = data.getStringExtra(Constants.STATUS_ID);
                binding.get().statusTextView.setText(data.getStringExtra(Constants.STATUS_NAME));
            } else if (resultCode == Constants.SELECT_CITY) {
                itemViewModel.cityId = data.getStringExtra(Constants.CITY_ID);
                binding.get().cityTextView1.setText(data.getStringExtra(Constants.CITY_NAME));
            }
        } else if (requestCode == Constants.RESULT_GO_TO_IMAGE_UPLOAD && resultCode == Constants.RESULT_CODE_FROM_IMAGE_UPLOAD) {
            itemViewModel.img_id = data.getStringExtra(Constants.IMG_ID);
            editMode(itemViewModel.itemSelectId);
            itemViewModel.edit_mode = true;
        } else if (requestCode == Constants.REQUEST_CODE_TO_MAP_VIEW && resultCode == Constants.RESULT_CODE_FROM_MAP_VIEW) {
            //itemViewModel.lat = data.getStringExtra(Constants.LAT);
            //itemViewModel.lng = data.getStringExtra(Constants.LNG);

            changeCamera(data.getStringExtra(Constants.LAT), data.getStringExtra(Constants.LNG));

            //binding.get().latitudeTextView.setText(itemViewModel.lat);
            //binding.get().longitudeTextView.setText(itemViewModel.lng);
        }
    }

    String checkedPromotion;
    private void saveItem() {

        locationAddress.getAddressFromLocation(binding.get().txtAutocomplete.getText().toString(), getApplicationContext(), new
                GeoCoderHandler());
        if(itemViewModel.savedInstagram.contains("?")){
            String foo = itemViewModel.savedInstagram.split("[?]")[0];
            itemViewModel.savedInstagram = foo;
        }

        if (binding.get().isPromotion.isChecked()) {
            checkedPromotion = Constants.CHECKED_PROMOTION;
        } else {
            checkedPromotion = Constants.NOT_CHECKED_PROMOTION;
        }

        String checkedFeatured; //set all added items to featured
        if (!binding.get().isFeature.isChecked()) {
            checkedFeatured = Constants.CHECKED_FEATURED;
        } else {
            checkedFeatured = Constants.NOT_CHECKED_FEATURED;
        }

        String publishOrUnpublish;
        if (binding.get().statusTextView.getText().toString().equals(Constants.CHECKED_UNPUBLISH)) {
            publishOrUnpublish = Constants.UNPUBLISH;
        } else {
            publishOrUnpublish = Constants.PUBLISH;
        }
        binding.get().statusTextView.setText(publishOrUnpublish);

        itemViewModel.setSaveOneItemObj(
                loginUserId,
                itemViewModel.cityId,
                itemViewModel.catSelectId,
                itemViewModel.subCatSelectId,
                publishOrUnpublish,
                binding.get().itemNameEditText.getText().toString(),
                binding.get().itemDescriptionTextView.getText().toString(),
                binding.get().searchTagEditText.getText().toString(),
                binding.get().itemHighlightInformationTextView.getText().toString(),
                checkedFeatured,
                String.valueOf(latitude),
                String.valueOf(longitude),
                binding.get().openTimeTextView.getText().toString(),
                binding.get().closeTimeTextView.getText().toString(),
                checkedPromotion,
                binding.get().phoneOneTextView.getText().toString(),
                binding.get().phoneTwoTextView.getText().toString(),
                binding.get().phoneThreeTextView.getText().toString(),
                binding.get().emailTextView.getText().toString(),
                binding.get().txtAutocomplete.getText().toString(),
                binding.get().facebookTextView.getText().toString().replaceFirst("^(http://www\\.|http://|www\\.|https://www\\.|https://|http//www\\.|http//|https//www\\.)",""),
                binding.get().googlePlusTextView.getText().toString().replaceFirst("^(http://www\\.|http://|www\\.|https://www\\.|https://|http//www\\.|http//|https//www\\.)",""),
                binding.get().twitterTextView.getText().toString().replaceFirst("^(http://www\\.|http://|www\\.|https://www\\.|https://|http//www\\.|http//|https//www\\.)",""),
                binding.get().youtubeTextView.getText().toString().replaceFirst("^(http://www\\.|http://|www\\.|https://www\\.|https://|http//www\\.|http//|https//www\\.)",""),
                binding.get().instagrmTextView.getText().toString().replaceFirst("^(http://www\\.|http://|www\\.|https://www\\.|https://|http//www\\.|http//|https//www\\.)",""),
                binding.get().pinterestTextView.getText().toString().replaceFirst("^(http://www\\.|http://|www\\.|https://www\\.|https://|http//www\\.|http//|https//www\\.)",""),
                binding.get().websiteTextView.getText().toString().replaceFirst("^(http://www\\.|http://|www\\.|https://www\\.|https://|http//www\\.|http//|https//www\\.)",""),
                binding.get().whatappsTextView.getText().toString().replaceFirst("^(http://www\\.|http://|www\\.|https://www\\.|https://|http//www\\.|http//|https//www\\.)",""),
                binding.get().messangerTextView2.getText().toString(),
                binding.get().timeRemarkTextView.getText().toString(),
                binding.get().termsAndConditionTextView.getText().toString(),
                binding.get().cancelationTextView.getText().toString(),
                binding.get().additionalTextView.getText().toString(),
                itemViewModel.itemSelectId);
    }

    private void openTimePicker(EditText editText) {
        TimePickerDialog.OnTimeSetListener timePickerDialog = (view, hourOfDay, minute) -> {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            updateTime(editText);
        };

        new TimePickerDialog(getContext(), timePickerDialog, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), false).show();
    }

    private void updateTime(EditText editText) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa", Locale.US);
        String shortTimeStr = sdf.format(dateTime.getTime());
        editText.setText(shortTimeStr);
    }

    private boolean checkNameTheSame() {

        Utils.psLog("***** Item Category ViewModel : " + itemViewModel.savedCategoryName);
        Utils.psLog("***** Item SubCategory ViewModel : " + itemViewModel.savedSubCategoryName);
        Utils.psLog("***** Item Category : " + binding.get().categoryTextView.getText().toString());
        Utils.psLog("***** Item SubCategory : " + binding.get().subCatTextView.getText().toString());


        return !binding.get().itemNameEditText.getText().toString().equals(itemViewModel.savedItemName) ||
                !binding.get().itemDescriptionTextView.getText().toString().equals(itemViewModel.savedDescription) ||
                !binding.get().categoryTextView.getText().toString().equals(itemViewModel.savedCategoryName) ||
                !binding.get().subCatTextView.getText().toString().equals(itemViewModel.savedSubCategoryName) ||
                !binding.get().cityNameTextView1.getText().toString().equals(itemViewModel.savedCityName) ||
                !binding.get().searchTagEditText.getText().toString().equals(itemViewModel.savedSearchTag) ||
                !binding.get().itemHighlightInformationTextView.getText().toString().equals(itemViewModel.savedHighLightInformation) ||
                //!String.valueOf(lat).equals(itemViewModel.savedLatitude) ||
                //!latitude.equals(itemViewModel.savedLatitude)||
                //!binding.get().latitudeTextView.getText().toString().equals(itemViewModel.savedLatitude) ||
                //!longitude.equals(itemViewModel.savedLongitude)||
                //!binding.get().longitudeTextView.getText().toString().equals(itemViewModel.savedLongitude) ||
                !binding.get().openTimeTextView.getText().toString().equals(itemViewModel.savedOpeningHour) ||
                !binding.get().closeTimeTextView.getText().toString().equals(itemViewModel.savedClosingHour) ||
                !binding.get().phoneOneTextView.getText().toString().equals(itemViewModel.savedPhoneOne) ||
                !binding.get().phoneTwoTextView.getText().toString().equals(itemViewModel.savedPhoneTwo) ||
                !binding.get().phoneThreeTextView.getText().toString().equals(itemViewModel.savedPhoneThree) ||
                !binding.get().emailTextView.getText().toString().equals(itemViewModel.savedEmail) ||
                !binding.get().txtAutocomplete.getText().toString().equals(itemViewModel.savedAddress) ||
                !binding.get().facebookTextView.getText().toString().equals(itemViewModel.savedFacebook) ||
                !binding.get().googlePlusTextView.getText().toString().equals(itemViewModel.savedGooglePlus) ||
                !binding.get().twitterTextView.getText().toString().equals(itemViewModel.savedTwitter) ||
                !binding.get().youtubeTextView.getText().toString().equals(itemViewModel.savedYoutube) ||
                !binding.get().instagrmTextView.getText().toString().equals(itemViewModel.savedInstagram) ||
                !binding.get().pinterestTextView.getText().toString().equals(itemViewModel.savedPinterest) ||
                !binding.get().websiteTextView.getText().toString().equals(itemViewModel.savedWebsite) ||
                !binding.get().whatappsTextView.getText().toString().equals(itemViewModel.savedWhatsapp) ||
                !binding.get().messangerTextView2.getText().toString().equals(itemViewModel.savedMessenger) ||
                !binding.get().timeRemarkTextView.getText().toString().equals(itemViewModel.savedTimeRemark) ||
                !binding.get().termsAndConditionTextView.getText().toString().equals(itemViewModel.savedTerms) ||
                !binding.get().cancelationTextView.getText().toString().equals(itemViewModel.savedCancelationPolicy) ||
                !binding.get().additionalTextView.getText().toString().equals(itemViewModel.savedAdditionalInfo) ||
                !binding.get().statusTextView.getText().toString().equals(itemViewModel.savedStatusSelectedId) ||
                binding.get().isPromotion.isChecked() != itemViewModel.savedIsPromotion ||
                binding.get().isFeature.isChecked() != itemViewModel.savedIsFeatured ;
    }

    private boolean checkCondition() {
        boolean result = true;

        int itemNameLength = binding.get().itemNameEditText.getText().toString().length();

        // Check if email id is valid or not

        if (binding.get().itemNameEditText.getText().toString().isEmpty()) {
            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog(getString(R.string.item_upload__item_name_required), getString(R.string.app__ok));

            if (!psDialogMsg.isShowing()) {
                psDialogMsg.show();
            }

            psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

            result = false;
        } else if (itemNameLength < 4) {
            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog(getString(R.string.item_upload__item_name_length), getString(R.string.app__ok));

            if (!psDialogMsg.isShowing()) {
                psDialogMsg.show();
            }

            psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

            result = false;
        } else if (binding.get().categoryTextView.getText().toString().isEmpty()) {
            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog(getString(R.string.item_upload__item_category_required), getString(R.string.app__ok));

            if (!psDialogMsg.isShowing()) {
                psDialogMsg.show();
            }

            psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

            result = false;
        } else if (binding.get().subCatTextView.getText().toString().isEmpty()) {
            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog(getString(R.string.item_upload__item_sub_category_required), getString(R.string.app__ok));

            if (!psDialogMsg.isShowing()) {
                psDialogMsg.show();
            }

            psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

            result = false;
        } else if (binding.get().cityTextView1.getText().toString().isEmpty() && selectedCityId.isEmpty()) {
            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog(getString(R.string.item_upload__item_city_required), getString(R.string.app__ok));

            if (!psDialogMsg.isShowing()) {
                psDialogMsg.show();
            }

            psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

            result = false;
        } else if (!binding.get().emailTextView.getText().toString().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(binding.get().emailTextView.getText().toString()).matches()) {
            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog("Email Address is invalid", getString(R.string.app__ok));

            if (!psDialogMsg.isShowing()) {
                psDialogMsg.show();
            }

            psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

            result = false;
        } else if (!binding.get().facebookTextView.getText().toString().isEmpty() && !binding.get().facebookTextView.getText().toString().contains("facebook.com")) {
               PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
               psDialogMsg.showErrorDialog("Facebook address is invalid", getString(R.string.app__ok));

               if (!psDialogMsg.isShowing()) {
                   psDialogMsg.show();
               }

               psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

               result = false;
        } else if (!binding.get().twitterTextView.getText().toString().isEmpty() && !binding.get().twitterTextView.getText().toString().contains("twitter.com")){
            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog("Twitter address is invalid", getString(R.string.app__ok));

            if (!psDialogMsg.isShowing()) {
                psDialogMsg.show();
            }

            psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

            result = false;
        } else if (!binding.get().youtubeTextView.getText().toString().isEmpty() && !binding.get().youtubeTextView.getText().toString().contains("youtube.com")){
            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog("YouTube address is invalid", getString(R.string.app__ok));

            if (!psDialogMsg.isShowing()) {
                psDialogMsg.show();
            }

            psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

            result = false;
        } else if (!binding.get().instagrmTextView.getText().toString().isEmpty() && !binding.get().instagrmTextView.getText().toString().contains("instagram.com")) {
            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog("Instagram address is invalid", getString(R.string.app__ok));

            if (!psDialogMsg.isShowing()) {
                psDialogMsg.show();
            }

            psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

            result = false;
        } else if (!binding.get().googlePlusTextView.getText().toString().isEmpty() && !binding.get().googlePlusTextView.getText().toString().contains("google.com")) {
            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog("Google+ address is invalid", getString(R.string.app__ok));

            if (!psDialogMsg.isShowing()) {
                psDialogMsg.show();
            }

            psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

            result = false;
        } else if (!binding.get().websiteTextView.getText().toString().isEmpty() && !Patterns.WEB_URL.matcher(binding.get().websiteTextView.getText().toString()).matches()) { //not working allows anything
            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog("Website address is invalid", getString(R.string.app__ok));

            if (!psDialogMsg.isShowing()) {
                psDialogMsg.show();
            }

            psDialogMsg.okButton.setOnClickListener(v -> psDialogMsg.cancel());

            result = false;
        }

        return result;
    }

    private void editMode(String itemIdToGet) {
        binding.get().imageTextView.setVisibility(View.VISIBLE);
        binding.get().viewAllTextView.setVisibility(View.VISIBLE);
        binding.get().itemImageRecyclerView.setVisibility(View.VISIBLE);
        binding.get().attributeHeaderButton.setVisibility(View.VISIBLE);
        binding.get().uploadImageButton.setVisibility(View.VISIBLE);

        itemViewModel.setItemDetailObj(itemIdToGet, selectedCityId, itemViewModel.historyFlag, loginUserId);
        getImage(itemIdToGet);
//        pendingOrRejectItem(itemViewModel.savedStatusSelectedId);
    }

    private void getImage(String itemId) {
        imageViewModel.setImageListByIdObj(itemId, Constants.IMAGE_COUNT_ENTRY2, String.valueOf(imageViewModel.offset), Constants.IMAGE_COUNT_ENTRY2);

    }

    private void pendingOrRejectItem(String itemStatusId) {
        binding.get().rejectOrPendingTextView.setVisibility(View.VISIBLE);
        binding.get().saveButton.setEnabled(false);
        binding.get().attributeHeaderButton.setEnabled(false);
        binding.get().uploadImageButton.setEnabled(false);
        binding.get().viewAllTextView.setEnabled(false);
        binding.get().saveButton.setVisibility(View.GONE);

        if (itemStatusId.equals("2")) {
            binding.get().rejectOrPendingTextView.setText(getString(R.string.item_upload__pending));
            binding.get().rejectOrPendingTextView.setBackground(getResources().getDrawable(R.drawable.rounded_corner_pending_transparent_shape));
        }
        if (itemStatusId.equals("3")) {
            binding.get().rejectOrPendingTextView.setText(getString(R.string.item_upload__reject));
            binding.get().rejectOrPendingTextView.setBackground(getResources().getDrawable(R.drawable.rounded_corner_reject_transparent_shape));
        }

    }

    private void replaceImages(List<Image> imageList) {
        if (imageList.size() == 0) {
            binding.get().noImagesTextView.setVisibility(View.VISIBLE);
        } else {
            binding.get().noImagesTextView.setVisibility(View.GONE);
        }
        itemEntryImageAdapter.get().replace(imageList);
        binding.get().executePendingBindings();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //changeCamera();

    }

    @Override
    public void onDestroyView() {

        binding.get().mapView.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        binding.get().mapView.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.get().mapView.onLowMemory();
    }

    protected LocationManager locationManager;

    @Override
    public void onResume() {
        locationManager = (LocationManager) getContext()
                .getSystemService(LOCATION_SERVICE);
        getLocation();
        Resource<List<Image>> resource = imageViewModel.getImageListByIdLiveData().getValue();

        if (resource != null) {
            List<Image> dataList = resource.data;

            if (dataList != null && dataList.size() == 0) {
                Utils.psLog("First Record Reload.");
                getImage(itemViewModel.itemSelectId);
            } else {
                Utils.psLog("Not First Record Reload.");
            }
        }
        binding.get().mapView.onResume();
        super.onResume();
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    Location locationGPS;

    private void getLocation() {
        int REQUEST_ACCESS_FINE_LOCATION = 111, REQUEST_ACCESS_COARSE_LOCATION = 114;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION);
            return;
        }
        locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (locationGPS != null) {
            latitude = locationGPS.getLatitude();
            longitude = locationGPS.getLongitude();
            changeCamera(String.valueOf(locationGPS.getLatitude()), String.valueOf(locationGPS.getLongitude()));
        } else {
            Toast.makeText(getContext(), "Unable to find location.", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeCamera(String latitude, String longitude) {

        if (marker != null) {
            marker.remove();
        }
        try {
            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(binding.get().latitudeTextView.getText().toString()), Double.parseDouble(binding.get().latitudeTextView.getText().toString())), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(Double.parseDouble(String.valueOf(latitude)), Double.parseDouble(String.valueOf(longitude))))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            String title = "You are here!";
            if(!binding.get().itemNameEditText.getText().toString().isEmpty()){
                title = binding.get().itemNameEditText.getText().toString();
            }
            marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(String.valueOf(latitude)), Double.parseDouble(String.valueOf(longitude))))
                    .title(title));

        } catch (Exception e) {
            Utils.psErrorLog("", e);
        }
    }


    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    //locationLat = bundle.getString("latitude");
                    break;
                default:
                    locationAddress = null;
            }
            //textViewLatLong.setText(locationAddress);
        }
    }
}
