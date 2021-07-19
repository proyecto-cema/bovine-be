package com.cema.bovine.mapping.impl;

import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.mapping.BovineMapping;
import org.springframework.stereotype.Service;

@Service
public class BovineMappingImpl implements BovineMapping {

    @Override
    public Bovine mapEntityToDomain(CemaBovine mapBovine) {
        Bovine bovine = new Bovine();
        bovine.setTag(mapBovine.getTag());
        bovine.setDescription(mapBovine.getDescription());
        bovine.setGenre(mapBovine.getGenre());
        bovine.setTaggingDate(mapBovine.getTaggingDate());

        return bovine;
    }

    @Override
    public CemaBovine mapDomainToEntity(Bovine mapBovine) {
        CemaBovine bovine = new CemaBovine();
        bovine.setTag(mapBovine.getTag());
        bovine.setDescription(mapBovine.getDescription());
        bovine.setGenre(mapBovine.getGenre());
        bovine.setTaggingDate(mapBovine.getTaggingDate());

        return bovine;
    }


    @Override
    public CemaBovine mapDomainToEntity(Bovine mapBovine, CemaBovine entityBovine) {
        entityBovine.setDescription(mapBovine.getDescription());
        entityBovine.setGenre(mapBovine.getGenre());
        entityBovine.setTaggingDate(mapBovine.getTaggingDate());

        return entityBovine;
    }
}
