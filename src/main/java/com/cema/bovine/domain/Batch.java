package com.cema.bovine.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

@Builder
@Getter
@Setter
public class Batch {

    @ApiModelProperty(notes = "The name of the batch, must be unique", example = "987")
    @NotEmpty(message = "Batch Name is required")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Cannot contain spaces or special characters")
    private String batchName;
    @ApiModelProperty(notes = "The cuig of the establishment this bovine belongs to", example = "321")
    @NotEmpty(message = "Establishment is required")
    private String establishmentCuig;
    @ApiModelProperty(notes = "The description for this bovine", example = "All brown cows")
    private String description;
    @ApiModelProperty(notes = "The tags for the bovines this batch groups", example = "[\"1234\",\"1235\",\"3333\",\"22222\"]")
    private List<String> bovineTags;
}
