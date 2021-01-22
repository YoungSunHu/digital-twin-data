package com.gs.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ：YoungSun
 * @date ：Created in 2021/1/17 15:14
 * @modified By：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("opc_item")
public class OPCItemEntity implements Serializable {

    @TableId("id")
    private Long id;

    @TableField("item_id")
    private String itemId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

}
