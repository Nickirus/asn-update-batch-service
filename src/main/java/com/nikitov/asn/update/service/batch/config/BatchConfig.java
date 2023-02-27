package com.nikitov.asn.update.service.batch.config;

import com.nikitov.asn.update.service.batch.dto.AsnDataFileRecord;
import com.nikitov.asn.update.service.batch.entity.AsnData;
import com.nikitov.asn.update.service.batch.listener.CustomItemWriterListener;
import com.nikitov.asn.update.service.batch.repository.AsnDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import static org.springframework.batch.item.file.transform.DelimitedLineTokenizer.DELIMITER_TAB;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig extends DefaultBatchConfiguration {

    private final PlatformTransactionManager transactionManager;
    private final AsnDataRepository repository;

    @Value("${batch.job.chunk-size}")
    private int chunkSize;

    @Value("${batch.job.classpath-tsv-resource-name}")
    private String resourceName;

    @Bean
    public Job asnDataImportJob(Step importAsnUsersStep) {
        return new JobBuilder("asnDataImportJob", super.jobRepository())
                .incrementer(new RunIdIncrementer())
                .start(importAsnUsersStep)
                .build();
    }

    @Bean
    public Step importAsnDataStep() {
        return new StepBuilder("importAsnDataStep", super.jobRepository())
                .<AsnDataFileRecord, AsnData>chunk(chunkSize, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .listener(new CustomItemWriterListener<>())
                .build();
    }

    @Bean
    public ItemReader<AsnDataFileRecord> reader() {
        return new FlatFileItemReaderBuilder<AsnDataFileRecord>()
                .name("asnDataReader")
                .resource(new ClassPathResource(resourceName))
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
}
