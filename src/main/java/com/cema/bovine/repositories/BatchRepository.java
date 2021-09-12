package com.cema.bovine.repositories;

import com.cema.bovine.entities.CemaBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BatchRepository extends JpaRepository<CemaBatch, Long> {

    CemaBatch findCemaBatchByBatchNameAndEstablishmentCuigIgnoreCase(String batchName, String cuig);

    List<CemaBatch> findAllByEstablishmentCuig(String cuig);

}
