package com.example.mind_chain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mind_chain.entity.Node;
import com.example.mind_chain.entity.Note;
import com.example.mind_chain.service.INodeService;
import com.example.mind_chain.service.INoteService;
import com.example.mind_chain.util.BizResponse;
import com.example.mind_chain.util.ResponseCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/graph")
@CrossOrigin
public class GraphController {

    @Autowired
    private INoteService noteService;

    @Autowired
    private INodeService nodeService;

    @GetMapping("/detail")
    public BizResponse<Map<String, Object>> getGraphDetail(@RequestParam("userId") Integer userId, @RequestParam(value = "noteId", required = false) Integer noteId) {
        try {
            Note note;
            List<Node> nodeList;
            if (noteId != null) {
                // 根据笔记id查询笔记详情
                note = noteService.getById(noteId);
                QueryWrapper<Node> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("note_id", noteId);
                nodeList = nodeService.list(queryWrapper);
            } else {
                // 查询最新的笔记
                QueryWrapper<Note> noteQueryWrapper = new QueryWrapper<>();
                noteQueryWrapper.eq("user_id", userId).eq("enabled", true).orderByDesc("created_time").last("limit 1");
                note = noteService.getOne(noteQueryWrapper);
                QueryWrapper<Node> nodeQueryWrapper = new QueryWrapper<>();
                nodeQueryWrapper.eq("note_id", note.getId());
                nodeList = nodeService.list(nodeQueryWrapper);
            }
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("note", note);
            resultMap.put("nodeList", nodeList);
            return BizResponse.success(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return BizResponse.fail(ResponseCodeEnum.ERROR);
        }
    }

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
