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
import com.zeoflow.zson.JsonObject;
import com.zeoflow.zson.common.TestTypes;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Functional tests for print formatting.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class PrintFormattingTest extends TestCase {

  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new Zson();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void testCompactFormattingLeavesNoWhiteSpace() {
    List list = new ArrayList();
    list.add(new TestTypes.BagOfPrimitives());
    list.add(new TestTypes.Nested());
    list.add(new TestTypes.PrimitiveArray());
    list.add(new TestTypes.ClassWithTransientFields());

    String json = zson.toJson(list);
    assertContainsNoWhiteSpace(json);
  }

  public void testJsonObjectWithNullValues() {
    JsonObject obj = new JsonObject();
    obj.addProperty("field1", "value1");
    obj.addProperty("field2", (String) null);
    String json = zson.toJson(obj);
    assertTrue(json.contains("field1"));
    assertFalse(json.contains("field2"));
  }

  public void testJsonObjectWithNullValuesSerialized() {
    zson = new ZsonBuilder().serializeNulls().create();
    JsonObject obj = new JsonObject();
    obj.addProperty("field1", "value1");
    obj.addProperty("field2", (String) null);
    String json = zson.toJson(obj);
    assertTrue(json.contains("field1"));
    assertTrue(json.contains("field2"));
  }

  private static void assertContainsNoWhiteSpace(String str) {
    for (char c : str.toCharArray()) {
      assertFalse(Character.isWhitespace(c));
    }
  }
}
