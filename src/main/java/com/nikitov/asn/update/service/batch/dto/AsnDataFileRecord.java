package com.nikitov.asn.update.service.batch.dto;

public record AsnDataFileRecord(Long id, long rangeStart, long rangeEnd, long asNumber, String countryCode, String asDescription) {
}
