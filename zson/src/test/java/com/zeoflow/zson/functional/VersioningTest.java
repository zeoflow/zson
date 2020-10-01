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
import com.zeoflow.zson.annotations.Since;
import com.zeoflow.zson.annotations.Until;
import com.zeoflow.zson.common.TestTypes;

import junit.framework.TestCase;

/**
 * Functional tests for versioning support in Zson.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class VersioningTest extends TestCase {
  private static final int A = 0;
  private static final int B = 1;
  private static final int C = 2;
  private static final int D = 3;

  private ZsonBuilder builder;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    builder = new ZsonBuilder();
  }

  public void testVersionedUntilSerialization() {
    Version1 target = new Version1();
    Zson zson = builder.setVersion(1.29).create();
    String json = zson.toJson(target);
    assertTrue(json.contains("\"a\":" + A));

    zson = builder.setVersion(1.3).create();
    json = zson.toJson(target);
    assertFalse(json.contains("\"a\":" + A));
  }

  public void testVersionedUntilDeserialization() {
    Zson zson = builder.setVersion(1.3).create();
    String json = "{\"a\":3,\"b\":4,\"c\":5}";
    Version1 version1 = zson.fromJson(json, Version1.class);
    assertEquals(A, version1.a);
  }

  public void testVersionedClassesSerialization() {
    Zson zson = builder.setVersion(1.0).create();
    String json1 = zson.toJson(new Version1());
    String json2 = zson.toJson(new Version1_1());
    assertEquals(json1, json2);
  }

  public void testVersionedClassesDeserialization() {
    Zson zson = builder.setVersion(1.0).create();
    String json = "{\"a\":3,\"b\":4,\"c\":5}";
    Version1 version1 = zson.fromJson(json, Version1.class);
    assertEquals(3, version1.a);
    assertEquals(4, version1.b);
    Version1_1 version1_1 = zson.fromJson(json, Version1_1.class);
    assertEquals(3, version1_1.a);
    assertEquals(4, version1_1.b);
    assertEquals(C, version1_1.c);
  }

  public void testIgnoreLaterVersionClassSerialization() {
    Zson zson = builder.setVersion(1.0).create();
    assertEquals("null", zson.toJson(new Version1_2()));
  }

  public void testIgnoreLaterVersionClassDeserialization() {
    Zson zson = builder.setVersion(1.0).create();
    String json = "{\"a\":3,\"b\":4,\"c\":5,\"d\":6}";
    Version1_2 version1_2 = zson.fromJson(json, Version1_2.class);
    // Since the class is versioned to be after 1.0, we expect null
    // This is the new behavior in Zson 2.0
    assertNull(version1_2);
  }

  public void testVersionedZsonWithUnversionedClassesSerialization() {
    Zson zson = builder.setVersion(1.0).create();
    TestTypes.BagOfPrimitives target = new TestTypes.BagOfPrimitives(10, 20, false, "stringValue");
    assertEquals(target.getExpectedJson(), zson.toJson(target));
  }

  public void testVersionedZsonWithUnversionedClassesDeserialization() {
    Zson zson = builder.setVersion(1.0).create();
    String json = "{\"longValue\":10,\"intValue\":20,\"booleanValue\":false}";

    TestTypes.BagOfPrimitives expected = new TestTypes.BagOfPrimitives();
    expected.longValue = 10;
    expected.intValue = 20;
    expected.booleanValue = false;
    TestTypes.BagOfPrimitives actual = zson.fromJson(json, TestTypes.BagOfPrimitives.class);
    assertEquals(expected, actual);
  }

  public void testVersionedZsonMixingSinceAndUntilSerialization() {
    Zson zson = builder.setVersion(1.0).create();
    SinceUntilMixing target = new SinceUntilMixing();
    String json = zson.toJson(target);
    assertFalse(json.contains("\"b\":" + B));

    zson = builder.setVersion(1.2).create();
    json = zson.toJson(target);
    assertTrue(json.contains("\"b\":" + B));

    zson = builder.setVersion(1.3).create();
    json = zson.toJson(target);
    assertFalse(json.contains("\"b\":" + B));
  }

  public void testVersionedZsonMixingSinceAndUntilDeserialization() {
    String json = "{\"a\":5,\"b\":6}";
    Zson zson = builder.setVersion(1.0).create();
    SinceUntilMixing result = zson.fromJson(json, SinceUntilMixing.class);
    assertEquals(5, result.a);
    assertEquals(B, result.b);

    zson = builder.setVersion(1.2).create();
    result = zson.fromJson(json, SinceUntilMixing.class);
    assertEquals(5, result.a);
    assertEquals(6, result.b);

    zson = builder.setVersion(1.3).create();
    result = zson.fromJson(json, SinceUntilMixing.class);
    assertEquals(5, result.a);
    assertEquals(B, result.b);
  }

  private static class Version1 {
    @Until(1.3) int a = A;
    @Since(1.0) int b = B;
  }

  private static class Version1_1 extends Version1 {
    @Since(1.1) int c = C;
  }

  @Since(1.2)
  private static class Version1_2 extends Version1_1 {
    @SuppressWarnings("unused")
    int d = D;
  }

  private static class SinceUntilMixing {
    int a = A;

    @Since(1.1)
    @Until(1.3)
    int b = B;
  }
}
