package com.cema.bovine.mapping.impl;

import com.cema.bovine.domain.Bovine;
import com.cema.bovine.entities.CemaBatch;
import com.cema.bovine.entities.CemaBovine;
import com.cema.bovine.mapping.BovineMapping;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BovineMappingImpl implements BovineMapping {

    @Override
    public Bovine mapEntityToDomain(CemaBovine cemaBovine) {
        List<String> batchNames = cemaBovine.getCemaBatches().stream().map(CemaBatch::getBatchName).collect(Collectors.toList());
        return Bovine.builder()
                .tag(cemaBovine.getTag())
                .description(cemaBovine.getDescription())
                .genre(StringUtils.capitalize(cemaBovine.getGenre()))
                .taggingDate(cemaBovine.getTaggingDate())
                .establishmentCuig(cemaBovine.getEstablishmentCuig())
                .status(StringUtils.capitalize(cemaBovine.getStatus()))
                .category(StringUtils.capitalize(cemaBovine.getCategory()))
                .batchNames(batchNames)
                .build();
    }

    @Override
    public CemaBovine mapDomainToEntity(Bovine bovine) {
        CemaBovine cemaBovine = new CemaBovine();
        cemaBovine.setTag(bovine.getTag());
        cemaBovine.setDescription(bovine.getDescription());
        cemaBovine.setGenre(bovine.getGenre());
        cemaBovine.setTaggingDate(bovine.getTaggingDate());
        cemaBovine.setEstablishmentCuig(bovine.getEstablishmentCuig());
        cemaBovine.setStatus(bovine.getStatus());
        cemaBovine.setCategory(bovine.getCategory());
        return cemaBovine;
    }


    @Override
    public CemaBovine mapDomainToEntity(Bovine bovine, CemaBovine cemaBovine) {
        String description = StringUtils.hasText(bovine.getDescription()) ? bovine.getDescription() : cemaBovine.getDescription();
        String genre = StringUtils.hasText(bovine.getGenre()) ? StringUtils.capitalize(bovine.getGenre()) : cemaBovine.getGenre();
        Date taggingDate = bovine.getTaggingDate() != null ? bovine.getTaggingDate() : cemaBovine.getTaggingDate();
        String establishmentCuig = StringUtils.hasText(bovine.getEstablishmentCuig()) ? bovine.getEstablishmentCuig() : cemaBovine.getEstablishmentCuig();
        String status = StringUtils.hasText(bovine.getStatus()) ? StringUtils.capitalize(bovine.getStatus()) : cemaBovine.getStatus();
        String category = StringUtils.hasText(bovine.getCategory()) ? StringUtils.capitalize(bovine.getCategory()) : cemaBovine.getCategory();

        cemaBovine.setDescription(description);
        cemaBovine.setGenre(genre);
        cemaBovine.setTaggingDate(taggingDate);
        cemaBovine.setEstablishmentCuig(establishmentCuig);
        cemaBovine.setStatus(status);
        cemaBovine.setCategory(category);

        return cemaBovine;
    }
}
