package com.macinternetservices.sablebusinessdirectory.viewmodel.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;
import com.macinternetservices.sablebusinessdirectory.repository.aboutus.AboutUsRepository;
import com.macinternetservices.sablebusinessdirectory.ui.common.BackgroundTaskHandler;
import com.macinternetservices.sablebusinessdirectory.ui.common.NotificationTaskHandler;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;

import javax.inject.Inject;

/**
 * Sable Business Directory on 1/4/18.
 * Contact Email : admin@sablebusinessdirectory.com
 */

public class NotificationViewModel extends ViewModel {

    private final NotificationTaskHandler backgroundTaskHandler;

    public boolean pushNotificationSetting = false;
    public boolean isLoading = false;

    @Inject
    NotificationViewModel(AboutUsRepository repository) {
        Utils.psLog("Inside NewsViewModel");

        backgroundTaskHandler = new NotificationTaskHandler(repository);
    }

    public void registerNotification(Context context, String platform, String token, String loginUserId) {

        if(token == null || platform == null) return;

        if(platform.equals("")) return;

        backgroundTaskHandler.registerNotification(context, platform, token,loginUserId);
    }

    public void unregisterNotification(Context context, String platform, String token, String loginUserId) {

        if(token == null || platform == null) return;

        if(platform.equals("")) return;

        backgroundTaskHandler.unregisterNotification(context, platform, token,loginUserId);
    }

    public LiveData<BackgroundTaskHandler.LoadingState> getLoadingStatus() {
        return backgroundTaskHandler.getLoadingState();
    }



}
