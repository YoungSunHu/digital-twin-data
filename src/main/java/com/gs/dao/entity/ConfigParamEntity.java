package com.gs.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ：YoungSun
 * @date ：Created in 2021/1/18 16:09
 * @modified By：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("config_param")
public class ConfigParamEntity {

    @TableId("id")
    private Long id;

    @TableField("param_type")
    private String paramType;

    @TableField("param_key")
    private String paramKey;

    @TableField("param_value")
    private String paramValue;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
