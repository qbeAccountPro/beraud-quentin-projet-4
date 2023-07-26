package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.*;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.*;
import com.parkit.parkingsystem.service.FareUtil;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {
  private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  private static ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
  private static TicketDAO ticketDAO = new TicketDAO();
  private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
  private ParkingService parkingService;
  private Ticket ticket;
  private int HOURMILLISEC = 3600000;
  private String REGNUMBER = "ABCDEF";

  @Mock
  private static InputReaderUtil inputReaderUtil;

  @Mock
  private ParkingService parkingServiceMock;

  @BeforeAll
  private static void setUp() throws Exception {
    parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    ticketDAO.dataBaseConfig = dataBaseTestConfig;
  }

  @BeforeEach
  private void setUpPerTest() throws Exception {
    dataBasePrepareService.clearDataBaseEntries();
    try {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
      parkingServiceMock = Mockito.spy(parkingService);
      // Create increasing time to increment system time for testing
      Date nowHour = new Date(), hour_1 = new Date(), hour_2 = new Date(), hour_3 = new Date(), hour_4 = new Date();
      hour_1.setTime(nowHour.getTime() + (HOURMILLISEC * 1));
      hour_2.setTime(nowHour.getTime() + (HOURMILLISEC * 2));
      hour_3.setTime(nowHour.getTime() + (HOURMILLISEC * 3));
      hour_4.setTime(nowHour.getTime() + (HOURMILLISEC * 4));
      // Each call to the setTimes() method in the test methods below will return, in
      // order of program execution, the dates below to simulate the temporary
      // unfolding
      when(parkingServiceMock.setTimes()).thenReturn(nowHour, hour_1, hour_2, hour_3, hour_4);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "Failed to set up test mock objects setUpPerTest() of ParkingDataBaseIT");
    }
  }

  /**
   * Some javadoc.
   * Test that a ticket is actualy saved in database and parking table is updated
   * with availability data.
   *
   */
  @Test
  public void testParkingACar() {
    parkingServiceMock.processIncomingVehicle(); // Entry into the park
    ticket = ticketDAO.getTicket(REGNUMBER);
    ParkingSpot parkingSpot = ticket.getParkingSpot();
    assertNotNull(ticket.getInTime());
    assertNull(ticket.getOutTime());
    assertEquals(parkingSpot.isAvailable(), false);
  }

  /**
   * Some javadoc.
   * Test that the fare generated and out time are populated correctly in
   * the database.
   * 
   */
  @Test
  public void testParkingLotExit() {
    testParkingACar(); // Entry into the park
    parkingServiceMock.processExitingVehicle(); // Exit the car park
    ticket = ticketDAO.getTicket(REGNUMBER);
    assertNotNull(ticket.getPrice());
    // Get the delata betwen the in/out times.
    double delatDate = ticket.getOutTime().compareTo(ticket.getInTime()); 
    assertTrue(delatDate > 0);
  }

  /**
   * Some javadoc.
   * Test the discount of 5% on the fare for a reccuring user.
   * 
   */
  @Test
  public void testParkingLotExitRecurringUser() {
    testParkingLotExit(); // 1st entry into the car park
    testParkingLotExit(); // 2nd entry into the car park
    switch (ticket.getParkingSpot().getParkingType()) {
      case CAR: {
        assertEquals(ticket.getPrice(), FareUtil.
        roundedFareToCents(0.95 * Fare.CAR_RATE_PER_HOUR));
        break;
      }
      case BIKE: {
        assertEquals(ticket.getPrice(), FareUtil.
        roundedFareToCents(0.95 * Fare.BIKE_RATE_PER_HOUR));
        break;
      }
      default:
        throw new IllegalArgumentException("Unkown Parking Type");
    }
  }
}