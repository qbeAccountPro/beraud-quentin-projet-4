package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.model.ParkingSpot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {
    @Mock
    private DataBaseConfig mockDataBaseConfig;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private ParkingSpotDAO parkingSpotDAO;

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
     * Verifies getNextAvailableSlot method when parking is empty, expecting -1 as
     * there is no available spot.
     * 
     */
    @Test
    public void getNextAvailableSlotWhenParkingIsEmptyTest() {
        try {
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);
        } catch (Exception e) {
            System.out.println(e);
        }
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = mockDataBaseConfig;
        int nextAvailableSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(-1, nextAvailableSlot);
    }

    /**
     * Verifies getNextAvailableSlot method when parking has available spots,
     * expecting the next available spot number for example "5".
     * 
     */
    @Test
    public void getNextAvailableSlotWhenParkingIsNotEmptyTest() {
        try {
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(5);
        } catch (Exception e) {
            System.out.println(e);
        }
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = mockDataBaseConfig;
        int nextAvailableSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(5, nextAvailableSlot);
    }

    /**
     * Verifies updateParking method for a successful parking spot status update,
     * expecting true.
     * 
     */
    @Test
    public void updateParkingSuccessTest() {
        try {
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        } catch (Exception e) {
            System.out.println(e);
        }
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = mockDataBaseConfig;
        boolean isUpdated = parkingSpotDAO.updateParking(parkingSpot);
        assertTrue(isUpdated);
    }

    /**
     * Verifies updateParking method when a parking spot status update fails,
     * expecting false.
     * 
     */
    @Test
    public void updateParkingFailedTest() {
        try {
            when(mockPreparedStatement.executeUpdate()).thenReturn(2);
        } catch (Exception e) {
            System.out.println(e);
        }
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = mockDataBaseConfig;
        boolean isUpdated = parkingSpotDAO.updateParking(parkingSpot);
        assertFalse(isUpdated);
    }
}
