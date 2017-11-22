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

import com.google.gson.Gson;
import io.rockscript.Engine;
import io.rockscript.http.servlet.HttpRequest;
import io.rockscript.http.servlet.HttpResponse;
import io.rockscript.http.servlet.Post;

@Post("/command")
public class CommandHandler extends AbstractRequestHandler {

  public CommandHandler(Engine engine) {
    super(engine);
  }

  @Override
  public void handle(HttpRequest request, HttpResponse response) {
    Command command = null;
    try {
      Gson gson = engine.getGson();
      String jsonBodyString = request.getBodyStringUtf8();
      command = gson.fromJson(jsonBodyString, Command.class);
      Response commandResponse = command.execute(engine);

      String responseBodyJson = gson.toJson(commandResponse);
      response.bodyString(responseBodyJson);
      response.status(commandResponse.getStatus());

    } catch (Exception e) {
      response.statusInternalServerError();
    }
  }
}