package com.nikitov.asn.update.service.batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final JobLauncher jobLauncher;
    private final Job job;

    @GetMapping("run")
    public void handle() throws Exception {
        var dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm");
        var now = LocalDateTime.now();
        jobLauncher.run(job, new JobParametersBuilder()
                .addString("fileHash", String.valueOf(new ClassPathResource("ip2asn-v4-u32.tsv").hashCode()))
                .addString("fileDate", dtf.format(now))
                .toJobParameters());
    }
}

