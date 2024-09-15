package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

import java.util.List;

public interface WarehouseStore {

  List<Warehouse> getAll();

  void create(Warehouse warehouse);

  void update(Warehouse warehouse);

  void archive(String buCode);

  Warehouse findWarehouseById(long id);

  void remove(Warehouse warehouse);

  Warehouse findByBusinessUnitCode(String buCode);

  boolean existsByBusinessUnitCode(String buCode);

  List<Warehouse> findAllByLocation(String location, String buCode);

  List<Warehouse> findAllNotArchived();
}
