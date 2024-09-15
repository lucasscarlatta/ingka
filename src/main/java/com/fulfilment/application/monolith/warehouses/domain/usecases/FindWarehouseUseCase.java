package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.FindWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class FindWarehouseUseCase implements FindWarehouseOperation {

    private final WarehouseStore warehouseStore;

    public FindWarehouseUseCase(WarehouseStore warehouseStore) {
        this.warehouseStore = warehouseStore;
    }

    @Override
    public Warehouse findById(long id) {
       return warehouseStore.findWarehouseById(id);
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseStore.findAllNotArchived();
    }
}
