package validation;

import java.time.LocalDateTime;

import com.crossover.techtrial.model.Ride;

public final class RideValidation {

	private RideValidation() {
	}

	/**
	 * 
	 * @param ride
	 * @return
	 */
	public static boolean validateRideTimes(Ride ride) {
		LocalDateTime startDate = LocalDateTime.parse(ride.getStartTime());
		LocalDateTime endDate = LocalDateTime.parse(ride.getEndTime());
		
		if (startDate.isAfter(endDate) || startDate.equals(endDate)) {
			return false;
		}
		return true;
	}
}
