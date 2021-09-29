package com.sable.businesslistingapi.viewmodel.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;
import com.sable.businesslistingapi.repository.aboutus.AboutUsRepository;
import com.sable.businesslistingapi.ui.common.BackgroundTaskHandler;
import com.sable.businesslistingapi.ui.common.NotificationTaskHandler;
import com.sable.businesslistingapi.utils.Utils;

import javax.inject.Inject;

/**
 * Sable Business Directory on 09/01/2021
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
