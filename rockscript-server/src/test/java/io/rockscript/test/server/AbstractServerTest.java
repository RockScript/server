/*
 * Copyright (c) 2017 RockScript.io.
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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
package io.rockscript.test.server;

import io.rockscript.Engine;
import io.rockscript.Servlet;
import io.rockscript.TestEngine;
import io.rockscript.http.client.Http;
import io.rockscript.http.server.HttpServer;
import io.rockscript.test.AbstractHttpServerTest;
import io.rockscript.test.HttpServerTest;
import org.junit.AfterClass;
import org.junit.Before;

import static org.junit.Assert.assertNotNull;

public class AbstractServerTest extends AbstractHttpServerTest {

  static Engine engine = null;

  @Override
  @Before
  public void setUp() {
    if (engine==null) {
      // First the engine will be initialized
      engine = new TestEngine().start();
    }
    // The super.setUp will invoke the configureServer below
    super.setUp();
  }

  /** Invoked by super.setUp */
  @Override
  protected void configureServer(HttpServer server) {
    // Now, the engine is already initialized.  See #setUp()
    assertNotNull(engine);
    Servlet servlet = new Servlet(engine);
    servlet.gson(engine.getGson());
    server.servlet(servlet);
  }

  /** All tests in subclasses of AbstractServerTest that are executed
   * subsequent, will use the same server as configured in
   * {@link #configureServer(HttpServer)}*/
  @Override
  protected String getServerName() {
    return AbstractServerTest.class.getName();
  }

  @Override
  protected Http createHttp() {
    return new Http(engine.getGson());
  }

  /** {@link AbstractHttpServerTest#tearDownStatic()} will shut down the server */
  @AfterClass
  public static void tearDownStatic() {
    // AbstractHttpServerTest.tearDownStatic will shutdown the server
    engine = null;
  }
}
