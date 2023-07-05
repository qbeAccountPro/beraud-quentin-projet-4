package com.parkit.parkingsystem.service;

public class FareUtil {
    
  /**
   * Some javadoc.
   * Take a Fare on Param with high precision after the decimal point and return
   * the rounded fare to cent to the upper digit. 
   * 
   * @param Fare : Fare with too high precision.
   * @return the fare with a precision to cent.
   * 
   */
    public static double roundedFareToCents(double fare) {
        int decimalPlaces = 2;
        double factor = Math.pow(10, decimalPlaces);
        double roundedFare = Math.ceil(fare * factor) / factor;
        return roundedFare;
    }
}
