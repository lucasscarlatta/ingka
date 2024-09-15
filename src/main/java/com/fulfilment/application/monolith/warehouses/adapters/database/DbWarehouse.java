package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse")
@Cacheable
public class DbWarehouse {

    @Id @GeneratedValue private Long id;

    private String businessUnitCode;

    private String location;

    private Integer capacity;

    private Integer stock;

    private LocalDateTime createdAt;

    private LocalDateTime archivedAt;

    public DbWarehouse() {
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Warehouse toWarehouse() {
        return new Warehouse(this.businessUnitCode, this.location, this.capacity, this.stock, this.createdAt,
                this.archivedAt);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessUnitCode() {
        return businessUnitCode;
    }

    public void setBusinessUnitCode(String businessUnitCode) {
        this.businessUnitCode = businessUnitCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }
}
