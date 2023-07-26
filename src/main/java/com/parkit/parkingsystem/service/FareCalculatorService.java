package com.parkit.parkingsystem.service;

import java.util.Date;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

  /**
   * Some javadoc.
   * Call the method : calculateFare for a vehicle existing the car park without
   * the
   * discount fare.
   * 
   * @param ticket : Ticket of the vehicle existing the car park.
   * 
   */
  public void calculateFare(Ticket ticket) {
    calculateFare(ticket, false);
  }

  /**
   * Some javadoc.
   * Calculate the fare for a vehicle existing the car park, a discount of 5% is
   * possible if the discount parameter is true.
   * 
   * @param ticket   : Ticket of the vehicle existing the car park.
   * @param discount : Boolean who affect or not the discount on the fare.
   * 
   */
  public void calculateFare(Ticket ticket, boolean discount) {
    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
    }
    
    Date inDate = ticket.getInTime();
    Date outHour = ticket.getOutTime();
    double deltaMilliSecond = outHour.getTime() - inDate.getTime();
    double duration = deltaMilliSecond / (1000 * 60 * 60);
    double HALFHOUR = 0.5;

    switch (ticket.getParkingSpot().getParkingType()) {
      case CAR: {
        if (duration < HALFHOUR) {
          ticket.setPrice(0);
          break;
        } else if (discount) {
          ticket.setPrice(FareUtil.
          roundedFareToCents(duration * Fare.CAR_RATE_PER_HOUR * 0.95));
          break;
        } else {
          ticket.setPrice(FareUtil.
          roundedFareToCents(duration * Fare.CAR_RATE_PER_HOUR));
          break;
        }
      }
      case BIKE: {
        if (duration < HALFHOUR) {
          ticket.setPrice(0);
          break;
        } else if (discount) {
          ticket.setPrice(FareUtil.roundedFareToCents(duration * Fare.BIKE_RATE_PER_HOUR * 0.95));
          break;
        } else {
          ticket.setPrice(FareUtil.roundedFareToCents(duration * Fare.BIKE_RATE_PER_HOUR));
          break;
        }
      }
      default:
        throw new IllegalArgumentException("Unkown Parking Type");
    }
  }
}