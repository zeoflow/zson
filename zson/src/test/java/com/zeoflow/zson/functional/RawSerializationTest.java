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

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import com.zeoflow.zson.Zson;
import com.zeoflow.zson.reflect.TypeToken;

/**
 * Unit tests to validate serialization of parameterized types without explicit types
 *
 * @author Inderjeet Singh
 */
public class RawSerializationTest extends TestCase {

  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new Zson();
  }

  public void testCollectionOfPrimitives() {
    Collection<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
    String json = zson.toJson(ints);
    assertEquals("[1,2,3,4,5]", json);
  }

  public void testCollectionOfObjects() {
    Collection<Foo> foos = Arrays.asList(new Foo(1), new Foo(2));
    String json = zson.toJson(foos);
    assertEquals("[{\"b\":1},{\"b\":2}]", json);
  }

  public void testParameterizedObject() {
    Bar<Foo> bar = new Bar<Foo>(new Foo(1));
    String expectedJson = "{\"t\":{\"b\":1}}";
    // Ensure that serialization works without specifying the type explicitly
    String json = zson.toJson(bar);
    assertEquals(expectedJson, json);
    // Ensure that serialization also works when the type is specified explicitly
    json = zson.toJson(bar, new TypeToken<Bar<Foo>>(){}.getType());
    assertEquals(expectedJson, json);
  }

  public void testTwoLevelParameterizedObject() {
    Bar<Bar<Foo>> bar = new Bar<Bar<Foo>>(new Bar<Foo>(new Foo(1)));
    String expectedJson = "{\"t\":{\"t\":{\"b\":1}}}";
    // Ensure that serialization works without specifying the type explicitly
    String json = zson.toJson(bar);
    assertEquals(expectedJson, json);
    // Ensure that serialization also works when the type is specified explicitly
    json = zson.toJson(bar, new TypeToken<Bar<Bar<Foo>>>(){}.getType());
    assertEquals(expectedJson, json);
  }

  public void testThreeLevelParameterizedObject() {
    Bar<Bar<Bar<Foo>>> bar = new Bar<Bar<Bar<Foo>>>(new Bar<Bar<Foo>>(new Bar<Foo>(new Foo(1))));
    String expectedJson = "{\"t\":{\"t\":{\"t\":{\"b\":1}}}}";
    // Ensure that serialization works without specifying the type explicitly
    String json = zson.toJson(bar);
    assertEquals(expectedJson, json);
    // Ensure that serialization also works when the type is specified explicitly
    json = zson.toJson(bar, new TypeToken<Bar<Bar<Bar<Foo>>>>(){}.getType());
    assertEquals(expectedJson, json);
  }

  private static class Foo {
    @SuppressWarnings("unused")
    int b;
    Foo(int b) {
      this.b = b;
    }
  }

  private static class Bar<T> {
    @SuppressWarnings("unused")
    T t;
    Bar(T t) {
      this.t = t;
    }
  }
}
