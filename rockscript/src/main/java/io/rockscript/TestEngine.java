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
package io.rockscript;

import io.rockscript.engine.job.Job;
import io.rockscript.engine.job.JobService;

import java.util.List;
import java.util.concurrent.Executor;

/** Engine with predictable execution of async stuff because
 * it's all executed directly in the thread of the client. */
public class TestEngine extends Engine {

  public TestEngine() {
    this.executor = createExecutor();
  }

  @Override
  public TestEngine start() {
    return (TestEngine) super.start();
  }

  protected Executor createExecutor() {
    // In test, commands are executed in the thread of the caller
    // when they commands are being added to the executor.
    // This makes testing predictable and hence easier.
    return new Executor() {
      @Override
      public void execute(Runnable command) {
        command.run();
      }
    };
  }

  @Override
  protected JobService createJobService() {
    return new TestJobService(this);
  }

  public static class TestJobService extends JobService {
    public TestJobService(Engine engine) {
      super(engine);
    }
    public List<Job> getjobs() {
      return jobs;
    }

    @Override
    public void executeJob(Job job) {
      super.executeJob(job);
    }
  }

  @Override
  public TestJobService getJobService() {
    return (TestJobService) super.getJobService();
  }
}
