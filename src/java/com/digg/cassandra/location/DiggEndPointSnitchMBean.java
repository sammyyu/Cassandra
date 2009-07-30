package com.digg.cassandra.location;

import java.io.IOException;

/**
 * DiggEndPointSnitchMBean
 * 
 * DiggEndPointSnitchMBean is the management interface for Digg's EndpointSnitch MBean
 * 
 * @author Sammy Yu <syu@sammyyu.net>
 * 
 */

public interface DiggEndPointSnitchMBean {
    /**
     * The object name of the mbean.
     */
    public static String MBEAN_OBJECT_NAME = "com.digg.cassandra.location:type=EndPointSnitch";
    
    /**
     * Reload the rack configuration
     */
    public void reloadConfiguration() throws IOException;
    
    /**
     * Display the current configuration
     */
    public String displayConfiguration();

}
