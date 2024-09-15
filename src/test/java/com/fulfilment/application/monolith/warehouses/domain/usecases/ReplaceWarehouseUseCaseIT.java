package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
public class ReplaceWarehouseUseCaseIT {

    @Inject
    ReplaceWarehouseUseCase useCase;

    @Inject
    WarehouseStore warehouseStore;

    @Test
    public void testReplaceWarehouseSuccess() {
        // Given
        var buCode = "MWH.001";
        var newWarehouse = new Warehouse(buCode, "ZWOLLE-001", 20, 5);

        // When
        var updatedWarehouse = useCase.replace(buCode, newWarehouse);

        // Then
        assertNotNull(updatedWarehouse);
        assertEquals(buCode, updatedWarehouse.businessUnitCode());
        assertEquals("ZWOLLE-001", updatedWarehouse.location());
        assertEquals(20, updatedWarehouse.capacity());
        assertEquals(15, updatedWarehouse.stock());
    }

    @Test
    public void testReplaceWarehouse_notFound() {
        // Given
        var newWarehouse = new Warehouse("MWH.999", "ZWOLLE-001", 200, 100);

        // When
        var exception = assertThrows(WebApplicationException.class, () -> useCase.replace("MWH.999", newWarehouse));

        // Then
        assertEquals("Warehouse not found", exception.getMessage());
    }

    @Test
    public void testReplaceWarehouse_cannotExceedStockCapacity() {
        // Given
        warehouseStore.create(new Warehouse("MWH.002", "ZWOLLE-002", 50, 25));
        var invalidWarehouse = new Warehouse("MWH.002", "ZWOLLE-002", 50, 40);

        // When
        var exception = assertThrows(WebApplicationException.class, () -> useCase.replace("MWH.002", invalidWarehouse));

        // Then
        assertEquals("Warehouse stock cannot exceed capacity", exception.getMessage());
    }

    @Test
    public void testReplaceWarehouse_cannotExceedLocationCapacity() {
        // Given
        warehouseStore.create(new Warehouse("MWH.002", "ZWOLLE-002", 50, 25));
        var invalidWarehouse = new Warehouse("MWH.002", "ZWOLLE-002", 150, 40);

        // When
        var exception = assertThrows(WebApplicationException.class, () -> useCase.replace("MWH.002", invalidWarehouse));

        // Then
        assertEquals("Warehouse capacity exceeds the location's maximum allowed capacity", exception.getMessage());
    }

    @Test
    public void testReplaceWarehouse_cannotExceedMaxCapacity() {
        // Given
        warehouseStore.create(new Warehouse("MWH.002", "ZWOLLE-002", 25, 15));
        warehouseStore.create(new Warehouse("MWH.003", "ZWOLLE-002", 25, 5));
        var invalidWarehouse = new Warehouse("MWH.003", "ZWOLLE-002", 50, 45);

        // When
        var exception = assertThrows(WebApplicationException.class, () -> useCase.replace("MWH.003", invalidWarehouse));

        // Then
        assertEquals("Exceeds maximum capacity", exception.getMessage());
    }
}
