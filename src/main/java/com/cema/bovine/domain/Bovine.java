package com.cema.bovine.domain;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

public class Bovine {

    @ApiModelProperty(notes = "The tag attached to this bovine", example = "123")
    @NotEmpty(message = "Tag is required")
    private String tag;
    @ApiModelProperty(notes = "The description for this bovine", example = "Brown with spots")
    private String description;
    @ApiModelProperty(notes = "The bovine genre", example = "macho")
    @NotEmpty(message = "genre is required")
    @Pattern(regexp = "(?i)macho|hembra")
    private String genre;
    @ApiModelProperty(notes = "The cuig of the establishment this bovine belongs to", example = "321")
    @NotEmpty(message = "Establishment is required")
    private String establishmentCuig;
    @ApiModelProperty(notes = "Date when the bovine was tagged")
    private Date taggingDate;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getEstablishmentCuig() {
        return establishmentCuig;
    }

    public void setEstablishmentCuig(String establishmentCuig) {
        this.establishmentCuig = establishmentCuig;
    }

    public Date getTaggingDate() {
        return taggingDate;
    }

    public void setTaggingDate(Date taggingDate) {
        this.taggingDate = taggingDate;
    }
}
