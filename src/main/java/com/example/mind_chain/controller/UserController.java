package com.example.mind_chain.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mind_chain.entity.User;
import com.example.mind_chain.service.IUserService;
import com.example.mind_chain.util.BizResponse;
import com.example.mind_chain.util.ResponseCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author 许志龙
 * @since 2024-04-15
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public BizResponse<?> register(@RequestBody User user) {
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account", user.getAccount());
            if (userService.count(queryWrapper) > 0) {
                return BizResponse.fail(ResponseCodeEnum.ACCOUNT_EXIST);
            }
            if (userService.save(user)) {
                return BizResponse.success(user.getId());
            } else {
                return BizResponse.fail(ResponseCodeEnum.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BizResponse.fail(ResponseCodeEnum.ERROR);
        }
    }

    /**
     * 用户登录
     *
     * @param user 用户信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public BizResponse<?> login(@RequestBody User user) {
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account", user.getAccount());
            queryWrapper.eq("password", user.getPassword());
            User loggedInUser = userService.getOne(queryWrapper);
            if (loggedInUser != null) {
                return BizResponse.success(loggedInUser.getId());
            } else {
                return BizResponse.fail(ResponseCodeEnum.PARAM_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BizResponse.fail(ResponseCodeEnum.ERROR);
        }
    }

}
