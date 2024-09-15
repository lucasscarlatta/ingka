package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

    private final WarehouseStore warehouseStore;

    public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
        this.warehouseStore = warehouseStore;
    }

    @Override
    public void archive(long id) {
        var warehouse = warehouseStore.findWarehouseById(id);
        warehouseStore.archive(warehouse.businessUnitCode());
    }
}
