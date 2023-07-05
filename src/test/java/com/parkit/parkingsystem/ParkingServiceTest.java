package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

  @Mock
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDAO parkingSpotDAO;
  @Mock
  private static TicketDAO ticketDAO;
  @Mock
  private static ParkingSpot parkingSpot;
  @Mock
  private static ParkingService parkingService;
  @Mock
  private static Ticket ticket;

  @BeforeEach
  public void setUpPerTest() {
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
    ticket = new Ticket();
    ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber("ABCDEF");
    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
  }

  @AfterEach
  public void restartData() {
    ticket = null;
    parkingSpot = null;
  }

  /**
   * Some javadoc.
   * Test the method processExitingVehicle() with mock objects.
   *
   */
  @Test
  public void processExitingVehicleTest() {
    try {
      when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
      when(ticketDAO.getNbTicket(anyString())).thenReturn(2);
      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up test mock objects of processExitingVehicleTest()");
    }
    parkingService.processExitingVehicle();
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
  }

  /**
   * Some javadoc.
   * Test the method processIncomingVehicle() with mock objects.
   *
   */
  @Test
  public void testProcessIncomingVehicle() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(4);
      when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    } catch (Exception e) {
      throw new RuntimeException("Failed to set up test mock objects processIncomingVehicle()");
    }
    parkingService.processIncomingVehicle();
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
  }

  /**
   * Some javadoc.
   * Test the method processExitingVehicle() with mock objects when it's
   * impossible to update a ticket.
   *
   */
  @Test
  public void processExitingVehicleTestUnableUpdate() {
    try {
      when(ticketDAO.updateTicket(ticket)).thenReturn(false);
      when(ticketDAO.getTicket(any())).thenReturn(ticket);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up test mock objects processExitingVehicleTestUnableUpdate()");
    }
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
    parkingService.processExitingVehicle();
    String output = outputStream.toString().trim();
    assertTrue(output.contains("Unable to update ticket information. Error occurred"));
  }

  /**
   * Some javadoc.
   * Test the method getNextParkingNumberIfAvailable() with mock objects.
   *
   */
  @Test
  public void testGetNextParkingNumberIfAvailable() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up test mock objects GetNextParkingNumberIfAvailable()");
    }
    parkingService.getNextParkingNumberIfAvailable();
    assertEquals(parkingSpot.getId(), 1);
    assertEquals(parkingSpot.isAvailable(), true);
  }

  /**
   * Some javadoc.
   * Test the method getNextParkingNumberIfAvailable() with mock objects when no
   * places are aviable.
   *
   */
  @Test
  public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(0);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "Failed to set up test mock objects GetNextParkingNumberIfAvailableParkingNumberNotFound()");
    }
    assertEquals(parkingService.getNextParkingNumberIfAvailable(), null);
  }

  /**
   * Some javadoc.
   * Test the method getNextParkingNumberIfAvailable() with mock objects when the
   * vehicle type is not standard.
   *
   */
  @Test
  public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(3);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "Failed to set up test mock objects GetNextParkingNumberIfAvailableParkingNumberWrongArgument()");
    }
    assertEquals(parkingService.getNextParkingNumberIfAvailable(), null);
  }

}
