package com.sable.businesslistingapi.ui.stripe;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
//import androidx.browser.trusted.Token;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.binding.FragmentDataBindingComponent;
import com.sable.businesslistingapi.binding.PlaceArrayAdapter;
import com.sable.businesslistingapi.databinding.FragmentStripeBinding;
import com.sable.businesslistingapi.ui.common.PSFragment;
import com.sable.businesslistingapi.utils.AutoClearedValue;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.Utils;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class StripeFragment extends PSFragment {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private Card card;
    private ProgressDialog progressDialog;
    private PlaceArrayAdapter mAutoCompleteAdapter;
//    private PSAPPLoadingViewModel psappLoadingViewModel;
    private String stripePublishableKey;
    @VisibleForTesting
    private AutoClearedValue<Stripe> stripe;
    private AutoClearedValue<FragmentStripeBinding> binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        FragmentStripeBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stripe, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();

    }

    @Override
    protected void initUIAndActions() {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Processing Payment");
        progressDialog.setCancelable(false);
        binding.get().editTextPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        Places.initialize(getContext(), getResources().getString(R.string.google_map_api_key));
        mAutoCompleteAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item);
        binding.get().autoCompleteTextView2.setAdapter(mAutoCompleteAdapter);
        binding.get().autoCompleteTextView2.setThreshold(3);//start searching from 3 characters
        binding.get().autoCompleteTextView2.setAdapter(mAutoCompleteAdapter);

        binding.get().autoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!binding.get().autoCompleteTextView2.getText().toString().isEmpty()) {
                    LatLng latLng = getLatLngFromAddress(binding.get().autoCompleteTextView2.getText().toString());
                    if (latLng != null) {
                        getAddressFromLatLng(latLng);
                        if (address != null) {
                            Log.d("Adddress", address.toString());
                        } else {
                            Log.d("Adddress", "Address Not Found");
                        }
                    } else {
                        Log.d("Lat Lng", "Lat Lng Not Found");
                    }
                }
            }
        });

        binding.get().submitStripeButton.setOnClickListener(v -> {

            card = binding.get().cardInputWidget.getCard();

            if (card != null) {
                createTransaction();
            }
        });

    }
    String address, state, country, zipcode, city, street, bldgNo;
    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses != null) {
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()/*String city = addresses.get(0).getLocality();
            //binding.get().autoCompleteTextView2.setText(addresses.get(0).getSubThoroughfare());
            binding.get().autoCompleteTextView2.setText(addresses.get(0).getSubThoroughfare() +" "+addresses.get(0).getThoroughfare());
            binding.get().textViewCity.setText(addresses.get(0).getLocality());
            binding.get().textViewState.setText(addresses.get(0).getAdminArea());
            binding.get().textViewZip.setText(addresses.get(0).getPostalCode());
            //tvCurrentAddress.setText(address);
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
//        psappLoadingViewModel = ViewModelProviders.of(this,viewModelFactory).get(PSAPPLoadingViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        getIntentData();

        Utils.psLog("Stripe Publishable Key ::::: " + stripePublishableKey);
        stripe = new AutoClearedValue<>(this, new Stripe(getContext(), stripePublishableKey));

    }

    private void createTransaction() {
        progressDialog.show();
        if(getContext() != null) {

            stripe.get().createCardToken(
                    card,
                    new ApiResultCallback<Token>() {
                        public void onSuccess(Token token) {
                            // Send token to your server
                            Utils.psLog("PAYMENT_STRIPE Token Id" + token.getId());
                            progressDialog.cancel();
                            close(token.getId());
                        }

                        public void onError(Exception error) {
                            // Show localized error message
                            Utils.psLog("PAYMENT_STRIPE ERROR");

                            // setup the alert builder
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setCancelable(true);
                            // add a button
                            builder.setPositiveButton("OK", null);
                            builder.setTitle("Payment Processing Error");
                            builder.setMessage(String.valueOf(error.getMessage()));
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    progressDialog.cancel();
                                }
                            });

                            // create and show the alert dialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
            );
        }
    }

    public void close(String token) {

        if(getActivity() != null) {
            navigationController.navigateBackToCheckoutFragment(getActivity(), token);
            getActivity().finish();
        }
    }

    private void getIntentData(){
        try {
            if(getActivity() != null){
                if(getActivity().getIntent().getExtras() != null){
                    stripePublishableKey = getActivity().getIntent().getExtras().getString(Constants.STRIPEPUBLISHABLEKEY);
                    Utils.psLog(stripePublishableKey);
                }
            }
        }catch (Exception e){
            Utils.psErrorLog("",e);
        }
    }
}
