package com.sky.service;

import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

public interface UserService {

    /**
     * 用户登录
     * @param code
     * @return
     */
    User wxLogin(String code);
}
