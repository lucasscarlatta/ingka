package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

public interface ReplaceWarehouseOperation {
  Warehouse replace(String businessUnitCode, Warehouse warehouse);
}
