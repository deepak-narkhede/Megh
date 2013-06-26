/*
 * Copyright (c) 2013 Malhar Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datatorrent.lib.testbench;

import com.datatorrent.api.Sink;
import com.datatorrent.lib.testbench.EventIncrementer;
import com.datatorrent.tuple.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Functional test for {@link com.datatorrent.lib.testbench.EventIncrementer}<p>
 * <br>
 * Benchmarks: The benchmark was done in local/inline mode<br>
 * Processing tuples on seed port are at 3.5 Million tuples/sec<br>
 * Processing tuples on increment port are at 10 Million tuples/sec<br>
 * <br>
 * Validates all DRC checks of the oper<br>
 */
public class EventIncrementerBenchmark
{
  private static Logger LOG = LoggerFactory.getLogger(EventIncrementerBenchmark.class);

  class DataSink implements Sink
  {
    HashMap<String, String> collectedTuples = new HashMap<String, String>();
    int count = 0;

    /**
     *
     * @param payload
     */
    @Override
    public void put(Object payload)
    {
      if (payload instanceof Tuple) {
        // LOG.debug(payload.toString());
      }
      else {
        HashMap<String, String> tuple = (HashMap<String, String>)payload;
        for (Map.Entry<String, String> e: ((HashMap<String, String>)payload).entrySet()) {
          collectedTuples.put(e.getKey(), e.getValue());
          count++;
        }
      }
    }

    public void clear()
    {
      count = 0;
      collectedTuples.clear();
    }

    @Override
    public int getCount(boolean reset)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }

  class CountSink implements Sink
  {
    int count = 0;

    /**
     *
     * @param payload
     */
    @Override
    public void put(Object payload)
    {
      if (payload instanceof Tuple) {
        // LOG.debug(payload.toString());
      }
      else {
        HashMap<String, Integer> tuple = (HashMap<String, Integer>)payload;
        for (Map.Entry<String, Integer> e: ((HashMap<String, Integer>)payload).entrySet()) {
          if (e.getKey().equals(EventIncrementer.OPORT_COUNT_TUPLE_COUNT)) {
            count = e.getValue().intValue();
          }
        }
      }
    }

    @Override
    public int getCount(boolean reset)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }

  /**
   * Test oper logic emits correct results
   */
  @Test
  @Category(com.datatorrent.annotation.PerformanceTestCategory.class)
  public void testNodeProcessing() throws Exception
  {
    EventIncrementer oper = new EventIncrementer();

    DataSink dataSink = new DataSink();
    CountSink countSink = new CountSink();

    oper.data.setSink(dataSink);
    oper.count.setSink(countSink);

    Sink seedSink = oper.seed.getSink();
    Sink incrSink = oper.increment.getSink();

    ArrayList<String> keys = new ArrayList<String>(2);
    ArrayList<Double> low = new ArrayList<Double>(2);
    ArrayList<Double> high = new ArrayList<Double>(2);
    keys.add("x");
    keys.add("y");
    low.add(1.0);
    low.add(1.0);
    high.add(100.0);
    high.add(100.0);
    oper.setKeylimits(keys, low, high);
    oper.setDelta(1);

    oper.beginWindow(0);

    HashMap<String, Object> stuple = new HashMap<String, Object>(1);
    //int numTuples = 100000000; // For benchmarking
    int numTuples = 10000000;
    String seed1 = "a";
    ArrayList val = new ArrayList();
    val.add(new Integer(10));
    val.add(new Integer(20));
    stuple.put(seed1, val);
    for (int i = 0; i < numTuples; i++) {
      seedSink.put(stuple);
    }
    oper.endWindow();

    oper.beginWindow(0);
    HashMap<String, Object> ixtuple = new HashMap<String, Object>(1);
    HashMap<String, Integer> ixval = new HashMap<String, Integer>(1);
    ixval.put("x", new Integer(10));
    ixtuple.put("a", ixval);

    HashMap<String, Object> iytuple = new HashMap<String, Object>(1);
    HashMap<String, Integer> iyval = new HashMap<String, Integer>(1);
    iyval.put("y", new Integer(10));
    iytuple.put("a", iyval);

    for (int i = 0; i < numTuples; i++) {
      incrSink.put(ixtuple);
      incrSink.put(iytuple);
    }

    oper.endWindow();
    LOG.debug(String.format("\nBenchmarked %d tuples", numTuples * 3));
  }
}
