package com.nikitov.asn.update.service.batch.repository;

import com.nikitov.asn.update.service.batch.entity.AsnData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsnDataRepository extends JpaRepository<AsnData, Long> {
}
