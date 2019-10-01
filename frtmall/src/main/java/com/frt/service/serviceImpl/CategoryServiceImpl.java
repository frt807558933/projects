package com.frt.service.serviceImpl;

import com.frt.common.ServerResponse;
import com.frt.dao.CategoryMapper;
import com.frt.pojo.Category;
import com.frt.service.ICategoryService;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("categoryServiceImpl")
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    //添加分类
    @Override
    public ServerResponse<Category> addCategory(String categoryName, Integer parentId) {
        //调用dao
        Category category=new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        int count = categoryMapper.insert(category);
        if(count==0){
             return ServerResponse.createError("创建失败");
        }
        return ServerResponse.createSuccess("添加成功！"+category);
    }
   public ServerResponse<Category> setCategoryName(String newCategoryName,Integer id){
       Category category=new Category();
       category.setName(newCategoryName);
       category.setId(id);
       int count = categoryMapper.updateByPrimaryKeySelective(category);
       if(count==0){
            return  ServerResponse.createError("更新失败！");
       }
       return ServerResponse.createSuccess("更新成功！",category);
    }
//获取这个节点下一级的所有节点
    public  ServerResponse<List<Category>> getAllCategory(Integer  parent_id){
        List<Category> list = categoryMapper.getAllCategory( parent_id);
          if(!CollectionUtils.isEmpty(list)){
              return ServerResponse.createSuccess("查询成功！",list);
            }
       return ServerResponse.createError("查询失败！");
    }

 //获取这个节点及节点下所有级的所有节点
      public  ServerResponse<Set<Category>> getDeepAll(Integer  id){
        //初始化set
          Set<Category> set= Sets.newHashSet();
        getDeepCatetory(set,id);
     return ServerResponse.createSuccess("查询成功！",set);

    }
    //被递归调用的方法，获得当前节点及当前节点下的所有节点
        private Set<Category> getDeepCatetory(Set<Category> set,Integer  id) {
        //获取此节点
        Category category = categoryMapper.selectByPrimaryKey(id);
        if (category != null) {
            //添加此类到集合
            set.add(category);
        }
        //获取当前节点的所有子节点的分类的集合
        List<Category> list = categoryMapper.getAllCategory(id);
        //遍历它，得到每一个节点代表的分类
        for (Category category1 : list) {
            //递归调用，以子节点做父节点
            System.out.println(category1.getName());
            getDeepCatetory(set,category1.getId());
        }
        return  set;
    }

}
