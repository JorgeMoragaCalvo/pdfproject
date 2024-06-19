package com.app.pdfproject.services;

import com.app.pdfproject.entities.EventEntity;
import com.app.pdfproject.repositories.EventRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.IIOException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventEntity create(EventEntity eventEntity){
        if(eventEntity.getDay() == null) eventEntity.setDay(LocalDate.now());

        return eventRepository.save(eventEntity);
    }

    public Optional<EventEntity> getEventById(Long id){
        return eventRepository.findById(id);
    }

    public byte[] generateEventPdf(Long id){
        Optional<EventEntity> optionalEvent = eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            throw new RuntimeException("Event not found");
        }

        EventEntity event = optionalEvent.get();
        ByteArrayOutputStream byteArrayOuputStream = new ByteArrayOutputStream();

        try{
            PdfWriter writer = new PdfWriter(byteArrayOuputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Armiris Events"));
            document.add(new Paragraph("Name: " + event.getName()));
            document.add(new Paragraph("Amount of people: " + event.getAmountOfPeople()));
            document.add(new Paragraph("Day: " + event.getDay().toString()));
            document.add(new Paragraph("Place: " + event.getPlace()));
            document.add(new Paragraph("Price: " + event.getPrice()));

            document.close();

            // to save PDF file in Downloads folder
            String userHome = System.getProperty("user.home");
            String downloadsDir = Paths.get(userHome, "Downloads").toString();
            String filePath = Paths.get(downloadsDir, "event_" + event.getName() + ".pdf").toString(); //id?

            try (FileOutputStream fos = new FileOutputStream(filePath)){
                fos.write(byteArrayOuputStream.toByteArray());

            }
        } catch (IOException e){
            throw new RuntimeException("Error to save or generate PDFs file");
        }

        return byteArrayOuputStream.toByteArray();
    }
}
