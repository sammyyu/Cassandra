package com.digg.cassandra;

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
     * Reload the rack configuration
     */
    public void reloadConfiguration() throws IOException;
    
    /**
     * Display the current configuration
     */
    public String displayConfiguration();

}
