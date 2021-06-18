package com.cema.bovine.mapping;

import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;

public interface BovineMapping {
    Bovine mapEntityToDomain(CemaBovine bovine);

    CemaBovine mapDomainToEntity(Bovine bovine);
}
