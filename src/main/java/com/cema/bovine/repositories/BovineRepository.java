package com.cema.bovine.repositories;

import com.cema.bovine.entities.CemaBovine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BovineRepository extends JpaRepository<CemaBovine, Long> {

    CemaBovine findCemaBovineByTag(String tag);
}
