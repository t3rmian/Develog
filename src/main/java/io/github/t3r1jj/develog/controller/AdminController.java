package io.github.t3r1jj.develog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.t3r1jj.develog.model.monitor.Call;
import io.github.t3r1jj.develog.model.monitor.Error;
import io.github.t3r1jj.develog.model.monitor.Event;
import io.github.t3r1jj.develog.repository.monitoring.CallRepository;
import io.github.t3r1jj.develog.repository.monitoring.ErrorRepository;
import io.github.t3r1jj.develog.repository.monitoring.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.trace.TraceEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private CallRepository callRepository;
    @Autowired
    private ErrorRepository errorRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private HealthEndpoint healthEndpoint;
    @Autowired
    private TraceEndpoint traceEndpoint;
    @Autowired
    private InfoEndpoint infoEndpoint;

    @RequestMapping("admin")
    String getPage(Model model) {
        return "admin";
    }

    @RequestMapping("admin/users")
    @ResponseBody
    String getUsersFragment(Model model) {
        return "users";
    }

    @RequestMapping("admin/events")
    @ResponseBody
    List<Event> getEventsFragment(Model model) {
        return eventRepository.findAll();
    }

    @RequestMapping("admin/errors")
    @ResponseBody
    List<Error> getErrorsFragment(Model model) {
        return errorRepository.findAll();
    }

    @RequestMapping("admin/logs")
    @ResponseBody
    List<Call> getLogsFragment(Model model) {
        return callRepository.findAll();
    }

    @RequestMapping("admin/health")
    @ResponseBody
    String getHealthFragment(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(Arrays.asList(
                healthEndpoint.health().getDetails(),
                traceEndpoint.traces().getTraces(),
                infoEndpoint.info())
        );
    }

}
