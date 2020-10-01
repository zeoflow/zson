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

package com.zeoflow.zson;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import junit.framework.TestCase;

/**
 * Contains numerous tests involving registered type converters with a Zson instance.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class ZsonTypeAdapterTest extends TestCase {
  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new ZsonBuilder()
        .registerTypeAdapter(AtomicLong.class, new ExceptionTypeAdapter())
        .registerTypeAdapter(AtomicInteger.class, new AtomicIntegerTypeAdapter())
        .create();
  }

  public void testDefaultTypeAdapterThrowsParseException() throws Exception {
    try {
      zson.fromJson("{\"abc\":123}", BigInteger.class);
      fail("Should have thrown a JsonParseException");
    } catch (JsonParseException expected) { }
  }

  public void testTypeAdapterThrowsException() throws Exception {
    try {
      zson.toJson(new AtomicLong(0));
      fail("Type Adapter should have thrown an exception");
    } catch (IllegalStateException expected) { }

    try {
      zson.fromJson("123", AtomicLong.class);
      fail("Type Adapter should have thrown an exception");
    } catch (JsonParseException expected) { }
  }

  public void testTypeAdapterProperlyConvertsTypes() throws Exception {
    int intialValue = 1;
    AtomicInteger atomicInt = new AtomicInteger(intialValue);
    String json = zson.toJson(atomicInt);
    assertEquals(intialValue + 1, Integer.parseInt(json));

    atomicInt = zson.fromJson(json, AtomicInteger.class);
    assertEquals(intialValue, atomicInt.get());
  }

  public void testTypeAdapterDoesNotAffectNonAdaptedTypes() throws Exception {
    String expected = "blah";
    String actual = zson.toJson(expected);
    assertEquals("\"" + expected + "\"", actual);

    actual = zson.fromJson(actual, String.class);
    assertEquals(expected, actual);
  }

  private static class ExceptionTypeAdapter
      implements JsonSerializer<AtomicLong>, JsonDeserializer<AtomicLong> {
    @Override public JsonElement serialize(
        AtomicLong src, Type typeOfSrc, JsonSerializationContext context) {
      throw new IllegalStateException();
    }
    @Override public AtomicLong deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      throw new IllegalStateException();
    }
  }

  private static class AtomicIntegerTypeAdapter
      implements JsonSerializer<AtomicInteger>, JsonDeserializer<AtomicInteger> {
    @Override public JsonElement serialize(AtomicInteger src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.incrementAndGet());
    }

    @Override public AtomicInteger deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      int intValue = json.getAsInt();
      return new AtomicInteger(--intValue);
    }
  }

  static abstract class Abstract {
    String a;
  }

  static class Concrete extends Abstract {
    String b;
  }

  // https://groups.google.com/d/topic/google-Zson/EBmOCa8kJPE/discussion
  public void testDeserializerForAbstractClass() {
    Concrete instance = new Concrete();
    instance.a = "android";
    instance.b = "beep";
    assertSerialized("{\"a\":\"android\"}", Abstract.class, true, true, instance);
    assertSerialized("{\"a\":\"android\"}", Abstract.class, true, false, instance);
    assertSerialized("{\"a\":\"android\"}", Abstract.class, false, true, instance);
    assertSerialized("{\"a\":\"android\"}", Abstract.class, false, false, instance);
    assertSerialized("{\"b\":\"beep\",\"a\":\"android\"}", Concrete.class, true, true, instance);
    assertSerialized("{\"b\":\"beep\",\"a\":\"android\"}", Concrete.class, true, false, instance);
    assertSerialized("{\"b\":\"beep\",\"a\":\"android\"}", Concrete.class, false, true, instance);
    assertSerialized("{\"b\":\"beep\",\"a\":\"android\"}", Concrete.class, false, false, instance);
  }

  private void assertSerialized(String expected, Class<?> instanceType, boolean registerAbstractDeserializer,
      boolean registerAbstractHierarchyDeserializer, Object instance) {
    JsonDeserializer<Abstract> deserializer = new JsonDeserializer<Abstract>() {
      public Abstract deserialize(JsonElement json, Type typeOfT,
          JsonDeserializationContext context) throws JsonParseException {
        throw new AssertionError();
      }
    };
    ZsonBuilder builder = new ZsonBuilder();
    if (registerAbstractDeserializer) {
      builder.registerTypeAdapter(Abstract.class, deserializer);
    }
    if (registerAbstractHierarchyDeserializer) {
      builder.registerTypeHierarchyAdapter(Abstract.class, deserializer);
    }
    Zson zson = builder.create();
    assertEquals(expected, zson.toJson(instance, instanceType));
  }
}
