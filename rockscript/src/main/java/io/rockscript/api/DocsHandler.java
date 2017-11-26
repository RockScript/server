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
package io.rockscript.api;

import io.rockscript.Engine;
import io.rockscript.http.servlet.ServerRequest;
import io.rockscript.http.servlet.ServerResponse;
import io.rockscript.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.rockscript.util.Maps.entry;
import static io.rockscript.util.Maps.hashMap;

public class DocsHandler extends AbstractRequestHandler {

  static Logger log = LoggerFactory.getLogger(DocsHandler.class);

  public DocsHandler(Engine engine) {
    super(GET, "/docs", engine);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void handle(ServerRequest request, ServerResponse response) {
    response.bodyJson(hashMap(
      entry("commands", Lists.of(
        hashMap(
          entry("id", "saveScript"),
          entry("label", "Save script"),
          entry("content",
            "# Save script \n" +
            "Here's the `explaination` on how to \n" +
            "```\n" +
            "save.script();\n" +
            "```"
          )
        )
      ))
    ));
  }
}
