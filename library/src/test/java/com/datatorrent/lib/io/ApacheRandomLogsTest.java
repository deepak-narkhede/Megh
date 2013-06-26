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
package com.datatorrent.lib.io;

import junit.framework.Assert;
import org.junit.Test;

import com.datatorrent.engine.TestSink;
import com.datatorrent.lib.io.ApacheGenRandomLogs;

/**
 * Unit test for emit tuples.
 */
public class ApacheRandomLogsTest
{
	@Test
	public void test()
	{
		ApacheGenRandomLogs oper = new ApacheGenRandomLogs();
		TestSink sink = new TestSink();
		oper.outport.setSink(sink);
		oper.setup(null);
		
		Thread t = new EmitTuples(oper);
		t.start();
		try
		{
			Thread.sleep(1000);
		} catch (InterruptedException e)
		{
		}
		t.stop();
		Assert.assertTrue("Tuples emitted", sink.collectedTuples.size() > 0);
		System.out.println(sink.collectedTuples.size());
	}
	
	private class EmitTuples extends Thread {
		private ApacheGenRandomLogs oper;
		public EmitTuples(ApacheGenRandomLogs oper)
		{
			this.oper = oper;
		}
		@Override
		public void run()
		{
			oper.emitTuples();
		}
	}
}
