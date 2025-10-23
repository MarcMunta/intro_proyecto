package com.intermodular.intro_backend.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intermodular.intro_backend.Nurse;
import com.intermodular.intro_backend.repository.NurseRepository;

@Component
public class NurseDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(NurseDataInitializer.class);
    private static final String NURSE_RESOURCE = "data/nurse.json";

    private final NurseRepository nurseRepository;
    private final ObjectMapper objectMapper;

    public NurseDataInitializer(NurseRepository nurseRepository, ObjectMapper objectMapper) {
        this.nurseRepository = nurseRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        try {
            if (nurseRepository.count() > 0) {
                return; // Ya hay datos
            }

            Resource resource = new ClassPathResource(NURSE_RESOURCE);
            if (!resource.exists()) {
                log.warn("Resource {} not found; skipping nurse bootstrap.", NURSE_RESOURCE);
                return;
            }

            try (InputStream inputStream = resource.getInputStream()) {
                List<Nurse> nurses = objectMapper.readValue(inputStream, new TypeReference<List<Nurse>>(){});
                for (Nurse n : nurses) {
                    n.setId(null);
                }
                nurseRepository.saveAll(nurses);
                log.info("Imported {} nurses from {}", nurses.size(), NURSE_RESOURCE);
            }
        } catch (IOException e) {
            log.error("Failed to import nurses from {}", NURSE_RESOURCE, e);
        }
    }
}
