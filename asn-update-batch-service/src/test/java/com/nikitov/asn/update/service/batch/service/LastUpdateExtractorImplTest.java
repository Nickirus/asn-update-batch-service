package com.nikitov.asn.update.service.batch.service;

import com.nikitov.asn.update.service.batch.exception.HeaderParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LastUpdateExtractorImplTest {

    private static final String TEST_URI = "https://test.com";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LastUpdateExtractorImpl extractor;

    @Test
    @DisplayName("It must translate response last-modified header to valid format")
    void getResourceLastUpdatedHeaderValueTransformToValidFormatTest() {
        var httpHeaders = HttpHeaders.readOnlyHttpHeaders(new MultiValueMapAdapter<>(
                Map.of(HttpHeaders.LAST_MODIFIED, List.of("Sun, 12 Mar 2023 18:59:22 GMT"))));
        when(restTemplate.headForHeaders(any(URI.class))).thenReturn(httpHeaders);
        assertEquals("Sun Mar 12 22:59:22 GET 2023", extractor.getResourceLastUpdatedHeaderValue(URI.create(TEST_URI)).get().toString());
    }

    @Test
    @DisplayName("It must return Optional.empty() if response doesn't contain target header")
    void getResourceLastUpdatedHeaderValueEmptyAnswerTest() {
        var httpHeaders = HttpHeaders.readOnlyHttpHeaders(new MultiValueMapAdapter<>(Collections.emptyMap()));
        when(restTemplate.headForHeaders(any(URI.class))).thenReturn(httpHeaders);
        assertEquals(Optional.empty(), extractor.getResourceLastUpdatedHeaderValue(URI.create(TEST_URI)));
    }

    @Test
    @DisplayName("It must throw HeaderParseException if it can't parse date")
    void getResourceLastUpdatedHeaderValueUnreadableDateFormatTest() {
        var httpHeaders = HttpHeaders.readOnlyHttpHeaders(new MultiValueMapAdapter<>(
                Map.of(HttpHeaders.LAST_MODIFIED, List.of("Unreadable Date Format"))));
        when(restTemplate.headForHeaders(any(URI.class))).thenReturn(httpHeaders);
        assertThrows(HeaderParseException.class, () -> extractor.getResourceLastUpdatedHeaderValue(URI.create(TEST_URI)));
    }

}