package com.nikitov.asn.update.service.batch.dto;

public record AsnData(long rangeStart, long rangeEnd, long asNumber, String countryCode, String asDescription) {
}
