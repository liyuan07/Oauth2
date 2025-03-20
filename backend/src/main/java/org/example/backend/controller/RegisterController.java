package org.example.backend.controller;

import org.example.backend.service.user.account.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class.getName());
    @PostMapping("/user/account/register")
    public Map<String,String> register(@RequestBody Map<String , String> map) {
        logger.info("revieve register");
        return registerService.register(map.get("username"), map.get("password") , map.get("confirmPassword"));
    }
}
