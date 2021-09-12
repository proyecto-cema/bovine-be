package com.cema.bovine.mapping.impl;

import com.cema.bovine.domain.Batch;
import com.cema.bovine.entities.CemaBatch;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.mapping.BatchMapping;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BatchMappingImpl implements BatchMapping {

    @Override
    public Batch mapEntityToDomain(CemaBatch cemaBatch) {
        Set<CemaBovine> cemaBovineList = cemaBatch.getCemaBovines();
        return Batch.builder()
                .establishmentCuig(cemaBatch.getEstablishmentCuig())
                .batchName(cemaBatch.getBatchName())
                .description(cemaBatch.getDescription())
                .bovineTags(cemaBovineList.stream().map(CemaBovine::getTag).collect(Collectors.toList()))
                .build();
    }

    @Override
    public CemaBatch mapDomainToEntity(Batch batch) {
        CemaBatch cemaBatch = new CemaBatch();
        cemaBatch.setBatchName(batch.getBatchName());
        cemaBatch.setDescription(batch.getDescription());
        cemaBatch.setEstablishmentCuig(batch.getEstablishmentCuig());
        cemaBatch.setCreationDate(new Date());
        return cemaBatch;
    }
}
