/*
 * Copyright (C) 2016 The Zson Authors
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
package com.zeoflow.zson.functional;

import com.zeoflow.zson.Zson;
import com.zeoflow.zson.ZsonBuilder;
import com.zeoflow.zson.reflect.TypeToken;

import java.util.List;
import junit.framework.TestCase;

import static java.util.Collections.singletonList;

/**
 * Functional tests for leniency option.
 */
public class LeniencyTest extends TestCase {

  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new ZsonBuilder().setLenient().create();
  }

  public void testLenientFromJson() {
    List<String> json = zson.fromJson(""
        + "[ # One!\n"
        + "  'Hi' #Element!\n"
        + "] # Array!", new TypeToken<List<String>>() {}.getType());
    assertEquals(singletonList("Hi"), json);
  }
}
