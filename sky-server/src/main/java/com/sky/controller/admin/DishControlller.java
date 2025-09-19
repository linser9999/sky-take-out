package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api("菜品管理相关接口")
public class DishControlller {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result sava(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询：{}", dishPageQueryDTO);
        return dishService.pageQuery(dishPageQueryDTO);
    }


    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除菜品")
    public Result deleteBatch(@RequestParam List<Long> ids) {
        log.info("删除菜品：{}", ids);
        return dishService.deleteBatch(ids);
    }

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询菜品信息")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id{}查询菜品信息", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品信息
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改菜品信息")
    public Result updateWithFlavor(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品信息：{}", dishDTO);
        return dishService.updateWithFlavor(dishDTO);
    }

    /**
     * 设置禁用启用
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "设置禁用启用")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("设置菜品id{}禁用启用情况：{}", id, status);
        return dishService.startOrStop(status, id);
    }
}
