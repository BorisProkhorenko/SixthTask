package com.epam.task.sixth.builders;

import com.epam.task.sixth.entity.BusStop;
import com.epam.task.sixth.entity.Route;
import com.epam.task.sixth.entity.runnable.Bus;
import com.epam.task.sixth.entity.runnable.Passenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RouteBuilder {

    public Route route = Route.getInstance().get();

    public Route build(LinkedList<BusStop> stops, List<Passenger> passengers) {
        route.setStops(stops);
        fillBusesOnStopsWithEmptyLists(stops);
        setRouteToPassengers(passengers);
        distributePassengers(stops, passengers);
        return route;
    }

    private void distributePassengers(LinkedList<BusStop> stops, List<Passenger> passengers) {
        HashMap<BusStop, List<Passenger>> passengersOnStops = route.getPassengersOnStops();
        for (BusStop stop : stops) {
            List<Passenger> passengersOnStop = new ArrayList<>();
            for (Passenger passenger : passengers) {
                if (passenger.getLocation().equals(stop)) {
                    passengersOnStop.add(passenger);
                }
            }
            passengersOnStops.put(stop, passengersOnStop);
        }
    }

    private void fillBusesOnStopsWithEmptyLists(LinkedList<BusStop> stops) {
        HashMap<BusStop, List<Bus>> busesOnStops = route.getBusesOnStops();
        for (BusStop stop : stops) {
            busesOnStops.put(stop, new ArrayList<>());
        }
    }


    private void setRouteToPassengers(List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            passenger.setRoute(route);
        }
    }
}
