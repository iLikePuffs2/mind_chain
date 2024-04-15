package com.example.mind_chain.service.impl;

import com.example.mind_chain.entity.Note;
import com.example.mind_chain.mapper.NoteMapper;
import com.example.mind_chain.service.INoteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 笔记表 服务实现类
 * </p>
 *
 * @author 许志龙
 * @since 2024-04-15
 */
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements INoteService {

}
