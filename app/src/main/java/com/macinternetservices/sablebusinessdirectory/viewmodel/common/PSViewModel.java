package com.macinternetservices.sablebusinessdirectory.viewmodel.common;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.macinternetservices.sablebusinessdirectory.utils.Utils;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */


public class PSViewModel extends ViewModel{

    public Utils.LoadingDirection loadingDirection = Utils.LoadingDirection.none;
    private final MutableLiveData<Boolean> loadingState = new MutableLiveData<>();

    public int offset = 0;

    public boolean forceEndLoading = false;
    public boolean isLoading = false;


    //region For loading status
    public void setLoadingState(Boolean state) {
        isLoading = state;
        loadingState.setValue(state);
    }

    public MutableLiveData<Boolean> getLoadingState() {
        return loadingState;
    }

    //endregion
}
