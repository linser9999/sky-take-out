package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class HistoryOrdersPageQueryDTO implements Serializable {
    private int page;

    private int pageSize;

    //状态 6表示：已取消   1表示：待付款
    private Integer status;
}
