package com.cema.bovine.entities;

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

@Entity
@Table(name = "cema_bovine")
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

    @ManyToMany(cascade = { CascadeType.PERSIST })
    @JoinTable(
            name = "bovine_batch",
            joinColumns = { @JoinColumn(name = "bovine_id") },
            inverseJoinColumns = { @JoinColumn(name = "batch_id") }
    )
    private Set<CemaBatch> cemaBatches = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Set<CemaBatch> getCemaBatches() {
        return cemaBatches;
    }

    public void setCemaBatches(Set<CemaBatch> cemaBatches) {
        this.cemaBatches = cemaBatches;
    }

    public void addBatch(CemaBatch cemaBatch){
        cemaBatches.add(cemaBatch);
        cemaBatch.getCemaBovines().add(this);
    }

    public void removeBatch(CemaBatch cemaBatch){
        cemaBatches.remove(cemaBatch);
        cemaBatch.getCemaBovines().remove(this);
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
