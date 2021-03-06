package com.sable.businesslistingapi.repository.item;

import android.content.ContentResolver;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.sable.businesslistingapi.AppExecutors;
import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.api.ApiResponse;
import com.sable.businesslistingapi.api.PSApiService;
import com.sable.businesslistingapi.db.ItemDao;
import com.sable.businesslistingapi.db.PSCoreDb;
import com.sable.businesslistingapi.repository.common.NetworkBoundResource;
import com.sable.businesslistingapi.repository.common.PSRepository;
import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewobject.ApiStatus;
import com.sable.businesslistingapi.viewobject.Image;
import com.sable.businesslistingapi.viewobject.Item;
import com.sable.businesslistingapi.viewobject.ItemFavourite;
import com.sable.businesslistingapi.viewobject.ItemHistory;
import com.sable.businesslistingapi.viewobject.ItemMap;
import com.sable.businesslistingapi.viewobject.ItemSpecs;
import com.sable.businesslistingapi.viewobject.common.Resource;
import com.sable.businesslistingapi.viewobject.holder.ItemParameterHolder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

@Singleton
public class ItemRepository extends PSRepository {
    /**
     * Constructor of PSRepository
     *
     * @param psApiService Sable Business Directory API Service Instance
     * @param appExecutors Executors Instance
     * @param db           Sable Business Directory DB
     */
    private String isSelected;
    private final ItemDao itemDao;

