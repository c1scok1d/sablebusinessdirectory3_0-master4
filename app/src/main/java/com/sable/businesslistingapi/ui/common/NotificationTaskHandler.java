package com.sable.businesslistingapi.ui.common;

import android.content.Context;

import com.sable.businesslistingapi.repository.aboutus.AboutUsRepository;
import com.sable.businesslistingapi.utils.Utils;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */


public class NotificationTaskHandler extends BackgroundTaskHandler {

    private final AboutUsRepository repository;

    public NotificationTaskHandler(AboutUsRepository repository) {
        super();

        this.repository = repository;
    }

    public void registerNotification(Context context, String platform, String token, String loginUserId) {

        if(platform == null) return;

        if(platform.equals("")) return;

        Utils.psLog("Register Notification : Notification Handler");
        holdLiveData = repository.registerNotification(context, platform, token,loginUserId);
        loadingState.setValue(new LoadingState(true, null));

        //noinspection ConstantConditions
        holdLiveData.observeForever(this);

    }

    public void unregisterNotification(Context context, String platform, String token, String loginUserId) {

        if(platform == null) return;

        if(platform.equals("")) return;

        Utils.psLog("Unregister Notification : Notification Handler");
        holdLiveData = repository.unregisterNotification(context, platform, token, loginUserId);
        loadingState.setValue(new LoadingState(true, null));

        //noinspection ConstantConditions
        holdLiveData.observeForever(this);

    }

}