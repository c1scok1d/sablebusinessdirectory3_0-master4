package com.macinternetservices.sablebusinessdirectory.repository.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.macinternetservices.sablebusinessdirectory.api.PSApiService;
import com.macinternetservices.sablebusinessdirectory.db.PSCoreDb;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewobject.User;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;


/**
 * General Delete Task Sample
 * Sable Business Directory on 12/14/17.
 * Contact Email : admin@sablebusinessdirectory.com
 */
public class DeleteTask implements Runnable {


    //region Variables

    private final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();

    public final PSApiService service;
    private final PSCoreDb db;
    private final Object obj;

    //endregion


    //region Constructor

    DeleteTask(PSApiService service, PSCoreDb db, Object obj) {
        this.service = service;
        this.db = db;
        this.obj = obj;
    }

    //endregion


    //region Override Methods

    @Override
    public void run() {
        try {
            try{
                db.runInTransaction(() -> {

                    if (obj instanceof User) {
                        db.userDao().deleteUserLogin();

                        db.itemDao().deleteAllFavouriteItems();

                        db.historyDao().deleteHistoryItem();
                    }
                });

            }catch (Exception ex){
                Utils.psErrorLog("Error at ", ex);
            }
            statusLiveData.postValue(Resource.success(true));
        } catch (Exception e) {
            statusLiveData.postValue(Resource.error(e.getMessage(), true));
        }
    }

    //endregion


    //region public SyncCategory Methods

    /**
     * This function will return Status of Process
     * @return statusLiveData
     */
    public LiveData<Resource<Boolean>> getStatusLiveData() { return statusLiveData; }

    //endregion
}