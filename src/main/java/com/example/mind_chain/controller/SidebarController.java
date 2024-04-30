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
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 笔记表 前端控制器
 * </p>
 *
 * @author 许志龙
 * @since 2024-04-15
 */
@RestController
@RequestMapping("/sidebar")
@CrossOrigin
public class SidebarController {

    @Autowired
    private INoteService noteService;

    @Autowired
    private INodeService nodeService;

    /**
     * 查询笔记列表
     *
     * @param userId 用户id
     * @return 笔记列表
     */
    @GetMapping("/list")
    public BizResponse<List<Note>> getNoteList(@RequestParam("userId") Integer userId) {
        try {
            QueryWrapper<Note> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("enabled", 1).orderByDesc("created_time");
            List<Note> noteList = noteService.list(queryWrapper);
            return BizResponse.success(noteList);
        } catch (Exception e) {
            e.printStackTrace();
            return BizResponse.fail(ResponseCodeEnum.ERROR);
        }
    }

    /**
     * 新增笔记
     *
     * @param userId 用户id
     * @param name   笔记名称
     * @return 新增结果
     */
    @PostMapping("/add")
    public BizResponse<String> addNote(@RequestParam("userId") Integer userId, @RequestParam("name") String name) {
        try {
            QueryWrapper<Note> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("name", name);
            if (noteService.count(queryWrapper) > 0) {
                return BizResponse.fail(ResponseCodeEnum.FAIL.getCode(), "该笔记已存在");
            }
            Note note = new Note();
            note.setUserId(userId);
            note.setName(name);
            note.setEnabled(1);
            note.setCreatedTime(LocalDateTime.now().toString());
            noteService.save(note);
            return BizResponse.success("新增笔记成功");
        } catch (Exception e) {
            e.printStackTrace();
            return BizResponse.fail(ResponseCodeEnum.ERROR);
        }
    }

    /**
     * 删除笔记
     *
     * @param userId 用户id
     * @param name   笔记名称
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public BizResponse<String> deleteNote(@RequestParam("userId") Integer userId, @RequestParam("name") String name) {
        try {
            QueryWrapper<Note> noteQueryWrapper = new QueryWrapper<>();
            noteQueryWrapper.eq("user_id", userId).eq("name", name);
            List<Note> noteList = noteService.list(noteQueryWrapper);
            List<Integer> noteIdList = noteList.stream().map(Note::getId).collect(Collectors.toList());
            if (!noteIdList.isEmpty()) {
                // 删除所有对应的节点
                QueryWrapper<Node> nodeQueryWrapper = new QueryWrapper<>();
                nodeQueryWrapper.in("note_id", noteIdList);
                nodeService.remove(nodeQueryWrapper);

                // 删除所有对应的笔记
                noteService.removeByIds(noteIdList);
            }
            return BizResponse.success("删除笔记成功");
        } catch (Exception e) {
            e.printStackTrace();
            return BizResponse.fail(ResponseCodeEnum.ERROR);
        }
    }

    /**
     * 查询笔记详情
     *
     * @param noteId 笔记id
     * @return 笔记详情
     */
    @GetMapping("/detail")
    public BizResponse<List<Node>> getNoteDetail(@RequestParam("noteId") Integer noteId) {
        try {
            QueryWrapper<Node> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("note_id", noteId);
            List<Node> nodeList = nodeService.list(queryWrapper);
            return BizResponse.success(nodeList);
        } catch (Exception e) {
            e.printStackTrace();
            return BizResponse.fail(ResponseCodeEnum.ERROR);
        }
    }

    /**
     * 笔记重命名
     *
     * @param userId  用户id
     * @param oldName 旧笔记名称
     * @param newName 新笔记名称
     * @return 重命名结果
     */
    @PutMapping("/rename")
    public BizResponse<String> renameNote(@RequestParam("userId") Integer userId, @RequestParam("oldName") String oldName, @RequestParam("newName") String newName) {
        try {
            // 检查新笔记名称是否已存在
            QueryWrapper<Note> checkQueryWrapper = new QueryWrapper<>();
            checkQueryWrapper.eq("user_id", userId).eq("name", newName);
            if (noteService.count(checkQueryWrapper) > 0) {
                return BizResponse.fail(ResponseCodeEnum.FAIL.getCode(), "笔记已存在");
            }

            // 查找与旧笔记名称相同的笔记
            QueryWrapper<Note> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("name", oldName);
            List<Note> noteList = noteService.list(queryWrapper);

            if (!noteList.isEmpty()) {
                // 更新笔记名称
                noteList.forEach(note -> note.setName(newName));
                noteService.updateBatchById(noteList);
                return BizResponse.success("笔记重命名成功");
            } else {
                return BizResponse.fail(ResponseCodeEnum.FAIL.getCode(), "未找到指定笔记");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BizResponse.fail(ResponseCodeEnum.ERROR);
        }
    }


}