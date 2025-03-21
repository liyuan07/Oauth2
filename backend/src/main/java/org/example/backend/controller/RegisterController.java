package org.example.backend.controller;

import org.example.backend.pojo.User;
import org.example.backend.service.user.account.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class.getName());
    @PostMapping("/user/account/register")
    public Map<String, String> register(@RequestParam Map<String,String> map) {
        String username = map.get("username");
        String password = map.get("password");
        logger.info("username: " + username + " password: " + password);
        return registerService.register(username, password);
    }


}

