CustomEndPointSnitch illustrates how a custom EndPointSnitch could be plugged in and define
for a given node which data center and rack it resides in.  It operates by looking
at the property file, /etc/cassandra/rack.properties which is in the following format:
<node IP>\:<port>=<data center name>:<rack name>
There's also a special line that starts with default that defines what information to use
when a given node can't be found
default=<data center name>:<rack name>

Installing
----------
Run the ant jar target 
Copy the customendpointsnitch.jar file into where the lib directory is located
Modify EndPointSnitch element in storage-conf.xml to:
    org.apache.cassandra.locator.CustomEndPointSnitch
Create the file /etc/cassandra/rack.properties

Running/Managing
----------------
This endpointsnitch also registers itself as an MBean which can be use to reload
the configuration file in the case the rack.properties file has changed.  In addition,
the current rack information can be retrieved as well.
