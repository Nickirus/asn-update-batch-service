package com.nikitov.asn.update.service.batch.config;

import com.nikitov.asn.update.service.batch.dto.AsnDataFileRecord;
import com.nikitov.asn.update.service.batch.entity.AsnData;
import com.nikitov.asn.update.service.batch.listener.CustomItemWriterListener;
import com.nikitov.asn.update.service.batch.repository.AsnDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import static org.springframework.batch.item.file.transform.DelimitedLineTokenizer.DELIMITER_TAB;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class BatchConfig extends DefaultBatchConfiguration {

    private final PlatformTransactionManager transactionManager;
    private final AsnDataRepository repository;

    @Value("${batch.job.chunk-size}")
    private int chunkSize;

    @Value("${batch.job.remote-resource-url}")
    private String url;

    @Bean
    public Job asnDataImportJob(Step importAsnUsersStep) {
        return new JobBuilder("asnDataImportJob", super.jobRepository())
                .incrementer(new RunIdIncrementer())
                .start(importAsnUsersStep)
                .build();
    }

    @Bean
    public Step importAsnDataStep(FlatFileItemReader<AsnDataFileRecord> reader) {
        return new StepBuilder("importAsnDataStep", super.jobRepository())
                .<AsnDataFileRecord, AsnData>chunk(chunkSize, transactionManager)
                .reader(reader())
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        getActualTsvResource().ifPresentOrElse(reader::setResource,
                                () -> stepExecution.getJobExecution().setStatus(BatchStatus.FAILED));
                    }
                })
                .processor(processor())
                .writer(writer())
                .listener(new CustomItemWriterListener<>())
                .build();
    }

    @Bean
    public FlatFileItemReader<AsnDataFileRecord> reader() {
        return new FlatFileItemReaderBuilder<AsnDataFileRecord>()
                .name("asnDataReader")
                // TODO: 01.03.2023 mb we can use just a mock resource for the first init before the start
                .resource(new InputStreamResource(new ByteArrayInputStream(new byte[]{})))
                .delimited()
                .delimiter(DELIMITER_TAB)
                .includedFields(0, 1, 2, 3, 4)
                .names("rangeStart", "rangeEnd", "asNumber", "countryCode", "asDescription")
                .targetType(AsnDataFileRecord.class)
                .build();
    }

    @Bean
    public ItemProcessor<AsnDataFileRecord, AsnData> processor() {
        return item -> {
            var asnData = new AsnData();
            asnData.setRangeStart(item.rangeStart());
            asnData.setRangeEnd(item.rangeEnd());
            asnData.setAsNumber(item.asNumber());
            asnData.setCountryCode(item.countryCode());
            asnData.setAsDescription(item.asDescription());
            return asnData;
        };
    }

    @Bean
    public ItemWriter<AsnData> writer() {
        return repository::saveAll;
    }

    private Optional<InputStreamResource> getActualTsvResource() {
        byte[] buffer;
        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
            var gis = new GZIPInputStream(Channels.newInputStream(readableByteChannel));
            buffer = gis.readAllBytes();
            gis.close();
        } catch (IOException e) {
            log.error("Error while reading remote resource. {} - {}", e.getClass().getName(), e.getMessage());
            return Optional.empty();
        }
        return Optional.of(new InputStreamResource(new ByteArrayInputStream(buffer)));
    }
}
