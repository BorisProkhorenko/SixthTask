package com.epam.task.sixth;

import com.epam.task.sixth.entity.BusStop;
import com.epam.task.sixth.entity.Passengers;
import com.epam.task.sixth.entity.Route;
import com.epam.task.sixth.builders.RouteBuilder;
import com.epam.task.sixth.entity.runnable.Passenger;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {
    private final static int BUSES_COUNT = 4;

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Passengers passengersWrapper = mapper.readValue(new File("src/main/resources/passengers.json"),
                Passengers.class);
        List<Passenger> passengers = passengersWrapper.getPassengers();
        List<BusStop> temp = Arrays.asList(BusStop.UNIVERSITY, BusStop.PARK, BusStop.THEATRE,
                BusStop.SQUARE, BusStop.RAILWAY_STATION);
        LinkedList<BusStop> stops = new LinkedList<>(temp);
        RouteBuilder routeBuilder = new RouteBuilder();
        Route route = routeBuilder.build(stops, passengers);
        route.sendBuses(BUSES_COUNT);




    }
}
