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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import com.zeoflow.zson.Zson;
import com.zeoflow.zson.ZsonBuilder;
import com.zeoflow.zson.LongSerializationPolicy;

import junit.framework.TestCase;

/**
 * Functional test for Json serialization and deserialization for classes in java.util.concurrent.atomic
 */
public class JavaUtilConcurrentAtomicTest extends TestCase {
  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new Zson();
  }

  public void testAtomicBoolean() throws Exception {
    AtomicBoolean target = zson.fromJson("true", AtomicBoolean.class);
    assertTrue(target.get());
    String json = zson.toJson(target);
    assertEquals("true", json);
  }

  public void testAtomicInteger() throws Exception {
    AtomicInteger target = zson.fromJson("10", AtomicInteger.class);
    assertEquals(10, target.get());
    String json = zson.toJson(target);
    assertEquals("10", json);
  }

  public void testAtomicLong() throws Exception {
    AtomicLong target = zson.fromJson("10", AtomicLong.class);
    assertEquals(10, target.get());
    String json = zson.toJson(target);
    assertEquals("10", json);
  }

  public void testAtomicLongWithStringSerializationPolicy() throws Exception {
    Zson zson = new ZsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .create();
    AtomicLongHolder target = zson.fromJson("{'value':'10'}", AtomicLongHolder.class);
    assertEquals(10, target.value.get());
    String json = zson.toJson(target);
    assertEquals("{\"value\":\"10\"}", json);
  }

  public void testAtomicIntegerArray() throws Exception {
    AtomicIntegerArray target = zson.fromJson("[10, 13, 14]", AtomicIntegerArray.class);
    assertEquals(3, target.length());
    assertEquals(10, target.get(0));
    assertEquals(13, target.get(1));
    assertEquals(14, target.get(2));
    String json = zson.toJson(target);
    assertEquals("[10,13,14]", json);
  }

  public void testAtomicLongArray() throws Exception {
    AtomicLongArray target = zson.fromJson("[10, 13, 14]", AtomicLongArray.class);
    assertEquals(3, target.length());
    assertEquals(10, target.get(0));
    assertEquals(13, target.get(1));
    assertEquals(14, target.get(2));
    String json = zson.toJson(target);
    assertEquals("[10,13,14]", json);
  }

  public void testAtomicLongArrayWithStringSerializationPolicy() throws Exception {
    Zson zson = new ZsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .create();
    AtomicLongArray target = zson.fromJson("['10', '13', '14']", AtomicLongArray.class);
    assertEquals(3, target.length());
    assertEquals(10, target.get(0));
    assertEquals(13, target.get(1));
    assertEquals(14, target.get(2));
    String json = zson.toJson(target);
    assertEquals("[\"10\",\"13\",\"14\"]", json);
  }

  private static class AtomicLongHolder {
    AtomicLong value;
  }
}
