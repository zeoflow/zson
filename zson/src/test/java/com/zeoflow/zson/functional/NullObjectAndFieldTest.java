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
import com.zeoflow.zson.JsonElement;
import com.zeoflow.zson.JsonNull;
import com.zeoflow.zson.JsonObject;
import com.zeoflow.zson.JsonDeserializationContext;
import com.zeoflow.zson.JsonDeserializer;
import com.zeoflow.zson.JsonSerializationContext;
import com.zeoflow.zson.JsonSerializer;
import com.zeoflow.zson.common.TestTypes;

import junit.framework.TestCase;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Functional tests for the different cases for serializing (or ignoring) null fields and object.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class NullObjectAndFieldTest extends TestCase {
  private ZsonBuilder zsonBuilder;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zsonBuilder = new ZsonBuilder().serializeNulls();
  }

  public void testTopLevelNullObjectSerialization() {
    Zson zson = zsonBuilder.create();
    String actual = zson.toJson(null);
    assertEquals("null", actual);

    actual = zson.toJson(null, String.class);
    assertEquals("null", actual);
  }

  public void testTopLevelNullObjectDeserialization() throws Exception {
    Zson zson = zsonBuilder.create();
    String actual = zson.fromJson("null", String.class);
    assertNull(actual);
  }

  public void testExplicitSerializationOfNulls() {
    Zson zson = zsonBuilder.create();
    TestTypes.ClassWithObjects target = new TestTypes.ClassWithObjects(null);
    String actual = zson.toJson(target);
    String expected = "{\"bag\":null}";
    assertEquals(expected, actual);
  }

  public void testExplicitDeserializationOfNulls() throws Exception {
    Zson zson = zsonBuilder.create();
    TestTypes.ClassWithObjects target = zson.fromJson("{\"bag\":null}", TestTypes.ClassWithObjects.class);
    assertNull(target.bag);
  }
  
  public void testExplicitSerializationOfNullArrayMembers() {
    Zson zson = zsonBuilder.create();
    ClassWithMembers target = new ClassWithMembers();
    String json = zson.toJson(target);
    assertTrue(json.contains("\"array\":null"));
  }
  
  /** 
   * Added to verify http://code.google.com/p/google-Zson/issues/detail?id=68
   */
  public void testNullWrappedPrimitiveMemberSerialization() {
    Zson zson = zsonBuilder.serializeNulls().create();
    ClassWithNullWrappedPrimitive target = new ClassWithNullWrappedPrimitive();
    String json = zson.toJson(target);
    assertTrue(json.contains("\"value\":null"));
  }
  
  /** 
   * Added to verify http://code.google.com/p/google-Zson/issues/detail?id=68
   */
  public void testNullWrappedPrimitiveMemberDeserialization() {
    Zson zson = zsonBuilder.create();
    String json = "{'value':null}";
    ClassWithNullWrappedPrimitive target = zson.fromJson(json, ClassWithNullWrappedPrimitive.class);
    assertNull(target.value);
  }
  
  public void testExplicitSerializationOfNullCollectionMembers() {
    Zson zson = zsonBuilder.create();
    ClassWithMembers target = new ClassWithMembers();
    String json = zson.toJson(target);
    assertTrue(json.contains("\"col\":null"));
  }
  
  public void testExplicitSerializationOfNullStringMembers() {
    Zson zson = zsonBuilder.create();
    ClassWithMembers target = new ClassWithMembers();
    String json = zson.toJson(target);
    assertTrue(json.contains("\"str\":null"));
  }

  public void testCustomSerializationOfNulls() {
    zsonBuilder.registerTypeAdapter(TestTypes.ClassWithObjects.class, new ClassWithObjectsSerializer());
    Zson zson = zsonBuilder.create();
    TestTypes.ClassWithObjects target = new TestTypes.ClassWithObjects(new TestTypes.BagOfPrimitives());
    String actual = zson.toJson(target);
    String expected = "{\"bag\":null}";
    assertEquals(expected, actual);
  }
  
  public void testPrintPrintingObjectWithNulls() throws Exception {
    zsonBuilder = new ZsonBuilder();
    Zson zson = zsonBuilder.create();
    String result = zson.toJson(new ClassWithMembers());
    assertEquals("{}", result);

    zson = zsonBuilder.serializeNulls().create();
    result = zson.toJson(new ClassWithMembers());
    assertTrue(result.contains("\"str\":null"));
  }
  
  public void testPrintPrintingArraysWithNulls() throws Exception {
    zsonBuilder = new ZsonBuilder();
    Zson zson = zsonBuilder.create();
    String result = zson.toJson(new String[] { "1", null, "3" });
    assertEquals("[\"1\",null,\"3\"]", result);

    zson = zsonBuilder.serializeNulls().create();
    result = zson.toJson(new String[] { "1", null, "3" });
    assertEquals("[\"1\",null,\"3\"]", result);
  }

  // test for issue 389
  public void testAbsentJsonElementsAreSetToNull() {
    Zson zson = new Zson();
    ClassWithInitializedMembers target =
        zson.fromJson("{array:[1,2,3]}", ClassWithInitializedMembers.class);
    assertTrue(target.array.length == 3 && target.array[1] == 2);
    assertEquals(ClassWithInitializedMembers.MY_STRING_DEFAULT, target.str1);
    assertNull(target.str2);
    assertEquals(ClassWithInitializedMembers.MY_INT_DEFAULT, target.int1);
    assertEquals(0, target.int2); // test the default value of a primitive int field per JVM spec
    assertEquals(ClassWithInitializedMembers.MY_BOOLEAN_DEFAULT, target.bool1);
    assertFalse(target.bool2); // test the default value of a primitive boolean field per JVM spec
  }

  public static class ClassWithInitializedMembers {
    // Using a mix of no-args constructor and field initializers
    // Also, some fields are intialized and some are not (so initialized per JVM spec)
    public static final String MY_STRING_DEFAULT = "string";
    private static final int MY_INT_DEFAULT = 2;
    private static final boolean MY_BOOLEAN_DEFAULT = true;
    int[] array;
    String str1, str2;
    int int1 = MY_INT_DEFAULT;
    int int2;
    boolean bool1 = MY_BOOLEAN_DEFAULT;
    boolean bool2;
    public ClassWithInitializedMembers() {
      str1 = MY_STRING_DEFAULT;
    }
  }

  private static class ClassWithNullWrappedPrimitive {
    private Long value;
  }

  @SuppressWarnings("unused")
  private static class ClassWithMembers {
    String str;
    int[] array;
    Collection<String> col;
  }
  
  private static class ClassWithObjectsSerializer implements JsonSerializer<TestTypes.ClassWithObjects> {
    @Override public JsonElement serialize(TestTypes.ClassWithObjects src, Type typeOfSrc,
                                           JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.add("bag", JsonNull.INSTANCE);
      return obj;
    }
  }

  public void testExplicitNullSetsFieldToNullDuringDeserialization() {
    Zson zson = new Zson();
    String json = "{value:null}";
    ObjectWithField obj = zson.fromJson(json, ObjectWithField.class);
    assertNull(obj.value);
  }

  public void testCustomTypeAdapterPassesNullSerialization() {
    Zson zson = new ZsonBuilder()
        .registerTypeAdapter(ObjectWithField.class, new JsonSerializer<ObjectWithField>() {
          @Override public JsonElement serialize(ObjectWithField src, Type typeOfSrc,
              JsonSerializationContext context) {
            return context.serialize(null);
          }
        }).create();
    ObjectWithField target = new ObjectWithField();
    target.value = "value1";
    String json = zson.toJson(target);
    assertFalse(json.contains("value1"));
  }

  public void testCustomTypeAdapterPassesNullDesrialization() {
    Zson zson = new ZsonBuilder()
        .registerTypeAdapter(ObjectWithField.class, new JsonDeserializer<ObjectWithField>() {
          @Override public ObjectWithField deserialize(JsonElement json, Type type,
              JsonDeserializationContext context) {
            return context.deserialize(null, type);
          }
        }).create();
    String json = "{value:'value1'}";
    ObjectWithField target = zson.fromJson(json, ObjectWithField.class);
    assertNull(target);
  }

  private static class ObjectWithField {
    String value = "";
  }
}
