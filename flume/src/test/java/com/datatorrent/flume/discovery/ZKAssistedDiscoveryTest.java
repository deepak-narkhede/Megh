/**
 * Copyright (c) 2016 DataTorrent, Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.datatorrent.flume.discovery;

import org.codehaus.jackson.type.TypeReference;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;

import com.datatorrent.flume.discovery.Discovery.Service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author Chetan Narsude <chetan@datatorrent.com>
 */
@Ignore
public class ZKAssistedDiscoveryTest
{
  public ZKAssistedDiscoveryTest()
  {
  }

  @Test
  public void testSerialization() throws Exception
  {
    ZKAssistedDiscovery discovery = new ZKAssistedDiscovery();
    discovery.setServiceName("DTFlumeTest");
    discovery.setConnectionString("localhost:2181");
    discovery.setBasePath("/HelloDT");
    discovery.setup(null);
    ServiceInstance<byte[]> instance = discovery.getInstance(new Service<byte[]>()
    {
      @Override
      public String getHost()
      {
        return "localhost";
      }

      @Override
      public int getPort()
      {
        return 8080;
      }

      @Override
      public byte[] getPayload()
      {
        return null;
      }

      @Override
      public String getId()
      {
        return "localhost8080";
      }

    });
    InstanceSerializer<byte[]> instanceSerializer =
        discovery.getInstanceSerializerFactory().getInstanceSerializer(new TypeReference<ServiceInstance<byte[]>>()
        {
        });
    byte[] serialize = instanceSerializer.serialize(instance);
    logger.debug("serialized json = {}", new String(serialize));
    ServiceInstance<byte[]> deserialize = instanceSerializer.deserialize(serialize);
    assertArrayEquals("Metadata", instance.getPayload(), deserialize.getPayload());
  }

  @Test
  public void testDiscover()
  {
    ZKAssistedDiscovery discovery = new ZKAssistedDiscovery();
    discovery.setServiceName("DTFlumeTest");
    discovery.setConnectionString("localhost:2181");
    discovery.setBasePath("/HelloDT");
    discovery.setup(null);
    assertNotNull("Discovered Sinks", discovery.discover());
    discovery.teardown();
  }

  @Test
  public void testAdvertize()
  {
    ZKAssistedDiscovery discovery = new ZKAssistedDiscovery();
    discovery.setServiceName("DTFlumeTest");
    discovery.setConnectionString("localhost:2181");
    discovery.setBasePath("/HelloDT");
    discovery.setup(null);

    Service<byte[]> service = new Service<byte[]>()
    {
      @Override
      public String getHost()
      {
        return "chetan";
      }

      @Override
      public int getPort()
      {
        return 5033;
      }

      @Override
      public byte[] getPayload()
      {
        return new byte[] {3, 2, 1};
      }

      @Override
      public String getId()
      {
        return "uniqueId";
      }

    };
    discovery.advertise(service);
    discovery.teardown();
  }

  private static final Logger logger = LoggerFactory.getLogger(ZKAssistedDiscoveryTest.class);
}
