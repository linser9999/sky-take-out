package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味
     * @param flavors
     */
    @AutoFill(value = OperationType.INSERT)
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据id删除口味
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteById(Long id);

    /**
     * 批量删除口味
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询口味信息
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> selectById(Long id);
}
