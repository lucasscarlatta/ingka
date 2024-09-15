package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ArchiveWarehouseUseCaseTest {

    @Test
    public void testArchiveWarehouse() {
        var warehouseStore = mock(WarehouseStore.class);
        var useCase = new ArchiveWarehouseUseCase(warehouseStore);

        // Given
        var id = 1L;
        var warehouse = new Warehouse("MWH.001", "ZWOLLE-001", 40, 10);
        when(warehouseStore.findWarehouseById(id)).thenReturn(warehouse);
        doNothing().when(warehouseStore).archive(warehouse.businessUnitCode());

        // When
        useCase.archive(1);

        // Then
        verify(warehouseStore, times(1)).archive(warehouse.businessUnitCode());
    }
}
