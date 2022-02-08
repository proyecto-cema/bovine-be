package com.cema.bovine.domain;

import com.cema.bovine.domain.health.Illness;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    @ApiModelProperty(notes = "The bovine category", example = "vaca")
    @Pattern(regexp = "(?i)vaca|toro|ternero")
    private String category;
    @ApiModelProperty(notes = "The bovine status", example = "destetado")
    @NotEmpty(message = "status is required")
    @Pattern(regexp = "(?i)mamando|destetado|preñada|sin preñez|en servicio|muerto|vendido|fuera de servicio")
    private String status;
    @ApiModelProperty(notes = "The cuig of the establishment this bovine belongs to", example = "321")
    @NotEmpty(message = "Establishment is required")
    private String establishmentCuig;
    @ApiModelProperty(notes = "Date when the bovine was tagged", example = "2021-03-12")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date taggingDate;
    @ApiModelProperty(notes = "Date when the bovine was born", example = "2021-03-12")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthDate;
    @ApiModelProperty(notes = "The batches this bovine belongs to", example = "[\"batch_1\",\"OtherBatch\",\"bigbatch\",\"Some_Batch\"]")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> batchNames;
    @ApiModelProperty(notes = "The id of the operation that created this obvine", example = "b000bba4-229e-4b59-8548-1c26508e459c")
    private UUID operationId;
    @ApiModelProperty(notes = "Information about the bovine illness, if it has one")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Illness illness;

}
