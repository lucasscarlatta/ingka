package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final CreateWarehouseUseCase createWarehouseUseCase;

    public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, CreateWarehouseUseCase createWarehouseUseCase) {
        this.warehouseStore = warehouseStore;
        this.createWarehouseUseCase = createWarehouseUseCase;
    }

    @Override
    @Transactional // To ensure if creation fails the archive call is rollback
    public Warehouse replace(String businessUnitCode, Warehouse newWarehouse) {
        var currentWarehouse = warehouseStore.findByBusinessUnitCode(businessUnitCode);
        warehouseStore.archive(businessUnitCode);

        // All the validation related to the stock will be done in creation
        return createWarehouseUseCase.create(new Warehouse(
                        // newWarehouse.businessUnitCode() is ignored to create the same businessUnitCode
                        businessUnitCode,
                        // According to `BRIEFING.md` the replace method create a new warehouse in the same location
                        currentWarehouse.location(),
                        // Use the new capacity in the newWarehouse
                        newWarehouse.capacity(),
                        // I sum the currentWarehouse stock and newWarehouse stock to get the final stock
                        currentWarehouse.stock() + newWarehouse.stock()
                )
        );
    }
}
