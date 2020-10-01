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

import com.zeoflow.zson.reflect.TypeToken;
import com.zeoflow.zson.stream.JsonReader;
import com.zeoflow.zson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

public final class MixedStreamTest extends TestCase {

  private static final Car BLUE_MUSTANG = new Car("mustang", 0x0000FF);
  private static final Car BLACK_BMW = new Car("bmw", 0x000000);
  private static final Car RED_MIATA = new Car("miata", 0xFF0000);
  private static final String CARS_JSON = "[\n"
      + "  {\n"
      + "    \"name\": \"mustang\",\n"
      + "    \"color\": 255\n"
      + "  },\n"
      + "  {\n"
      + "    \"name\": \"bmw\",\n"
      + "    \"color\": 0\n"
      + "  },\n"
      + "  {\n"
      + "    \"name\": \"miata\",\n"
      + "    \"color\": 16711680\n"
      + "  }\n"
      + "]";

  public void testWriteMixedStreamed() throws IOException {
    Zson zson = new Zson();
    StringWriter stringWriter = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(stringWriter);

    jsonWriter.beginArray();
    jsonWriter.setIndent("  ");
    zson.toJson(BLUE_MUSTANG, Car.class, jsonWriter);
    zson.toJson(BLACK_BMW, Car.class, jsonWriter);
    zson.toJson(RED_MIATA, Car.class, jsonWriter);
    jsonWriter.endArray();

    assertEquals(CARS_JSON, stringWriter.toString());
  }

  public void testReadMixedStreamed() throws IOException {
    Zson zson = new Zson();
    StringReader stringReader = new StringReader(CARS_JSON);
    JsonReader jsonReader = new JsonReader(stringReader);

    jsonReader.beginArray();
    assertEquals(BLUE_MUSTANG, zson.fromJson(jsonReader, Car.class));
    assertEquals(BLACK_BMW, zson.fromJson(jsonReader, Car.class));
    assertEquals(RED_MIATA, zson.fromJson(jsonReader, Car.class));
    jsonReader.endArray();
  }

  public void testReaderDoesNotMutateState() throws IOException {
    Zson zson = new Zson();
    JsonReader jsonReader = new JsonReader(new StringReader(CARS_JSON));
    jsonReader.beginArray();

    jsonReader.setLenient(false);
    zson.fromJson(jsonReader, Car.class);
    assertFalse(jsonReader.isLenient());

    jsonReader.setLenient(true);
    zson.fromJson(jsonReader, Car.class);
    assertTrue(jsonReader.isLenient());
  }

  public void testWriteDoesNotMutateState() throws IOException {
    Zson zson = new Zson();
    JsonWriter jsonWriter = new JsonWriter(new StringWriter());
    jsonWriter.beginArray();

    jsonWriter.setHtmlSafe(true);
    jsonWriter.setLenient(true);
    zson.toJson(BLUE_MUSTANG, Car.class, jsonWriter);
    assertTrue(jsonWriter.isHtmlSafe());
    assertTrue(jsonWriter.isLenient());

    jsonWriter.setHtmlSafe(false);
    jsonWriter.setLenient(false);
    zson.toJson(BLUE_MUSTANG, Car.class, jsonWriter);
    assertFalse(jsonWriter.isHtmlSafe());
    assertFalse(jsonWriter.isLenient());
  }

  public void testReadInvalidState() throws IOException {
    Zson zson = new Zson();
    JsonReader jsonReader = new JsonReader(new StringReader(CARS_JSON));
    jsonReader.beginArray();
    jsonReader.beginObject();
    try {
      zson.fromJson(jsonReader, String.class);
      fail();
    } catch (JsonParseException expected) {
    }
  }

  public void testReadClosed() throws IOException {
    Zson zson = new Zson();
    JsonReader jsonReader = new JsonReader(new StringReader(CARS_JSON));
    jsonReader.close();
    try {
      zson.fromJson(jsonReader, new TypeToken<List<Car>>() {}.getType());
      fail();
    } catch (JsonParseException expected) {
    }
  }

  public void testWriteInvalidState() throws IOException {
    Zson zson = new Zson();
    JsonWriter jsonWriter = new JsonWriter(new StringWriter());
    jsonWriter.beginObject();
    try {
      zson.toJson(BLUE_MUSTANG, Car.class, jsonWriter);
      fail();
    } catch (IllegalStateException expected) {
    }
  }

  public void testWriteClosed() throws IOException {
    Zson zson = new Zson();
    JsonWriter jsonWriter = new JsonWriter(new StringWriter());
    jsonWriter.beginArray();
    jsonWriter.endArray();
    jsonWriter.close();
    try {
      zson.toJson(BLUE_MUSTANG, Car.class, jsonWriter);
      fail();
    } catch (IllegalStateException expected) {
    }
  }

  public void testWriteNulls() {
    Zson zson = new Zson();
    try {
      zson.toJson(new JsonPrimitive("hello"), (JsonWriter) null);
      fail();
    } catch (NullPointerException expected) {
    }

    StringWriter stringWriter = new StringWriter();
    zson.toJson(null, new JsonWriter(stringWriter));
    assertEquals("null", stringWriter.toString());
  }

  public void testReadNulls() {
    Zson zson = new Zson();
    try {
      zson.fromJson((JsonReader) null, Integer.class);
      fail();
    } catch (NullPointerException expected) {
    }
    try {
      zson.fromJson(new JsonReader(new StringReader("true")), null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testWriteHtmlSafe() {
    List<String> contents = Arrays.asList("<", ">", "&", "=", "'");
    Type type = new TypeToken<List<String>>() {}.getType();

    StringWriter writer = new StringWriter();
    new Zson().toJson(contents, type, new JsonWriter(writer));
    assertEquals("[\"\\u003c\",\"\\u003e\",\"\\u0026\",\"\\u003d\",\"\\u0027\"]",
        writer.toString());

    writer = new StringWriter();
    new ZsonBuilder().disableHtmlEscaping().create()
        .toJson(contents, type, new JsonWriter(writer));
    assertEquals("[\"<\",\">\",\"&\",\"=\",\"'\"]",
        writer.toString());
  }

  public void testWriteLenient() {
    List<Double> doubles = Arrays.asList(Double.NaN, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY, -0.0d, 0.5d, 0.0d);
    Type type = new TypeToken<List<Double>>() {}.getType();

    StringWriter writer = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(writer);
    new ZsonBuilder().serializeSpecialFloatingPointValues().create()
        .toJson(doubles, type, jsonWriter);
    assertEquals("[NaN,-Infinity,Infinity,-0.0,0.5,0.0]", writer.toString());

    try {
      new Zson().toJson(doubles, type, new JsonWriter(new StringWriter()));
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  static final class Car {
    String name;
    int color;

    Car(String name, int color) {
      this.name = name;
      this.color = color;
    }

    // used by Zson
    Car() {}

    @Override public int hashCode() {
      return name.hashCode() ^ color;
    }

    @Override public boolean equals(Object o) {
      return o instanceof Car
          && ((Car) o).name.equals(name)
          && ((Car) o).color == color;
    }
  }
}
