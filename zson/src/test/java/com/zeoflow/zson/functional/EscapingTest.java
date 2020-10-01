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

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 * Performs some functional test involving JSON output escaping.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class EscapingTest extends TestCase {
  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new Zson();
  }

  public void testEscapingQuotesInStringArray() throws Exception {
    String[] valueWithQuotes = { "beforeQuote\"afterQuote" };
    String jsonRepresentation = zson.toJson(valueWithQuotes);
    String[] target = zson.fromJson(jsonRepresentation, String[].class);
    assertEquals(1, target.length);
    assertEquals(valueWithQuotes[0], target[0]);
  }

  public void testEscapeAllHtmlCharacters() {
    List<String> strings = new ArrayList<String>();
    strings.add("<");
    strings.add(">");
    strings.add("=");
    strings.add("&");
    strings.add("'");
    strings.add("\"");
    assertEquals("[\"\\u003c\",\"\\u003e\",\"\\u003d\",\"\\u0026\",\"\\u0027\",\"\\\"\"]",
        zson.toJson(strings));
  }

  public void testEscapingObjectFields() throws Exception {
    TestTypes.BagOfPrimitives objWithPrimitives = new TestTypes.BagOfPrimitives(1L, 1, true, "test with\" <script>");
    String jsonRepresentation = zson.toJson(objWithPrimitives);
    assertFalse(jsonRepresentation.contains("<"));
    assertFalse(jsonRepresentation.contains(">"));
    assertTrue(jsonRepresentation.contains("\\\""));

    TestTypes.BagOfPrimitives expectedObject = zson.fromJson(jsonRepresentation, TestTypes.BagOfPrimitives.class);
    assertEquals(objWithPrimitives.getExpectedJson(), expectedObject.getExpectedJson());
  }
  
  public void testZsonAcceptsEscapedAndNonEscapedJsonDeserialization() throws Exception {
    Zson escapeHtmlZson = new ZsonBuilder().create();
    Zson noEscapeHtmlZson = new ZsonBuilder().disableHtmlEscaping().create();
    
    TestTypes.BagOfPrimitives target = new TestTypes.BagOfPrimitives(1L, 1, true, "test' / w'ith\" / \\ <script>");
    String escapedJsonForm = escapeHtmlZson.toJson(target);
    String nonEscapedJsonForm = noEscapeHtmlZson.toJson(target);
    assertFalse(escapedJsonForm.equals(nonEscapedJsonForm));
    
    assertEquals(target, noEscapeHtmlZson.fromJson(escapedJsonForm, TestTypes.BagOfPrimitives.class));
    assertEquals(target, escapeHtmlZson.fromJson(nonEscapedJsonForm, TestTypes.BagOfPrimitives.class));
  }

  public void testZsonDoubleDeserialization() {
    TestTypes.BagOfPrimitives expected = new TestTypes.BagOfPrimitives(3L, 4, true, "value1");
    String json = zson.toJson(zson.toJson(expected));
    String value = zson.fromJson(json, String.class);
    TestTypes.BagOfPrimitives actual = zson.fromJson(value, TestTypes.BagOfPrimitives.class);
    assertEquals(expected, actual);
  }
}
