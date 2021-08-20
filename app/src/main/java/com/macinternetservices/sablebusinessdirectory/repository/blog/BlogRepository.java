package com.macinternetservices.sablebusinessdirectory.repository.blog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.macinternetservices.sablebusinessdirectory.AppExecutors;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.api.ApiResponse;
import com.macinternetservices.sablebusinessdirectory.api.PSApiService;
import com.macinternetservices.sablebusinessdirectory.db.BlogDao;
import com.macinternetservices.sablebusinessdirectory.db.PSCoreDb;
import com.macinternetservices.sablebusinessdirectory.repository.common.NetworkBoundResource;
import com.macinternetservices.sablebusinessdirectory.repository.common.PSRepository;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewobject.Blog;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BlogRepository extends PSRepository {

    private final BlogDao blogDao;

    @Inject
    BlogRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, BlogDao blogDao) {
        super(psApiService, appExecutors, db);
        this.blogDao = blogDao;
    }

    public LiveData<Resource<List<Blog>>> getNewsFeedList(String limit, String offset) {
        return new NetworkBoundResource<List<Blog>, List<Blog>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Blog> itemList) {
                Utils.psLog("SaveCallResult of getNewsFeedList.");

                try {
                    db.runInTransaction(() -> {
                        blogDao.deleteAll();
                        blogDao.insertAll(itemList);

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getNewsFeedList.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Blog> data) {

                // Recent news always load from server
                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<List<Blog>> loadFromDb() {
                Utils.psLog("Load getNewsFeedList From Db");
                if(limit.equals(String.valueOf(Config.LIST_NEW_FEED_COUNT_PAGER))){
                    return blogDao.getAllNewsFeedByLimit(limit);
                }else {
                    return blogDao.getAllNewsFeed();
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Blog>>> createCall() {
                Utils.psLog("Call API Service to getNewsFeedList.");

                return psApiService.getAllNewsFeed(Config.API_KEY, limit, offset);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getNewsFeedList) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.blogDao().deleteAll()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Blog>>> getNewsFeedListByCityId(String cityId, String limit, String offset) {
        return new NetworkBoundResource<List<Blog>, List<Blog>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Blog> itemList) {
                Utils.psLog("SaveCallResult of getNewsFeedListByCityId.");

                try {
                    db.runInTransaction(() -> {
                        blogDao.deleteAll();
                        blogDao.insertAll(itemList);

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getNewsFeedListByCityId.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Blog> data) {

                // Recent news always load from server
                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<List<Blog>> loadFromDb() {
                Utils.psLog("Load getNewsFeedListByCityId From Db");

                return blogDao.getAllNewsFeed(cityId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Blog>>> createCall() {

                Utils.psLog("Call API Service to getNewsFeedListByCityId.");
                return psApiService.getAllNewsFeedByCityId(Config.API_KEY, cityId, limit, offset);

            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getNewsFeedListByCityId) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.blogDao().deleteAll()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }
        }.asLiveData();
    }


    public LiveData<Resource<Boolean>> getNextPageNewsFeedList(String apiKey, String limit, String offset) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();

        LiveData<ApiResponse<List<Blog>>> apiResponse = psApiService.getAllNewsFeed(apiKey, limit, offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {

                    try {

                        db.runInTransaction(() -> {

                            db.blogDao().insertAll(response.body);

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

    public LiveData<Resource<Blog>> getBlogById(String id, String cityId) {
        return new NetworkBoundResource<Blog, Blog>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull Blog blog) {
                Utils.psLog("SaveCallResult of getBlogById.");

                try {
                    db.runInTransaction(() -> {

                        blogDao.deleteAll();
                        blogDao.insert(blog);

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getBlogById.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Blog blog) {

                // Recent news always load from server
                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<Blog> loadFromDb() {

                Utils.psLog("Load getBlogById From Db");
                return blogDao.getBlogById(id);

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Blog>> createCall() {

                Utils.psLog("Call API Service to getBlogById.");
                return psApiService.getNewsById(Config.API_KEY, id, cityId);

            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getBlogById) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.blogDao().deleteAll()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }
        }.asLiveData();
    }

}