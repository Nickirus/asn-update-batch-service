package com.nikitov.asn.update.service.batch.dto;

public record AsnDataFileRecord(long rangeStart, long rangeEnd, long asNumber, String countryCode, String asDescription) {
}
