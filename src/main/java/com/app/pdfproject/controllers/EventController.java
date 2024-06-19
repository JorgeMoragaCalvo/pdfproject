package com.app.pdfproject.controllers;

import com.app.pdfproject.entities.EventEntity;
import com.app.pdfproject.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public EventEntity createEvent(@RequestBody EventEntity eventEntity){
        return eventService.create(eventEntity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventEntity> eventById(@PathVariable("id") Long id){
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable("id") Long id){
        byte[] pdfContents = eventService.generateEventPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "event.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContents);
    }
}
