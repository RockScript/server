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

import java.util.ArrayList;
import java.util.List;

public class ArgumentsExpressionExecution extends Execution<ArgumentsExpression> {

  public ArgumentsExpressionExecution(ArgumentsExpression operation, Execution parent) {
    super(parent.createInternalExecutionId(), operation, parent);
  }

  @Override
  public void start() {
    startChild(operation.getFunctionExpression());
  }

  @Override
  public void childEnded(Execution child) {
    startNextParameter();
  }

  private void startNextParameter() {
    int parameterIndex = children.size()-1; // -1 because the first one is the function expression
    List<SingleExpression> parameters = operation.getArgumentExpressions();
    if (parameterIndex < parameters.size()) {
      Operation piece = parameters.get(parameterIndex);
      startChild(piece);
    } else {
      Execution functionExpressionExecution = children.get(0);
      Action action = (Action) functionExpressionExecution.getResult();
      if (action instanceof SystemImportAction) {
        invokeSystemImportFunction();
      } else {
        startAction();
      }
    }
  }

  private void invokeSystemImportFunction() {
    // import functions have to be re-executed when the events
    // are applied because they can return functions
    dispatchAndApply(new ImportFunctionEvent(this));
    end();
  }

  private void startAction() {
    dispatchAndProceed(new ActionStartEvent(this));
  }

  public void proceedStartAction() {
    ActionResponse actionResponse = invokeAction();
    if (actionResponse.isEnded()) {
      dispatchAndProceed(new ActionEndedEvent(this, actionResponse.getResult()));

    } else {
      dispatchAndApply(new ActionWaitEvent(this));
    }
  }

  public void proceedActionEnded() {
    end();
  }

  public ActionResponse invokeAction() {
    Execution actionExecution = children.get(0);
    Action action = (Action) actionExecution.getResult();
    List<Object> args = collectArgs();
    return action.invoke(this, args);
  }

  private List<Object> collectArgs() {
    List<Object> args = new ArrayList<>();
    List<Execution> argExecutions = children.subList(1, children.size());
    for (Execution argExecution: argExecutions) {
      args.add(argExecution.getResult());
    }
    return args;
  }

  public void functionEnded() {
    functionEnded(null);
  }

  public void functionEnded(Object result) {
    dispatchAndProceed(new ActionEndedEvent(this, result));
  }
}