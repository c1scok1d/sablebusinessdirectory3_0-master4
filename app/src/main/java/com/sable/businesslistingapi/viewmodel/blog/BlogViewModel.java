package com.sable.businesslistingapi.viewmodel.blog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.sable.businesslistingapi.Config;
import com.sable.businesslistingapi.repository.blog.BlogRepository;
import com.sable.businesslistingapi.utils.AbsentLiveData;
import com.sable.businesslistingapi.viewmodel.common.PSViewModel;
import com.sable.businesslistingapi.viewobject.Blog;
import com.sable.businesslistingapi.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;

public class BlogViewModel extends PSViewModel {
    private final LiveData<Resource<List<Blog>>> newsFeedData;
    private MutableLiveData<BlogViewModel.TmpDataHolder> newsFeedObj = new MutableLiveData<>();

    private final LiveData<Resource<List<Blog>>> newsFeedByCityIdData;
    private MutableLiveData<BlogViewModel.NewFeedByCityIdTmpDataHolder> newsFeedByCityIdObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageNewsFeedData;
    private MutableLiveData<BlogViewModel.TmpDataHolder> nextPageNewsFeedObj = new MutableLiveData<>();

    private final LiveData<Resource<Blog>> blogByIdData;
    private MutableLiveData<BlogViewModel.BlogByIdTmpDataHolder> blogByIdObj = new MutableLiveData<>();

    public String cityName, cityId;

    @Inject
    BlogViewModel(BlogRepository repository) {

        newsFeedData = Transformations.switchMap(newsFeedObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNewsFeedList(obj.limit, obj.offset);

        });

        newsFeedByCityIdData = Transformations.switchMap(newsFeedByCityIdObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNewsFeedListByCityId(obj.cityId, obj.limit, obj.offset);

        });

        nextPageNewsFeedData = Transformations.switchMap(nextPageNewsFeedObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextPageNewsFeedList(Config.API_KEY, obj.limit, obj.offset);

        });

        blogByIdData = Transformations.switchMap(blogByIdObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getBlogById(obj.id, obj.cityId);

        });

    }

    public void setNewsFeedObj(String limit, String offset) {
        TmpDataHolder tmpDataHolder = new TmpDataHolder(limit, offset);

        this.newsFeedObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<List<Blog>>> getNewsFeedData() {
        return newsFeedData;
    }

    public void setNewsFeedByCityIdObj(String cityId, String limit, String offset) {
        NewFeedByCityIdTmpDataHolder tmpDataHolder = new NewFeedByCityIdTmpDataHolder(cityId, limit, offset);

        this.newsFeedByCityIdObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<List<Blog>>> getNewsFeedByCityIdData() {
        return newsFeedByCityIdData;
    }


    public void setNextPageNewsFeedObj(String limit, String offset) {
        TmpDataHolder tmpDataHolder = new TmpDataHolder(limit, offset);

        this.nextPageNewsFeedObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<Boolean>> getNextPageNewsFeedData() {
        return nextPageNewsFeedData;
    }

    public void setBlogByIdObj(String id, String cityId) {
        BlogByIdTmpDataHolder blogByIdTmpDataHolder = new BlogByIdTmpDataHolder(id, cityId);

        this.blogByIdObj.setValue(blogByIdTmpDataHolder);
    }

    public LiveData<Resource<Blog>> getBlogByIdData() {
        return blogByIdData;
    }

    class NewFeedByCityIdTmpDataHolder {

        String limit, offset,  cityId;

        private NewFeedByCityIdTmpDataHolder(String cityId, String limit, String offset) {
            this.limit = limit;
            this.offset = offset;
            this.cityId = cityId;
        }
    }

    class TmpDataHolder {

        String  limit, offset;

        public TmpDataHolder(String limit, String offset) {
            this.limit = limit;
            this.offset = offset;
        }
    }

    class BlogByIdTmpDataHolder {

        String id, cityId;

        private BlogByIdTmpDataHolder(String id, String cityId) {
            this.id = id;
            this.cityId = cityId;
        }
    }
}
