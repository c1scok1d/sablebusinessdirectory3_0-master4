/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.macinternetservices.sablebusinessdirectory.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.macinternetservices.sablebusinessdirectory.MainActivity;
import com.macinternetservices.sablebusinessdirectory.R;

/**
 * Displays rationale for allowing the activity recognition permission and allows user to accept
 * the permission. After permission is accepted, finishes the activity so main activity can
 * show transitions.
 */
public class PermissionRationaleActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "PermissionRational";


    /* Id to identify Activity Recognition permission request. */
    //private static final int PERMISSION_BACKGROUD_LOCATION = 45;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 111,
            REQUEST_ACCESS_COARSE_LOCATION = 114,
            REQUEST_BACKGROUND_LOCATION = 119,
            REQUEST_FOREGROUND_SERVICE = 120;


    TextSwitcher textSwitcher;
    ImageView imageView;
    Button deny_permission_request, approve_permission_request;
    private static final int FRAME_TIME_MS = 5000;

    private Handler imageSwitchHandler;
    ProgressBar progressBar;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_rationale);
        Animation imgAnimationIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        Animation imgAnimationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        pref = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

        /**
         * ABOUT US
         */
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        imageView = findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);
        ImageView imageView = new ImageView(getApplicationContext());

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        ViewGroup.LayoutParams params = new ImageSwitcher.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);

        deny_permission_request =findViewById(R.id.deny_permission_request);
        deny_permission_request.setVisibility(View.GONE);
        approve_permission_request = findViewById(R.id.approve_permission_request);
        approve_permission_request.setVisibility(View.GONE);

        imageSwitchHandler = new Handler();
        imageSwitchHandler.post(runnableCode);

        /**
         *  txt switchers for animations
         */
        LinearLayout textSwitcherLayout = new LinearLayout(getApplicationContext());
        ViewGroup.LayoutParams textSwitcherLayoutParams = new ImageSwitcher.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textSwitcherLayout.setLayoutParams(textSwitcherLayoutParams);
        textSwitcherLayout.setAnimation(imgAnimationIn);
        textSwitcherLayout.setAnimation(imgAnimationOut);
        textSwitcherLayout.post(runnableCode);

        textSwitcher = findViewById(R.id.textSwitcher);
        textSwitcher.setFactory(() -> {
            TextView textView = new TextView(getApplicationContext());
            textView.setLayoutParams(new TextSwitcher.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setTextSize(22);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000));
            return textView;
        });
    }

    public void onClickApprovePermissionRequest(View view) {
        Log.e(TAG, "onClickApprovePermissionRequest()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissionsQ();
        } else {
            checkPermissions();
        }


    }
    private  boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
        }else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION);
         } else {
            //Ask se to geo to settings and manually allow permissions
            showDialog("", "You have denied some permissions.  Allow all permissions at [Go to Settings] > [Permissions]",
                    "Go to Settings",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            //Go to app settings
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                    },
                    "No, Exit app", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            ;
                            fileList();
                        }
                    }, false);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private  boolean checkPermissionsQ() {
        //progressBar.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
        }else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION);
        } else  if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);{
                //Show permission explanation dialog...
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Special Permissions Required");
                alertBuilder.setMessage("This app requires special permission to monitor your location while working in the background to alert you when near a registered business.\n\nWe respect user privacy. No location data is collected.\n\nTap 'Continue' and select 'Allow all the time' from the next screen to receive alerts.");
                alertBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(Constants.GEO_SERVICE_KEY, "false").apply();
                            //Go to app settings
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            ActivityCompat.requestPermissions(PermissionRationaleActivity.this,
                                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                    REQUEST_BACKGROUND_LOCATION);
                        }
                    }
                });
                alertBuilder.setNegativeButton("Deny", (dialog, id) -> {
                    showDialog("", "You will not be alerted when you are near a registered black owned business.\n\nWe respect user privacy. No location data is collected.\n\nTo enable alerts when near a registered black owned business select 'allow all the time' at [Go to Settings] > [Permissions]",
                            "Go to Settings",
                            (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(Constants.GEO_SERVICE_KEY, "false").apply();
                                //Go to app settings
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                //finish();
                            },
                            "Continue", (dialogInterface, i) -> {
                                if (getApplicationContext() != null) {
                                    //SharedPreferences.Editor editor = pref.edit();
                                    //editor.putString(Constants.GEO_SERVICE_KEY, "false").apply();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    //finish();
                                }
                            }, false);
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        }  else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.FOREGROUND_SERVICE},
                    REQUEST_FOREGROUND_SERVICE);
        }
        return true;
    }

    public AlertDialog showDialog(String title, String msg, String positiveLabel, DialogInterface.OnClickListener positiveOnClick,
                                  String negativeLabel, DialogInterface.OnClickListener negativeOnClick, boolean isCancelAble){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(isCancelAble);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }
    /*
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            switch (requestCode) {
                case REQUEST_ACCESS_COARSE_LOCATION: {
                    if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    }
                }
                case REQUEST_ACCESS_FINE_LOCATION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    }
                }
                case REQUEST_BACKGROUND_LOCATION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //pref.edit().putString(Constants.GEO_SERVICE_KEY, "true").apply();
                    }
                }
                default:
                    /*startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish(); */
            }
           if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                progressBar.setVisibility(View.VISIBLE);
                checkPermissionsQ();
            } else {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();            //h.postDelayed(r, 1500);
            }
        } else {
            /*switch (requestCode) {
                case REQUEST_ACCESS_COARSE_LOCATION: {
                    if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //checkPermissionsAndCall();
                    }
                }
                case REQUEST_ACCESS_FINE_LOCATION: {
                    if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //checkPermissionsAndCall();
                    }
                }
               /* case REQUEST_BACKGROUND_LOCATION: {
                    if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        pref.edit().putString(Constants.GEO_SERVICE_KEY, "true").apply();
                    }
                }
                default:
                    startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
            } */
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                progressBar.setVisibility(View.VISIBLE);
                checkPermissions();
            } else {
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }


    /**
     * ANIMATIONS
     */


    private Runnable runnableCode = new Runnable() {
       int count=0;

        @Override
        public void run() {
            Animation imgAnimationIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

            String[] text = {
                    "The app requires special permission to alert you when you are near a registered black owned business.",
                    "Click begin and allow all of the following permissions when prompted to receive alerts."
            };

            int[] images = {R.mipmap.making_thumbs_up_foreground, R.mipmap.smiling_peace_foreground};

            switch (count) {

                case 1:
                    imageView.setImageResource(images[count]);
                    imageView.setAnimation(imgAnimationIn);
                    imageView.setVisibility(View.VISIBLE);

                    textSwitcher.setText(text[count]);
                    textSwitcher.setAnimation(imgAnimationIn);
                    textSwitcher.setVisibility(View.VISIBLE);

                    approve_permission_request.setAnimation(imgAnimationIn);
                    approve_permission_request.setVisibility(View.VISIBLE);

                    imageSwitchHandler.removeCallbacks(this);
                    count++;
                    break;
                default:
                    imageView.setImageResource(images[count]);
                    imageView.setAnimation(imgAnimationIn);
                    imageView.setVisibility(View.VISIBLE);

                    textSwitcher.setText(text[count]);
                    textSwitcher.setAnimation(imgAnimationIn);
                    textSwitcher.setVisibility(View.VISIBLE);

                    imageSwitchHandler.postDelayed(this, FRAME_TIME_MS);
                    count++;
                    break;
            }
        }
    };
}
