/*
 * Copyright 2013-2014 Alexei Barantsev
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package ru.stqa.selenium.wait;

import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Duration;
import org.openqa.selenium.support.ui.Sleeper;

import java.util.concurrent.TimeUnit;

public class TestingClock implements Clock, Sleeper {

  private long now = 0;

  @Override
  public long now() {
    return now;
  }

  @Override
  public long laterBy(long duration) {
    return now + duration;
  }

  @Override
  public boolean isNowBefore(long finish) {
    return now < finish;
  }

  @Override
  public void sleep(Duration duration) {
    now += duration.in(TimeUnit.MILLISECONDS);
  }
}
