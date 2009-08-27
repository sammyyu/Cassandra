/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cassandra.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.Column;
import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.db.IColumn;
import org.apache.cassandra.db.RowMutation;
import org.apache.cassandra.db.filter.QueryPath;
import org.apache.cassandra.dht.IPartitioner;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.io.DataOutputBuffer;
import org.apache.cassandra.net.EndPoint;
import org.apache.cassandra.net.Message;
import org.apache.cassandra.net.MessagingService;
import org.apache.cassandra.net.SelectorManager;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.utils.FBUtilities;
import org.apache.cassandra.utils.FileUtils;

public class BinaryMemtableLoader {
    private static final int port_ = 7000;
    private static final long waitTime_ = 10000;

    public static Object[] createMessage(String table, String key,
            String cfname, List<ColumnFamily> columnFamiles) {
        ColumnFamily baseColumnFamily;
        DataOutputBuffer bufOut = new org.apache.cassandra.io.DataOutputBuffer();
        RowMutation rm;
        Message message;
        IColumn column;
        Object[] objects = new Object[2];

        /*
         * Get the first column family from list, this is just to get past
         * validation
         */
        baseColumnFamily = new ColumnFamily(cfname, "Standard",
                DatabaseDescriptor.getComparator(table, cfname),
                DatabaseDescriptor.getSubComparator(table, cfname));

        for (ColumnFamily cf : columnFamiles) {
            bufOut.reset();
            try {
                ColumnFamily.serializer().serialize(cf, bufOut);
//                ColumnFamily.serializerWithIndexes().serialize(cf, bufOut);
//                ColumnFamily.serializer().serializeWithIndexes(columnFamily, bufOut);

                objects[0] = bufOut.size();
                column = new Column(cf.name().getBytes("UTF-8"), bufOut.getData(), 0, false);
                baseColumnFamily.addColumn(column);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        rm = new RowMutation(table, StorageService.getPartitioner()
                .decorateKey(key));
        rm.add(baseColumnFamily);

        try {
            /* Make message */
            message = rm.makeRowMutationMessage(StorageService.binaryVerbHandler_);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        objects[1] = message;
        return objects;
    }

    public static void main(String[] args) throws Throwable {
        Thread selectorThread = SelectorManager.getSelectorManager();
        selectorThread.setDaemon(true);
        selectorThread.start();

        String ipPort = args[0];
        IPartitioner p = StorageService.getPartitioner();

        String[] ipPortPair = ipPort.split(":");
        EndPoint target = new EndPoint(ipPortPair[0], Integer
                .valueOf(ipPortPair[1]));

        String table = "Digg";
        String cfname = "FriendActions";
        String key = "itemdiggs1";
        long timestamp = 0;
        String supercolumnName = "12345";
        String [] columnNames = {"1", "2", "3"};
        
        List<ColumnFamily> columnFamilies = new ArrayList<ColumnFamily>();
        ColumnFamily columnFamily = ColumnFamily.create(table, cfname);
        byte val[] = "0".getBytes();

        // exercise addColumn(QueryPath, ...)
        for (String columnName: columnNames)
        {
            QueryPath column = new QueryPath(cfname, supercolumnName.getBytes(), columnName.getBytes());
            columnFamily.addColumn(column, val, timestamp);
            columnFamilies.add(columnFamily);
        }
        
        Object objects[] = createMessage(table, key, cfname, columnFamilies);
        /* Send message to end point */
        MessagingService.getMessagingInstance().sendOneWay((Message)objects[1], target);
 
        Thread.sleep(BinaryMemtableLoader.waitTime_);
        System.out.println("Done sending the update message");

        MessagingService.shutdown();
        FileUtils.shutdown();
    }

}
