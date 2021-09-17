package com.macinternetservices.sablebusinessdirectory.ui.notification.setting;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.macinternetservices.sablebusinessdirectory.MainActivity;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentNotificationSettingBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.GeolocationService;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.utils.PermissionRationaleActivity;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.common.NotificationViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class NotificationSettingFragment extends PSFragment {


    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private PSDialogMsg psDialogMsg;

    SharedPreferences preferences;
    SharedPreferences pref;


    @VisibleForTesting
    private AutoClearedValue<FragmentNotificationSettingBinding> binding;


    private NotificationViewModel notificationViewModel;

    //endregion


    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentNotificationSettingBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_setting, container, false, dataBindingComponent);


        preferences=PreferenceManager.getDefaultSharedPreferences(getContext());
        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(getActivity(), false);
        pref = getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

        //fadeIn Animation
        fadeIn(binding.get().getRoot());
        if (pref.getString(Constants.GEO_SERVICE_KEY,"false").equals("true")){
            binding.get().pushNotSwitch.setChecked(true);
        } else {
            binding.get().pushNotSwitch.setChecked(false);
        }

        binding.get().pushNotSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                //
                if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    startActivity(new Intent(getApplicationContext(), PermissionRationaleActivity.class));
                } else {
                    pref.edit().putString(Constants.GEO_SERVICE_KEY, "true").apply();
                }
            } else {
                if (isMyServiceRunning(GeolocationService.class)) {
                    getActivity().stopService(new Intent(getContext(), GeolocationService.class));
                }
                pref.edit().putString(Constants.GEO_SERVICE_KEY, "false").apply();
            }
        });
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
        }

    @Override
    protected void initViewModels() {
        notificationViewModel = new ViewModelProvider(this, viewModelFactory).get(NotificationViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        try {

            if (pref != null) {
                updateNotificationMessage();
            }

        } catch (NullPointerException ne) {
            Utils.psErrorLog("Null Pointer Exception.", ne);
        } catch (Exception e) {
            Utils.psErrorLog("Error in getting notification flag data.", e);
        }

        notificationViewModel.getLoadingStatus().observe(this, status -> {
            if (status == null) {
                Utils.psLog("Status is null");
                notificationViewModel.isLoading = false;
            } else {
                Utils.psLog("Status Update : " + status.isRunning());
                notificationViewModel.isLoading = status.isRunning();
                String error = status.getErrorMessageIfNotHandled();
                if (error != null) {
                    notificationViewModel.isLoading = false;
                    Utils.psLog("Error in Status : " + error);

                    psDialogMsg.showErrorDialog(error, getString(R.string.app__ok));
                    psDialogMsg.show();
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        if (pref != null) {
            updateNotificationMessage();
        }
    }

    private void updateNotificationMessage() {

        notificationViewModel.pushNotificationSetting = pref.getBoolean(Constants.NOTI_SETTING, false);
        String message = pref.getString(Constants.NOTI_MSG, "");

        if (!message.equals("")) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                binding.get().messageTextView.setText(Html.fromHtml(message, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
            } else {
                binding.get().messageTextView.setText(Html.fromHtml(message));
            }
        }
    }

    @VisibleForTesting
    private void updateNotificationSetting(Boolean setting) {

        if (getActivity() != null) {
            if (notificationViewModel.pushNotificationSetting != setting) {

                if (setting) {
                    notificationViewModel.registerNotification(getContext(), Constants.PLATFORM, "",loginUserId);

                } else {
                    notificationViewModel.unregisterNotification(getContext(), Constants.PLATFORM, "",loginUserId);

                }
                notificationViewModel.pushNotificationSetting = setting;
            }
        }
    }
/*
geolocationService on/off setting
 */
    @VisibleForTesting
    private void updateAlertSetting(Boolean setting) {

        if (getActivity() != null) {

            if (notificationViewModel.pushNotificationSetting != setting) {

                if (setting.equals(true)) {
                    notificationViewModel.pushNotificationSetting = pref.getBoolean(Constants.NOTI_SETTING, false);

                    if (!isMyServiceRunning(GeolocationService.class)) {
                        getActivity().startService(new Intent(getContext(), GeolocationService.class));
                    }
                   // notificationViewModel.registerNotification(getContext(), Constants.PLATFORM, "",loginUserId);

                } else {
                    notificationViewModel.unregisterNotification(getContext(), Constants.PLATFORM, "",loginUserId);

                }
                notificationViewModel.pushNotificationSetting = setting;
            }
        }
    }
    //endregion


}
