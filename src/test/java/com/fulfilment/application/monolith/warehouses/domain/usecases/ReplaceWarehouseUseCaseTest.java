package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ReplaceWarehouseUseCaseTest {

    @Test
    public void testReplaceWarehouse() {
        var warehouseStore = mock(WarehouseStore.class);
        var createWarehouseUseCase = mock(CreateWarehouseUseCase.class);
        var useCase = new ReplaceWarehouseUseCase(warehouseStore, createWarehouseUseCase);

        // Given
        var buCode = "MWH.001";
        var currentWarehouse = new Warehouse(buCode, "ZWOLLE-001", 40, 5);
        var newWarehouse = new Warehouse(buCode, "ZWOLLE-001", 30, 20);
        var finalWarehouse = new Warehouse(buCode, "ZWOLLE-001", 30, 25);

        when(warehouseStore.findByBusinessUnitCode(buCode)).thenReturn(currentWarehouse);
        doNothing().when(warehouseStore).archive(buCode);
        when(createWarehouseUseCase.create(finalWarehouse)).thenReturn(finalWarehouse);

        // When
        useCase.replace(buCode, newWarehouse);

        // Then
        verify(warehouseStore, times(1)).archive(buCode);
        verify(createWarehouseUseCase, times(1)).create(finalWarehouse);
    }
}
