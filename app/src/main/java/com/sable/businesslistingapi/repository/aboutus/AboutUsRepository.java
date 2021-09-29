package com.sable.businesslistingapi.repository.aboutus;

import android.content.Context;

import com.sable.businesslistingapi.AppExecutors;
import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.api.ApiResponse;
import com.sable.businesslistingapi.api.PSApiService;
import com.sable.businesslistingapi.db.AboutUsDao;
import com.sable.businesslistingapi.db.PSCoreDb;
import com.sable.businesslistingapi.repository.common.NetworkBoundResource;
import com.sable.businesslistingapi.repository.common.NotificationTask;
import com.sable.businesslistingapi.repository.common.PSRepository;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewobject.AboutUs;
import com.sable.businesslistingapi.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */

@Singleton
public class AboutUsRepository extends PSRepository {


    //region Variables

    private final AboutUsDao aboutUsDao;

    //endregion


    //region Constructor

    @Inject
    AboutUsRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, AboutUsDao aboutUsDao) {
        super(psApiService, appExecutors, db);

        this.aboutUsDao = aboutUsDao;
    }

    //endregion


    //region About Us Repository Functions for ViewModel

    /**
     * Load About Us
     */
    public LiveData<Resource<AboutUs>> getAboutUs(String apiKey) {

        String functionKey = "getAboutUs";

        return new NetworkBoundResource<AboutUs, List<AboutUs> >(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<AboutUs> item) {

                try {
                    db.runInTransaction(() -> {
                        aboutUsDao.deleteTable();

                        aboutUsDao.insertAll(item);

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in inserting about us.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable AboutUs data) {
                // API level cache
                //if (connectivity.isConnected())
                    //if (data == null || rateLimiter.shouldFetch(functionKey)) return true;
                //return false;

                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<AboutUs> loadFromDb() {
                Utils.psLog("Load AboutUs From DB.");

                return aboutUsDao.get();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<AboutUs>>> createCall() {
                Utils.psLog("Call About us webservice.");
                return psApiService.getAboutUs(apiKey);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of About Us");
                rateLimiter.reset(functionKey);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.aboutUsDao().deleteTable()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }


    //endregion

    /**
     * Function to call background task to register Notification
     *
     * @param platform Current Platform
     * @return Status of Process
     */
    public LiveData<Resource<Boolean>> registerNotification(Context context, String platform, String token, String loginUserId) {
        NotificationTask notificationTask = new NotificationTask( context,
                psApiService, platform, true, token,loginUserId);

        Utils.psLog("Register Notification : News repository.");
        appExecutors.networkIO().execute(notificationTask);

        return notificationTask.getStatusLiveData();
    }


    /**
     * Function to call background task to un-register notification.
     *
     * @param platform Current Platform
     * @return Status of Process
     */
    public LiveData<Resource<Boolean>> unregisterNotification(Context context, String platform, String token, String loginUserId) {
        NotificationTask notificationTask = new NotificationTask(context,
                psApiService, platform, false, token,loginUserId);

        Utils.psLog("Unregister Notification : News repository.");
        appExecutors.networkIO().execute(notificationTask);

        return notificationTask.getStatusLiveData();
    }
}
