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
import com.zeoflow.zson.JsonDeserializationContext;
import com.zeoflow.zson.JsonDeserializer;
import com.zeoflow.zson.JsonElement;
import com.zeoflow.zson.JsonParseException;

import com.zeoflow.zson.reflect.TypeToken;
import com.zeoflow.zson.common.TestTypes;

import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

import java.lang.reflect.Type;

/**
 * Functional tests that do not fall neatly into any of the existing classification.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class UncategorizedTest extends TestCase {

  private Zson zson = null;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new Zson();
  }

  public void testInvalidJsonDeserializationFails() throws Exception {
    try {
      zson.fromJson("adfasdf1112,,,\":", TestTypes.BagOfPrimitives.class);
      fail("Bad JSON should throw a ParseException");
    } catch (JsonParseException expected) { }

    try {
      zson.fromJson("{adfasdf1112,,,\":}", TestTypes.BagOfPrimitives.class);
      fail("Bad JSON should throw a ParseException");
    } catch (JsonParseException expected) { }
  }

  public void testObjectEqualButNotSameSerialization() throws Exception {
    TestTypes.ClassOverridingEquals objA = new TestTypes.ClassOverridingEquals();
    TestTypes.ClassOverridingEquals objB = new TestTypes.ClassOverridingEquals();
    objB.ref = objA;
    String json = zson.toJson(objB);
    assertEquals(objB.getExpectedJson(), json);
  }

  public void testStaticFieldsAreNotSerialized() {
    TestTypes.BagOfPrimitives target = new TestTypes.BagOfPrimitives();
    assertFalse(zson.toJson(target).contains("DEFAULT_VALUE"));
  }

  public void testZsonInstanceReusableForSerializationAndDeserialization() {
    TestTypes.BagOfPrimitives bag = new TestTypes.BagOfPrimitives();
    String json = zson.toJson(bag);
    TestTypes.BagOfPrimitives deserialized = zson.fromJson(json, TestTypes.BagOfPrimitives.class);
    assertEquals(bag, deserialized);
  }

  /**
   * This test ensures that a custom deserializer is able to return a derived class instance for a
   * base class object. For a motivation for this test, see Issue 37 and
   * http://groups.google.com/group/google-Zson/browse_thread/thread/677d56e9976d7761
   */
  public void testReturningDerivedClassesDuringDeserialization() {
    Zson zson = new ZsonBuilder().registerTypeAdapter(Base.class, new BaseTypeAdapter()).create();
    String json = "{\"opType\":\"OP1\"}";
    Base base = zson.fromJson(json, Base.class);
    assertTrue(base instanceof Derived1);
    assertEquals(OperationType.OP1, base.opType);

    json = "{\"opType\":\"OP2\"}";
    base = zson.fromJson(json, Base.class);
    assertTrue(base instanceof Derived2);
    assertEquals(OperationType.OP2, base.opType);
  }

  /**
   * Test that trailing whitespace is ignored.
   * http://code.google.com/p/google-Zson/issues/detail?id=302
   */
  public void testTrailingWhitespace() throws Exception {
    List<Integer> integers = zson.fromJson("[1,2,3]  \n\n  ",
        new TypeToken<List<Integer>>() {}.getType());
    assertEquals(Arrays.asList(1, 2, 3), integers);
  }

  private enum OperationType { OP1, OP2 }
  private static class Base {
    OperationType opType;
  }
  private static class Derived1 extends Base {
    Derived1() { opType = OperationType.OP1; }
  }
  private static class Derived2 extends Base {
    Derived2() { opType = OperationType.OP2; }
  }
  private static class BaseTypeAdapter implements JsonDeserializer<Base> {
    @Override public Base deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      String opTypeStr = json.getAsJsonObject().get("opType").getAsString();
      OperationType opType = OperationType.valueOf(opTypeStr);
      switch (opType) {
      case OP1:
        return new Derived1();
      case OP2:
        return new Derived2();
      }
      throw new JsonParseException("unknown type: " + json);
    }
  }
}