package org.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        logger.info("getUserById");
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("id", userId);
        return userMapper.selectOne(queryWrapper);
    }
    @GetMapping("/user/delete/{userId}")
    public String deleteUserById(@PathVariable int userId) {
        userMapper.deleteById(userId);
        return "success";
    }
    @PostMapping("/testjson")
    public String testJson(@RequestBody User user) {
        logger.info("testJson");
        logger.info(user.toString());
        return "testJson success";
    }

    @PostMapping("/testxxx")
    public Map<String,String> testxxx(@RequestParam Map<String,String> map) {
        logger.info("testxxx");
        logger.info(map.toString());
        HashMap<String, String> mp = new HashMap<>();
        mp.put("error_message" , "success");
        return mp;
    }

}
