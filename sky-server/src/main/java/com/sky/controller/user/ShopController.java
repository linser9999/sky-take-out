package com.sky.controller.user;

import com.sky.constant.RedisConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "店铺营业相关接口")
public class ShopController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询店铺营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation(value = "查询店铺营业状态")
    public Result<Integer> seleteShopStatus() {
        Integer status = Integer.valueOf(stringRedisTemplate.opsForValue().get(RedisConstant.SHOPSTATUS));
        log.info("店铺{}中", status == 1 ? "营业" : "停业");
        return Result.success(status);
    }

}
