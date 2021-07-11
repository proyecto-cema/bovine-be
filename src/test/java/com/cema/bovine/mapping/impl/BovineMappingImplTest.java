package com.cema.bovine.mapping.impl;


import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.mapping.BovineMapping;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class BovineMappingImplTest {

    private BovineMapping bovineMapping = new BovineMappingImpl();

    @Test
    public void mapEntityCemaBovineToDomainBovineShouldReturnCorrectDomainBovine(){
        String tag = "123";
        String description = "black";
        String genre = "male";
        Date taggingDate = new Date();

        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setDescription(description);
        cemaBovine.setTag(tag);
        cemaBovine.setGenre(genre);
        cemaBovine.setTaggingDate(taggingDate);

        Bovine resultBovine = bovineMapping.mapEntityToDomain(cemaBovine);

        assertThat(resultBovine.getTag(), is(tag));
        assertThat(resultBovine.getDescription(), is(description));
        assertThat(resultBovine.getGenre(), is(genre));
        assertThat(resultBovine.getTaggingDate(), is(taggingDate));
    }

    @Test
    public void mapDomainBovineToEntityCemaBovineShouldReturnCorrectEntityCemaBovine(){
        String tag = "123";
        String description = "black";
        String genre = "female";
        Date taggingDate = new Date();

        Bovine bovine = new Bovine();
        bovine.setDescription(description);
        bovine.setTag(tag);
        bovine.setGenre(genre);
        bovine.setTaggingDate(taggingDate);

        CemaBovine resultBovine = bovineMapping.mapDomainToEntity(bovine);

        assertThat(resultBovine.getTag(), is(tag));
        assertThat(resultBovine.getDescription(), is(description));
        assertThat(resultBovine.getGenre(), is(genre));
        assertThat(resultBovine.getTaggingDate(), is(taggingDate));

    }

    @Test
    public void mapDomainBovineToEntityCemaBovineWithTagShouldReturnCorrectEntityCemaBovine(){
        String tag = "123";
        String description = "black";
        String genre = "female";
        Date taggingDate = new Date();

        Bovine bovine = new Bovine();
        bovine.setDescription(description);
        bovine.setTag(tag);
        bovine.setGenre(genre);
        bovine.setTaggingDate(taggingDate);

        CemaBovine resultBovine = bovineMapping.mapDomainToEntity(bovine, tag);

        assertThat(resultBovine.getTag(), is(tag));
        assertThat(resultBovine.getDescription(), is(description));
        assertThat(resultBovine.getGenre(), is(genre));
        assertThat(resultBovine.getTaggingDate(), is(taggingDate));

    }

}