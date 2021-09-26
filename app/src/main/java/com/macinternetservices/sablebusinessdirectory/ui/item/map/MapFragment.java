package com.macinternetservices.sablebusinessdirectory.ui.item.map;


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
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentMapBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;

import java.util.Map;

import static com.macinternetservices.sablebusinessdirectory.utils.GeofenceNotification.getBitmapFromURL;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends PSFragment {

    private String latValue = "48.856452647178386";
    private String lngValue = "2.3523519560694695";
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
            CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(getActivity(), rating, itemImage, totalRatings);

            Glide.with(getActivity())
                    .asBitmap()
                    .load(itemImage)
                    .fitCenter()
                    .into(new SimpleTarget<Bitmap>(500,500) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            //marker
                            MarkerOptions markerOpt = new MarkerOptions();
                            markerOpt.position(new LatLng(Double.parseDouble(latValue), Double.parseDouble(lngValue)))
                                    .title(itemName)
                                    .snippet(description);
                                    //.icon(BitmapDescriptorFactory.fromBitmap(resource));
                            //Set Custom InfoWindow Adapter
                            map.setInfoWindowAdapter(adapter);
                            map.addMarker(markerOpt).showInfoWindow();
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            //marker
                            MarkerOptions markerOpt = new MarkerOptions();
                            markerOpt.position(new LatLng(Double.parseDouble(latValue), Double.parseDouble(lngValue)))
                                    .title(itemName)
                                    .snippet(description);
                                    //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.logo_blk));
                            //Set Custom InfoWindow Adapter
                            map.setInfoWindowAdapter(adapter);
                            map.addMarker(markerOpt).showInfoWindow();
                        }
                    });

            //zoom
            if (!latValue.isEmpty() && !lngValue.isEmpty()) {
                int zoomlevel = 15;
                // Animating to the touched position
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latValue), Double.parseDouble(lngValue)), zoomlevel));
            }
        });
    }

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
