package com.example.mind_chain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mind_chain.entity.Node;
import com.example.mind_chain.entity.Note;
import com.example.mind_chain.service.INodeService;
import com.example.mind_chain.service.INoteService;
import com.example.mind_chain.util.BizResponse;
import com.example.mind_chain.util.ResponseCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/graph")
@CrossOrigin
public class GraphController {

    @Autowired
    private INoteService noteService;

    @Autowired
    private INodeService nodeService;

    /**
     * 查询某篇笔记的详情
     *
     * @param userId
     * @param noteId
     * @return
     */
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
     * 保存和修改笔记内容
     *
     * @param userId   用户id
     * @param noteId   笔记id
     * @param name     笔记名称
     * @param nodeList 节点列表
     * @return 结果
     */
    @PostMapping("/saveAndUpdate")
    @Transactional
    public BizResponse<String> saveAndUpdate(@RequestParam("userId") Integer userId, @RequestParam("noteId") Integer noteId,
                                             @RequestParam("name") String name, @RequestBody List<Node> nodeList) {
        try {
            // 创建新笔记
            Note newNote = new Note();
            newNote.setUserId(userId);
            newNote.setName(name);
            newNote.setEnabled(1);
            newNote.setCreatedTime(LocalDateTime.now().toString());
            noteService.save(newNote);

            // 为节点设置笔记ID,并临时存储旧的节点ID
            List<Integer> oldNodeIds = new ArrayList<>();
            for (Node node : nodeList) {
                oldNodeIds.add(node.getId());
                node.setId(null);
                node.setNoteId(newNote.getId());

                // 将blockedTime的字符串值转换为MySQL兼容的datetime格式
                if (node.getBlockedTime() != null && !node.getBlockedTime().isEmpty()) {
                    LocalDateTime blockedTime = LocalDateTime.parse(node.getBlockedTime(), DateTimeFormatter.ISO_DATE_TIME);
                    node.setBlockedTime(blockedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            }

            // 插入节点并获取新节点的ID
            nodeService.saveBatch(nodeList);
            List<Integer> newNodeIds = nodeList.stream().map(Node::getId).collect(Collectors.toList());

            // 创建旧节点ID到新节点ID的映射
            Map<Integer, Integer> nodeIdMap = new HashMap<>();
            for (int i = 0; i < nodeList.size(); i++) {
                nodeIdMap.put(oldNodeIds.get(i), newNodeIds.get(i));
            }

            // 更新节点的parentId
            for (Node node : nodeList) {
                if (node.getParentId() != null && !node.getParentId().isEmpty()) {
                    String[] parentIds = node.getParentId().split(",");
                    List<String> newParentIds = new ArrayList<>();
                    for (String parentId : parentIds) {
                        Integer newParentId = nodeIdMap.get(Integer.parseInt(parentId));
                        if (newParentId != null) {
                            newParentIds.add(newParentId.toString());
                        }
                    }
                    if (!newParentIds.isEmpty()) {
                        node.setParentId(String.join(",", newParentIds));
                    } else {
                        node.setParentId(null);
                    }
                } else {
                    node.setParentId(null);
                }
            }

            // 更新节点
            nodeService.updateBatchById(nodeList);

            // 查询所有旧笔记
            QueryWrapper<Note> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("name", name)
                    .orderByDesc("created_time");
            List<Note> oldNotes = noteService.list(queryWrapper);

            // 剔除创建时间最新的笔记
            if (!oldNotes.isEmpty()) {
                oldNotes.remove(0);
            }

            // 禁用旧笔记
            for (Note oldNote : oldNotes) {
                oldNote.setEnabled(0);
            }
            noteService.updateBatchById(oldNotes);

            return BizResponse.success("保存笔记内容成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 手动回滚事务
            return BizResponse.fail(ResponseCodeEnum.ERROR);
        }
    }}
