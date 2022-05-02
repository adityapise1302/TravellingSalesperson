
/*
 * AUTHOR: Aditya Pise
 * FILE: PA11Main.java
 * ASSIGNMENT: Programming Assignment 11 - Traveling Salesperson
 * COURSE: CSC 210; Spring 2022
 * PURPOSE: The purpose of this code is to solve the traveling salesperson problem
 * where the program returns the travel order and the shortest route for the 
 * salesperson to travel across and back the cities provided in the input file 
 * according to the different algorithms such as heuristics, recursive backtracking
 * and my own implementation of an efficient recursive backtracking.
 * 
 * USAGE:
 * java PA11Main.java example.mtx [BACKTRACK, HEURISTIC, MINE, TIME]
 * 
 * where example.mtx contains the distance and the cities data.
 * 
 * -------------EXAMPLE INPUT------------------
 * %%MatrixMarket matrix coordinate real general
 * 3 3 6
 * 1 2 1.0
 * 2 1 2.0
 * 1 3 3.0
 * 3 1 4.0
 * 2 3 5.0
 * 3 2 6.0
 * 
 * --------------EXAMPLE OUTPUT-----------------
 * For HEURISTICS:
 * cost = 10.0, visitOrder = [1, 2, 3]
 * For BACKTRACK:
 * cost = 10.0, visitOrder = [1, 2, 3]
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class PA11Main {
    public static void main(String[] args) {
        DGraph graph = null;
        try {
            graph = mtxFileReader(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (args[1].equals("HEURISTIC")) {
            Trip trip = heuristic(graph);
            System.out.println(trip.toString(graph));
        } else if (args[1].equals("BACKTRACK")) {
            Trip minTrip = backTracking(graph);
            System.out.println(minTrip.toString(graph));
        } else if (args[1].equals("MINE")) {
            Trip mineTrip = mine(graph);
            System.out.println(mineTrip.toString(graph));
        } else if (args[1].equals("TIME")) {
            time(graph);
        }
    }

    /*
     * This method reads the provide mtx file and converts it to a graph which
     * is returned by the method.
     * 
     * @param file - file is a string suggesting the file name
     * 
     * @return graph - graph is the DGraph object representing the graph of the
     * provided file.
     */
    private static DGraph mtxFileReader(String file) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = br.readLine();
        while (line.contains("%")) {
            line = br.readLine();
        }
        String[] string = line.split("( )+");
        int nodes = (Integer.valueOf(string[0].trim())).intValue();
        DGraph graph = new DGraph(nodes);
        while (true) {
            line = br.readLine();
            if (line == null) {
                break;
            }
            string = line.split("( )+");
            graph.addEdge((Integer.valueOf(string[0].trim())).intValue(),
                    (Integer.valueOf(string[1].trim())).intValue(),
                    (Double.valueOf(string[2].trim())).doubleValue());
        }
        br.close();
        return graph;
    }

    /*
     * This method implements the Heuristic approach to the Traveling
     * Salesperson problem and returns a Trip object which contains the trip
     * information.
     * 
     * @param - graph is the DGraph object which contains the graph
     * representation of the cities.
     * 
     * @return - trip is the Trip object which contains the trip information.
     */
    private static Trip heuristic(DGraph graph) {
        Trip trip = new Trip(graph.getNumNodes());
        int currentCity = 1;
        trip.chooseNextCity(currentCity);
        for (int k = 2; k <= graph.getNumNodes(); k++) {
            List<Integer> neighbors = graph.getNeighbors(currentCity);
            int closestCity = 0;
            double distance = Math.pow(10, 15);
            for (int neighbor : neighbors) {
                if (trip.isCityAvailable(neighbor)
                        && graph.getWeight(currentCity, neighbor) < distance) {
                    distance = graph.getWeight(currentCity, neighbor);
                    closestCity = neighbor;
                }
            }
            trip.chooseNextCity(closestCity);
            currentCity = closestCity;
        }
        return trip;
    }

    /*
     * This method implements the recursive back tracking approach to the
     * Traveling Salesperson problem and returns a trip object which contains
     * the trip information.
     * 
     * @param - graph is the graph is the DGraph object which contains the graph
     * representation of the cities.
     * 
     * @return - trip is the Trip object which contains the trip information.
     */
    private static Trip backTracking(DGraph graph) {
        Trip trip = new Trip(graph.getNumNodes());
        trip.chooseNextCity(1);
        Trip minTrip = new Trip(graph.getNumNodes());
        backTrackingHelper(graph, trip, minTrip);
        return minTrip;
    }

    /*
     * This is a helper method to perform the recursive backtracking for the
     * backTracking method.
     * 
     * @param - graph is the graph is the DGraph object which contains the graph
     * representation of the cities.
     * soFar is a Trip object containing the information regarding the trip so
     * far taken.
     * previousMin is the trip object containing the minimum length trip.
     */
    private static void backTrackingHelper(DGraph graph, Trip soFar,
            Trip previousMin) {
        if (soFar.isPossible(graph)) {
            if (soFar.tripCost(graph) < previousMin.tripCost(graph)) {
                previousMin.copyOtherIntoSelf(soFar);
                return;
            }
        }

        if (soFar.tripCost(graph) < previousMin.tripCost(graph)) {
            for (int city : soFar.citiesLeft()) {
                soFar.chooseNextCity(city);
                backTrackingHelper(graph, soFar, previousMin);
                soFar.unchooseLastCity();
            }
        }
    }

    /*
     * This method implements the efficient recursive back tracking approach to
     * the Traveling Salesperson problem and returns a trip object which
     * contains the trip information.
     * 
     * @param - graph is the graph is the DGraph object which contains the graph
     * representation of the cities.
     * 
     * @return - trip is the Trip object which contains the trip information.
     */
    private static Trip mine(DGraph graph) {
        Trip trip = new Trip(graph.getNumNodes());
        trip.chooseNextCity(1);
        Trip minTrip = new Trip(graph.getNumNodes());
        mineHelper(graph, trip, minTrip);
        return minTrip;
    }

    /*
     * This is a helper method to perform the recursive backtracking for the
     * mine method.
     * 
     * @param - graph is the graph is the DGraph object which contains the graph
     * representation of the cities.
     * soFar is a Trip object containing the information regarding the trip so
     * far taken.
     * previousMin is the trip object containing the minimum length trip.
     */
    private static void mineHelper(DGraph graph, Trip soFar, Trip previousMin) {
        if (soFar.isPossible(graph)) {
            if (soFar.tripCost(graph) < previousMin.tripCost(graph)) {
                previousMin.copyOtherIntoSelf(soFar);
                return;
            }
        }
        if (soFar.tripCost(graph) < previousMin.tripCost(graph)) {
            for (int city : soFar.citiesLeft()) {
                soFar.chooseNextCity(city);
                if (soFar.tripCost(graph) < previousMin.tripCost(graph)) {
                    mineHelper(graph, soFar, previousMin);
                    soFar.unchooseLastCity();
                }
            }
        }
    }

    /*
     * This method keeps track and prints the time for each approach. It prints
     * in the order Heuristic, Back Tracking and mine approach.
     * 
     * @param - graph is the graph is the DGraph object which contains the graph
     * representation of the cities.
     */
    private static void time(DGraph graph) {
        long startTime = System.nanoTime();
        Trip heuristicTrip = heuristic(graph);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("heuristic: cost = " + heuristicTrip.tripCost(graph)
                + ", " + duration + " milliseconds");

        startTime = System.nanoTime();
        Trip mineTrip = mine(graph);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.println("mine: cost = " + mineTrip.tripCost(graph) + ", "
                + duration + " milliseconds");

        startTime = System.nanoTime();
        Trip backTrackingTrip = backTracking(graph);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out
                .println("backtrack: cost = " + backTrackingTrip.tripCost(graph)
                        + ", " + duration + " milliseconds");
    }

}
