package com.nikitov.asn.update.service.batch.controller;

import com.nikitov.asn.update.service.batch.service.LastUpdateExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.sql.Date;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("asn-update")
public class Controller {

    private final JobLauncher jobLauncher;
    private final Job job;
    private final LastUpdateExtractor lastUpdateExtractor;
    @Value("${batch.job.remote-resource-url}")
    private String url;

    @GetMapping("run")
    public ResponseEntity<Void> handle() throws Exception {
        jobLauncher.run(job, new JobParametersBuilder()
                .addString("resourceDate", lastUpdateExtractor.getResourceLastUpdatedHeaderValue(URI.create(url))
                        .orElseGet(() -> Date.valueOf(LocalDate.now()))
                        .toString())
                .toJobParameters());
        return ResponseEntity.noContent().build();
    }
}

