package com.example.mind_chain.service.impl;

import com.example.mind_chain.entity.Node;
import com.example.mind_chain.mapper.NodeMapper;
import com.example.mind_chain.service.INodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 节点表 服务实现类
 * </p>
 *
 * @author 许志龙
 * @since 2024-04-15
 */
@Service
public class NodeServiceImpl extends ServiceImpl<NodeMapper, Node> implements INodeService {

}
