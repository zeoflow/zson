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

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import junit.framework.TestCase;

import com.zeoflow.zson.stream.JsonReader;
import com.zeoflow.zson.stream.JsonWriter;

/**
 * Unit tests for {@link ZsonBuilder}.
 *
 * @author Inderjeet Singh
 */
public class ZsonBuilderTest extends TestCase {
  private static final TypeAdapter<Object> NULL_TYPE_ADAPTER = new TypeAdapter<Object>() {
    @Override public void write(JsonWriter out, Object value) {
      throw new AssertionError();
    }
    @Override public Object read(JsonReader in) {
      throw new AssertionError();
    }
  };

  public void testCreatingMoreThanOnce() {
    ZsonBuilder builder = new ZsonBuilder();
    builder.create();
    builder.create();
  }

  public void testExcludeFieldsWithModifiers() {
    Zson zson = new ZsonBuilder()
        .excludeFieldsWithModifiers(Modifier.VOLATILE, Modifier.PRIVATE)
        .create();
    assertEquals("{\"d\":\"d\"}", zson.toJson(new HasModifiers()));
  }

  public void testRegisterTypeAdapterForCoreType() {
    Type[] types = {
        byte.class,
        int.class,
        double.class,
        Short.class,
        Long.class,
        String.class,
    };
    for (Type type : types) {
      new ZsonBuilder().registerTypeAdapter(type, NULL_TYPE_ADAPTER);
    }
  }

  @SuppressWarnings("unused")
  static class HasModifiers {
    private String a = "a";
    volatile String b = "b";
    private volatile String c = "c";
    String d = "d";
  }

  public void testTransientFieldExclusion() {
    Zson zson = new ZsonBuilder()
        .excludeFieldsWithModifiers()
        .create();
    assertEquals("{\"a\":\"a\"}", zson.toJson(new HasTransients()));
  }

  static class HasTransients {
    transient String a = "a";
  }
}
