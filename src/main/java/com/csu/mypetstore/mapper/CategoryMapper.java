package com.csu.mypetstore.mapper;

import com.csu.mypetstore.domain.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface CategoryMapper {

    List<Category> getCategoryList();

    Category getCategory(String categoryId);

}