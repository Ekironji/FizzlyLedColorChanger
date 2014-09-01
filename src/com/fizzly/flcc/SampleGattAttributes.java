/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fizzly.flcc;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT       = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";


    public static String ACC_SERVICE_UUID		= "f000aa10-0451-4000-cb00-5ec0a1d1cb00";
    public static String ACC_ENABLER_UUID		= "f000aa11-0451-4000-cb00-5ec0a1d1cb00";
    public static String ACC_RANGE_UUID		    = "f000aa12-0451-4000-cb00-5ec0a1d1cb00";
    public static String ACC_DATA_UUID		    = "f000aa13-0451-4000-cb00-5ec0a1d1cb00";
    public static String ACC_PERIOD_UUID		= "f000aa14-0451-4000-cb00-5ec0a1d1cb00";
        
    public static String RGB_SERVICE_UUID		= "f000aa50-0451-4000-cb00-5ec0a1d1cb00";
    public static String RGB_ENABLER_UUID       = "f000aa51-0451-4000-cb00-5ec0a1d1cb00";
    public static String RGB_DATA_UUID          = "f000aa52-0451-4000-cb00-5ec0a1d1cb00";
    public static String RGB_COMMAND_UUID       = "f000aa53-0451-4000-cb00-5ec0a1d1cb00";
    
    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put( HEART_RATE_MEASUREMENT               , "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        
        // Fizzly
        
        // Accelerometer
        attributes.put( ACC_SERVICE_UUID            , "Accelerometer Service");
        attributes.put( ACC_ENABLER_UUID            , "Enable Accelerometer");
        attributes.put( ACC_RANGE_UUID              , "Accelerometer range");
        attributes.put( ACC_DATA_UUID               , "Accelerometer data");
        attributes.put( ACC_PERIOD_UUID             , "Accelerometer period");
              
        // Giroscope
        
        // Magnetometer
        
        // Batteria      
        
        // RGB
        attributes.put(RGB_SERVICE_UUID, "RGB service");
        attributes.put(RGB_ENABLER_UUID, "RGB enabler service");
        attributes.put(RGB_DATA_UUID,    "RGB values");
        attributes.put(RGB_COMMAND_UUID, "RGB command");
        
        // Beeper
        
        // Touch key
  
      
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
