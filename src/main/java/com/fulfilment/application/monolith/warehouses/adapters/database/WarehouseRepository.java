package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

    @Override
    public List<Warehouse> getAll() {
        return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
    }

    @Override
    @Transactional
    public void create(Warehouse warehouse) {
        DbWarehouse dbWarehouse = new DbWarehouse();
        dbWarehouse.setBusinessUnitCode(warehouse.businessUnitCode());
        dbWarehouse.setStock(warehouse.stock());
        dbWarehouse.setLocation(warehouse.location());
        dbWarehouse.setCapacity(warehouse.capacity());

        this.persist(dbWarehouse);
    }

    @Override
    public void update(Warehouse warehouse) {
        DbWarehouse dbWarehouse = getByBusinessUnitCode(warehouse.businessUnitCode());
        dbWarehouse.setStock(warehouse.stock());
        dbWarehouse.setLocation(warehouse.location());
        dbWarehouse.setCapacity(warehouse.capacity());
        dbWarehouse.setArchivedAt(warehouse.archivedAt());
    }

    @Override
    @Transactional
    public void archive(String buCode) {
        var dbWarehouse = getByBusinessUnitCode(buCode);
        dbWarehouse.setArchivedAt(LocalDateTime.now());
    }

    @Override
    public Warehouse findWarehouseById(long id) {
        return findByIdOptional(id).orElseThrow(() -> new WebApplicationException(
                "Warehouse " + id + " not found", Response.Status.NOT_FOUND
        )).toWarehouse();
    }

    @Override
    public void remove(Warehouse warehouse) {
        DbWarehouse dbWarehouse = getByBusinessUnitCode(warehouse.businessUnitCode());
        this.delete(dbWarehouse);
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
        return getByBusinessUnitCode(buCode).toWarehouse();
    }

    @Override
    public boolean existsByBusinessUnitCode(String buCode) {
        return count("businessUnitCode = ?1 and archivedAt is null", buCode) > 0;
    }

    @Override
    public List<Warehouse> findAllByLocation(String location, String buCode) {
        return find("location = ?1 and archivedAt is null and businessUnitCode <> ?2", location, buCode).stream()
                .map(DbWarehouse::toWarehouse).toList();
    }

    @Override
    public List<Warehouse> findAllNotArchived() {
        return find("archivedAt is null").stream().map(DbWarehouse::toWarehouse).toList();
    }

    private DbWarehouse getByBusinessUnitCode(String buCode) {
        DbWarehouse dbWarehouse = find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();

        if (dbWarehouse == null) {
            throw new WebApplicationException("Warehouse not found", Response.Status.NOT_FOUND);
        }

        return dbWarehouse;
    }
}
