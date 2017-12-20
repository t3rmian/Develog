package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.model.domain.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import java.util.Locale;

@Controller
@ControllerAdvice
public class RedirectController {

    private final MessageSource messageSource;

    @Autowired
    public RedirectController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

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
    public ModelAndView _404(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) {
        String type = messageSource.getMessage("404_type", new Object[]{}, locale);
        String message = messageSource.getMessage("404_message", new Object[]{}, locale);
        model.addAttribute("status", "404");
        model.addAttribute("type", type);
        model.addAttribute("message", message);
        ModelAndView modelAndView = new ModelAndView("error", model.asMap());
        if (isAjax(request)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @RequestMapping("/403")
    public ModelAndView _403(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) {
        String type = messageSource.getMessage("403_type", new Object[]{}, locale);
        String message = messageSource.getMessage("403_message", new Object[]{}, locale);
        model.addAttribute("status", "403");
        model.addAttribute("type", type);
        model.addAttribute("message", message + " (" + request.getRequestURL() + ")");
        ModelAndView modelAndView = new ModelAndView("error", model.asMap());
        if (isAjax(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        return modelAndView;
    }

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public ModelAndView defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Locale locale, Exception e) {
        String status = messageSource.getMessage("unexpectedException", new Object[]{}, locale);
        String message = messageSource.getMessage("unexpectedExceptionMessage", new Object[]{}, locale);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", status);
        modelAndView.addObject("type", e.getLocalizedMessage() + " (" + request.getRequestURL() + ")");
        modelAndView.addObject("message", message);
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
