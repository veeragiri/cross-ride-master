/**
 * 
 */
package com.crossover.techtrial.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.RideRepository;

import validation.RideValidation;

/**
 * @author crossover
 *
 */
@Service
public class RideServiceImpl implements RideService {

	@Autowired
	RideRepository rideRepository;

	@Autowired
	PersonService personService;

	public Ride save(Ride ride) {

		// Check that is driver and rider already registered
		// Check if end time less than or equal to start time.
		if (personService.findById(ride.getDriver().getId()) == null
				|| personService.findById(ride.getRider().getId()) == null || !RideValidation.validateRideTimes(ride))
			return null;

		return rideRepository.save(ride);
	}

	@Override
	public List<Ride> getAll() {
		List<Ride> rideList = new ArrayList<>();
		rideRepository.findAll().forEach(rideList::add);
		return rideList;

	}

	public Ride findById(Long rideId) {
		Optional<Ride> optionalRide = rideRepository.findById(rideId);
		if (optionalRide.isPresent()) {
			return optionalRide.get();
		} else
			return null;
	}

	@Override
	public List<TopDriverDTO> findTopDrivers(long count, LocalDateTime startTime, LocalDateTime endTime) {

		// Check rides which start and end within the mentioned durations
		List<Ride> rides = getAll().stream()
				.filter(ride -> (LocalDateTime.parse(ride.getStartTime()).equals(startTime)
						|| LocalDateTime.parse(ride.getStartTime()).isAfter(startTime))
						&& (LocalDateTime.parse(ride.getEndTime()).equals(endTime)
								|| LocalDateTime.parse(ride.getEndTime()).isBefore(endTime)))
				.collect(Collectors.toList());

		// initialize map of driver and its rides info
		Map<Person, DriverRide> driversMap = new HashMap<>();

		for (Ride ride : rides) {

			// Check to add rides to its driver
			if (driversMap.containsKey(ride.getDriver())) {
				driversMap.get(ride.getDriver()).addRide(ride);
			} else {
				DriverRide driverRides = new DriverRide(ride.getDriver());
				// update ride info
				driverRides.addRide(ride);
				driversMap.put(ride.getDriver(), driverRides);
			}
		}

		Set<DriverRide> driverRides = new TreeSet<>(driversMap.values());

		// initialize result list
		List<TopDriverDTO> result = new ArrayList<>();

		// get only Top 5 drivers with max total duration value and calculate
		// their average
		for (DriverRide driverRide : driverRides.stream().limit(count).collect(Collectors.toList())) {
			result.add(new TopDriverDTO(driverRide.getDriver().getName(), driverRide.getDriver().getEmail(),
					driverRide.getTotalRideDurationInSeconds(), driverRide.getMaxRideDurationInSecods(),
					driverRide.getAverageDistance()));

		}

		return result;
	}

	/**
	 * 
	 * @author alshimaa.ammar inner class which can calculate the info which we
	 *         need about every driver
	 */
	class DriverRide implements Comparable<DriverRide> {

		private Person driver;
		private Set<Ride> rides;
		private long totalRideDurationInSeconds;
		private long maxRideDurationInSecods;
		private long totalDistance;

		public DriverRide(Person driver) {
			this.driver = driver;
			this.rides = new HashSet<>();
		}

		public Person getDriver() {
			return driver;
		}

		public long getTotalRideDurationInSeconds() {
			return totalRideDurationInSeconds;
		}

		public long getMaxRideDurationInSecods() {
			return maxRideDurationInSecods;
		}

		public double getAverageDistance() {
			return (double) totalDistance / rides.size();
		}

		public void addRide(Ride ride) {

			rides.add(ride);

			long rideDuration = ride.getRideDurationInSeconds();

			if (rideDuration > maxRideDurationInSecods)
				maxRideDurationInSecods = rideDuration;

			totalRideDurationInSeconds += rideDuration;

			totalDistance += ride.getDistance();
		}

		/**
		 * to compare between total rides duration
		 */
		@Override
		public int compareTo(DriverRide driverRides) {

			if (this.totalRideDurationInSeconds > driverRides.totalRideDurationInSeconds) {
				return 1;
			} else if (this.totalRideDurationInSeconds < driverRides.totalRideDurationInSeconds) {
				return -1;
			}
			return 0;
		}
	}

}
