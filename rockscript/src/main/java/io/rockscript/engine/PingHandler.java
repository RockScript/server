/*
 * Copyright ©2017, RockScript.io. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rockscript.engine;

import io.rockscript.Engine;
import io.rockscript.api.AbstractRequestHandler;
import io.rockscript.http.servlet.Get;
import io.rockscript.http.servlet.HttpRequest;
import io.rockscript.http.servlet.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Get("/ping")
public class PingHandler extends AbstractRequestHandler {

  static Logger log = LoggerFactory.getLogger(PingHandler.class);

  public PingHandler(Engine engine) {
    super(engine);
  }

  @Override
  public void handle(HttpRequest request, HttpResponse response) {
    response.statusOk();
    response.bodyString("pong");
    response.headerContentTypeTextPlain();
  }
}