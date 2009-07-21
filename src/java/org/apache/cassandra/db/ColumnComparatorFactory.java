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

package org.apache.cassandra.db;

import java.io.Serializable;
import java.util.Comparator;


/**
 * Author : Avinash Lakshman ( alakshman@facebook.com) & Prashant Malik ( pmalik@facebook.com )
 */

public class ColumnComparatorFactory
{
    public static enum ComparatorType
    {
        NAME,
    }

    public static final Comparator<IColumn> nameComparator_ = new ColumnNameComparator();

    public static Comparator<IColumn> getComparator(ComparatorType comparatorType)
    {
        assert comparatorType == ComparatorType.NAME;
        return nameComparator_;
    }

    public static Comparator<IColumn> getComparator(int comparatorTypeInt)
    {
        return getComparator(ComparatorType.values()[comparatorTypeInt]);
    }

}

abstract class AbstractColumnComparator implements Comparator<IColumn>, Serializable
{
    protected ColumnComparatorFactory.ComparatorType comparatorType_;

    public AbstractColumnComparator(ColumnComparatorFactory.ComparatorType comparatorType)
    {
        comparatorType_ = comparatorType;
    }

    ColumnComparatorFactory.ComparatorType getComparatorType()
    {
        return comparatorType_;
    }
}

class ColumnNameComparator extends AbstractColumnComparator
{
    ColumnNameComparator()
    {
        super(ColumnComparatorFactory.ComparatorType.NAME);
    }

    /* if the names are the same then sort by time-stamps */
    public int compare(IColumn column1, IColumn column2)
    {
        assert column1.getClass() == column2.getClass();
        long result = column1.name().compareTo(column2.name());
        int finalResult = 0;
        if (result == 0 && (column1 instanceof Column))
        {
            /* inverse sort by time to get the latest first */
            result = column2.timestamp() - column1.timestamp();
        }
        if (result > 0)
        {
            finalResult = 1;
        }
        if (result < 0)
        {
            finalResult = -1;
        }
        return finalResult;
    }
}
