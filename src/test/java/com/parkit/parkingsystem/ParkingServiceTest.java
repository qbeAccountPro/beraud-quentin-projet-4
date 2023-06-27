package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
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
    try {
      parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
      ticket = new Ticket();
      ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
      ticket.setParkingSpot(parkingSpot);
      ticket.setVehicleRegNumber("ABCDEF");
      // when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up test mock objects");
    }
  }

  @Test
  public void processExitingVehicleTest() {
    try {
      when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
      when(ticketDAO.getNbTicket(anyString())).thenReturn(2);
      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up test mock objects");
    }
    parkingService.processExitingVehicle();
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
  }

  @Test
  public void processIncomingVehicle() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(2);
    } catch (Exception e) {

    }
    parkingService.processIncomingVehicle();
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
  }

  @Test
  public void processExitingVehicleTestUnableUpdate() {
    try {
      when(ticketDAO.updateTicket(ticket)).thenReturn(false);
      when(ticketDAO.getTicket(any())).thenReturn(ticket);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up test mock objects");
    }
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
    parkingService.processExitingVehicle();
    String output = outputStream.toString().trim();
    assertTrue(output.contains("Unable to update ticket information. Error occurred"));
  }

  @Test
  public void GetNextParkingNumberIfAvailable() { // obtention d’un spot dont l’ID est 1 et qui est disponible
    parkingService.getNextParkingNumberIfAvailable();
    assertEquals(parkingSpot.getId(), 1);
    assertEquals(parkingSpot.isAvailable(), true);
  }

  @Test
  public void GetNextParkingNumberIfAvailableParkingNumberNotFound() {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    assertEquals(parkingService.getNextParkingNumberIfAvailable(), null);    
  }

  @Test
  public void GetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
    when(inputReaderUtil.readSelection()).thenReturn(3);
    assertEquals(parkingService.getNextParkingNumberIfAvailable(), null);    
  }

}
