package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品和口味
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    Result deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    DishVO getById(Long id);

    /**
     * 修改菜品信息
     * @param dishDTO
     * @return
     */
    Result updateWithFlavor(DishDTO dishDTO);

    /**
     * 设置禁用启用
     * @param status
     * @param id
     * @return
     */
    Result startOrStop(Integer status, Long id);
}
