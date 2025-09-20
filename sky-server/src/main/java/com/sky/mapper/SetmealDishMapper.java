package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 保存套餐菜品关系
     * @param list
     */
    @AutoFill(value = OperationType.INSERT)
    void insertBatch(List<SetmealDish> list);

    /**
     * 根据id查询套餐菜品信息
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> selectById(Long id);

    /**
     * 根据条件查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> selectBatch(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 删除套餐菜品关联表有关数据
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void delete(Long id);

    /**
     * 根据id删除套餐菜品关联信息
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}
