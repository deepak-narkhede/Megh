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
package com.datatorrent.contrib.romesyndication;

import java.io.IOException;
import java.io.InputStream;

/**
 * The interface to implement for any provider that wants to provide syndication feed.<p><br>
 *
 * <br>
 *
 */
public interface RomeStreamProvider
{
  /**
   * Get the feed input stream.
   *
   * @return The feed input stream
   * @throws IOException
   */
  public InputStream getInputStream() throws IOException;

}
