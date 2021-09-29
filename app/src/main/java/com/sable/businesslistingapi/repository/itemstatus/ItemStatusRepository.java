package com.sable.businesslistingapi.repository.itemstatus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.sable.businesslistingapi.AppExecutors;
import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.api.ApiResponse;
import com.sable.businesslistingapi.api.PSApiService;
import com.sable.businesslistingapi.db.ItemStatusDao;
import com.sable.businesslistingapi.db.PSCoreDb;
import com.sable.businesslistingapi.repository.common.NetworkBoundResource;
import com.sable.businesslistingapi.repository.common.PSRepository;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewobject.ItemStatus;
import com.sable.businesslistingapi.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;

public class ItemStatusRepository extends PSRepository {

    private final ItemStatusDao itemStatusDao;

    @Inject
    protected ItemStatusRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, ItemStatusDao itemStatusDao) {
        super(psApiService, appExecutors, db);

        this.itemStatusDao = itemStatusDao;
    }

    public LiveData<Resource<List<ItemStatus>>> getAllItemStatus(String limit, String offset) {

        return new NetworkBoundResource<List<ItemStatus>, List<ItemStatus>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<ItemStatus> item) {
                Utils.psLog("SaveCallResult of getAllCategoriesWithUserId");


                try {
                    db.runInTransaction(() -> {

                        db.itemStatusDao().deleteAllCityCategory();

                        for (int i = 0; i < item.size(); i++) {
                            db.itemStatusDao().insert(new ItemStatus(item.get(i).id, item.get(i).title, item.get(i).addedDate));
                        }

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getAllCategoriesWithUserId", e);
                }
            }


            @Override
            protected boolean shouldFetch(@Nullable List<ItemStatus> data) {

                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<ItemStatus>> loadFromDb() {

                Utils.psLog("Load From Db (All Categories)");

                return db.itemStatusDao().getAllCityCategoryById();

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ItemStatus>>> createCall() {
                Utils.psLog("Call Get All Item Status webservice.");

                return psApiService.getItemStatus(Config.API_KEY,limit, offset);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of About Us");

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.itemStatusDao().deleteAllCityCategory()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextPageItemStatus(String limit, String offset) {
        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();
        LiveData<ApiResponse<List<ItemStatus>>> apiResponse = psApiService.getItemStatus(Config.API_KEY, limit, offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {


                    try {
                        db.runInTransaction(() -> {

                            if (response.body != null) {

//                            int finalIndex = db.itemStatusDao().getMaxSortingByValue();
//
//                            int startIndex = finalIndex + 1;

                                for (int i = 0; i < response.body.size(); i++) {
                                    db.itemStatusDao().insert(new ItemStatus(response.body.get(i).id, response.body.get(i).title, response.body.get(i).addedDate));
                                }

                                //db.trendingCategoryDao().insertAll(new TrendingCategory(apiResponse.body.));
                            }

                        });
                    } catch (NullPointerException ne) {
                        Utils.psErrorLog("Null Pointer Exception : ", ne);
                    } catch (Exception e) {
                        Utils.psErrorLog("Exception : ", e);
                    }

                    statusLiveData.postValue(Resource.success(true));
                });

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }

        });

        return statusLiveData;
    }
}
