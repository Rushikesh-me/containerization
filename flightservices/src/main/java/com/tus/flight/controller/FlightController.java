package com.tus.flight.controller;

import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tus.flight.model.Flight;
import com.tus.flight.repo.FlightRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class FlightController {
	
	// App version
    private static final String APP_VERSION = "2.0";

    // Time the service was started
    private final Instant startTime = Instant.now();

	private Logger logger = LoggerFactory.getLogger(FlightController.class);

    @Autowired
    private FlightRepository repo;

    @GetMapping("/flights")
    public List<Flight> getFlights(@RequestParam(required = false, defaultValue = "No Request Provided") String request) {
        logger.info("Received request: " + request);
        generateCpuLoad();
        List<Flight> flights = repo.findAll();
        logger.info("Processed request successfully");
        return flights;
    }
    
    //new api route to get the api version
    @GetMapping("/version")
    public String getAppVersion() {
        // Convert startTime to a readable format
        LocalDateTime startDateTime = LocalDateTime.ofInstant(startTime, ZoneId.systemDefault());
        return String.format("App Version: %s, Started At: %s", APP_VERSION, startDateTime);
    }
    
    public String generateCpuLoad() {
		// Perform a CPU-intensive operation
	    performIntensiveCalculation();
	        
	    return "CPU load generated!";
	}

	private void performIntensiveCalculation() {
		// CPU-intensive task: calculating a large Fibonacci number
	    long result = fibonacci(8); // Adjust the number for more intensity
	    System.out.println("Calculated Fibonacci: " + result);
	}

    private long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

}
