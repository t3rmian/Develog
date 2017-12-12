package io.github.t3r1jj.develog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import io.github.t3r1jj.develog.model.data.User;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @ResponseBody
    List<HashMap.Entry<User, Long>> getUsersFragment(Model model) {
        return userService.findAllUsersDataSize();
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
        Map<String, Object> dbDetails = new HashMap<>(healthEndpoint.health().getDetails());
        dbDetails.put("mongoDbSize", getMongoDbSize());
        dbDetails.put("postgresDbSize", userService.getUsersDataSize());
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

}
