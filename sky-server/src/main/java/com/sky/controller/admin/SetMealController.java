package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据id查询套餐");
        SetmealVO setmealVO = setMealService.getById(id);
        return Result.success(setmealVO);
    }


    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        return setMealService.saveWithSetmealDish(setmealDTO);
    }

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询：{}", setmealPageQueryDTO);
        return setMealService.pageQuery(setmealPageQueryDTO);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐");
        return setMealService.updateWithSetmealDish(setmealDTO);
    }

    /**
     * 根据id删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "根据id删除套餐")
    public Result deleteByIds(@RequestParam List<Long> ids) {
        log.info("根据id删除套餐：{}", ids);
        return setMealService.deleteByIds(ids);
    }

    /**
     * 设置套餐起售情况
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "设置套餐起售情况")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("设置套餐{}起售情况：{}", id, status);
        return setMealService.startOrStop(status, id);
    }
}
