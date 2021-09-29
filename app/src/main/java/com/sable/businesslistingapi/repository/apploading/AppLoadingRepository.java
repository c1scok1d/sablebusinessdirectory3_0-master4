package com.sable.businesslistingapi.repository.apploading;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sable.businesslistingapi.AppExecutors;
import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.api.ApiResponse;
import com.sable.businesslistingapi.api.PSApiService;
import com.sable.businesslistingapi.db.PSCoreDb;
import com.sable.businesslistingapi.repository.common.PSRepository;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewobject.DeletedObject;
import com.sable.businesslistingapi.viewobject.PSAppInfo;
import com.sable.businesslistingapi.viewobject.common.Resource;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;

public class AppLoadingRepository extends PSRepository {

    @Inject
    AppLoadingRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db) {
        super(psApiService, appExecutors, db);
    }

    public LiveData<Resource<PSAppInfo>> deleteTheSpecificObjects(String startDate, String endDate,String user_id) {

        final MutableLiveData<Resource<PSAppInfo>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<PSAppInfo> response;

            try {
                response = psApiService.getDeletedHistory(Config.API_KEY, startDate, endDate,Utils.checkUserId(user_id)).execute();

                ApiResponse<PSAppInfo> apiResponse = new ApiResponse<>(response);

                if (apiResponse.isSuccessful()) {

                    try {
                        db.runInTransaction(() -> {

                            if (apiResponse.body != null) {

                                if (apiResponse.body.deletedObjects.size() > 0) {
                                    for (DeletedObject deletedObject : apiResponse.body.deletedObjects) {
                                        switch (deletedObject.typeName) {
                                            case Constants.APPINFO_NAME_CITY:
                                                db.cityDao().deleteCityById(deletedObject.id);

                                                break;
                                            case Constants.APPINFO_NAME_ITEM:
                                                db.itemDao().deleteItemById(deletedObject.id);
                                                db.historyDao().deleteHistoryItemById(deletedObject.id);

                                                break;
                                            case Constants.APPINFO_NAME_CATEGORY:
                                                db.itemCategoryDao().deleteItemCategoryById(deletedObject.id);
                                                break;
                                        }
                                    }
                                }
                            }

                        });
                    } catch (NullPointerException ne) {
                        Utils.psErrorLog("Null Pointer Exception : ", ne);
                    } catch (Exception e) {
                        Utils.psErrorLog("Exception : ", e);
                    }

                    statusLiveData.postValue(Resource.success(apiResponse.body));

                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), null));
            }

        });

        return statusLiveData;
    }
}
