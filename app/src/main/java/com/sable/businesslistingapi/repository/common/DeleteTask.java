package com.sable.businesslistingapi.repository.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sable.businesslistingapi.api.PSApiService;
import com.sable.businesslistingapi.db.PSCoreDb;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewobject.User;
import com.sable.businesslistingapi.viewobject.common.Resource;


/**
 * General Delete Task Sample
 * Sable Business Directory on 09/01/2021
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