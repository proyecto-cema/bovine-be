package com.cema.bovine.mapping.impl;


import com.cema.bovine.domain.Bovine;
import com.cema.bovine.constants.Categories;
import com.cema.bovine.entities.CemaBatch;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.mapping.BovineMapping;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.Date;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BovineMappingImplTest {

    private BovineMapping bovineMapping = new BovineMappingImpl();

    @Test
    public void mapEntityCemaBovineToDomainBovineShouldReturnCorrectDomainBovine(){
        String tag = "123";
        String description = "black";
        String genre = "Male";
        String batch1 = "batch1";
        String batch2 = "batch2";
        String category = "Toro";
        CemaBatch cemaBatch1 = new CemaBatch();
        cemaBatch1.setBatchName(batch1);
        CemaBatch cemaBatch2 = new CemaBatch();
        cemaBatch2.setBatchName(batch2);
        Set<CemaBatch> cemaBatches = Sets.newSet(cemaBatch1, cemaBatch2);
        Date taggingDate = new Date();

        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setDescription(description);
        cemaBovine.setTag(tag);
        cemaBovine.setGenre(genre);
        cemaBovine.setTaggingDate(taggingDate);
        cemaBovine.setCemaBatches(cemaBatches);
        cemaBovine.setCategory(category);

        Bovine resultBovine = bovineMapping.mapEntityToDomain(cemaBovine);

        assertThat(resultBovine.getTag(), is(tag));
        assertThat(resultBovine.getDescription(), is(description));
        assertThat(resultBovine.getGenre(), is(genre));
        assertThat(resultBovine.getTaggingDate(), is(taggingDate));
        assertTrue(resultBovine.getBatchNames().contains(batch1));
        assertTrue(resultBovine.getBatchNames().contains(batch2));
        assertThat(resultBovine.getCategory(), is(category));
    }

    @Test
    public void mapDomainBovineToEntityCemaBovineShouldReturnCorrectEntityCemaBovine(){
        String tag = "123";
        String description = "black";
        String genre = "female";
        String category = "Vaca";
        Date taggingDate = new Date();

        Bovine bovine = Bovine.builder().build();
        bovine.setDescription(description);
        bovine.setTag(tag);
        bovine.setGenre(genre);
        bovine.setTaggingDate(taggingDate);
        bovine.setCategory(category);

        CemaBovine resultBovine = bovineMapping.mapDomainToEntity(bovine);

        assertThat(resultBovine.getTag(), is(tag));
        assertThat(resultBovine.getDescription(), is(description));
        assertThat(resultBovine.getGenre(), is(genre));
        assertThat(resultBovine.getTaggingDate(), is(taggingDate));
        assertThat(resultBovine.getCategory(), is(category));

    }

    @Test
    public void updateDomainWithEntityShouldUpdateAllPresentFields(){
        String tag = "123";
        String description = "black";
        String genre = "Female";
        String establishmentCuig = "establishmentCuig";
        Date taggingDate = new Date();

        Bovine bovine = Bovine.builder().build();
        bovine.setDescription(description);
        bovine.setTag(tag);
        bovine.setGenre(genre);
        bovine.setEstablishmentCuig(establishmentCuig);
        bovine.setTaggingDate(taggingDate);

        CemaBovine cemaBovine = new CemaBovine();

        CemaBovine resultBovine = bovineMapping.mapDomainToEntity(bovine, cemaBovine);

        assertThat(resultBovine.getDescription(), is(description));
        assertThat(resultBovine.getGenre(), is(genre));
        assertThat(resultBovine.getEstablishmentCuig(), is(establishmentCuig));
        assertThat(resultBovine.getTaggingDate(), is(taggingDate));

    }

    @Test
    public void updateDomainWithEntityShouldLeaveFieldsUntouchedIfNotPresent(){
        String tag = "123";
        String description = "black";
        String genre = "female";
        String establishmentCuig = "establishmentCuig";
        Date taggingDate = new Date();

        Bovine bovine = Bovine.builder().build();

        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setDescription(description);
        cemaBovine.setTag(tag);
        cemaBovine.setGenre(genre);
        cemaBovine.setEstablishmentCuig(establishmentCuig);
        cemaBovine.setTaggingDate(taggingDate);

        CemaBovine resultBovine = bovineMapping.mapDomainToEntity(bovine, cemaBovine);

        assertThat(resultBovine.getDescription(), is(description));
        assertThat(resultBovine.getGenre(), is(genre));
        assertThat(resultBovine.getEstablishmentCuig(), is(establishmentCuig));
        assertThat(resultBovine.getTaggingDate(), is(taggingDate));

    }

}