package com.sable.businesslistingapi.ui.item.map;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sable.businesslistingapi.R;
import com.sable.businesslistingapi.binding.FragmentDataBindingComponent;
import com.sable.businesslistingapi.databinding.FragmentMapBinding;
import com.sable.businesslistingapi.ui.common.PSFragment;
import com.sable.businesslistingapi.utils.AutoClearedValue;
import com.sable.businesslistingapi.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends PSFragment {

    private String latValue = "41.6100344";
    private String lngValue = "-87.6467132";
    private String itemName, itemImage, description;
    private Float rating, totalRatings;

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private GoogleMap map;

    @VisibleForTesting
    private AutoClearedValue<FragmentMapBinding> binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentMapBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        initializeMap(savedInstanceState);

        return binding.get().getRoot();
    }
    //private Marker marker;

    private void initializeMap(Bundle savedInstanceState) {
        try {
            if (this.getActivity() != null) {
                MapsInitializer.initialize(this.getActivity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.get().mapView.onCreate(savedInstanceState);
        binding.get().mapView.onResume();

        binding.get().mapView.getMapAsync(googleMap -> {
            map = googleMap;
            MarkerOptions markerOpt = new MarkerOptions();
            CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(getActivity(), rating, itemImage, totalRatings, description);

            Glide.with(getActivity())
                    .asBitmap()
                    .load(itemImage)
                    .fitCenter()
                    .into(new SimpleTarget<Bitmap>(500,500) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            //marker
                            markerOpt.position(new LatLng(Double.parseDouble(latValue), Double.parseDouble(lngValue)))
                                    .title(itemName)
                                    .snippet(description);
                            //.icon(BitmapDescriptorFactory.fromBitmap(resource));
                            //Set Custom InfoWindow Adapter
                            map.setInfoWindowAdapter(adapter);
                            //zoom
                            if (!latValue.isEmpty() && !lngValue.isEmpty()) {
                                int zoomlevel = 15;
                                // Animating to the touched position
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latValue), Double.parseDouble(lngValue)), zoomlevel));
                            }
                            map.addMarker(markerOpt).showInfoWindow();
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            //marker
                            //MarkerOptions markerOpt = new MarkerOptions();
                            markerOpt.position(new LatLng(Double.parseDouble(latValue), Double.parseDouble(lngValue)))
                                    .title(itemName)
                                    .snippet(description);
                            //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.logo_blk));
                            //Set Custom InfoWindow Adapter
                            map.setInfoWindowAdapter(adapter);
                            //zoom
                            if (!latValue.isEmpty() && !lngValue.isEmpty()) {
                                int zoomlevel = 15;
                                // Animating to the touched position
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latValue), Double.parseDouble(lngValue)), zoomlevel));
                            }
                            map.addMarker(markerOpt).showInfoWindow();
                        }
                    });

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if(!map.addMarker(markerOpt).isInfoWindowShown()){
                        map.addMarker(markerOpt).showInfoWindow();
                    }
                    return false;
                }
            });

           // AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
            /*map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    // do stuff on click
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setCancelable(true);
                    //alertBuilder.setTitle("Visit " +itemName);
                    alertBuilder.setMessage("Would you like directions to "+itemName);
                    alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int id) {
                            //do stuff
                        }
                    });
                    alertBuilder.setNegativeButton("No", (dialog, id) -> {
                        dialog.dismiss();
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
            }); */
        });
    }
   /* public AlertDialog showDialog(String title, String msg, String positiveLabel, DialogInterface.OnClickListener positiveOnClick,
                                  String negativeLabel, DialogInterface.OnClickListener negativeOnClick, boolean isCancelAble){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setCancelable(isCancelAble);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    } */

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        if(binding != null) {
            if (binding.get() != null) {
                if (binding.get().mapView != null) {

                    binding.get().mapView.onDestroy();

                    if (map != null) {
                        map.clear();
                    }

                }
            }
        }
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        binding.get().mapView.onLowMemory();
        super.onLowMemory();

    }

    @Override
    public void onPause() {
        binding.get().mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        binding.get().mapView.onResume();
        super.onResume();
    }

    @Override
    protected void initUIAndActions() {

    }

    @Override
    protected void initViewModels() {

    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        if (getActivity() != null) {
            latValue = getActivity().getIntent().getStringExtra(Constants.LAT);
            lngValue = getActivity().getIntent().getStringExtra(Constants.LNG);
            itemName = getActivity().getIntent().getStringExtra(Constants.ITEM_NAME);
            itemImage = "https://api.sablebusinessdirectory.com/uploads/" +getActivity().getIntent().getStringExtra(Constants.ITEM_IMAGE_URL);
            description = getActivity().getIntent().getStringExtra(Constants.ITEM_DESCRIPTION);
            rating = getActivity().getIntent().getFloatExtra(Constants.RATING_STARS,0);
            totalRatings = getActivity().getIntent().getFloatExtra(Constants.TOTAL_RATINGS, 0);
        }

    }
}
