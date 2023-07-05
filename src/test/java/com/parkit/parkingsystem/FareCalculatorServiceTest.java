package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.*;
import com.parkit.parkingsystem.model.*;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.FareUtil;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class FareCalculatorServiceTest {

  private static FareCalculatorService fareCalculatorService;
  private Ticket ticket;
  private Date inTime, outTime;
  private ParkingSpot parkingSpot;

  @BeforeAll
  private static void setUp() {
    fareCalculatorService = new FareCalculatorService();
  }

  @BeforeEach
  private void setUpPerTest() {
    ticket = new Ticket();
    inTime = new Date();
    outTime = new Date();
  }

  @AfterEach
  private void restartData() {
    ticket = null;
    inTime = null;
    outTime = null;
  }

  /**
   * Some javadoc.
   * Set up on a ticket object three of this construcor data : {inTim, outTime,
   * parkingSpot}.
   *
   */
  private void setTicketValue() {
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
  }

  /**
   * Some javadoc.
   * Test if the fare is calculated correctly for one hour with a car.
   *
   */
  @Test
  public void calculateFareCar() {
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    setTicketValue();
    fareCalculatorService.calculateFare(ticket);
    assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
  }

  /**
   * Some javadoc.
   * Test if the fare is calculated correctly for one hour with a bike.
   *
   */
  @Test
  public void calculateFareBike() {
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    setTicketValue();
    fareCalculatorService.calculateFare(ticket);
    assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
  }

  /**
   * Some javadoc.
   * Test if the calculate fare send back an error message "Unkown Parking Type"
   * when the parking type of parking spot is out of both data : "Bike or Car".
   *
   */
  @Test
  public void calculateFareUnkownType() {
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    parkingSpot = new ParkingSpot(1, null, false);
    setTicketValue();
    assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
  }

  /**
   * Some javadoc.
   * Test if the calculate fare send back an error message "Out time provided is
   * incorrect:" when the in time value is higher than the out time value of data
   * ticket with a bike.
   *
   */
  @Test
  public void calculateFareBikeWithFutureInTime() {
    inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
    parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    setTicketValue();
    assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
  }

  /**
   * Some javadoc.
   * Test if the fare is calculated correctly for less than one hour with a bike.
   *
   */
  @Test
  public void calculateFareBikeWithLessThanOneHourParkingTime() {
    inTime.setTime(System.currentTimeMillis() - (36 * 60 * 1000));
    parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    setTicketValue();
    fareCalculatorService.calculateFare(ticket);
    assertEquals(FareUtil.roundedFareToCents(0.60 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
  }

  /**
   * Some javadoc.
   * Test if the fare is calculated correctly for less than one hour with a car.
   *
   */
  @Test
  public void calculateFareCarWithLessThanOneHourParkingTime() {
    inTime.setTime(System.currentTimeMillis() - (36 * 60 * 1000));
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    setTicketValue();
    fareCalculatorService.calculateFare(ticket);
    assertEquals(FareUtil.roundedFareToCents(0.60 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  /**
   * Some javadoc.
   * Test if the fare is calculated correctly for a duration longer than one day
   * with a car.
   *
   */
  @Test
  public void calculateFareCarWithMoreThanADayParkingTime() {
    inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    setTicketValue();
    fareCalculatorService.calculateFare(ticket);
    assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  /**
   * Some javadoc.
   * Test if the fare is free for less than 30 minutes with a car.
   *
   */
  @Test
  public void calculateFareCarWithLessThan30minutesParkingTime() {
    inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    setTicketValue();
    fareCalculatorService.calculateFare(ticket);
    assertEquals((0), ticket.getPrice());
  }

  /**
   * Some javadoc.
   * Test if the fare is free for less than 30 minutes with a bike.
   *
   */
  @Test
  public void calculateFareBikeWithLessThan30minutesParkingTime() {
    inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));
    parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    setTicketValue();
    fareCalculatorService.calculateFare(ticket);
    assertEquals((0), ticket.getPrice());
  }

  /**
   * Some javadoc.
   * Test if the fare take the discount with a car.
   *
   */
  @Test
  public void calculateFareCarWithDiscount() {
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    setTicketValue();
    fareCalculatorService.calculateFare(ticket, true);
    assertEquals(FareUtil.roundedFareToCents(0.95 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  /**
   * Some javadoc.
   * Test if the fare take the discount with a bike.
   *
   */
  @Test
  public void calculateFareBikeWithDiscount() {
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    setTicketValue();
    fareCalculatorService.calculateFare(ticket, true);
    assertEquals(FareUtil.roundedFareToCents(0.95 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
  }

 
}
