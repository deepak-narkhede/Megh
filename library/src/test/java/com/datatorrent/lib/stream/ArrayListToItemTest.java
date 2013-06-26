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
package com.datatorrent.lib.stream;

import com.datatorrent.lib.stream.ArrayListToItem;
import com.datatorrent.lib.testbench.CountTestSink;

import java.util.ArrayList;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performance test for {@link com.datatorrent.lib.testbench.StreamDuplicater}<p>
 * Benchmarks: Currently does about ?? Million tuples/sec in debugging environment. Need to test on larger nodes<br>
 * <br>
 */
public class ArrayListToItemTest {

    private static Logger log = LoggerFactory.getLogger(ArrayListToItemTest.class);

    /**
     * Test oper pass through. The Object passed is not relevant
     */
    @Test
    public void testNodeProcessing() throws Exception
    {
      ArrayListToItem oper = new ArrayListToItem();
      CountTestSink itemSink = new CountTestSink();
      oper.item.setSink(itemSink);

      oper.beginWindow(0);
      ArrayList<String> input = new ArrayList<String>();
      input.add("a");
      // Same input object can be used as the oper is just pass through
      int numtuples = 1000;
      for (int i = 0; i < numtuples; i++) {
        oper.data.process(input);
      }

      oper.endWindow();
      Assert.assertEquals("number emitted tuples", numtuples, itemSink.count);
    }
}
