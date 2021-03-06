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
package io.rockscript.engine;

import io.rockscript.Engine;
import io.rockscript.api.AbstractRequestHandler;
import io.rockscript.http.servlet.ServerRequest;
import io.rockscript.http.servlet.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingHandler extends AbstractRequestHandler {

  static Logger log = LoggerFactory.getLogger(PingHandler.class);

  public PingHandler() {
    super(GET, "/ping");
  }

  @Override
  public void handle(ServerRequest request, ServerResponse response) {
    response
      .headerContentTypeTextPlain()
      .bodyString("pong\n")
      .statusOk();
  }
}
