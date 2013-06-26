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
package com.datatorrent.lib.algo;

import com.datatorrent.engine.TestSink;
import com.datatorrent.lib.algo.FirstN;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Functional tests for {@link com.datatorrent.lib.algo.FirstN}<p>
 */
public class FirstNTest
{
  private static Logger log = LoggerFactory.getLogger(FirstNTest.class);

  /**
   * Test node logic emits correct results
   */
  @Test
  @SuppressWarnings("SleepWhileInLoop")
  public void testNodeProcessing() throws Exception
  {
    testNodeProcessingSchema(new FirstN<String, Integer>());
    testNodeProcessingSchema(new FirstN<String, Double>());
    testNodeProcessingSchema(new FirstN<String, Float>());
    testNodeProcessingSchema(new FirstN<String, Short>());
    testNodeProcessingSchema(new FirstN<String, Long>());
  }

  public void testNodeProcessingSchema(FirstN oper)
  {
    TestSink sortSink = new TestSink();
    oper.first.setSink(sortSink);
    oper.setN(3);

    oper.beginWindow(0);
    HashMap<String, Number> input = new HashMap<String, Number>();

    input.put("a", 2);
    oper.data.process(input);

    input.clear();
    input.put("a", 20);
    oper.data.process(input);

    input.clear();
    input.put("a", 1000);
    oper.data.process(input);

    input.clear();
    input.put("a", 5);
    oper.data.process(input);

    input.clear();
    input.put("a", 20);
    input.put("b", 33);
    oper.data.process(input);

    input.clear();
    input.put("a", 33);
    input.put("b", 34);
    oper.data.process(input);

    input.clear();
    input.put("b", 34);
    input.put("a", 1001);
    oper.data.process(input);

    input.clear();
    input.put("b", 6);
    input.put("a", 1);
    oper.data.process(input);
    input.clear();
    input.put("c", 9);
    oper.data.process(input);
    oper.endWindow();

    Assert.assertEquals("number emitted tuples", 7, sortSink.collectedTuples.size());
    int aval = 0;
    int bval = 0;
    int cval = 0;
    for (Object o: sortSink.collectedTuples) {
      for (Map.Entry<String, Number> e: ((HashMap<String, Number>)o).entrySet()) {
        if (e.getKey().equals("a")) {
          aval += e.getValue().intValue();
        }
        else if (e.getKey().equals("b")) {
          bval += e.getValue().intValue();
        }
        else if (e.getKey().equals("c")) {
          cval += e.getValue().intValue();
        }
      }
    }
    Assert.assertEquals("Value of \"a\" was ", 1022, aval);
    Assert.assertEquals("Value of \"a\" was ", 101, bval);
    Assert.assertEquals("Value of \"a\" was ", 9, cval);
    log.debug("Done testing round\n");
  }
}
