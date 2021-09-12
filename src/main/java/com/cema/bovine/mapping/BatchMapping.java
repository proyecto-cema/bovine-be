package com.cema.bovine.mapping;

import com.cema.bovine.domain.Batch;
import com.cema.bovine.entities.CemaBatch;

public interface BatchMapping {
    Batch mapEntityToDomain(CemaBatch cemaBatch);

    CemaBatch mapDomainToEntity(Batch batch);
}
