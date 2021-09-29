package com.sable.businesslistingapi.repository.common;

import androidx.lifecycle.LiveData;
import com.sable.businesslistingapi.AppExecutors;
import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.api.PSApiService;
import com.sable.businesslistingapi.db.PSCoreDb;
import com.sable.businesslistingapi.utils.AbsentLiveData;
import com.sable.businesslistingapi.utils.Connectivity;
import com.sable.businesslistingapi.utils.RateLimiter;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewobject.common.Resource;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Parent Class of All Repository Class in this project
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */

public abstract class PSRepository {


    //region Variables

    protected final PSApiService psApiService;
    protected final AppExecutors appExecutors;
    protected final PSCoreDb db;
    @Inject
    protected Connectivity connectivity;
    protected RateLimiter<String> rateLimiter = new RateLimiter<>( Config.API_SERVICE_CACHE_LIMIT, TimeUnit.MINUTES);

    //endregion


    //region Constructor

    /**
     * Constructor of PSRepository
     * @param psApiService Sable Business Directory API Service Instance
     * @param appExecutors Executors Instance
     * @param db Sable Business Directory DB
     */
    protected PSRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db) {
        Utils.psLog("Inside NewsRepository");
        this.psApiService = psApiService;
        this.appExecutors = appExecutors;
        this.db = db;
    }

    //endregion


    //region public Methods

    public LiveData<Resource<Boolean>> save(Object obj) {

        if(obj == null) {
            return AbsentLiveData.create();
        }

        SaveTask saveTask = new SaveTask(psApiService, db, obj);
        appExecutors.diskIO().execute(saveTask);
        return saveTask.getStatusLiveData();
    }


    public LiveData<Resource<Boolean>> delete(Object obj) {

        if(obj == null) {
            return AbsentLiveData.create();
        }

        DeleteTask deleteTask = new DeleteTask(psApiService, db, obj);
        appExecutors.diskIO().execute(deleteTask);
        return deleteTask.getStatusLiveData();
    }
    //endregion

}
