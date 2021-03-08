package com.epam.task.sixth.entity.runnable;


import com.epam.task.sixth.entity.BusStop;
import com.epam.task.sixth.entity.Route;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Passenger implements Runnable {

    private String name;
    private BusStop destination;
    private BusStop location;
    private Bus bus;
    private Route route;
    private final static Lock LOCKER = new ReentrantLock();


    @Override
    public void run() {

        if (bus == null) {
            chooseBus();
        } else {
            location = bus.getLocation();
            checkStopAndLeaveIfDestination();
            bus.getCountDownLatch().countDown();
        }

    }

    private void checkStopAndLeaveIfDestination() {

        LOCKER.lock();
        try {
            if (checkStop()) {
                bus.removePassenger(this);
            }
        } finally {
            LOCKER.unlock();
        }

    }

    private void chooseBus() {
        LOCKER.lock();
        try {
            List<Bus> busesOnStop = route.getBusesOnStops().get(location);
            for (Bus newBus : busesOnStop) {
                if (checkBus(newBus)) {
                    newBus.addPassenger(this);
                    bus = newBus;
                    route.removePassenger(this);
                    return;
                }
            }
        } finally {
            LOCKER.unlock();
        }

    }

    private boolean checkBus(Bus bus) {
        List<BusStop> stops = bus.getRoute().getStops();
        BusStop busLocation = bus.getLocation();
        int locationIndex = stops.indexOf(busLocation);
        int stopsCount = route.getStops().size();
        for (int i = locationIndex; i < stopsCount; i++) {
            BusStop destinationName = stops.get(i);
            int busFullness = bus.getPassengers().size();
            if (destinationName.equals(destination) && busFullness < Bus.getBusCapacity()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkStop() {
        return location.equals(destination);
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BusStop getDestination() {
        return destination;
    }

    public void setDestination(BusStop destination) {
        this.destination = destination;
    }

    public BusStop getLocation() {
        return location;
    }

    public void setLocation(BusStop location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "name='" + name +
                '}';
    }
}
