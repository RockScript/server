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
package io.rockscript.http.servlet;

import com.google.gson.Gson;
import io.rockscript.http.client.Http;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

public class ServerResponse {

  HttpServletResponse response;
  Gson gson;
  String logProtocol;
  String logBody;
  boolean isBodyStarted;

  public ServerResponse(HttpServletResponse response, Gson gson, String logProtocol) {
    this.response = response;
    this.gson = gson;
    this.logProtocol = logProtocol;
  }

  public ServerResponse statusOk() {
    return status(200);
  }

  public ServerResponse statusNotFound() {
    return status(404);
  }

  public ServerResponse statusInternalServerError() {
    return status(500);
  }

  public ServerResponse status(int status) {
    this.response.setStatus(status);
    return this;
  }

  public ServerResponse bodyBytes(byte[] bytes) {
    if (bytes!=null) {
      bodyBytesLog(bytes, "..." + bytes.length + " bytes...");
    }
    return this;
  }

  public ServerResponse bodyBytesLog(byte[] bytes, String bodyLog) {
    if (bytes!=null) {
      writeBodyBytes(bytes);
    }
    this.logBody = bodyLog;
    return this;
  }

  private void writeBodyBytes(byte[] bytes) {
    try {
      headerContentLength(bytes.length);
      isBodyStarted = true;
      ServletOutputStream out = response.getOutputStream();
      out.write(bytes);
      out.flush();
    } catch (IOException e) {
      throw new RuntimeException("Couldn't send body: "+e.getMessage(), e);
    }
  }

  public ServerResponse bodyString(String responseBody) {
    return bodyStringLog(responseBody, responseBody);
  }

  public ServerResponse bodyStringLog(String responseBody, String bodyLog) {
    this.logBody = bodyLog;
    byte[] bytes = responseBody.getBytes(Charset.forName("UTF-8"));
    writeBodyBytes(bytes);
    return this;
  }

  public ServerResponse bodyJsonString(String responseBody) {
    headerContentTypeApplicationJson();
    bodyString(responseBody);
    return this;
  }

  public ServerResponse bodyJson(Object object) {
    return bodyJsonString(gson.toJson(object));
  }

  public ServerResponse header(String name, String value) {
    if (isBodyStarted) {
      throw new RuntimeException("Bug: headers need to be set before the body is sent");
    }
    if (name!=null && value!=null) {
      response.addHeader(name, value);
    }
    return this;
  }

  public ServerResponse headerContentLength(Number value) {
    return header(Http.Headers.CONTENT_LENGTH, value!=null ? value.toString() : null);
  }

  public ServerResponse headerContentType(String value) {
    return header(Http.Headers.CONTENT_TYPE, value);
  }

  public ServerResponse headerContentTypeTextPlain() {
    return header(Http.Headers.CONTENT_TYPE, Http.ContentTypes.TEXT_PLAIN);
  }

  public ServerResponse headerContentTypeApplicationJson() {
    return header(Http.Headers.CONTENT_TYPE, Http.ContentTypes.APPLICATION_JSON);
  }

  public ServerResponse headerContentTypeTextHtml() {
    return header(Http.Headers.CONTENT_TYPE, Http.ContentTypes.TEXT_HTML);
  }

  public String toString() {
    String prefix = "  ";
    return "\n< " + logProtocol + " " + response.getStatus() + " " + Http.ResponseCodes.getText(response.getStatus()) +
           // getLogHeaders(prefix) +
           ServerRequest.getLogBody(prefix, logBody);
  }

  private String getLogHeaders(String prefix) {
    if (response.getHeaderNames()!=null && !response.getHeaderNames().isEmpty()) {
      return "\n" +
        response.getHeaderNames().stream()
          .map(headerName->{
            return response.getHeaders(headerName).stream()
              .map(headerValue->{
                return prefix+headerName+": "+headerValue;
              }).collect(Collectors.joining("\n"));
          }).collect(Collectors.joining("\n"));
    } else {
      return "";
    }
  }

  public void sendRedirect(String location) {
    try {
      response.sendRedirect(location);
    } catch (IOException e) {
      throw new InternalServerException();
    }
  }
}