package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

import java.util.List;

public interface FindWarehouseOperation {
  Warehouse findById(long id);

  List<Warehouse> findAll();
}
