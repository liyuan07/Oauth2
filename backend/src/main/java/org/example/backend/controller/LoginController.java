package org.example.backend.controller;

import org.example.backend.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.logging.Logger;

@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    @PostMapping("/user/account/token")
    public Map<String , String> getToken(@RequestParam Map<String, String> params){
        String username = params.get("username");
        String password = params.get("password");
        return loginService.getToken(username, password);
    }
}
