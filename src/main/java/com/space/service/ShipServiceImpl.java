package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exceptions.BadRequestException;
import com.space.exceptions.ShipNotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validation;
import javax.validation.Validator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Repository
@Service("shipService")
@Transactional
@Slf4j
public class ShipServiceImpl implements ShipService {

    private static final Validator VALIDDATOR = Validation.byDefaultProvider().configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory().getValidator();

    @Autowired
    private ShipRepository shipRepository;

    @Override
    public void createShip(Ship ship) {
        log.debug("inside createShip() method");
        if (VALIDDATOR.validate(ship).size() > 0)
            throw new BadRequestException();
        if (ship.getIsUsed() == null)
            ship.setIsUsed(false);
        updateRating(ship);
        shipRepository.save(ship);
    }

    @Override
    public Ship getOneShipById(long id) {
        log.debug("inside getOneShipById() method");
        checkValid(id);
        return shipRepository.findById(id).get();
    }

    @Override
    public void deleteShip(Long id) {
        log.debug("inside deleteShip() method");
        checkValid(id);
        shipRepository.deleteById(id);
    }

    protected void updateRating(Ship ship) {
        log.debug("inside updateRating() method");
        double rating = (80 * ship.getSpeed() * (ship.getIsUsed() ? 0.5 : 1)) /
                (3019 - Integer.parseInt(new SimpleDateFormat("yyyy")
                        .format(ship.getProdDate())) + 1);
        ship.setRating(Precision.round(rating, 2));
    }

    @Override
    public void updateShip(Long id, Ship newShip) {
        log.debug("inside updateShip() method");
        checkValid(id);
        Optional<Ship> optionalShip = shipRepository.findById(id);
        if (optionalShip.isPresent()) {
            Ship oldShip = optionalShip.get();
            newShip.setId(id);
            if (newShip.getName() == null)
                newShip.setName(oldShip.getName());
            if (newShip.getPlanet() == null)
                newShip.setPlanet(oldShip.getPlanet());
            if (newShip.getShipType() == null)
                newShip.setShipType(oldShip.getShipType());
            if (newShip.getProdDate() == null)
                newShip.setProdDate(oldShip.getProdDate());
            if (newShip.getIsUsed() == null)
                newShip.setIsUsed(oldShip.getIsUsed());
            if (newShip.getSpeed() == null)
                newShip.setSpeed(oldShip.getSpeed());
            if (newShip.getCrewSize() == null)
                newShip.setCrewSize(oldShip.getCrewSize());

            if (VALIDDATOR.validate(newShip).size() > 0)
                throw new BadRequestException();

            if (newShip.getIsUsed() == null)
                newShip.setIsUsed(false);

            updateRating(newShip);
        }
        shipRepository.save(newShip);
    }

    @Override
    public List<Ship> getAllShips(String name,
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
                                  ShipOrder order) {
        Date afterDate = after == null ? null : new Date(after);
        Date beforeDate = before == null ? null : new Date(before);
        List<Ship> allShips = shipRepository.findAll();
        Iterator iterator = allShips.iterator();
        while (iterator.hasNext()) {
            Ship tmp = (Ship) iterator.next();
            if (name != null && !tmp.getName().toLowerCase().contains(name.toLowerCase())) {
                iterator.remove();
                continue;
            }
            if (planet != null && !tmp.getPlanet().toLowerCase().contains(planet.toLowerCase())) {
                iterator.remove();
                continue;
            }
            if (shipType != null && tmp.getShipType() != shipType) {
                iterator.remove();
                continue;
            }
            if (beforeDate != null && tmp.getProdDate().after(beforeDate)) {
                iterator.remove();
                continue;
            }
            if (afterDate != null && tmp.getProdDate().before(afterDate)) {
                iterator.remove();
                continue;
            }
            if (minCrewSize != null && tmp.getCrewSize() < minCrewSize) {
                iterator.remove();
                continue;
            }
            if (maxCrewSize != null && tmp.getCrewSize() > maxCrewSize) {
                iterator.remove();
                continue;
            }
            if (isUsed != null && tmp.getIsUsed().booleanValue() != isUsed.booleanValue()) {
                iterator.remove();
                continue;
            }
            if (minSpeed != null && tmp.getSpeed() < minSpeed) {
                iterator.remove();
                continue;
            }
            if (maxSpeed != null && tmp.getSpeed() > maxSpeed) {
                iterator.remove();
                continue;
            }
            if (minRating != null && tmp.getRating() < minRating) {
                iterator.remove();
                continue;
            }
            if (maxRating != null && tmp.getRating() > maxRating) {
                iterator.remove();
                continue;
            }
        }

        if (order != null)
            allShips.sort((o1, o2) -> {
                switch (order) {
                    case SPEED:
                        return o1.getSpeed().compareTo(o2.getSpeed());
                    case ID:
                        return o1.getId().compareTo(o2.getId());
                    case DATE:
                        return o1.getProdDate().compareTo(o2.getProdDate());
                    case RATING:
                        return o1.getRating().compareTo(o2.getRating());
                    default:
                        return 0;
                }
            });
        return allShips;
    }

    @Override
    public Integer getAllShipsCount(String name,
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
                                    ShipOrder order) {
        return getAllShips(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order).size();
    }

    @Override
    public List<Ship> pagination(List<Ship> ships, Integer pageNumber, Integer pageSize) {
        int first = pageNumber * pageSize;
        int last = Math.min(first + pageSize, ships.size());
        return ships.subList(first, last);
    }

    protected void checkValid(Long id) {
        if (id == null || id == 0)
            throw new BadRequestException();

        if (!shipRepository.existsById(id))
            throw new ShipNotFoundException();
    }
}
