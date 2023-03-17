package com.nikitov.asn.update.service.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

@Slf4j
public class CustomItemWriterListener<T> implements ItemWriteListener<T> {

    @Override
    public void afterWrite(Chunk<? extends T> items) {
        log.info("  Chunk has written: {}", items.size());
    }
}
