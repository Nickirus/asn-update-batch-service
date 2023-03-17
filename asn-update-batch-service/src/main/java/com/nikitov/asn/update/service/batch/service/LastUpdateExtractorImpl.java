package com.nikitov.asn.update.service.batch.service;

import com.nikitov.asn.update.service.batch.exception.HeaderParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastUpdateExtractorImpl implements LastUpdateExtractor {

    public static final String INPUT_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    private final RestTemplate restTemplate;

    @Override
    public Optional<Date> getResourceLastUpdatedHeaderValue(URI resourceLocation) {
        HttpHeaders httpHeaders = restTemplate.headForHeaders(resourceLocation);
        Optional<List<String>> lastUpdatedValueListBox = Optional.ofNullable(httpHeaders.get(HttpHeaders.LAST_MODIFIED));
        List<String> headerValues = lastUpdatedValueListBox.orElse(new ArrayList<>());
        return headerValues.stream().findFirst().map(s -> {
            try {
                var format = new SimpleDateFormat(INPUT_DATE_PATTERN, Locale.US);
                return format.parse(s);
            } catch (ParseException e) {
                log.error("Error while parsing date \"{}\" by pattern \"{}\"", s, INPUT_DATE_PATTERN);
                throw new HeaderParseException(e);
            }
        });
    }
}
