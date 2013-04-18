/*
 *  Copyright (c) 2012 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.lib.algo;

import com.malhartech.annotation.InputPortFieldAnnotation;
import com.malhartech.annotation.OutputPortFieldAnnotation;
import com.malhartech.api.BaseOperator;
import com.malhartech.api.DefaultInputPort;
import com.malhartech.api.DefaultOutputPort;
import com.malhartech.api.Operator.Unifier;

/**
 * Counts the number of tuples emitted in a window. <p>
 * This is an end of window operator<br>
 * <br>
 * <b>Ports</b>:<br>
 * <b>data</b>: expects K<br>
 * <b>count</b>: emits Integer<br>
 * <b>Properties</b>: None<br>
 * <br>
 * <b>Specific compile time checks</b>: None<br>
 * <b>Specific run time checks</b>:<br>
 * <br>
 * <b>Benchmarks</b>: Blast as many tuples as possible in inline mode<br>
 * <table border="1" cellspacing=1 cellpadding=1 summary="Benchmark table for UniqueCounter&lt;K&gt; operator template">
 * <tr><th>In-Bound</th><th>Out-bound</th><th>Comments</th></tr>
 * <tr><td><b>&gt; processes 110 Million K,V pairs/s</b></td><td>Emits one tuple per window</td><td>In-bound throughput
 * and number of unique k are the main determinant of performance. Tuples are assumed to be immutable. If you use mutable tuples and have lots of keys,
 * the benchmarks may be lower</td></tr>
 * </table><br>
 * <p>
 * <b>Function Table (K=String)</b>:
 * <table border="1" cellspacing=1 cellpadding=1 summary="Function table for UniqueCounter&lt;K&gt; operator template">
 * <tr><th rowspan=2>Tuple Type (api)</th><th>In-bound (process)</th><th>Out-bound (emit)</th></tr>
 * <tr><th><i>data</i>(K)</th><th><i>count</i>(Integer)</th></tr>
 * <tr><td>Begin Window (beginWindow())</td><td>N/A</td></tr>
 * <tr><td>Data (process())</td><td>a</td></tr>
 * <tr><td>Data (process())</td><td>b</td></tr>
 * <tr><td>Data (process())</td><td>c</td></tr>
 * <tr><td>Data (process())</td><td>4</td></tr>
 * <tr><td>Data (process())</td><td>5ah</td></tr>
 * <tr><td>Data (process())</td><td>h</td></tr>
 * <tr><td>Data (process())</td><td>a</td></tr>
 * <tr><td>Data (process())</td><td>a</td></tr>
 * <tr><td>Data (process())</td><td>a</td></tr>
 * <tr><td>Data (process())</td><td>a</td></tr>
 * <tr><td>Data (process())</td><td>5ah</td></tr>
 * <tr><td>Data (process())</td><td>a</td></tr>
 * <tr><td>Data (process())</td><td>c</td></tr>
 * <tr><td>Data (process())</td><td>c</td></tr>
 * <tr><td>Data (process())</td><td>b</td></tr>
 * <tr><td>End Window (endWindow())</td><td>N/A</td><td>15</td></tr>
 * </table>
 * <br>
 *
 * @author Locknath Shil <locknath@malhar-inc.com><br>
 */
public class UniqueCounterValue<K> extends BaseOperator implements Unifier<Integer>
{
  @InputPortFieldAnnotation(name = "data")
  public final transient DefaultInputPort<K> data = new DefaultInputPort<K>(this)
  {
    /**
     * Counts tuples.
     */
    @Override
    public void process(K tuple)
    {
      counts++;
    }
  };
  @OutputPortFieldAnnotation(name = "count")
  public final transient DefaultOutputPort<Integer> count = new DefaultOutputPort<Integer>(this)
  {
    @Override
    public Unifier<Integer> getUnifier()
    {
      return UniqueCounterValue.this;
    }
  };
  protected transient int counts = 0;

  /**
   * Emits total number of tuples.
   */
  @Override
  public void endWindow()
  {
    if (counts != 0) {
      count.emit(counts);
    }
    counts = 0;
  }

  @Override
  public void process(Integer tuple)
  {
    counts += tuple;
  }
}
