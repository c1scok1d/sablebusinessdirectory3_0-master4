package com.sable.businesslistingapi.ui.category.categoryfilter;

import com.sable.businesslistingapi.utils.Constants;
import com.sable.businesslistingapi.viewobject.ItemCategory;
import com.sable.businesslistingapi.viewobject.ItemSubCategory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Grouping {

    public String catId,subCatId;
    public LinkedHashMap<ItemCategory,List<ItemSubCategory>> map = new LinkedHashMap<>();

    public LinkedHashMap<ItemCategory,List<ItemSubCategory>> group(List<ItemCategory> categoryList, List<ItemSubCategory> subCategoryList)
    {
        map.clear();

        for (int i = 0;i < categoryList.size();i++) {

            List<ItemSubCategory> subCategories = new ArrayList<>();

            ItemSubCategory subCategory = new ItemSubCategory(Constants.ZERO,Constants.CATEGORY_ALL,"","","","","","","","","" +
                    "","",null,null);
            subCategories.add(0,subCategory);

            catId = categoryList.get(i).id;

            for (int j = 0;j < subCategoryList.size();j++)
            {
                subCatId = subCategoryList.get(j).catId;

                if (catId.equals(subCatId))
                {
                    subCategories.add(subCategoryList.get(j));
                }
            }

            if(!subCategories.isEmpty())
            {

                map.put(categoryList.get(i),subCategories);
            }
        }

        return map;
    }
}
