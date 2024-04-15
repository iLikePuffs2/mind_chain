package com.example.mind_chain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 模板表
 * </p>
 *
 * @author 许志龙
 * @since 2024-04-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Template implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 模板所属用户的外键
     */
    private Integer userId;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 更新时间
     */
    private String updatedTime;


}
