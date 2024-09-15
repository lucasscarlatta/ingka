package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class CreateWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private LocationGateway locationGateway;
    private CreateWarehouseUseCase useCase;

    @BeforeEach
    public void setUp() {
        warehouseStore = Mockito.mock(WarehouseStore.class);
        locationGateway = Mockito.mock(LocationGateway.class);
        useCase = new CreateWarehouseUseCase(warehouseStore, locationGateway);
    }

    @Test
    public void testCreateWarehouse() {
        // Given
        var warehouse = new Warehouse("MWH.001", "ZWOLLE-001", 40, 5);
        var location = new Location("ZWOLLE-001", 1, 40);

        when(warehouseStore.existsByBusinessUnitCode(warehouse.businessUnitCode())).thenReturn(false);
        when(locationGateway.resolveByIdentifier(warehouse.location())).thenReturn(location);
        doNothing().when(warehouseStore).create(warehouse);
        when(warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode())).thenReturn(warehouse);

        // When
        useCase.create(warehouse);

        // Then
        verify(warehouseStore, times(1)).create(warehouse);
    }

    @ParameterizedTest
    @MethodSource("provideFailureScenarios")
    public void testCreateWarehouseFailureScenarios(Warehouse warehouse, Location location,
            List<Warehouse> existingWarehouses, boolean buExists, String expectedMessage, Response.Status status) {

        // Given
        when(warehouseStore.existsByBusinessUnitCode(warehouse.businessUnitCode())).thenReturn(buExists);
        when(locationGateway.resolveByIdentifier(warehouse.location())).thenReturn(location);
        when(warehouseStore.findAllByLocation(warehouse.location(), warehouse.businessUnitCode())).thenReturn(
                existingWarehouses);

        // When
        var exception = assertThrows(WebApplicationException.class, () -> useCase.create(warehouse));

        // Then
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(status.getStatusCode(), exception.getResponse().getStatus());
        verify(warehouseStore, times(0)).create(any());
    }

    private static Stream<Arguments> provideFailureScenarios() {
        return Stream.of(
                Arguments.of(new Warehouse("MWH.001", "ZWOLLE-001", 50, 10),
                        new Location("ZWOLLE-001", 1, 40), emptyList(), true,
                        "Warehouse business unit code already exist", Response.Status.CONFLICT),

                Arguments.of(new Warehouse("MWH.002", "ZWOLLE-001", 50, 10),
                        null, emptyList(), false, "Warehouse location doesn't exist", Response.Status.CONFLICT),

                Arguments.of(new Warehouse("MWH.002", "ZWOLLE-001", 50, 60),
                        new Location("ZWOLLE-001", 1, 40), emptyList(), false,
                        "Warehouse stock cannot exceed capacity", Response.Status.BAD_REQUEST),

                Arguments.of(new Warehouse("MWH.002", "ZWOLLE-001", 50, 10),
                        new Location("ZWOLLE-001", 1, 40),
                        List.of(new Warehouse("MWH.001", "ZWOLLE-001", 30, 10)),
                        false, "Warehouse capacity exceeds the location's maximum allowed capacity", Response.Status.BAD_REQUEST),

                Arguments.of(new Warehouse("MWH.002", "ZWOLLE-001", 30, 10),
                        new Location("ZWOLLE-001", 1, 40),
                        List.of(new Warehouse("MWH.001", "ZWOLLE-001", 10, 10)),
                        false, "Exceeds maximum number of warehouses", Response.Status.CONFLICT),

                Arguments.of(new Warehouse("MWH.002", "ZWOLLE-001", 30, 25),
                        new Location("ZWOLLE-001", 2, 40),
                        List.of(new Warehouse("MWH.001", "ZWOLLE-001", 30, 20)),
                        false, "Exceeds maximum capacity", Response.Status.CONFLICT)
        );
    }
}
