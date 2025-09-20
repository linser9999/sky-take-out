package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
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

import static com.sky.constant.MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private CategoryMapper categoryMapper;


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
        // flavors != null：确保列表对象不为 null，避免空指针异常
        // flavors.size() > 0：确保列表中至少有一个元素，避免对空列表进行不必要的操作
        // 可以简化为 if (!flavors.isEmpty())，因为 flavors 已经判断不为 null
        // 或使用 CollectionUtils.isNotEmpty(flavors)
        // if (flavors != null && flavors.size() > 0) {
        if (!flavors.isEmpty()) {
            // 向1口味表dish_flavor插入n条
            flavors.forEach((item)-> {
                item.setDishId(id);
            });
            // 批量插入口味
            dishFlavorMapper.insertBatch(flavors);
        }


    }

    @Override
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        // 分页
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return Result.success(new PageResult(page.getTotal(), page.getResult()));
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public Result deleteBatch(List<Long> ids) {
        // 从数据库中查询相关菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);

            // 1.判断每个菜品是不是起售中
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }

            // 2.判断是否有被套餐关联的菜品
            List<Category> categoryList = categoryMapper.getById(id);
            if (!categoryList.isEmpty()) {
                // categoryList 不为空说明有关联的套餐
                throw new DeletionNotAllowedException(CATEGORY_BE_RELATED_BY_SETMEAL);
            }

        }

        // 3.删除菜品基本数据
        dishMapper.deleteBatch(ids);

        // 4.删除菜品口味信息
        dishFlavorMapper.deleteBatch(ids);

        return Result.success();
    }

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        // 1.根据id查询菜品基本信息
        Dish dish = dishMapper.getById(id);

        // 2.根据id查询相关口味信息
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectById(id);

        // 3.转换为DishVo对象并返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    @Transactional
    public Result updateWithFlavor(DishDTO dishDTO) {

        // 转换为Dish对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 1.先更新菜品基本信息
        dishMapper.updateById(dish);

        // 2.删除口味有关信息
        dishFlavorMapper.deleteById(dishDTO.getId());

        // 3.获得口味信息列表
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if (!flavors.isEmpty()) {
            // 设置口味对应的dishId
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(dishDTO.getId());});

            // 4.插入口味有关信息
            dishFlavorMapper.insertBatch(flavors);
        }
        return Result.success();
    }

    /**
     * 设置禁用启用
     * @param status
     * @param id
     * @return
     */
    @Override
    public Result startOrStop(Integer status, Long id) {
        // 创建Dish对象
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.updateById(dish);

        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public Result<List<DishVO>> getByCategoryId(Long categoryId) {
        List<DishVO> list = dishMapper.getByCategoryId(categoryId);
        return Result.success(list);
    }
}
