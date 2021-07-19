package com.cema.bovine.repositories;

import com.cema.bovine.entities.CemaBovine;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BovineRepository extends CrudRepository<CemaBovine, Long> {

    CemaBovine findCemaBovineByTag(String tag);
}
