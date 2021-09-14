package com.macinternetservices.sablebusinessdirectory.repository.image;

import com.macinternetservices.sablebusinessdirectory.AppExecutors;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.api.ApiResponse;
import com.macinternetservices.sablebusinessdirectory.api.PSApiService;
import com.macinternetservices.sablebusinessdirectory.db.ImageDao;
import com.macinternetservices.sablebusinessdirectory.db.PSCoreDb;
import com.macinternetservices.sablebusinessdirectory.repository.common.NetworkBoundResource;
import com.macinternetservices.sablebusinessdirectory.repository.common.PSRepository;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewobject.ApiStatus;
import com.macinternetservices.sablebusinessdirectory.viewobject.Image;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import retrofit2.Response;

/**
 * Sable Business Directory on 09/01/2021
 */

@Singleton
public class ImageRepository extends PSRepository {


    //region Variables

    private final ImageDao imageDao;

    //endregion


    //region Constructor

    @Inject
    ImageRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, ImageDao imageDao) {
        super(psApiService, appExecutors, db);

        this.imageDao = imageDao;
    }

    //endregion


    //region News Repository Functions for ViewModel

    /**
     * Load Image List
     *
     * @param imgType     Image Type
     * @param imgParentId Image Parent Id
     * @return Image List filter by news id
     */
    public LiveData<Resource<List<Image>>> getImageList(String imgType, String imgParentId) {

        String functionKey = "getImageList" + imgParentId;

        return new NetworkBoundResource<List<Image>, List<Image>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Image> item) {
                Utils.psLog("SaveCallResult of getImageList.");

                try {
                    db.runInTransaction(() -> {
                        imageDao.deleteByImageIdAndType(imgParentId, imgType);

                        imageDao.insertAll(item);

                    });
                } catch (Exception e) {
                    Utils.psErrorLog("Error", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Image> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<Image>> loadFromDb() {
                Utils.psLog("Load image list from db");
                return imageDao.getByImageIdAndType(imgParentId, imgType);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Image>>> createCall() {
                Utils.psLog("Call API webservice to get image list." + psApiService.getImageList(Config.API_KEY, imgParentId, imgType));
                return psApiService.getImageList(Config.API_KEY, imgParentId, imgType);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of getting image list.");
                rateLimiter.reset(functionKey);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.imageDao().deleteByImageIdAndType(imgParentId, imgType)));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }
    //endregion

    //region image list show in item upload after image upload
    public LiveData<Resource<List<Image>>> getImagesFromServer(String itemId, String limit, String offset, String limitFromDB) {
        return new NetworkBoundResource<List<Image>, List<Image>>(appExecutors) {


            @Override
            protected void saveCallResult(@NonNull List<Image> item) {

                try {
                    db.runInTransaction(() -> {

                        imageDao.deleteTable();
                        imageDao.insertAll(item);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Image> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<Image>> loadFromDb() {
                if (limitFromDB.equals("0")) {
                    return imageDao.getImageByItemId(itemId);
                } else {
                    return imageDao.getImageByItemIdLimit(itemId, limitFromDB);
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Image>>> createCall() {
                return psApiService.getImagesInItemUploadAfterImageUpload(Config.API_KEY, itemId, limit, offset);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of getting image list.");

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.imageDao().deleteTable()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }
    //endregion


    //region delete image
    public LiveData<Resource<Boolean>> deleteImage(String item_id, String imgId) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        appExecutors.networkIO().execute(() -> {

            try {
                Response<ApiStatus> response1 = psApiService.deleteImage(Config.API_KEY, item_id, imgId).execute();

                ApiResponse<ApiStatus> apiResponse = new ApiResponse<ApiStatus>(response1);

                //noinspection ConstantConditions
                if (apiResponse.isSuccessful()) {

                    appExecutors.diskIO().execute(() -> {

                        try {
                            db.runInTransaction(() -> {

                                Item item = db.itemDao().getOneItemObject(item_id);

                                if (item.defaultPhoto.imgId.equals(imgId)) {
                                    item.defaultPhoto.imgId = "";
                                    item.defaultPhoto.imgParentId = "";
                                    item.defaultPhoto.imgType = "";
                                    item.defaultPhoto.imgPath = "";
                                    item.defaultPhoto.imgWidth = "";
                                    item.defaultPhoto.imgHeight = "";
                                    item.defaultPhoto.imgDesc = "";

                                    db.itemDao().insert(item);
                                }

                                db.imageDao().deleteById(imgId);

                            });

                        } catch (NullPointerException ne) {
                            Utils.psErrorLog("Null Pointer Exception : ", ne);
                        } catch (Exception e) {
                            Utils.psErrorLog("Exception : ", e);
                        }

                        statusLiveData.postValue(Resource.success(true));


                    });

                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, null));
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return statusLiveData;
    }
    //endregion


    //region image next page
    public LiveData<Resource<Boolean>> getNextImagesFromServer(String itemId, String limit, String offset) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<List<Image>>> apiResponse = psApiService.getImagesInItemUploadAfterImageUpload(Config.API_KEY, itemId, limit, offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);


            //noinspection ConstantConditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {


                    try {
                        db.runInTransaction(() -> {

                            imageDao.insertAll(response.body);

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
    //endregion
}
