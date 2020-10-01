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

import com.zeoflow.zson.ExclusionStrategy;
import com.zeoflow.zson.FieldAttributes;
import com.zeoflow.zson.Zson;
import com.zeoflow.zson.ZsonBuilder;
import com.zeoflow.zson.JsonObject;
import com.zeoflow.zson.JsonPrimitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import junit.framework.TestCase;

/**
 * Performs some functional tests when Zson is instantiated with some common user defined
 * {@link ExclusionStrategy} objects.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class ExclusionStrategyFunctionalTest extends TestCase {
  private static final ExclusionStrategy EXCLUDE_SAMPLE_OBJECT_FOR_TEST = new ExclusionStrategy() {
    @Override public boolean shouldSkipField(FieldAttributes f) {
      return false;
    }
    @Override public boolean shouldSkipClass(Class<?> clazz) {
      return clazz == SampleObjectForTest.class;
    }
  };

  private SampleObjectForTest src;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    src = new SampleObjectForTest();
  }

  public void testExclusionStrategySerialization() throws Exception {
    Zson zson = createZson(new MyExclusionStrategy(String.class), true);
    String json = zson.toJson(src);
    assertFalse(json.contains("\"stringField\""));
    assertFalse(json.contains("\"annotatedField\""));
    assertTrue(json.contains("\"longField\""));
  }

  public void testExclusionStrategySerializationDoesNotImpactDeserialization() {
    String json = "{\"annotatedField\":1,\"stringField\":\"x\",\"longField\":2}";
    Zson zson = createZson(new MyExclusionStrategy(String.class), true);
    SampleObjectForTest value = zson.fromJson(json, SampleObjectForTest.class);
    assertEquals(1, value.annotatedField);
    assertEquals("x", value.stringField);
    assertEquals(2, value.longField);
  }

  public void testExclusionStrategyDeserialization() throws Exception {
    Zson zson = createZson(new MyExclusionStrategy(String.class), false);
    JsonObject json = new JsonObject();
    json.add("annotatedField", new JsonPrimitive(src.annotatedField + 5));
    json.add("stringField", new JsonPrimitive(src.stringField + "blah,blah"));
    json.add("longField", new JsonPrimitive(1212311L));

    SampleObjectForTest target = zson.fromJson(json, SampleObjectForTest.class);
    assertEquals(1212311L, target.longField);

    // assert excluded fields are set to the defaults
    assertEquals(src.annotatedField, target.annotatedField);
    assertEquals(src.stringField, target.stringField);
  }

  public void testExclusionStrategySerializationDoesNotImpactSerialization() throws Exception {
    Zson zson = createZson(new MyExclusionStrategy(String.class), false);
    String json = zson.toJson(src);
    assertTrue(json.contains("\"stringField\""));
    assertTrue(json.contains("\"annotatedField\""));
    assertTrue(json.contains("\"longField\""));
  }

  public void testExclusionStrategyWithMode() throws Exception {
    SampleObjectForTest testObj = new SampleObjectForTest(
        src.annotatedField + 5, src.stringField + "blah,blah",
        src.longField + 655L);

    Zson zson = createZson(new MyExclusionStrategy(String.class), false);
    JsonObject json = zson.toJsonTree(testObj).getAsJsonObject();
    assertEquals(testObj.annotatedField, json.get("annotatedField").getAsInt());
    assertEquals(testObj.stringField, json.get("stringField").getAsString());
    assertEquals(testObj.longField, json.get("longField").getAsLong());

    SampleObjectForTest target = zson.fromJson(json, SampleObjectForTest.class);
    assertEquals(testObj.longField, target.longField);

    // assert excluded fields are set to the defaults
    assertEquals(src.annotatedField, target.annotatedField);
    assertEquals(src.stringField, target.stringField);
  }

  public void testExcludeTopLevelClassSerialization() {
    Zson zson = new ZsonBuilder()
        .addSerializationExclusionStrategy(EXCLUDE_SAMPLE_OBJECT_FOR_TEST)
        .create();
    assertEquals("null", zson.toJson(new SampleObjectForTest(), SampleObjectForTest.class));
  }

  public void testExcludeTopLevelClassSerializationDoesNotImpactDeserialization() {
    Zson zson = new ZsonBuilder()
        .addSerializationExclusionStrategy(EXCLUDE_SAMPLE_OBJECT_FOR_TEST)
        .create();
    String json = "{\"annotatedField\":1,\"stringField\":\"x\",\"longField\":2}";
    SampleObjectForTest value = zson.fromJson(json, SampleObjectForTest.class);
    assertEquals(1, value.annotatedField);
    assertEquals("x", value.stringField);
    assertEquals(2, value.longField);
  }

  public void testExcludeTopLevelClassDeserialization() {
    Zson zson = new ZsonBuilder()
        .addDeserializationExclusionStrategy(EXCLUDE_SAMPLE_OBJECT_FOR_TEST)
        .create();
    String json = "{\"annotatedField\":1,\"stringField\":\"x\",\"longField\":2}";
    SampleObjectForTest value = zson.fromJson(json, SampleObjectForTest.class);
    assertNull(value);
  }

  public void testExcludeTopLevelClassDeserializationDoesNotImpactSerialization() {
    Zson zson = new ZsonBuilder()
        .addDeserializationExclusionStrategy(EXCLUDE_SAMPLE_OBJECT_FOR_TEST)
        .create();
    String json = zson.toJson(new SampleObjectForTest(), SampleObjectForTest.class);
    assertTrue(json.contains("\"stringField\""));
    assertTrue(json.contains("\"annotatedField\""));
    assertTrue(json.contains("\"longField\""));
  }

  private static Zson createZson(ExclusionStrategy exclusionStrategy, boolean serialization) {
    ZsonBuilder zsonBuilder = new ZsonBuilder();
    if (serialization) {
      zsonBuilder.addSerializationExclusionStrategy(exclusionStrategy);
    } else {
      zsonBuilder.addDeserializationExclusionStrategy(exclusionStrategy);
    }
    return zsonBuilder
        .serializeNulls()
        .create();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD})
  private static @interface Foo {
    // Field tag only annotation
  }

  private static class SampleObjectForTest {
    @Foo
    private final int annotatedField;
    private final String stringField;
    private final long longField;

    public SampleObjectForTest() {
      this(5, "someDefaultValue", 12345L);
    }

    public SampleObjectForTest(int annotatedField, String stringField, long longField) {
      this.annotatedField = annotatedField;
      this.stringField = stringField;
      this.longField = longField;
    }
  }

  private static final class MyExclusionStrategy implements ExclusionStrategy {
    private final Class<?> typeToSkip;

    private MyExclusionStrategy(Class<?> typeToSkip) {
      this.typeToSkip = typeToSkip;
    }

    @Override public boolean shouldSkipClass(Class<?> clazz) {
      return (clazz == typeToSkip);
    }

    @Override public boolean shouldSkipField(FieldAttributes f) {
      return f.getAnnotation(Foo.class) != null;
    }
  }
}
