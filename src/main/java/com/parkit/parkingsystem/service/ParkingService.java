package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class ParkingService {

  private static final Logger logger = LogManager.getLogger("ParkingService");

  private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

  private InputReaderUtil inputReaderUtil;
  private ParkingSpotDAO parkingSpotDAO;
  private TicketDAO ticketDAO;

  public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
    this.inputReaderUtil = inputReaderUtil;
    this.parkingSpotDAO = parkingSpotDAO;
    this.ticketDAO = ticketDAO;
  }

  /*
   * Some javadoc.
   * Method executing the creation of a ticket during the entry of vehicles into
   * the car park. If it's a reccurente user the announcement of the 5% reduction
   * is announced to the customer
   * 
   */
  public void processIncomingVehicle() {
    try {
      ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
      if (parkingSpot != null && parkingSpot.getId() > 0) {
        String vehicleRegNumber = getVehichleRegNumber();
        parkingSpot.setAvailable(false);
        parkingSpotDAO.updateParking(parkingSpot);
        Date inTime = setTimes();
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        if (ticketDAO.getNbTicket(vehicleRegNumber) >= 1) {
          if (ticketDAO.findTicketsWithOutTimeNull(vehicleRegNumber)) {
            System.out.println(
                "Your vehicle already has a ticket with no out time, so please contact the assistance");
            parkingSpot.setAvailable(true); // make the parking space available again
            parkingSpotDAO.updateParking(parkingSpot);
            return;
          } else {
            System.out.println(
                "Happy to see you back! As a regular user of our parking, you will receive a 5% discount.");
          }
        }
        ticketDAO.saveTicket(ticket);
        System.out.println("Generated Ticket and saved in DB");
        System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
        System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);

      }
    } catch (Exception e) {
      logger.error("Unable to process incoming vehicle", e);
    }

  }

  private String getVehichleRegNumber() throws Exception {
    System.out.println("Please type the vehicle registration number and press enter key");
    return inputReaderUtil.readVehicleRegistrationNumber();
  }

  public ParkingSpot getNextParkingNumberIfAvailable() {
    int parkingNumber = 0;
    ParkingSpot parkingSpot = null;
    try {
      ParkingType parkingType = getVehichleType();
      parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
      if (parkingNumber > 0) {
        parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
      } else {
        throw new Exception("Error fetching parking number from DB. Parking slots might be full");
      }
    } catch (IllegalArgumentException ie) {
      logger.error("Error parsing user input for type of vehicle", ie);
    } catch (Exception e) {
      logger.error("Error fetching next available parking slot", e);
    }
    return parkingSpot;
  }

  public ParkingType getVehichleType() {
    System.out.println("Please select vehicle type from menu");
    System.out.println("1 CAR");
    System.out.println("2 BIKE");
    int input = inputReaderUtil.readSelection();
    switch (input) {
      case 1: {
        return ParkingType.CAR;
      }
      case 2: {
        return ParkingType.BIKE;
      }
      default: {
        System.out.println("Incorrect input provided");
        throw new IllegalArgumentException("Entered input is invalid");
      }
    }
  }

  /*
   * Some javadoc.
   * Method executing the exit of vehicles from the car park.
   * 
   */
  public void processExitingVehicle() {
    try {
      String vehicleRegNumber = getVehichleRegNumber();
      Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
      Date outTime = setTimes();
      ticket.setOutTime(outTime);
      if (ticketDAO.getNbTicket(vehicleRegNumber) > 1) {
        fareCalculatorService.calculateFare(ticket, true);
      } else {
        fareCalculatorService.calculateFare(ticket, false);
      }
      if (ticketDAO.updateTicket(ticket)) {
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        parkingSpot.setAvailable(true);
        parkingSpotDAO.updateParking(parkingSpot);
        System.out.println("Please pay the parking fare:" + ticket.getPrice());
        System.out.println(
            "Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
      } else {
        System.out.println("Unable to update ticket information. Error occurred");
      }
    } catch (Exception e) {
      logger.error("Unable to process exiting vehicle", e);
    }
  }

  /**
   * This methode was created to Mock the system current time in the I.T. test.
   * It simply returns the same response as creating a new construtor of Date.
   * 
   * @return the sytem date
   */
  public Date setTimes() {
    Date date = new Date();
    return date;
  }
}
