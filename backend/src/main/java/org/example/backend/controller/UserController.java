package org.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/user/all")
    public List<User> getAll() {
        return userMapper.selectList(null);
    }

    @GetMapping("/user/{userId}")
    public User getUserById(@PathVariable int userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("id", userId);
        return userMapper.selectOne(queryWrapper);
    }
    @GetMapping("/user/delete/{userId}")
    public String deleteUserById(@PathVariable int userId) {
        userMapper.deleteById(userId);
        return "success";
    }
    @PostMapping("/test")
    public String test() {
        logger.info("test");
        return "success";
    }

    @GetMapping("/test2")
    public String test2() {
        logger.info("test2");
        return "success2";
    }
}
