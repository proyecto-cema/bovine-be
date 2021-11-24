package com.cema.bovine.services.database.impl;

import com.cema.bovine.domain.Batch;
import com.cema.bovine.entities.CemaBatch;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.mapping.BatchMapping;
import com.cema.bovine.repositories.BatchRepository;
import com.cema.bovine.repositories.BovineRepository;
import com.cema.bovine.services.database.DatabaseService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    private final BovineRepository bovineRepository;
    private final BatchMapping batchMapping;
    private final BatchRepository batchRepository;

    public DatabaseServiceImpl(BovineRepository bovineRepository, BatchMapping batchMapping, BatchRepository batchRepository) {
        this.bovineRepository = bovineRepository;
        this.batchMapping = batchMapping;
        this.batchRepository = batchRepository;
    }


    @Override
    public Page<CemaBovine> searchBovines(String cuig, String tag, String genre, String description, int page, int size) {
        CemaBovine bovineToSearch = new CemaBovine();
        bovineToSearch.setEstablishmentCuig(cuig);
        bovineToSearch.setTag(tag);
        bovineToSearch.setGenre(genre);
        bovineToSearch.setDescription(description);

        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Pageable paging = PageRequest.of(page, size);
        return bovineRepository.findAll(Example.of(bovineToSearch, caseInsensitiveExampleMatcher), paging);
    }

    @Override
    public CemaBatch saveCemaBatch(Batch batch) {
        CemaBatch cemaBatch = batchMapping.mapDomainToEntity(batch);
        List<String> bovineTags = batch.getBovineTags();
        List<CemaBovine> cemaBovines = bovineRepository.findCemaBovinesByTagIn(bovineTags);
        cemaBovines.forEach(cemaBovine -> cemaBovine.addBatch(cemaBatch));

        return batchRepository.save(cemaBatch);
    }

    @Override
    public void deleteBatch(CemaBatch cemaBatch) {
        List<CemaBovine> cemaBovineList = new ArrayList<>(cemaBatch.getCemaBovines());
        for (CemaBovine cemaBovine : cemaBovineList) {
            cemaBovine.removeBatch(cemaBatch);
        }
        bovineRepository.saveAll(cemaBovineList);
        batchRepository.delete(cemaBatch);
    }

    @Override
    public CemaBatch addBovinesToBatch(CemaBatch cemaBatch, List<String> bobineTags) {
        List<CemaBovine> cemaBovines = bovineRepository.findCemaBovinesByTagIn(bobineTags);
        for (CemaBovine cemaBovine : cemaBovines) {
            cemaBovine.addBatch(cemaBatch);
        }
        return batchRepository.save(cemaBatch);
    }

    @Override
    public CemaBatch removeBovinesFromBatch(CemaBatch cemaBatch, List<String> bobineTags) {
        List<CemaBovine> cemaBovines = bovineRepository.findCemaBovinesByTagIn(bobineTags);
        for (CemaBovine cemaBovine : cemaBovines) {
            cemaBovine.removeBatch(cemaBatch);
        }
        return batchRepository.save(cemaBatch);
    }
}
