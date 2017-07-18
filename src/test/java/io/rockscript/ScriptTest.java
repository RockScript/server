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
 *
 */
package io.rockscript;

import java.util.ArrayList;
import java.util.List;

import io.rockscript.engine.*;
import io.rockscript.test.DeepComparator;
import io.rockscript.test.TestEngine;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScriptTest {

  protected static Logger log = LoggerFactory.getLogger(ScriptTest.class);

  EngineImpl engine = createTestEngine();
  List<Object> synchronousCapturedData = new ArrayList<>();
  List<String> waitingAsyncFunctionInvocationIds = new ArrayList<>();

  public EngineImpl createTestEngine() {
    TestEngine engine = new TestEngine();
    ImportResolver importResolver = engine.getServiceLocator().getImportResolver();
    JsonObject helloService = new JsonObject()
      .put("aSyncFunction", functionInput->{
          synchronousCapturedData.add("Execution was here");
          synchronousCapturedData.add(functionInput.getArgs().get(0));
          return ActionResponse.endFunction();})
      .put("anAsyncFunction", functionInput->{
          ArgumentsExpressionExecution argumentsExpressionExecution = functionInput.getArgumentsExpressionExecution();
          waitingAsyncFunctionInvocationIds.add(argumentsExpressionExecution.getId());
          return ActionResponse.waitForFunctionToCompleteAsync();});
    importResolver.add("example.com/hello", helloService);
    return engine;
  }

  @Test
  public void testAsyncExecution() {
    String scriptId = engine.deployScript(
        "var helloService = system.import('example.com/hello'); \n" +
        "var message = 5; \n" +
        "helloService.aSyncFunction(message); \n"+
        "helloService.anAsyncFunction(); \n" +
        "helloService.aSyncFunction('hello');");

    String scriptExecutionId = engine.startScriptExecution(scriptId);

    assertEquals("Execution was here", synchronousCapturedData.get(0));
    assertEquals(5d, synchronousCapturedData.get(1));
    assertEquals(2, synchronousCapturedData.size());

    String waitingExecutionId = waitingAsyncFunctionInvocationIds.get(0);
    assertNotNull(waitingExecutionId);

    engine.endWaitingAction(scriptExecutionId, waitingExecutionId);

    assertEquals("Execution was here", synchronousCapturedData.get(2));
    assertEquals("hello", synchronousCapturedData.get(3));
    assertEquals(4, synchronousCapturedData.size());
  }

  @Test
  public void testSerialization() {
    String scriptId = engine.deployScript(
      "var helloService = system.import('example.com/hello'); \n" +
        "helloService.anAsyncFunction(); \n" +
        "helloService.aSyncFunction('hello');");

    ScriptExecution scriptExecution = engine.startScriptExecutionImpl(scriptId);
    String scriptExecutionId = scriptExecution.getId();

    ScriptExecution reloadedScriptExecution = engine
      .getServiceLocator()
      .getEventStore()
      .loadScriptExecution(scriptExecutionId);

    DeepComparator deepComparator = new DeepComparator()
      .ignoreField(ScriptExecution.class, "serviceLocator")
      .ignoreField(ScriptExecution.class, "eventListener")
      .ignoreField(Script.class, "executables")
      .ignoreField(Script.class, "serviceLocator")
      .ignoreField(SystemImportAction.class, "serviceLocator")
      .ignoreAnonymousField(Action.class, "val$functionHandler");
    deepComparator.assertEquals(scriptExecution, reloadedScriptExecution);

    String waitingExecutionId = waitingAsyncFunctionInvocationIds.get(0);

    scriptExecution = engine.endWaitingActionImpl(scriptExecutionId, waitingExecutionId, null);

    System.err.println();
    System.err.println();
    reloadedScriptExecution = engine
      .getServiceLocator()
      .getEventStore()
      .loadScriptExecution(scriptExecutionId);

    deepComparator.assertEquals(scriptExecution, reloadedScriptExecution);
  }

}
