package io.github.t3r1jj.develog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class RedirectController {

    @RequestMapping("/")
    public void redirectRootToNote(HttpServletResponse response) throws IOException {
        response.sendRedirect("/note");
    }

}
