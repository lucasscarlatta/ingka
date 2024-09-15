package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.usecases.FindWarehouseUseCase;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

    @Inject private FindWarehouseUseCase findWarehouseUseCase;
    @Inject private CreateWarehouseOperation createWarehouse;
    @Inject private ReplaceWarehouseOperation replaceWarehouseOperation;
    @Inject private ArchiveWarehouseOperation archiveWarehouseOperation;

    @Override
    public List<Warehouse> listAllWarehousesUnits() {
        return findWarehouseUseCase.findAll().stream().map(this::toWarehouseResponse).toList();
    }

    @Override
    public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
        if (data.getStock() < 0) {
            throw new WebApplicationException("Warehouse stock must be a non-negative integer",
                    Response.Status.BAD_REQUEST);
        }
        if (data.getCapacity() <= 0) {
            throw new WebApplicationException("Warehouse capacity must be greater than zero",
                    Response.Status.BAD_REQUEST);
        }
        var response = createWarehouse.create(toWarehouse(data));

        return toWarehouseResponse(response);
    }

    @Override
    public Warehouse getAWarehouseUnitByID(long id) {
        var response = findWarehouseUseCase.findById(id);
        return toWarehouseResponse(response);
    }

    @Override
    public void archiveAWarehouseUnitByID(long id) {
        archiveWarehouseOperation.archive(id);
    }

    @Override
    public Warehouse replaceTheCurrentActiveWarehouse(String businessUnitCode, @NotNull Warehouse data) {
        if (data.getStock() < 0) {
            throw new WebApplicationException("Warehouse stock must be a non-negative integer",
                    Response.Status.BAD_REQUEST);
        }
        if (data.getCapacity() <= 0) {
            throw new WebApplicationException("Warehouse capacity must be greater than zero",
                    Response.Status.BAD_REQUEST);
        }
        var response = replaceWarehouseOperation.replace(businessUnitCode, toWarehouse(data));

        return toWarehouseResponse(response);
    }

    private Warehouse toWarehouseResponse(
            com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
        var response = new Warehouse();
        response.setBusinessUnitCode(warehouse.businessUnitCode());
        response.setLocation(warehouse.location());
        response.setCapacity(warehouse.capacity());
        response.setStock(warehouse.stock());

        return response;
    }

    private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toWarehouse(Warehouse warehouse) {
        return new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse(
                warehouse.getBusinessUnitCode(), warehouse.getLocation(), warehouse.getCapacity(),
                warehouse.getStock());
    }
}
