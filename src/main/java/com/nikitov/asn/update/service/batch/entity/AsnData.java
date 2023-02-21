package com.nikitov.asn.update.service.batch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "asn_data")
@Data
public class AsnData {
    @Id
    private Long id;
    private long rangeStart;
    private long rangeEnd;
    private long asNumber;
    private String countryCode;
    private String asDescription;
}
