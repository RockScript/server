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

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Location {

  private int startIndex;
  private int startLine;
  private int startColumn;
  private int endIndex;
  private int endLine;
  private int endColumn;

  public Location(ParserRuleContext parserRuleContext) {
    Token start = parserRuleContext.getStart();
    startIndex = start.getStartIndex();
    startLine = start.getLine();
    startColumn = start.getCharPositionInLine();
    Token stop = parserRuleContext.getStop();
    endIndex = stop.getStopIndex();
    endLine = stop.getLine();
    endColumn = stop.getCharPositionInLine();
  }

  public int getStartIndex() {
    return startIndex;
  }

  public int getStartLine() {
    return startLine;
  }

  public int getStartColumn() {
    return startColumn;
  }

  public int getEndIndex() {
    return endIndex;
  }

  public int getEndLine() {
    return endLine;
  }

  public int getEndColumn() {
    return endColumn;
  }
}
