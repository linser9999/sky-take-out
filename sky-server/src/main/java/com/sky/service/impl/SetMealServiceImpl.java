package com.sky.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.NotFoundException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.sky.constant.MessageConstant.NOT_FOUND_SETMEAL;
import static com.sky.constant.MessageConstant.SETMEAL_ON_SALE;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String key = "dish_";

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {

        // 1.查询套餐基本信息
         Setmeal setmeal = setmealMapper.selectById(id);

        // 2.查询套餐相关菜品信息
        List<SetmealDish> list = setmealDishMapper.selectById(id);

        if (list.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_SETMEAL);
        }

        // 创建对象并返回
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(list);
        return setmealVO;
    }

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @Override
    @Transactional
    public Result saveWithSetmealDish(SetmealDTO setmealDTO) {

        // 转为Setmeal对象
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);

        // 1.保存套餐基本信息
        setmealMapper.insert(setmeal);

        // 获取id
        Long id = setmeal.getId();

        // 2.保存套餐菜品关系
        // 取出数据
        List<SetmealDish> list = new ArrayList<>(setmealDTO.getSetmealDishes());


        // 判断是否为空
        if (!list.isEmpty()) {

            // 设置每个对应的setmeal_id
            list.forEach(item -> {item.setSetmealId(id);});

            // 保存套餐菜品关系
            setmealDishMapper.insertBatch(list);
        }
        return Result.success();
    }

    @Override
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 1.分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        // 2.设置查询条件
        Page<SetmealVO> setmeals = setmealDishMapper.selectBatch(setmealPageQueryDTO);

        // 3.打包成PageResult对象
        return Result.success(new PageResult(setmeals.getTotal(), setmeals.getResult()));
    }

    @Override
    @Transactional
    public Result updateWithSetmealDish(SetmealDTO setmealDTO) {

        // 转为Setmeal对象
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 1.修改套餐基本信息
        setmealMapper.update(setmeal);

        // 2.删除套餐菜品关联表有关数据
        setmealDishMapper.delete(setmealDTO.getId());

        List<SetmealDish> list = setmealDTO.getSetmealDishes();
        list.forEach(li -> {li.setSetmealId(setmealDTO.getId());});

        // 3.添加新套餐菜品关联数据
        setmealDishMapper.insertBatch(list);
        return Result.success();
    }

    /**
     * 根据id删除套餐
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public Result deleteByIds(List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.selectById(id);
            if (setmeal.getStatus() != null && setmeal.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(SETMEAL_ON_SALE);
            }
        }

        // 1.根据id删除套餐基本信息
        setmealMapper.deleteBatch(ids);

        // 2.根据id删除套餐菜品关联信息
        setmealDishMapper.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 设置套餐起售情况
     * @param status
     * @param id
     * @return
     */
    @Override
    public Result startOrStop(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);

        setmealMapper.update(setmeal);
        return Result.success();
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        // 查缓存
        String s = stringRedisTemplate.opsForValue().get(key + setmeal.getCategoryId());
        if (s != null) {
            JSONUtil.parseArray(s);
            List<Setmeal> list = JSONUtil.toList(s, Setmeal.class);
            return list;
        }
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
