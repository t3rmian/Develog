package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.model.domain.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@ControllerAdvice
public class RedirectController {

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        return "about";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @RequestMapping("/404")
    public ModelAndView _404(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("status", "404");
        model.addAttribute("type", "Not Found");
        model.addAttribute("message", "Requested page has not been found");
        ModelAndView modelAndView = new ModelAndView("error", model.asMap());
        if (isAjax(request)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @RequestMapping("/403")
    public ModelAndView _403(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("status", "403");
        model.addAttribute("type", "Access Denied");
        model.addAttribute("message", "No access rights for this action (" + request.getRequestURL() + ")");
        ModelAndView modelAndView = new ModelAndView("error", model.asMap());
        if (isAjax(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        return modelAndView;
    }

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public ModelAndView defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", "Exception");
        modelAndView.addObject("type", e.getLocalizedMessage() + " (" + request.getRequestURL() + ")");
        modelAndView.addObject("message", "You can contact admin if you have encountered this error");
        if (isAjax(request)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return modelAndView;
    }

    @RequestMapping("/302")
    public void _302(HttpServletResponse response) throws IOException {
        response.sendRedirect("/");
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

}
