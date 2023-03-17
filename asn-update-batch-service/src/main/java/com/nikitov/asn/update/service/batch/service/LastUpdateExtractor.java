package com.nikitov.asn.update.service.batch.service;

import java.net.URI;
import java.util.Date;
import java.util.Optional;

public interface LastUpdateExtractor {
    Optional<Date> getResourceLastUpdatedHeaderValue(URI resourceLocation);
}
