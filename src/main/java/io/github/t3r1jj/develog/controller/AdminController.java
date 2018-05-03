package io.github.t3r1jj.develog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.model.domain.BusinessRoles;
import io.github.t3r1jj.develog.model.monitor.Call;
import io.github.t3r1jj.develog.model.monitor.Error;
import io.github.t3r1jj.develog.repository.monitoring.CallRepository;
import io.github.t3r1jj.develog.repository.monitoring.ErrorRepository;
import io.github.t3r1jj.develog.repository.monitoring.EventRepository;
import io.github.t3r1jj.develog.service.MonitoringService;
import io.github.t3r1jj.develog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.trace.TraceEndpoint;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@BusinessRoles(values = User.Role.ADMIN)
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
    @Autowired
    private MonitoringService monitoringService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageSource messageSource;

    @RequestMapping("admin")
    public String getPage(Model model) {
        return "admin";
    }

    @RequestMapping("admin/users")
    public ModelAndView getUsersFragment(Model model) {
        model.addAttribute("users", monitoringService.findAllUsersDataSize(userService.getAllUsers()));
        return new ModelAndView("fragments/users", model.asMap());
    }

    @RequestMapping("admin/events")
    @ResponseBody
    public ModelAndView getEventsFragment(Model model) {
        model.addAttribute("events", eventRepository.findAllByOrderByTimeAsc());
        return new ModelAndView("fragments/events", model.asMap());
    }

    @RequestMapping("admin/errors")
    @ResponseBody
    public List<Error> getErrorsFragment(Model model) {
        return errorRepository.findAll();
    }

    @RequestMapping("admin/logs")
    @ResponseBody
    public List<Map<String, String>> getLogsFragment(Model model, Locale locale) {
        String callName = messageSource.getMessage("callName", new Object[]{}, locale);
        String averageCallTime = messageSource.getMessage("averageCallTime", new Object[]{}, locale);
        String totalCallCount = messageSource.getMessage("totalCallCount", new Object[]{}, locale);
        String since = messageSource.getMessage("since", new Object[]{}, locale);
        return prepareCallLogs(callName, averageCallTime, totalCallCount, since);
    }

    List<Map<String, String>> prepareCallLogs(String callNameLabel, String averageCallTimeLabel, String totalCallCountLabel, String sinceLabel) {
        List<Map<String, String>> output = new ArrayList<>();
        callRepository.findAll().stream()
                .collect(Collectors.groupingBy(Call::getName))
                .values()
                .stream()
                .map(list -> new Call(list.get(0).getName(),
                        list.stream().mapToInt(Call::getCallCount).sum(),
                        list.stream().mapToLong(Call::getAccumulatedCallTime).sum(),
                        list.stream().mapToLong(Call::getLogTime).min().orElse(0)
                ))
                .sorted(Comparator.comparing(Call::getCallTime)
                        .reversed())
                .forEach(call -> {
                    Map<String, String> row = new HashMap<>();
                    row.put(callNameLabel, call.getName());
                    row.put(averageCallTimeLabel, String.valueOf(call.getCallTime()) + " ms");
                    row.put(totalCallCountLabel, String.valueOf(call.getCallCount()));
                    row.put(sinceLabel, LocalDateTime.ofEpochSecond(call.getLogTime() / 1000, 0, ZoneOffset.UTC).toString());
                    output.add(row);
                });
        return output;
    }

    @RequestMapping("admin/health")
    @ResponseBody
    public String getHealthFragment(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> dbDetails = new HashMap<>(healthEndpoint.health().getDetails());
        dbDetails.put("Mongo DB size", monitoringService.getMongoDbSize() + " MB");
        dbDetails.put("Users data size", monitoringService.getUsersDataSize() + " MB");
        dbDetails.put("Users notes size", monitoringService.getNotesDataSize() + " MB");
        return objectMapper.writeValueAsString(Arrays.asList(
                dbDetails,
                traceEndpoint.traces().getTraces(),
                infoEndpoint.info())
        );
    }

    @RequestMapping("admin/reset")
    @ResponseBody
    public boolean clearLogs() {
        callRepository.deleteAll();
        eventRepository.deleteAll();
        errorRepository.deleteAll();
        return true;
    }

    @RequestMapping("admin/user")
    @ResponseBody
    public boolean changeRole(@RequestParam Long id, @RequestParam User.Role role) {
        return userService.changeRole(id, role);
    }

    @RequestMapping("admin/email")
    @ResponseBody
    public String getEmail(@RequestParam Long userId) {
        return userService.getUser(userId).map(User::getEmail).orElse("");
    }

    @RequestMapping("admin/email/all")
    @ResponseBody
    public String getEmails() {
        return userService.getUserEmails().stream().collect(Collectors.joining(","));
    }

}
