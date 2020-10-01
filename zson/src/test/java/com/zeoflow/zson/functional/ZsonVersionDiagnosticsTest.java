/*
 * Copyright (C) 2018 Zson Authors
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

import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import com.zeoflow.zson.Zson;
import com.zeoflow.zson.ZsonBuilder;
import com.zeoflow.zson.TypeAdapter;
import com.zeoflow.zson.stream.JsonReader;
import com.zeoflow.zson.stream.JsonWriter;

import junit.framework.TestCase;

/**
 * Functional tests to validate printing of Zson version on AssertionErrors
 *
 * @author Inderjeet Singh
 */
public class ZsonVersionDiagnosticsTest extends TestCase {
  private static final Pattern Zson_VERSION_PATTERN = Pattern.compile("(\\(Zson \\d\\.\\d\\.\\d)(?:[-.][A-Z]+)?\\)$");

  private Zson zson;

  @Before
  public void setUp() {
    zson = new ZsonBuilder().registerTypeAdapter(TestType.class, new TypeAdapter<TestType>() {
      @Override public void write(JsonWriter out, TestType value) {
        throw new AssertionError("Expected during serialization");
      }
      @Override public TestType read(JsonReader in) throws IOException {
        throw new AssertionError("Expected during deserialization");
      }
    }).create();
  }

  @Test
  public void testVersionPattern() {
    assertTrue(Zson_VERSION_PATTERN.matcher("(Zson 2.8.5)").matches());
    assertTrue(Zson_VERSION_PATTERN.matcher("(Zson 2.8.5-SNAPSHOT)").matches());
  }

  @Test
  public void testAssertionErrorInSerializationPrintsVersion() {
    try {
      zson.toJson(new TestType());
      fail();
    } catch (AssertionError expected) {
      ensureAssertionErrorPrintsZsonVersion(expected);
    }
  }

  @Test
  public void testAssertionErrorInDeserializationPrintsVersion() {
    try {
      zson.fromJson("{'a':'abc'}", TestType.class);
      fail();
    } catch (AssertionError expected) {
      ensureAssertionErrorPrintsZsonVersion(expected);
    }
  }

  private void ensureAssertionErrorPrintsZsonVersion(AssertionError expected) {
    String msg = expected.getMessage();
    // System.err.println(msg);
    int start = msg.indexOf("(Zson");
    assertTrue(start > 0);
    int end = msg.indexOf("):") + 1;
    assertTrue(end > 0 && end > start + 6);
    String version = msg.substring(start, end);
    // System.err.println(version);
    assertTrue(Zson_VERSION_PATTERN.matcher(version).matches());
  }

  private static final class TestType {
    @SuppressWarnings("unused")
    String a;
  }
}
