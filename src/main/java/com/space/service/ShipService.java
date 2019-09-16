package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;

public interface ShipService {

    void createShip(Ship ship);

    Ship getOneShipById(long id);

    void deleteShip(Long id);

    void updateShip(Long id, Ship newShip);

    List<Ship> pagination(List<Ship> ships, Integer pageNumber, Integer pageSize);

    List<Ship> getAllShips(String name,
                           String planet,
                           ShipType shipType,
                           Long after,
                           Long before,
                           Boolean isUsed,
                           Double minSpeed,
                           Double maxSpeed,
                           Integer minCrewSize,
                           Integer maxCrewSize,
                           Double minRating,
                           Double maxRating,
                           ShipOrder order);

    Integer getAllShipsCount(String name,
                             String planet,
                             ShipType shipType,
                             Long after,
                             Long before,
                             Boolean isUsed,
                             Double minSpeed,
                             Double maxSpeed,
                             Integer minCrewSize,
                             Integer maxCrewSize,
                             Double minRating,
                             Double maxRating,
                             ShipOrder order);
    }
