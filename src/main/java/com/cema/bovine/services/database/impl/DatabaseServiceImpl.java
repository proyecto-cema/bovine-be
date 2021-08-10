package com.cema.bovine.services.database.impl;

import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.repositories.BovineRepository;
import com.cema.bovine.services.database.DatabaseService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    private final BovineRepository bovineRepository;

    public DatabaseServiceImpl(BovineRepository bovineRepository) {
        this.bovineRepository = bovineRepository;
    }


    @Override
    public Page<CemaBovine> searchBovines(String tag, String genre, String description, int page, int size) {
        CemaBovine bovineToSearch = new CemaBovine();
        bovineToSearch.setTag(tag);
        bovineToSearch.setGenre(genre);
        bovineToSearch.setDescription(description);

        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Pageable paging = PageRequest.of(page, size);
        return bovineRepository.findAll(Example.of(bovineToSearch, caseInsensitiveExampleMatcher), paging);
    }
}
