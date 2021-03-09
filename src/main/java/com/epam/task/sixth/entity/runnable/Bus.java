package com.epam.task.sixth.entity.runnable;

import com.epam.task.sixth.entity.BusStop;
import com.epam.task.sixth.entity.Route;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bus implements Runnable {


    private final static TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private final static int TIMEOUT = 5;
    private final static int STOP_CAPACITY = 3;
    private final static Semaphore SEMAPHORE = new Semaphore(STOP_CAPACITY, true);
    private final static int BUS_CAPACITY = 10;
    private final static Lock LOCKER = new ReentrantLock();
    private final static int THREAD_POOL = 10;
    private final static Logger LOGGER = LogManager.getLogger();


    private int id;
    private Route route;
    private List<Passenger> passengers;
    private BusStop location;
    private CountDownLatch countDownLatch;
    private ExecutorService executorService;


    public Bus(int id, Route route) {
        this.id = id;
        this.route = route;
        passengers = new ArrayList<>();
        location = route.getStops().getFirst();
    }

    @Override
    public void run() {
        for (BusStop stop : route.getStops()) {
            System.out.println("Bus " + id + " is waiting for stop at " + stop + " stop, passengers:" + passengers);
            countDownLatch = new CountDownLatch(passengers.size());
            try {
                SEMAPHORE.acquire();
                System.out.println("Bus " + id + " stopped at " + stop + " stop, passengers:" + passengers);
                location = stop;
                List<Bus> busesOnStop = route.getBusesOnStops().get(location);
                busesOnStop.add(this);
                startPassengersInBus();
                countDownLatch.await();
                startPassengersInBusStop();
                TIME_UNIT.sleep(TIMEOUT);
                busesOnStop.remove(this);
                System.out.println("Bus " + id + " left " + location + " stop, passengers:" + passengers);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
            SEMAPHORE.release();
        }
        this.passengers = null;
        System.out.println("Bus " + id + " finished it's route");

    }

    private void startPassengersInBus() {
        LOCKER.lock();
        try {
            executorService = Executors.newFixedThreadPool(THREAD_POOL);
            passengers.forEach(executorService::execute);
            executorService.shutdown();
        } finally {
            LOCKER.unlock();
        }
    }

    private void startPassengersInBusStop() {
        List<Passenger> passengersOnStop = route.getPassengersOnStops().get(location);
        executorService = Executors.newFixedThreadPool(THREAD_POOL);
        passengersOnStop.forEach(executorService::execute);
        executorService.shutdown();
    }

    public void addPassenger(Passenger passenger) {
        passengers.add(passenger);
    }

    public void removePassenger(Passenger passenger) {
        LOCKER.lock();
        try {
            passengers.remove(passenger);
        } finally {
            LOCKER.unlock();
        }

    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(ArrayList<Passenger> passengers) {
        this.passengers = passengers;
    }

    public BusStop getLocation() {
        return location;
    }

    public void setLocation(BusStop location) {
        this.location = location;
    }

    public static Semaphore getSEMAPHORE() {
        return SEMAPHORE;
    }

    public static int getBusCapacity() {
        return BUS_CAPACITY;
    }

    public static int getStopCapacity() {
        return STOP_CAPACITY;
    }
}
