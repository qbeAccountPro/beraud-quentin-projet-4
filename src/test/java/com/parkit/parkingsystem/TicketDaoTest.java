package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.sql.Timestamp;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
public class TicketDaoTest {
    @Mock
    private DataBaseConfig mockDataBaseConfig;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private Logger mockLogger;

    @Mock
    private Ticket ticket;

    @InjectMocks
    private TicketDAO ticketDAO;

    @BeforeEach
    public void setUpPerTest() {
        try {
            when(mockDataBaseConfig.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Test method to verify the successful saving of a ticket in the database.
     * 
     */
    @Test
    public void saveTicketSuccessTest() {
        try {
            when(mockPreparedStatement.execute()).thenReturn(true);
        } catch (Exception e) {
            System.out.println(e);
        }
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ticket.setParkingSpot(parkingSpot);
        ticket.setInTime(new Date());
        boolean isSaved = ticketDAO.saveTicket(ticket);
        assertTrue(isSaved);
    }

    /**
     * Test method to verify the failure of saving a ticket in the database due to
     * an exception.
     * 
     */
    @Test
    public void saveTicketFailureTest() {
        try {
            when(mockPreparedStatement.execute()).thenThrow(new SQLException("Test mock exception to interrupt the save"));
        } catch (Exception e) {
            System.out.println(e);
        }
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ticket.setParkingSpot(parkingSpot);
        ticket.setInTime(new Date());
        boolean isSaved = ticketDAO.saveTicket(ticket);
        assertFalse(isSaved);
    }

    /**
     * Test method to verify the retrieval of a ticket based on a valid vehicle
     * registration number. Compare all data from mock with the ticket.
     * 
     */
    @Test
    public void getTicketWithValidRegNumberTest() {
        try {
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(1);
            when(mockResultSet.getInt(2)).thenReturn(123);
            when(mockResultSet.getString(6)).thenReturn("CAR");
            when(mockResultSet.getDouble(3)).thenReturn(5.0);
            when(mockResultSet.getTimestamp(4)).thenReturn(new Timestamp(System.currentTimeMillis()));
            when(mockResultSet.getTimestamp(5)).thenReturn(null);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        } catch (Exception e) {
            System.out.println(e);
        }
        Ticket ticket = ticketDAO.getTicket("ABC123");
        assertNotNull(ticket);
        assertEquals(1, ticket.getParkingSpot().getId());
        assertEquals(123, ticket.getId());
        assertEquals("ABC123", ticket.getVehicleRegNumber());
        assertEquals(5.0, ticket.getPrice());
        assertNotNull(ticket.getInTime());
        assertNull(ticket.getOutTime());
    }

    /**
     * Test method to verify that retrieving a ticket based on an invalid vehicle
     * registration number returns null.
     * 
     */
    @Test
    public void getTicketFailureTest() {
        try {
            when(mockResultSet.next()).thenReturn(false);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        } catch (Exception e) {
            System.out.println(e);
        }
        Ticket ticket = ticketDAO.getTicket("XYZ456");
        assertNull(ticket);
    }

    /**
     * Test method to verify the successful retrieval of the total number of tickets
     * in the database.
     * 
     */
    @Test
    public void getNbTicketSuccessTest() {
        try {
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(5);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        } catch (Exception e) {
            System.out.println(e);
        }
        int ticketCount = ticketDAO.getNbTicket("ABCD");
        assertEquals(5, ticketCount);
    }

    /**
     * Test method to verify that retrieving the total number of tickets in the
     * database fails due to no data.
     * 
     */
    @Test
    public void getNbTicketFailureTest() {
        try {
            when(mockResultSet.next()).thenReturn(false);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        } catch (Exception e) {
            System.out.println(e);
        }
        int ticketCount = ticketDAO.getNbTicket("ABCD");
        assertEquals(0, ticketCount);
    }

    /**
     * Test method to verify the successful search for tickets with null outTime.
     * 
     */
    @Test
    public void findTicketsWithOutTimeNullSuccessTest() {
        try {
            when(mockResultSet.next()).thenReturn(true);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        } catch (Exception e) {
            System.out.println(e);
        }
        boolean hasTickets = ticketDAO.findTicketsWithOutTimeNull("ABCD");
        assertTrue(hasTickets);
    }

    /**
     * Test method to verify that the search for tickets with null outTime fails due
     * to no data.
     * 
     */
    @Test
    public void findTicketsWithOutTimeNullFailureTest() {
        try {
            when(mockResultSet.next()).thenReturn(false);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        } catch (Exception e) {
            System.out.println(e);
        }
        boolean hasTickets = ticketDAO.findTicketsWithOutTimeNull("ABCD");
        assertFalse(hasTickets);
    }

    /**
     * Test method to verify the successful update of ticket information in the
     * database.
     * 
     */
    @Test
    public void updateTicketSuccessTest() {
        try {
            when(mockPreparedStatement.execute()).thenReturn(true);
        } catch (Exception e) {
            System.out.println(e);
        }
        Ticket ticket = mock(Ticket.class);
        when(ticket.getId()).thenReturn(123);
        when(ticket.getPrice()).thenReturn(15.02); 
        when(ticket.getOutTime()).thenReturn(new Date()); 
        boolean isUpdated = ticketDAO.updateTicket(ticket);
        assertTrue(isUpdated);
    }

    /**
     * Test method to verify that updating ticket information in the database fails
     * due to an exception.
     * 
     */
    @Test
    public void updateTicketFailureTest() {
        try {
            doThrow(new SQLException("Test exception")).when(mockPreparedStatement).execute();
        } catch (Exception e) {
            System.out.println(e);
        }
        Ticket ticket = mock(Ticket.class);
        when(ticket.getId()).thenReturn(33);
        when(ticket.getPrice()).thenReturn(55.45);
        when(ticket.getOutTime()).thenReturn(new Date()); 
        boolean isUpdated = ticketDAO.updateTicket(ticket);
        assertFalse(isUpdated);
    }
}