    @Inject
    protected ItemRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, ItemDao itemDao) {
        super(psApiService, appExecutors, db);
        this.itemDao = itemDao;
    }

    //region Get Favourite Product

    public LiveData<Resource<List<Item>>> getFavouriteList(String apiKey, String loginUserId, String offset) {

        return new NetworkBoundResource<List<Item>, List<Item>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Item> itemList) {
                Utils.psLog("SaveCallResult of related products.");


                try {
                    db.runInTransaction(() -> {
                        db.itemDao().deleteAllFavouriteItems();

                        db.itemDao().insertAll(itemList);

                        for (int i = 0; i < itemList.size(); i++) {
                            db.itemDao().insertFavourite(new ItemFavourite(itemList.get(i).id, i + 1));
                        }

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of related list.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Item> data) {

                // Recent news always load from server
                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                Utils.psLog("Load related From Db");

//                return null;
                return db.itemDao().getAllFavouriteProducts();

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Item>>> createCall() {
                Utils.psLog("Call API Service to get related.");

                return psApiService.getFavouriteList(apiKey, Utils.checkUserId(loginUserId), String.valueOf(Config.ITEM_COUNT), offset);


            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getRelated) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.itemDao().deleteAllFavouriteItems()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextPageFavouriteProductList(String loginUserId, String offset) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<List<Item>>> apiResponse = psApiService.getFavouriteList(Config.API_KEY, Utils.checkUserId(loginUserId), String.valueOf(Config.ITEM_COUNT), offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {


                    try {
                        db.runInTransaction(() -> {

                            if (response.body != null) {

                                int lastIndex = db.itemDao().getMaxSortingFavourite();
                                lastIndex = lastIndex + 1;

                                for (int i = 0; i < response.body.size(); i++) {
                                    db.itemDao().insertFavourite(new ItemFavourite(response.body.get(i).id, lastIndex + i));
                                }

                                db.itemDao().insertAll(response.body);
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

    //region Get Item List

    public LiveData<Resource<List<Item>>> getItemListByKey(String loginUserId, String limit, String offset, ItemParameterHolder itemParameterHolder) {

        return new NetworkBoundResource<List<Item>, List<Item>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Item> itemList) {
                Utils.psLog("SaveCallResult of getProductListByKey.");

                try {

                    db.runInTransaction(() -> {

                        String mapKey = itemParameterHolder.getItemMapKey();

                        db.itemMapDao().deleteByMapKey(mapKey);

                        itemDao.insertAll(itemList);

                        String dateTime = Utils.getDateTime();

                        for (int i = 0; i < itemList.size(); i++) {
                            db.itemMapDao().insert(new ItemMap(mapKey + itemList.get(i).id, mapKey, itemList.get(i).id, i + 1, dateTime));
                        }

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getProductListByKey.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Item> data) {

                // Recent news always load from server
                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                Utils.psLog("Load getProductListByKey From Db");

                String mapKey = itemParameterHolder.getItemMapKey();
                if (!limit.equals(String.valueOf(Config.LIMIT_FROM_DB_COUNT))) {
                    return itemDao.getItemByKey(mapKey);
                } else {
                    return itemDao.getItemByKeyByLimit(mapKey, limit);
                }

            }


            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Item>>> createCall() {
                Utils.psLog("Call API Service to getProductListByKey.");


                return psApiService.searchItem(Config.API_KEY, limit, offset, Utils.checkUserId(loginUserId), itemParameterHolder.keyword, itemParameterHolder.city_id, itemParameterHolder.cat_id, itemParameterHolder.sub_cat_id,
                        itemParameterHolder.order_by, itemParameterHolder.order_type, itemParameterHolder.rating_value, itemParameterHolder.is_featured, itemParameterHolder.is_promotion, itemParameterHolder.lat,
                        itemParameterHolder.lng, itemParameterHolder.miles,itemParameterHolder.added_user_id,itemParameterHolder.isPaid, itemParameterHolder.status);

            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getProductListByKey) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> {

                            String mapKey = itemParameterHolder.getItemMapKey();

                            db.itemMapDao().deleteByMapKey(mapKey);

                        }));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();

    }

    public LiveData<Resource<Boolean>> getNextPageProductListByKey(ItemParameterHolder itemParameterHolder, String loginUserId, String limit, String offset) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

//        prepareRatingValueForServer(productParameterHolder);

        LiveData<ApiResponse<List<Item>>> apiResponse = psApiService.searchItem(Config.API_KEY, limit, offset, Utils.getCurrentLng(), itemParameterHolder.keyword, itemParameterHolder.city_id, itemParameterHolder.cat_id, itemParameterHolder.sub_cat_id,
                itemParameterHolder.order_by, itemParameterHolder.order_type, itemParameterHolder.rating_value, itemParameterHolder.is_featured, itemParameterHolder.is_promotion, itemParameterHolder.lat,
                itemParameterHolder.lng, itemParameterHolder.miles,itemParameterHolder.added_user_id,itemParameterHolder.isPaid, itemParameterHolder.status);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                if (response.body != null) {
                    appExecutors.diskIO().execute(() -> {

                        try {

                            db.runInTransaction(() -> {

                                itemDao.insertAll(response.body);

                                int finalIndex = db.itemMapDao().getMaxSortingByValue(itemParameterHolder.getItemMapKey());

                                int startIndex = finalIndex + 1;

                                String mapKey = itemParameterHolder.getItemMapKey();
                                String dateTime = Utils.getDateTime();

                                for (int i = 0; i < response.body.size(); i++) {
                                    db.itemMapDao().insert(new ItemMap(mapKey + response.body.get(i).id, mapKey, response.body.get(i).id, startIndex + i, dateTime));
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

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }
        });

        return statusLiveData;

    }

    //endregion

    //region Favourite post

    public LiveData<Resource<Boolean>> uploadFavouritePostToServer(String itemId, String userId, String cityId) {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            try {
                try {
                    db.runInTransaction(() -> {

                        isSelected = itemDao.selectFavouriteById(itemId);
                        if (isSelected.equals(Constants.ONE)) {
                            itemDao.updateProductForFavById(itemId, Constants.ZERO);
                        } else {
                            itemDao.updateProductForFavById(itemId, Constants.ONE);
                        }


                    });
                } catch (NullPointerException ne) {
                    Utils.psErrorLog("Null Pointer Exception : ", ne);
                } catch (Exception e) {
                    Utils.psErrorLog("Exception : ", e);
                }

                // Call the API Service
                Response<Item> response;

                response = psApiService.setPostFavourite(Config.API_KEY, itemId, userId, cityId).execute();

                // Wrap with APIResponse Class
                ApiResponse<Item> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    try {
                        db.runInTransaction(() -> {

                            if (apiResponse.body != null) {
                                itemDao.insert(apiResponse.body);

                                if (isSelected.equals(Constants.ONE)) {
                                    db.itemDao().deleteFavouriteItemByItemId(apiResponse.body.id);
                                } else {
                                    int lastIndex = db.itemDao().getMaxSortingFavourite();
                                    lastIndex = lastIndex + 1;

                                    db.itemDao().insertFavourite(new ItemFavourite(apiResponse.body.id, lastIndex));
                                }
                            }

                        });
                    } catch (NullPointerException ne) {
                        Utils.psErrorLog("Null Pointer Exception : ", ne);
                    } catch (Exception e) {
                        Utils.psErrorLog("Exception : ", e);
                    }

                    statusLiveData.postValue(Resource.success(apiResponse.getNextPage() != null));

                } else {

                    try {
                        db.runInTransaction(() -> {

                            isSelected = itemDao.selectFavouriteById(itemId);
                            if (isSelected.equals(Constants.ONE)) {
                                itemDao.updateProductForFavById(itemId, Constants.ZERO);
                            } else {
                                itemDao.updateProductForFavById(itemId, Constants.ONE);
                            }

                        });
                    } catch (NullPointerException ne) {
                        Utils.psErrorLog("Null Pointer Exception : ", ne);
                    } catch (Exception e) {
                        Utils.psErrorLog("Exception : ", e);
                    }

                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, false));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), false));
            }
        });

        return statusLiveData;
    }

    //endregion

    //region Get Product detail

    public LiveData<Resource<Item>> getItemDetail(String apiKey, String itemId, String cityId, String historyFlag, String userId) {

        return new NetworkBoundResource<Item, Item>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull Item itemList) {
                Utils.psLog("SaveCallResult of recent products.");


                try {
                    db.runInTransaction(() -> {
                        itemDao.insert(itemList);

                        db.specsDao().deleteItemSpecsById(itemId);
                        db.specsDao().insertAll(itemList.productSpecsList);

                        if (historyFlag.equals(Constants.ONE)) {

                            db.historyDao().insert(new ItemHistory(itemId, itemList.name, itemList.defaultPhoto.imgPath, Utils.getDateTime()));
                        }

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of discount list.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Item data) {

                // Recent news always load from server
                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<Item> loadFromDb() {
                Utils.psLog("Load discount From Db");

                return itemDao.getItemById(itemId);

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Item>> createCall() {
                Utils.psLog("Call API Service to get discount.");

                return psApiService.getItemDetail(apiKey, itemId, cityId, Utils.checkUserId(userId));

            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getDiscount) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.specsDao().deleteItemSpecsById(itemId)));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    //endregion


    //region Touch count post

    public LiveData<Resource<Boolean>> uploadTouchCountPostToServer(String userId, String typeId, String typeName, String cityId) {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<ApiStatus> response;

            try {
                response = psApiService.setrawPostTouchCount(
                        Config.API_KEY, typeId, typeName, Utils.checkUserId(userId), cityId).execute();

                ApiResponse<ApiStatus> apiResponse = new ApiResponse<>(response);

                if (apiResponse.isSuccessful()) {
                    statusLiveData.postValue(Resource.success(true));
                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, false));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), false));
            }

        });

        return statusLiveData;
    }

    //endregion

    //region Get history

    public LiveData<List<ItemHistory>> getAllHistoryList(String offset) {

        return db.historyDao().getAllHistoryItemListData(offset);

    }

    //endregion

    //region Get Product specs

    public LiveData<List<ItemSpecs>> getAllSpecifications(String itemId) {
        return db.specsDao().getItemSpecsById(itemId);
    }

    //endregion
    //region delete image
    public LiveData<Resource<Boolean>> deleteOneItem(String item_id, String user_id) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        appExecutors.networkIO().execute(() -> {

            try {
                Response<ApiStatus> response1 = psApiService.deleteItem(Config.API_KEY, item_id,user_id).execute();

                ApiResponse<ApiStatus> apiResponse = new ApiResponse<ApiStatus>(response1);

                //noinspection Constant Conditions
                if (apiResponse.isSuccessful()) {

                    appExecutors.diskIO().execute(() -> {

                        try {
                            db.runInTransaction(() -> {

                                db.itemDao().deleteItemById(item_id);
                                db.historyDao().deleteHistoryItemById(item_id);

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

    //region Delete Specification
    public LiveData<Resource<Boolean>> deleteSpecification(String itemId, String id) {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            try {
                // Call the API Service
                Response<ApiStatus> response;
                response = psApiService.deleteSpecification(Config.API_KEY, itemId, id).execute();


                // Wrap with APIResponse Class
                ApiResponse<ApiStatus> apiResponse = new ApiResponse<>(response);

                // If response is successful
                if (apiResponse.isSuccessful()) {

                    try {
                        db.runInTransaction(() -> {

                            if (apiResponse.body != null) {
                                db.specsDao().deleteItemSpecsById(id);
                            }

                        });
                    } catch (NullPointerException ne) {
                        Utils.psErrorLog("Null Pointer Exception : ", ne);
                    } catch (Exception e) {
                        Utils.psErrorLog("Exception : ", e);
                    }

                    statusLiveData.postValue(Resource.success(true));

                } else {
                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, false));
                }

            } catch (IOException e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), false));
            }
        });

        return statusLiveData;
    }
    //endregion

    //attribute header list
    public LiveData<Resource<List<ItemSpecs>>> getAllSpecificationList(String itemId, String limit, String offset) {
        return new NetworkBoundResource<List<ItemSpecs>, List<ItemSpecs>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<ItemSpecs> itemList) {

                Utils.psLog(itemList.size() + "SaveCallResult of new list.");

                try {
                    db.runInTransaction(() -> {

                        db.specsDao().deleteItemSpecsById(itemId);

                        db.specsDao().insertAll(itemList);

                    });
                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of recent product list.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ItemSpecs> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<ItemSpecs>> loadFromDb() {
                return db.specsDao().getItemSpecsById(itemId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ItemSpecs>>> createCall() {
                return psApiService.getAllSpecificationByItemId(Config.API_KEY, itemId, limit, offset);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getDiscount) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.specsDao().deleteItemSpecsById(itemId)));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }
        }.asLiveData();
    }
    //endregion

    //next page
    public LiveData<Resource<Boolean>> getNextPageSpecification(String itemId, String limit, String offset) {
        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<List<ItemSpecs>>> apiResponse = psApiService.getAllSpecificationByItemId(Config.API_KEY, itemId, limit, offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);


            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {


                    try {
                        db.runInTransaction(() -> {

                            db.specsDao().insertAll(response.body);

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

    // add specification
    public LiveData<Resource<ItemSpecs>> uploadSpecification(String item_id, String name, String description, String id) {

        final MediatorLiveData<Resource<ItemSpecs>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<ItemSpecs>> apiResponse = psApiService.addSpecification(Config.API_KEY, item_id, name,description,id);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {

                    try {
                        db.runInTransaction(() -> {

                            db.specsDao().insert(response.body);

                        });

                    } catch (NullPointerException ne) {
                        Utils.psErrorLog("Null Pointer Exception : ", ne);
                    } catch (Exception e) {
                        Utils.psErrorLog("Exception : ", e);
                    }

                    statusLiveData.postValue(Resource.success(response.body));

                });

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }

        });

        return statusLiveData;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }




    public LiveData<Resource<Image>> uploadItemImage(String filePath, Uri uri, String imageId, String imgDesc, String
            itemId, ContentResolver contentResolver) {

        //Init File
        MultipartBody.Part body = null;
        if (!filePath.equals("")) {
            File file = new File(filePath);
            if(Config.isCompressImage){
//                File compressedFile = compressImage(file, contentResolver);
                byte[] compressedFile = Utils.compressImage(file, uri, contentResolver, Config.uploadImageHeight, Config.uploadImageWidth);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), compressedFile);

                // MultipartBody.Part is used to send also the actual file news_title
                body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//                body = getFileBody(compressedFile);
            }else{
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);

                // MultipartBody.Part is used to send also the actual file news_title
                body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            }
        }
        // add another part within the multipart request

        RequestBody imgIdRB =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), imageId);

        RequestBody itemIdRB =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), itemId);

        RequestBody descRB =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), imgDesc);

        MultipartBody.Part finalBody = body;
        return new NetworkBoundResource<Image, Image>(appExecutors) {

            // Temp ResultType To Return

            @Override
            protected void saveCallResult(@NonNull Image image) {
                Utils.psLog("SaveCallResult");

                try {
                    db.runInTransaction(() -> {
                        Item item1 = itemDao.getOneItemObject(itemId);
                        // update user data

                        if (item1.defaultPhoto.imgId.equals("") || !item1.defaultPhoto.imgId.isEmpty()) {
                            item1.defaultPhoto = image;

                            itemDao.insert(item1);
                        }

                        if (item1.defaultPhoto.imgDesc.equals("") || !item1.defaultPhoto.imgDesc.isEmpty()) {
                            item1.defaultPhoto = image;
                            itemDao.insert(item1);
                        }

                        db.imageDao().deleteTable();
                        db.imageDao().insert(image);
                    });
                } catch (Exception ex) {
                    Utils.psErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Image data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<Image> loadFromDb() {

                return db.imageDao().getUploadImage();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Image>> createCall() {
                Utils.psLog("Call API Service to upload image.");

                return psApiService.uploadItemImage(Config.API_KEY,itemIdRB, descRB, finalBody, imgIdRB);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of uploading image.");

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

//    public LiveData<Resource<Item>> uploadImage(String itemId, String imgDesc, String filePath, String imgId)
//
//    {
//
//        //Init File
//        MultipartBody.Part body = null;
//        if(!filePath.equals("")) {
//            File file = new File(filePath);
//            RequestBody requestFile =
//                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
//
//            // MultipartBody.Part is used to send also the actual file news_title
//            body =
//                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//
//        }
//
//        // add another part within the multipart request

//
////        RequestBody platformRB =
////                RequestBody.create(
////                        MediaType.parse("multipart/form-data"), platformName);
//
//        RequestBody idRB =
//                RequestBody.create(
//                        MediaType.parse("multipart/form-data"), imgId);
//
//        MultipartBody.Part finalBody = body;
//        return new NetworkBoundResource<Item, Image>(appExecutors) {
//
//            // Temp ResultType To Return
//
//            @Override
//            protected void saveCallResult(@NonNull Image item) {
//                Utils.psLog("SaveCallResult");
//
//                try {
//                    db.runInTransaction(() -> {

//
//                        db.imageDao().insert(item);
//
//                    });
//
//                } catch (Exception e) {
//                    Utils.psErrorLog("Error", e);
//                }
//            }
//
//            @Override
//            protected boolean shouldFetch(@Nullable Item data) {
//                return connectivity.isConnected();
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<Item> loadFromDb() {
//
//                return itemDao.getOneItem(itemId);
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<ApiResponse<Image>> createCall() {
//                Utils.psLog("Call API Service to upload image.");
//
//                return psApiService.uploadItemImage(Config.API_KEY,itemIdRB, descRB, finalBody, idRB);
//            }
//
//            @Override
//            protected void onFetchFailed(String message) {
//                Utils.psLog("Fetch Failed of uploading image.");
//            }
//        }.asLiveData();
//    }

    //region Item Upload
    public LiveData<Resource<Item>> saveOneItem(String userId, String cityId, String categoryId, String subCategoryId, String status, String name, String description, String searchTag, String highlightInformation,
                                                String isFeatured, String latitude, String longitude, String openHour, String closeHour, String isPromotion, String phoneOne, String phoneTwo, String phoneThree,
                                                String email, String address, String facebook, String googlePlus, String twitter, String youtube, String instagram, String pinterest, String website, String whatesapp,
                                                String messenger, String timeRemark, String terms, String cancelationPolicy, String additionalInfo, String itemId) {
        final MediatorLiveData<Resource<Item>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<Item>> apiResponse = psApiService.saveItem(Config.API_KEY,userId,cityId,categoryId,subCategoryId,status,name,description,searchTag,highlightInformation,isFeatured,latitude,longitude,openHour,closeHour,
                isPromotion,phoneOne,phoneTwo,phoneThree,email,address,facebook,googlePlus,twitter,youtube,instagram,pinterest,website,whatesapp,messenger,timeRemark,terms,cancelationPolicy,additionalInfo,itemId);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection ConstantConditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {

                    try {
                        db.runInTransaction(() -> {

                            db.itemDao().insert(response.body);

                        });

                    } catch (NullPointerException ne) {
                        Utils.psErrorLog("Null Pointer Exception : ", ne);
                    } catch (Exception e) {
                        Utils.psErrorLog("Exception : ", e);
                    }

                    statusLiveData.postValue(Resource.success(response.body));

                });

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }

        });

        return statusLiveData;
    }
    //endregion

}
//endregion
