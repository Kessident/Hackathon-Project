package com.example.Hackathon.controller;

import com.example.Hackathon.exceptions.NotFoundException;
import com.example.Hackathon.model.Event;
import com.example.Hackathon.model.Status;
import com.example.Hackathon.model.User;
import com.example.Hackathon.repository.EventRepo;
import com.example.Hackathon.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duhlig on 8/10/17.
 */
@RestController
@RequestMapping("api/event")
public class EventController {
    @Autowired
    EventRepo eventRepo;

    @Autowired
    UserRepo userRepo;

//    @PostConstruct
//    public void init() {
//        if (userRepo.count() == 0){
//        User myUser = new User("newEmail", "firstName", "lastName", "password", null);
//        userRepo.save(myUser);
//
//        myUser = userRepo.findByEmail("email");
//        List<User> created = new ArrayList<>();
//        created.add(myUser);
//
//        Event newEvent = new Event("name", "description", "Some date", Status.NEW, "location", created, created);
//        eventRepo.save(newEvent);
//        }
//    }

    @GetMapping("/all")
    @CrossOrigin
    public List<Event> viewEvents() {
        ArrayList<Event> allEvents = new ArrayList<>();
        eventRepo.findAll().forEach(allEvents::add);

        return allEvents;
    }

    @GetMapping("/{eventId}")
    @CrossOrigin
    public Event getSingleEvent(@PathVariable int eventId) {
        Event selectedEvent = eventRepo.findOne(eventId);

        if (selectedEvent == null) {
            throw new NotFoundException("Event not found with supplied id");
        }

        return selectedEvent;
    }

    @PostMapping("/{eventId}/rsvp")
    @CrossOrigin
    public String rsvp(@PathVariable int eventId, HttpSession session) {
        Event selectedEvent = eventRepo.findOne(eventId);
        if (selectedEvent == null) {
            throw new NotFoundException("Event not found with supplied id");
        }

        List<User> attendingList = selectedEvent.getAttending();
        User currentUser = userRepo.findOne((Integer) session.getAttribute("userId"));

        if (attendingList.contains(currentUser)) {
            attendingList.remove(currentUser);
            eventRepo.save(selectedEvent);
            return "User no longer going to event";
        } else {
            attendingList.add(currentUser);
            eventRepo.save(selectedEvent);
            return "user is now attending this event";
        }
    }

    @PostMapping("/create")
    @CrossOrigin
    public String createNewEvent(@RequestBody Event newEvent, HttpSession session) {
        User createdBy = userRepo.findOne((Integer) session.getAttribute("userId"));
        List<User> createdByList = new ArrayList<>();
        createdByList.add(createdBy);

        newEvent.setStatus(Status.NEW);
        newEvent.setCreatedBy(createdByList);

        try {
            eventRepo.save(newEvent);
        } catch (Exception ex) {
            return "Error creating event";
        }

        return "Event created successfully";
    }
}
