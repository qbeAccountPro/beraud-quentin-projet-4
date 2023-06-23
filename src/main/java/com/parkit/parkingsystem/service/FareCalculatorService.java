package com.parkit.parkingsystem.service;

import java.util.Date;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }
        Date inDate = ticket.getInTime();
        Date outHour = ticket.getOutTime();
        double deltaMilliSecond = outHour.getTime() - inDate.getTime();
        double duration = deltaMilliSecond / (1000 * 60 * 60);
        double lessThan30Minutes = 0.5;

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                if (duration < lessThan30Minutes) {
                    ticket.setPrice(0);
                    break;
                } else {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
            }
            case BIKE: {
                 if (duration < lessThan30Minutes) {
                    ticket.setPrice(0);
                    break;
                } else {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}