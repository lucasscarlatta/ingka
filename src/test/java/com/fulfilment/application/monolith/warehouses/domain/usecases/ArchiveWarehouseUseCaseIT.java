package com.fulfilment.application.monolith.warehouses.domain.usecases;

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
public class ArchiveWarehouseUseCaseIT {

    @Inject
    ArchiveWarehouseUseCase useCase;

    @Inject
    WarehouseStore warehouseStore;

    @Test
    public void testArchiveWarehouseSuccess() {
        // Given
        var id = 3L;

        // When
        useCase.archive(id);

        // Then
        var archivedWarehouse = warehouseStore.findWarehouseById(id);
        assertNotNull(archivedWarehouse.archivedAt());
    }

    @Test
    public void testArchiveNonExistentWarehouse() {
        // Given
        var id = 999L;

        // When
        var exception = assertThrows(WebApplicationException.class, () -> useCase.archive(id));

        // Then
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }
}
