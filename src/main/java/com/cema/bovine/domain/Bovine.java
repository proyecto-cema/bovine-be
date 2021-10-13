package com.cema.bovine.domain;

import com.cema.bovine.entities.CemaBatch;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
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
    @ApiModelProperty(notes = "The batches this bovine belongs to", example = "[\"batch_1\",\"OtherBatch\",\"bigbatch\",\"Some_Batch\"]")
    private List<String> batchNames;

}
