package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/user")
    @ResponseBody
    public User getUserInfo() {
        return userService.getLoggedUser();
    }

}
