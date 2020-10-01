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

import com.zeoflow.zson.stream.JsonReader;
import com.zeoflow.zson.stream.JsonWriter;

import java.io.IOException;
import java.util.Locale;
import junit.framework.TestCase;

/**
 * @author Jesse Wilson
 */
public class OverrideCoreTypeAdaptersTest extends TestCase {
  private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
    @Override public void write(JsonWriter out, Boolean value) throws IOException {
      out.value(value ? 1 : 0);
    }
    @Override public Boolean read(JsonReader in) throws IOException {
      int value = in.nextInt();
      return value != 0;
    }
  };

  private static final TypeAdapter<String> swapCaseStringAdapter = new TypeAdapter<String>() {
    @Override public void write(JsonWriter out, String value) throws IOException {
      out.value(value.toUpperCase(Locale.US));
    }
    @Override public String read(JsonReader in) throws IOException {
      return in.nextString().toLowerCase(Locale.US);
    }
  };

  public void testOverrideWrapperBooleanAdapter() {
    Zson zson = new ZsonBuilder()
        .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
        .create();
    assertEquals("true", zson.toJson(true, boolean.class));
    assertEquals("1", zson.toJson(true, Boolean.class));
    assertEquals(Boolean.TRUE, zson.fromJson("true", boolean.class));
    assertEquals(Boolean.TRUE, zson.fromJson("1", Boolean.class));
    assertEquals(Boolean.FALSE, zson.fromJson("0", Boolean.class));
  }

  public void testOverridePrimitiveBooleanAdapter() {
    Zson zson = new ZsonBuilder()
        .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
        .create();
    assertEquals("1", zson.toJson(true, boolean.class));
    assertEquals("true", zson.toJson(true, Boolean.class));
    assertEquals(Boolean.TRUE, zson.fromJson("1", boolean.class));
    assertEquals(Boolean.TRUE, zson.fromJson("true", Boolean.class));
    assertEquals("0", zson.toJson(false, boolean.class));
  }

  public void testOverrideStringAdapter() {
    Zson zson = new ZsonBuilder()
        .registerTypeAdapter(String.class, swapCaseStringAdapter)
        .create();
    assertEquals("\"HELLO\"", zson.toJson("Hello", String.class));
    assertEquals("hello", zson.fromJson("\"Hello\"", String.class));
  }
}
