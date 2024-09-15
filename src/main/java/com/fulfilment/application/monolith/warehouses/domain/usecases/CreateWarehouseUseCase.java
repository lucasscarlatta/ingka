package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final LocationGateway locationGateway;

    public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationGateway locationGateway) {
        this.warehouseStore = warehouseStore;
        this.locationGateway = locationGateway;
    }

    @Override
    public Warehouse create(Warehouse warehouse) {
        if (warehouseStore.existsByBusinessUnitCode(warehouse.businessUnitCode())) {
            throw new WebApplicationException("Warehouse business unit code already exist", Response.Status.CONFLICT);
        }
        var location = locationGateway.resolveByIdentifier(warehouse.location());
        if (location == null) {
            throw new WebApplicationException("Warehouse location doesn't exist", Response.Status.CONFLICT);
        }

        validateStoke(warehouse);
        validateCapacity(warehouse, location);
        validateWarehouse(warehouse, location);

        // if all went well, create the warehouse
        warehouseStore.create(warehouse);

        return warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode());
    }

    private void validateStoke(Warehouse warehouse) {
        if (warehouse.stock() > warehouse.capacity()) {
            throw new WebApplicationException("Warehouse stock cannot exceed capacity", Response.Status.BAD_REQUEST);
        }
    }

    private void validateCapacity(Warehouse warehouse, Location location) {
        if (warehouse.capacity() > location.maxCapacity()) {
            throw new WebApplicationException("Warehouse capacity exceeds the location's maximum allowed capacity",
                    Response.Status.BAD_REQUEST);
        }
    }

    private void validateWarehouse(Warehouse warehouse, Location location) {
        var existingWarehouses = warehouseStore.findAllByLocation(warehouse.location(), warehouse.businessUnitCode());
        int currentNumberOfWarehouses = existingWarehouses.size();
        int currentStock = existingWarehouses.stream().mapToInt(Warehouse::stock).sum();

        if (currentNumberOfWarehouses >= location.maxNumberOfWarehouses()) {
            throw new WebApplicationException("Exceeds maximum number of warehouses", Response.Status.CONFLICT);
        }

        if (currentStock + warehouse.stock() > location.maxCapacity()) {
            throw new WebApplicationException("Exceeds maximum capacity", Response.Status.CONFLICT);
        }
    }
}
