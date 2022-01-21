package com.cema.bovine.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cema_bovine")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CemaBovine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tag")
    private String tag;

    @Column(name = "description")
    private String description;

    @Column(name = "genre")
    private String genre;

    @Column(name = "establishment_cuig")
    private String establishmentCuig;

    @Column(name = "tagging_date")
    private Date taggingDate;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "status")
    private String status;

    @Column(name = "category")
    private String category;

    @Column(name = "operation_id")
    private UUID operationId;

    @ManyToMany(cascade = { CascadeType.PERSIST })
    @JoinTable(
            name = "bovine_batch",
            joinColumns = { @JoinColumn(name = "bovine_id") },
            inverseJoinColumns = { @JoinColumn(name = "batch_id") }
    )
    private Set<CemaBatch> cemaBatches = new HashSet<>();

    public void addBatch(CemaBatch cemaBatch){
        cemaBatches.add(cemaBatch);
        cemaBatch.getCemaBovines().add(this);
    }

    public void removeBatch(CemaBatch cemaBatch){
        cemaBatches.remove(cemaBatch);
        cemaBatch.getCemaBovines().remove(this);
    }
}
