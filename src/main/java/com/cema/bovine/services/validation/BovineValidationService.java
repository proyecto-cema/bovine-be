package com.cema.bovine.services.validation;

import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;

public interface BovineValidationService {

    void validateBovineForUsage(Bovine bovine);

    void validateBovineCreation(Bovine bovine);

    void validateBovineUpdate(Bovine bovine, CemaBovine cemaBovine);
}
