package io.github.t3r1jj.develog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import io.github.t3r1jj.develog.model.monitor.Call;
import io.github.t3r1jj.develog.model.monitor.Error;
import io.github.t3r1jj.develog.model.monitor.Event;
import io.github.t3r1jj.develog.repository.monitoring.CallRepository;
import io.github.t3r1jj.develog.repository.monitoring.ErrorRepository;
import io.github.t3r1jj.develog.repository.monitoring.EventRepository;
import io.github.t3r1jj.develog.service.UserService;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.trace.TraceEndpoint;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserService userService;

    @RequestMapping("admin")
    String getPage(Model model) {
        return "admin";
    }

    @RequestMapping("admin/users")
    ModelAndView getUsersFragment(Model model) {
        model.addAttribute("users", userService.findAllUsersDataSize());
        return new ModelAndView("fragments/users", model.asMap());
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
    List<Map<String, String>> getLogsFragment(Model model) {
        List<Map<String, String>> output = new ArrayList<>();
        callRepository.findAll().stream()
                .collect(Collectors.groupingBy(Call::getName))
                .values()
                .stream()
                .map(list -> new Call(list.get(0).getName(),
                        list.stream().mapToInt(Call::getCallCount).sum(),
                        list.stream().mapToLong(Call::getAccumulatedCallTime).sum(),
                        list.stream().mapToLong(Call::getLogTime).min().getAsLong()
                ))
                .sorted(Comparator.comparing(Call::getCallTime)
                        .reversed())
                .forEach(call -> {
                    Map<String, String> row = new HashMap<>();
                    row.put("Call name", call.getName());
                    row.put("Average call time", String.valueOf(call.getCallTime()) + " ms");
                    row.put("Total call count", String.valueOf(call.getCallCount()));
                    row.put("Since", LocalDateTime.ofEpochSecond(call.getLogTime() / 1000, 0, ZoneOffset.UTC).toString());
                    output.add(row);
                });
        return output;
    }

    @RequestMapping("admin/health")
    @ResponseBody
    String getHealthFragment(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> dbDetails = new HashMap<>(healthEndpoint.health().getDetails());
        dbDetails.put("mongoDbSize", getMongoDbSize() + " MB");
        dbDetails.put("postgresDbSize", userService.getUsersDataSize() + " MB");
        System.out.println(objectMapper.writeValueAsString(Arrays.asList(
                dbDetails,
                traceEndpoint.traces().getTraces(),
                infoEndpoint.info())
        ));
        return objectMapper.writeValueAsString(Arrays.asList(
                dbDetails,
                traceEndpoint.traces().getTraces(),
                infoEndpoint.info())
        );
    }

    private long getMongoDbSize() {
        try {
            MongoDatabase db = mongoTemplate.getDb();
            Document document = db.runCommand(new BsonDocument("dbStats", new BsonInt32(1)).append("scale", new BsonInt32(1024 * 1024)));
            return Double.valueOf(document.get("dataSize").toString()).longValue();
        } catch (Exception ex) {
            return -1;
        }
    }

    @RequestMapping("admin/reset")
    @ResponseBody
    boolean getHealthFragment() {
        callRepository.deleteAll();
        eventRepository.deleteAll();
        errorRepository.deleteAll();
        return true;
    }

}
