package com.cema.bovine.entities;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "batch")
public class CemaBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "batch_name")
    private String batchName;

    @Column(name = "establishment_cuig")
    private String establishmentCuig;

    @Column(name = "description")
    private String description;

    @Column(name = "creation_date")
    private Date creationDate;

    @ManyToMany(mappedBy = "cemaBatches", cascade = {CascadeType.PERSIST})
    private Set<CemaBovine> cemaBovines = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchName() {
        return this.batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getEstablishmentCuig() {
        return this.establishmentCuig;
    }

    public void setEstablishmentCuig(String establishmentCuig) {
        this.establishmentCuig = establishmentCuig;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Set<CemaBovine> getCemaBovines() {
        return cemaBovines;
    }

    public void setCemaBovines(Set<CemaBovine> cemaBovines) {
        this.cemaBovines = cemaBovines;
    }

    public void addBovine(CemaBovine cemaBovine) {
        cemaBovines.add(cemaBovine);
        cemaBovine.getCemaBatches().add(this);
    }

    public void removeBovine(CemaBovine cemaBovine) {
        cemaBovines.remove(cemaBovine);
        cemaBovine.getCemaBatches().remove(this);
    }
}
