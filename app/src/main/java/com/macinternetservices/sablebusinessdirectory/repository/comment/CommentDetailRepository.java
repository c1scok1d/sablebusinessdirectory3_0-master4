package com.macinternetservices.sablebusinessdirectory.repository.comment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.macinternetservices.sablebusinessdirectory.AppExecutors;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.api.ApiResponse;
import com.macinternetservices.sablebusinessdirectory.api.PSApiService;
import com.macinternetservices.sablebusinessdirectory.db.CommentDetailDao;
import com.macinternetservices.sablebusinessdirectory.db.PSCoreDb;
import com.macinternetservices.sablebusinessdirectory.repository.common.NetworkBoundResource;
import com.macinternetservices.sablebusinessdirectory.repository.common.PSRepository;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewobject.CommentDetail;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class CommentDetailRepository extends PSRepository {

    //region Variables

    private final CommentDetailDao commentDetailDao;

    //endregion

    //region Constructor
    @Inject
    CommentDetailRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, CommentDetailDao commentDetailDao) {
        super(psApiService, appExecutors, db);
        this.commentDetailDao = commentDetailDao;
    }
    //endregion

    //Get comment detail list
    public LiveData<Resource<List<CommentDetail>>> getCommentDetailList(String apiKey, String offset, String commentid) {

        return new NetworkBoundResource<List<CommentDetail>, List<CommentDetail>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<CommentDetail> itemList) {
                Utils.psLog("SaveCallResult of getCommentDetailList.");

                try {

                    db.runInTransaction(() -> {

                        commentDetailDao.deleteCommentDetailListByHeaderId(commentid);

                        commentDetailDao.insertAllCommentDetailList(itemList);

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getCommentDetailList.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<CommentDetail> data) {

                // Recent news always load from server
                return connectivity.isConnected();

            }

            @NonNull
            @Override
            protected LiveData<List<CommentDetail>> loadFromDb() {
                Utils.psLog("Load getCommentDetailList Comment From Db");
                return commentDetailDao.getAllCommentDetailList(commentid);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<CommentDetail>>> createCall() {
                return psApiService.getCommentDetailList(apiKey, commentid, String.valueOf(Config.COMMENT_COUNT), offset);

            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed (getCommentDetailList) : " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.commentDetailDao().deleteCommentDetailListByHeaderId(commentid)));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextPageCommentDetailList(String offset, String commentid) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();
        LiveData<ApiResponse<List<CommentDetail>>> apiResponse = psApiService.getCommentDetailList(Config.API_KEY, commentid, String.valueOf(Config.COMMENT_COUNT), offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection ConstantConditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {


                    try {
                        db.runInTransaction(() -> {

                            if (response.body != null) {
                                db.commentDetailDao().insertAllCommentDetailList(response.body);
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
                statusLiveData.postValue(Resource.error(response.errorMessage, false));
            }
        });

        return statusLiveData;

    }


    public LiveData<Resource<Boolean>> uploadCommentDetailToServer(String headerId,
                                                                   String userId,
                                                                   String detailComment,
                                                                   String cityId
    ) {
        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

        appExecutors.networkIO().execute(() -> {

            Response<List<CommentDetail>> response;

            try {
                response = psApiService.rawCommentDetailPost(
                        Config.API_KEY,
                        headerId,
                        userId,
                        detailComment,
                        cityId).execute();

                ApiResponse<List<CommentDetail>> apiResponse = new ApiResponse<>(response);

                if (response.isSuccessful()) {

                    try {
                        db.runInTransaction(() -> {
                            if (apiResponse.body != null) {
                                db.commentDetailDao().insertAllCommentDetailList(apiResponse.body);
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

}
