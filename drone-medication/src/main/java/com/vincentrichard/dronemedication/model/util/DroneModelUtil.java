package com.vincentrichard.dronemedication.model.util;

import com.vincentrichard.dronemedication.model.enums.DroneModel;

public class DroneModelUtil {
    public static DroneModel determineModel(double weightLimit) {
        return (weightLimit==100)?DroneModel.LIGHTWEIGHT:(weightLimit <= 200)?
                DroneModel.MIDDLEWEIGHT:(weightLimit <= 300)?DroneModel.CRUISERWEIGHT:DroneModel.HEAVYWEIGHT;

    }
}
