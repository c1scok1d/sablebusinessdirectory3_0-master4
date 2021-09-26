package com.macinternetservices.sablebusinessdirectory.ui.item.map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.viewmodel.rating.RatingViewModel;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;
    private Float rating, totalRatings;
    private String image;

    public CustomInfoWindowAdapter(Activity context, Float rating, String image, Float totalRatings){
        this.context = context;
        this.rating = rating;
        this.image = image;
        this.totalRatings = totalRatings;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.custom_info_window, null);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView ratingNo = view.findViewById(R.id.ratingNo);
        ImageView itemImage = view.findViewById(R.id.imageView22);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar2);
        ratingBar.setStepSize(0.5f);
        tvTitle.setText(marker.getTitle());

        if (rating == 0.0) {
            ratingNo.setText(R.string.item_detail__rating);
        } else {
            ratingNo.setText(rating+ " " +"(" +totalRatings+ " ratings)");
            //ratingNo.setText(R.string.rating__total_count_n_value, rating + "", totalRatings + "");
        }

        ratingBar.setRating(rating);

        Glide.with(context)
                .asBitmap()
                .load(image)
                .fitCenter()
                .into(new SimpleTarget<Bitmap>(500,500) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        itemImage.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        itemImage.setImageResource(R.mipmap.logo_blk);
                    }
                });

        return view;
    }
}
