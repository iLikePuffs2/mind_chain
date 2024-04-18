package com.example.mind_chain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 节点表
 * </p>
 *
 * @author 许志龙
 * @since 2024-04-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Node implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 节点所属笔记的外键
     */
    private Integer noteId;

    /**
     * 节点所属模板的外键
     */
    private Integer templateId;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点层级
     */
    private Integer level;

    /**
     * 节点上下文
     */
    private String context;

    /**
     * 父节点的id(一个节点可能有多个父节点id，所以用英文逗号分割)
     */
    private String parentId;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 截止时间
     */
    private String deadline;

    /**
     * 状态(1 可执行,2 被阻塞,3 已完成)
     */
    private Integer status;

    /**
     * 阻塞原因(0 未被阻塞,1 全部的直接子节点被阻塞,2 同级的前置节点未完成,3 事件阻塞,4 时间阻塞)
     */
    private Integer blockedReason;


}
