package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;


    /**
     * 新增菜品和口味
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        // 将DishDTO转为为Dish类
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 1.保存菜品基本信息
        dishMapper.insert(dish);

        // 获取返回的主键id字段
        Long id = dish.getId();

        // 2.保存口味信息
        // 为每个口味对象关联菜品id
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach((item)-> {
            item.setDishId(id);
        });

        // 批量插入口味
        dishFlavorMapper.insertBatch(flavors);

    }

    @Override
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        // 分页
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return Result.success(new PageResult(page.getTotal(), page.getResult()));
    }
}
