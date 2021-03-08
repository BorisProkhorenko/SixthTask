package com.epam.task.sixth.entity;

import com.epam.task.sixth.entity.runnable.Bus;
import com.epam.task.sixth.entity.runnable.Passenger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;


public class Route {

    private HashMap<BusStop, List<Passenger>> passengersOnStops = new HashMap<>();
    private HashMap<BusStop, List<Bus>> busesOnStops = new HashMap<>();
    private LinkedList<BusStop> stops;
    private int busId = 0;
    private static AtomicReference<Route> instance;
    private final static Lock LOCKER = new ReentrantLock();


    public static AtomicReference<Route> getInstance() {
        AtomicReference<Route> localInstance = instance;
        if (localInstance == null) {
            LOCKER.lock();
            try {
                localInstance = instance;
                if (localInstance == null) {
                    instance = new AtomicReference<>();
                    instance.getAndSet(new Route());
                    localInstance = instance;
                }
            } finally {
                LOCKER.unlock();
            }
        }
        return localInstance;
    }

    private Route() {
    }


    public void sendBuses(int number) {
        ExecutorService executorService = Executors.newFixedThreadPool(number);

        IntStream.range(0, number).forEach(i -> executorService.execute(new Bus(busId++, this)));
        executorService.shutdown();
    }

    public void removePassenger(Passenger passenger) {
        passengersOnStops.remove(passenger);
    }

    public LinkedList<BusStop> getStops() {
        return stops;
    }

    public void setStops(LinkedList<BusStop> stops) {
        this.stops = stops;
    }

    public HashMap<BusStop, List<Passenger>> getPassengersOnStops() {
        return passengersOnStops;
    }

    public HashMap<BusStop, List<Bus>> getBusesOnStops() {
        return busesOnStops;
    }

}
