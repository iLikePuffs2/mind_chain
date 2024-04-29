package com.example.mind_chain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 笔记表
 * </p>
 *
 * @author 许志龙
 * @since 2024-04-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Note implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 笔记唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 笔记所属用户的外键
     */
    private Integer userId;

    /**
     * 笔记名称
     */
    private String name;

    /**
     * 是否启用(1启用，0禁用)
     */
    private int enabled;

    /**
     * 创建时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdTime;


}
