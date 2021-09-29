package com.sable.businesslistingapi.repository.contactus;

import com.sable.businesslistingapi.AppExecutors;
import com.sable.businesslistingapi.api.ApiResponse;
import com.sable.businesslistingapi.api.PSApiService;
import com.sable.businesslistingapi.db.PSCoreDb;
import com.sable.businesslistingapi.repository.common.PSRepository;
import com.sable.businesslistingapi.utils.Utils;
import com.sable.businesslistingapi.viewobject.ApiStatus;
import com.sable.businesslistingapi.viewobject.common.Resource;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Response;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 * Website : http://www.sablebusinessdirectory.com
 */

public class ContactUsRepository extends PSRepository {

    @Inject
    ContactUsRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db) {
        super(psApiService, appExecutors, db);

    }

    /**
     * Post Contact Us
     * @param apiKey APIKey to access Web Service
     * @param contactName Name
     * @param contactEmail Email
     * @param contactDesc Desc
     * @return Status of Post
     */
    public LiveData<Resource<Boolean>> postContactUs(String apiKey, String contactName, String contactEmail, String contactDesc, String contactPhone) {

        final MutableLiveData<Resource<Boolean>> statusLiveData = new MutableLiveData<>();
        appExecutors.networkIO().execute(() -> {
            try {

                // Call the API Service
                Response<ApiStatus> response = psApiService.rawPostContact(apiKey, contactName, contactEmail, contactDesc, contactPhone).execute();

                // Wrap with APIResponse Class
                ApiResponse<ApiStatus> apiResponse = new ApiResponse<>(response);

                Utils.psLog("apiResponse " + apiResponse);
                // If response is successful
                if (apiResponse.isSuccessful()) {

                    statusLiveData.postValue(Resource.success(true));
                } else {

                    statusLiveData.postValue(Resource.error(apiResponse.errorMessage, true));
                }
            } catch (Exception e) {
                statusLiveData.postValue(Resource.error(e.getMessage(), true));
            }

        });

        return statusLiveData;

    }

}
