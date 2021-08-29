package com.cema.bovine.services.database.impl;


import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.repositories.BovineRepository;
import com.sun.org.apache.xpath.internal.Arg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class DatabaseServiceImplTest {

    @Captor
    public ArgumentCaptor<Example> exampleArgumentCaptor;

    @Captor
    public ArgumentCaptor<Pageable> pageableArgumentCaptor;

    @Mock
    private BovineRepository bovineRepository;

    @BeforeEach
    public void startUp() {
        openMocks(this);
    }

    @Test
    public void searchBovinesShouldReturnBovinesFromDatabase(){
        String cuig = "cuig";
        String tag = "tag";
        String genre = "genre";
        String description = "description";
        int page = 0;
        int size = 1;

        Page<CemaBovine> mockPage = Mockito.mock(Page.class);
        when(bovineRepository.findAll(exampleArgumentCaptor.capture(), pageableArgumentCaptor.capture())).thenReturn(mockPage);

        DatabaseServiceImpl databaseService = new DatabaseServiceImpl(bovineRepository);

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

}