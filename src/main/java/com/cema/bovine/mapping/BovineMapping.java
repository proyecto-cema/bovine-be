package com.cema.bovine.mapping;

import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;

public interface BovineMapping {
    Bovine mapEntityToDomain(CemaBovine bovine);

    CemaBovine updateEntityWithDomain(Bovine bovine);

    CemaBovine updateEntityWithDomain(Bovine mapBovine, CemaBovine entityBovine);
}
