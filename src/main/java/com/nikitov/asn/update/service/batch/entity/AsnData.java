package com.nikitov.asn.update.service.batch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asn_data")
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Setter
@Getter
public class AsnData {

    @Id
    private long rangeStart;
    private long rangeEnd;
    private long asNumber;
    private String countryCode;
    private String asDescription;
}
