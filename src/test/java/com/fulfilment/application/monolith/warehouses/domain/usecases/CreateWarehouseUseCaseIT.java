package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class CreateWarehouseUseCaseIT {

    @Inject
    CreateWarehouseUseCase useCase;

    @Inject
    WarehouseStore warehouseStore;

    @Test
    void testCreateWarehouse_success() {
        // Given
        Warehouse warehouse = new Warehouse("MWH.013", "AMSTERDAM-001", 20, 5);

        // When
        var response = useCase.create(warehouse);

        // Then
        assertNotNull(response);
        assertEquals("MWH.013", response.businessUnitCode());
        assertEquals("AMSTERDAM-001", response.location());
        assertEquals(20, response.capacity());
        assertEquals(5, response.stock());
        assertNotNull(warehouseStore.findByBusinessUnitCode("MWH.013"));
    }

    @Test
    void testCreateWarehouse_exceedsMaxNumberOfWarehouses() {
        // Given
        Warehouse warehouse = new Warehouse("MWH.002", "ZWOLLE-001", 30, 10);

        // When
        WebApplicationException thrown = assertThrows(WebApplicationException.class, () -> {
            useCase.create(warehouse);
        });

        // Then
        assertEquals(Response.Status.CONFLICT.getStatusCode(), thrown.getResponse().getStatus());
        assertEquals("Exceeds maximum number of warehouses", thrown.getMessage());
    }

    @Test
    void testCreateWarehouse_duplicateBusinessUnitCode() {
        // Given
        Warehouse warehouse = new Warehouse("MWH.012", "AMSTERDAM-001", 30, 10);

        // When
        WebApplicationException thrown = assertThrows(WebApplicationException.class, () -> {
            useCase.create(warehouse);
        });

        // Then
        assertEquals(Response.Status.CONFLICT.getStatusCode(), thrown.getResponse().getStatus());
        assertEquals("Warehouse business unit code already exist", thrown.getMessage());
    }

    @Test
    void testCreateWarehouse_locationDoesNotExist() {
        var invalidLocation = "INVALID-LOC";
        // Given
        Warehouse warehouse = new Warehouse("MWH.014", invalidLocation, 30, 10);

        // When
        WebApplicationException thrown = assertThrows(WebApplicationException.class, () -> {
            useCase.create(warehouse);
        });

        // Then
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), thrown.getResponse().getStatus());
        assertEquals("Location with identifier " + invalidLocation + " not found", thrown.getMessage());
    }

    @Test
    void testCreateWarehouse_exceedsCapacity() {
        // Given
        Warehouse warehouse = new Warehouse("MWH.015", "AMSTERDAM-001", 100, 100);

        // When
        WebApplicationException thrown = assertThrows(WebApplicationException.class, () -> {
            useCase.create(warehouse);
        });

        // Then
        assertEquals(Response.Status.CONFLICT.getStatusCode(), thrown.getResponse().getStatus());
        assertEquals("Exceeds maximum capacity", thrown.getMessage());
    }
}
