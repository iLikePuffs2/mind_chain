package com.example.mind_chain.controller;

import com.example.mind_chain.entity.Node;
import com.example.mind_chain.entity.Note;
import com.example.mind_chain.service.INodeService;
import com.example.mind_chain.service.INoteService;
import com.example.mind_chain.util.BizResponse;
import com.example.mind_chain.util.ResponseCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/graph")
@CrossOrigin
public class GraphController {

    @Autowired
    private INoteService noteService;

    @Autowired
    private INodeService nodeService;

    /**
     * 修改笔记内容
     *
     * @param userId   用户id
     * @param noteId   笔记id
     * @param name     笔记名称
     * @param nodeList 节点列表
     * @return 修改结果
     */
    @PutMapping("/update")
    public BizResponse<String> updateNote(@RequestParam("userId") Integer userId, @RequestParam("noteId") Integer noteId,
                                          @RequestParam("name") String name, @RequestBody List<Node> nodeList) {
        try {
            // 创建新笔记
            Note newNote = new Note();
            newNote.setUserId(userId);
            newNote.setName(name);
            newNote.setCreatedTime(LocalDateTime.now().toString());
            noteService.save(newNote);

            // 添加节点
            nodeList.forEach(node -> node.setNoteId(newNote.getId()));
            nodeService.saveBatch(nodeList);

            // 禁用旧笔记
            Note oldNote = noteService.getById(noteId);
            oldNote.setEnabled(false);
            noteService.updateById(oldNote);

            return BizResponse.success("修改笔记内容成功");
        } catch (Exception e) {
            e.printStackTrace();
            return BizResponse.fail(ResponseCodeEnum.ERROR);
        }
    }
}
