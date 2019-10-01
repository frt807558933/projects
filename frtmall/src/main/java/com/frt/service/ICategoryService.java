package com.frt.service;

import com.frt.common.ServerResponse;
import com.frt.pojo.Category;

import java.util.List;
import java.util.Set;

public interface ICategoryService {
    ServerResponse<Category> addCategory(String categoryName,Integer parentId);
    ServerResponse<Category> setCategoryName(String newCategoryName,Integer id);
    ServerResponse<List<Category>> getAllCategory(Integer  parent_id);
    ServerResponse<Set<Category>> getDeepAll(Integer  id);
}
