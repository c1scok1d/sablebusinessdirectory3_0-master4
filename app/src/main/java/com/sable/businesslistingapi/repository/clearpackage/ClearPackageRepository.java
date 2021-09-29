package com.sable.businesslistingapi.repository.clearpackage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sable.businesslistingapi.AppExecutors;
import com.sable.businesslistingapi.api.PSApiService;
import com.sable.businesslistingapi.db.PSCoreDb;
import com.sable.businesslistingapi.repository.common.PSRepository;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewobject.common.Resource;

import javax.inject.Inject;

public class ClearPackageRepository extends PSRepository {

    @Inject
    ClearPackageRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db) {
        super(psApiService, appExecutors, db);

        Utils.psLog("Inside CityCategoryRepository");
    }

    public LiveData<Resource<Boolean>> clearAllTheData() {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            try {
                db.runInTransaction(() -> {

                    db.cityDao().deleteAll();
                    db.itemDao().deleteAll();
                    db.blogDao().deleteAll();
                    db.commentDao().deleteAll();
                    db.commentDetailDao().deleteAll();
                    db.deletedObjectDao().deleteAll();
                    db.imageDao().deleteTable();
                    db.notificationDao().deleteAllNotificationList();
                    db.specsDao().deleteAll();
                    db.psAppInfoDao().deleteAll();
                    db.psAppVersionDao().deleteAll();
                    db.ratingDao().deleteAll();
                });
            } catch (NullPointerException ne) {
                Utils.psErrorLog("Null Pointer Exception : ", ne);

                statusLiveData.postValue(Resource.error(ne.getMessage(), false));
            } catch (Exception e) {
                Utils.psErrorLog("Exception : ", e);

                statusLiveData.postValue(Resource.error(e.getMessage(), false));
            }

            statusLiveData.postValue(Resource.success(true));


        });

        return statusLiveData;
    }

}
