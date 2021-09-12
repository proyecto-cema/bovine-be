package com.cema.bovine.services.database.impl;


import com.cema.bovine.domain.Batch;
import com.cema.bovine.entities.CemaBatch;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.mapping.BatchMapping;
import com.cema.bovine.repositories.BatchRepository;
import com.cema.bovine.repositories.BovineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class DatabaseServiceImplTest {

    @Captor
    public ArgumentCaptor<Example> exampleArgumentCaptor;

    @Captor
    public ArgumentCaptor<Pageable> pageableArgumentCaptor;

    @Captor
    public ArgumentCaptor<List<CemaBovine>> cemaBovinesCaptor;

    @Mock
    private BovineRepository bovineRepository;
    @Mock
    private BatchMapping batchMapping;
    @Mock
    private BatchRepository batchRepository;

    private DatabaseServiceImpl databaseService;

    @BeforeEach
    public void startUp() {
        openMocks(this);
        databaseService = new DatabaseServiceImpl(bovineRepository, batchMapping, batchRepository);
    }

    @Test
    public void searchBovinesShouldReturnBovinesFromDatabase() {
        String cuig = "cuig";
        String tag = "tag";
        String genre = "genre";
        String description = "description";
        int page = 0;
        int size = 1;

        Page<CemaBovine> mockPage = Mockito.mock(Page.class);
        when(bovineRepository.findAll(exampleArgumentCaptor.capture(), pageableArgumentCaptor.capture())).thenReturn(mockPage);

        Page<CemaBovine> resultPage = databaseService.searchBovines(cuig, tag, genre, description, page, size);

        assertThat(resultPage, is(mockPage));

        Example resultExample = exampleArgumentCaptor.getValue();
        Pageable resultPageable = pageableArgumentCaptor.getValue();

        CemaBovine search = (CemaBovine) resultExample.getProbe();

        assertThat(search.getEstablishmentCuig(), is(cuig));
        assertThat(search.getTag(), is(tag));
        assertThat(search.getDescription(), is(description));
        assertThat(search.getGenre(), is(genre));

        assertThat(resultPageable.getPageSize(), is(size));
        assertThat(resultPageable.getOffset(), is(0L));
    }

    @Test
    public void saveCemaBatchShouldRetrieveAllBovinesAndAssociateThemWithTheBatchAndPersist() {
        List<String> bovineTags = new ArrayList<>();
        Batch batch = Batch.builder().bovineTags(bovineTags).build();
        CemaBovine cemaBovine1 = new CemaBovine();
        CemaBovine cemaBovine2 = new CemaBovine();
        List<CemaBovine> cemaBovines = Arrays.asList(cemaBovine1, cemaBovine2);
        CemaBatch cemaBatch = new CemaBatch();
        when(batchMapping.mapDomainToEntity(batch)).thenReturn(cemaBatch);
        when(bovineRepository.findCemaBovinesByTagIn(bovineTags)).thenReturn(cemaBovines);
        when(batchRepository.save(cemaBatch)).thenReturn(cemaBatch);

        CemaBatch result = databaseService.saveCemaBatch(batch);

        assertThat(result, is(cemaBatch));

        assertTrue(cemaBovine1.getCemaBatches().contains(cemaBatch));
        assertTrue(cemaBovine2.getCemaBatches().contains(cemaBatch));
    }

    @Test
    public void deleteCemaBatchShouldRemoveAllAssociationsFromBovinesToBatchesAndPersist() {
        CemaBatch cemaBatch = new CemaBatch();
        CemaBovine cemaBovine1 = new CemaBovine();
        CemaBovine cemaBovine2 = new CemaBovine();
        cemaBatch.addBovine(cemaBovine1);
        cemaBatch.addBovine(cemaBovine2);
        databaseService.deleteBatch(cemaBatch);

        verify(bovineRepository).saveAll(cemaBovinesCaptor.capture());
        verify(batchRepository).delete(cemaBatch);

        assertTrue(cemaBovinesCaptor.getValue().contains(cemaBovine1));
        assertTrue(cemaBovinesCaptor.getValue().contains(cemaBovine2));

        assertFalse(cemaBovine1.getCemaBatches().contains(cemaBatch));
        assertFalse(cemaBovine2.getCemaBatches().contains(cemaBatch));
    }

    @Test
    public void addBovinesToBatchShouldRetrieveAllBovinesAndAddThemToTheBatch(){
        CemaBatch cemaBatch = new CemaBatch();
        List<String> bovineTags = new ArrayList<>();
        CemaBovine cemaBovine1 = new CemaBovine();
        CemaBovine cemaBovine2 = new CemaBovine();
        List<CemaBovine> cemaBovines = Arrays.asList(cemaBovine1, cemaBovine2);

        when(bovineRepository.findCemaBovinesByTagIn(bovineTags)).thenReturn(cemaBovines);

        databaseService.addBovinesToBatch(cemaBatch, bovineTags);

        verify(batchRepository).save(cemaBatch);

        assertTrue(cemaBovine1.getCemaBatches().contains(cemaBatch));
        assertTrue(cemaBovine2.getCemaBatches().contains(cemaBatch));
    }

}