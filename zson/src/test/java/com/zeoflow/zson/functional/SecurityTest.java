/*
 * Copyright (C) 2020 ZeoFlow
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
import com.zeoflow.zson.common.TestTypes;

import junit.framework.TestCase;

/**
 * Tests for security-related aspects of Zson
 * 
 * @author Inderjeet Singh
 */
public class SecurityTest extends TestCase {
  /**
   * Keep this in sync with Zson.JSON_NON_EXECUTABLE_PREFIX
   */
  private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";

  private ZsonBuilder zsonBuilder;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zsonBuilder = new ZsonBuilder();
  }

  public void testNonExecutableJsonSerialization() {
    Zson zson = zsonBuilder.generateNonExecutableJson().create();
    String json = zson.toJson(new TestTypes.BagOfPrimitives());
    assertTrue(json.startsWith(JSON_NON_EXECUTABLE_PREFIX));
  }
  
  public void testNonExecutableJsonDeserialization() {
    String json = JSON_NON_EXECUTABLE_PREFIX + "{longValue:1}";
    Zson zson = zsonBuilder.create();
    TestTypes.BagOfPrimitives target = zson.fromJson(json, TestTypes.BagOfPrimitives.class);
    assertEquals(1, target.longValue);
  }
  
  public void testJsonWithNonExectuableTokenSerialization() {
    Zson zson = zsonBuilder.generateNonExecutableJson().create();
    String json = zson.toJson(JSON_NON_EXECUTABLE_PREFIX);
    assertTrue(json.contains(")]}'\n"));
  }
  
  /**
   *  Zson should be able to deserialize a stream with non-exectuable token even if it is created
   *  without {@link ZsonBuilder#generateNonExecutableJson()}.
   */
  public void testJsonWithNonExectuableTokenWithRegularZsonDeserialization() {
    Zson zson = zsonBuilder.create();
    String json = JSON_NON_EXECUTABLE_PREFIX + "{stringValue:')]}\\u0027\\n'}";
    TestTypes.BagOfPrimitives target = zson.fromJson(json, TestTypes.BagOfPrimitives.class);
    assertEquals(")]}'\n", target.stringValue);
  }  
  
  /**
   *  Zson should be able to deserialize a stream with non-exectuable token if it is created
   *  with {@link ZsonBuilder#generateNonExecutableJson()}.
   */
  public void testJsonWithNonExectuableTokenWithConfiguredZsonDeserialization() {
    // Zson should be able to deserialize a stream with non-exectuable token even if it is created 
    Zson zson = zsonBuilder.generateNonExecutableJson().create();
    String json = JSON_NON_EXECUTABLE_PREFIX + "{intValue:2,stringValue:')]}\\u0027\\n'}";
    TestTypes.BagOfPrimitives target = zson.fromJson(json, TestTypes.BagOfPrimitives.class);
    assertEquals(")]}'\n", target.stringValue);
    assertEquals(2, target.intValue);
  }  
}
