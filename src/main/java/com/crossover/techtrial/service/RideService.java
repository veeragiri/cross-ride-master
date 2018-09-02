/**
 * 
 */
package com.crossover.techtrial.service;

import java.time.LocalDateTime;
import java.util.List;

import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.model.Ride;

/**
 * RideService for rides.
 * 
 * @author crossover
 *
 */
public interface RideService {

	Ride save(Ride ride);

	Ride findById(Long rideId);

	List<Ride> getAll();
	/**
	 * 
	 * @param count
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<TopDriverDTO> findTopDrivers(long count, LocalDateTime startTime, LocalDateTime endTime);
}
