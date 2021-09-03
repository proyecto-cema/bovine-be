package com.cema.bovine.mapping.impl;

import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.mapping.BovineMapping;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class BovineMappingImpl implements BovineMapping {

    @Override
    public Bovine mapEntityToDomain(CemaBovine mapBovine) {
        Bovine bovine = new Bovine();
        bovine.setTag(mapBovine.getTag());
        bovine.setDescription(mapBovine.getDescription());
        bovine.setGenre(mapBovine.getGenre());
        bovine.setTaggingDate(mapBovine.getTaggingDate());
        bovine.setEstablishmentCuig(mapBovine.getEstablishmentCuig());

        return bovine;
    }

    @Override
    public CemaBovine updateEntityWithDomain(Bovine mapBovine) {
        CemaBovine bovine = new CemaBovine();
        bovine.setTag(mapBovine.getTag());
        bovine.setDescription(mapBovine.getDescription());
        bovine.setGenre(mapBovine.getGenre());
        bovine.setTaggingDate(mapBovine.getTaggingDate());
        bovine.setEstablishmentCuig(mapBovine.getEstablishmentCuig());

        return bovine;
    }


    @Override
    public CemaBovine updateEntityWithDomain(Bovine mapBovine, CemaBovine entityBovine) {
        String description = StringUtils.hasText(mapBovine.getDescription()) ? mapBovine.getDescription() : entityBovine.getDescription();
        String genre = StringUtils.hasText(mapBovine.getGenre()) ? mapBovine.getGenre() : entityBovine.getGenre();
        Date taggingDate = mapBovine.getTaggingDate() != null ? mapBovine.getTaggingDate() : entityBovine.getTaggingDate();
        String establishmentCuig = StringUtils.hasText(mapBovine.getEstablishmentCuig()) ? mapBovine.getEstablishmentCuig() : entityBovine.getEstablishmentCuig();
        entityBovine.setDescription(description);
        entityBovine.setGenre(genre);
        entityBovine.setTaggingDate(taggingDate);
        entityBovine.setEstablishmentCuig(establishmentCuig);

        return entityBovine;
    }
}
