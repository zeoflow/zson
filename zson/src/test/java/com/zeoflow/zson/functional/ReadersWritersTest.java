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
import com.zeoflow.zson.JsonStreamParser;
import com.zeoflow.zson.JsonSyntaxException;

import com.zeoflow.zson.reflect.TypeToken;
import com.zeoflow.zson.common.TestTypes;

import java.util.Map;
import junit.framework.TestCase;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Functional tests for the support of {@link Reader}s and {@link Writer}s.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class ReadersWritersTest extends TestCase {
  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new Zson();
  }

  public void testWriterForSerialization() throws Exception {
    Writer writer = new StringWriter();
    TestTypes.BagOfPrimitives src = new TestTypes.BagOfPrimitives();
    zson.toJson(src, writer);
    assertEquals(src.getExpectedJson(), writer.toString());
  }

  public void testReaderForDeserialization() throws Exception {
    TestTypes.BagOfPrimitives expected = new TestTypes.BagOfPrimitives();
    Reader json = new StringReader(expected.getExpectedJson());
    TestTypes.BagOfPrimitives actual = zson.fromJson(json, TestTypes.BagOfPrimitives.class);
    assertEquals(expected, actual);
  }

  public void testTopLevelNullObjectSerializationWithWriter() {
    StringWriter writer = new StringWriter();
    zson.toJson(null, writer);
    assertEquals("null", writer.toString());
  }

  public void testTopLevelNullObjectDeserializationWithReader() {
    StringReader reader = new StringReader("null");
    Integer nullIntObject = zson.fromJson(reader, Integer.class);
    assertNull(nullIntObject);
  }

  public void testTopLevelNullObjectSerializationWithWriterAndSerializeNulls() {
    Zson zson = new ZsonBuilder().serializeNulls().create();
    StringWriter writer = new StringWriter();
    zson.toJson(null, writer);
    assertEquals("null", writer.toString());
  }

  public void testTopLevelNullObjectDeserializationWithReaderAndSerializeNulls() {
    Zson zson = new ZsonBuilder().serializeNulls().create();
    StringReader reader = new StringReader("null");
    Integer nullIntObject = zson.fromJson(reader, Integer.class);
    assertNull(nullIntObject);
  }

  public void testReadWriteTwoStrings() throws IOException {
    Zson zson = new Zson();
    CharArrayWriter writer= new CharArrayWriter();
    writer.write(zson.toJson("one").toCharArray());
    writer.write(zson.toJson("two").toCharArray());
    CharArrayReader reader = new CharArrayReader(writer.toCharArray());
    JsonStreamParser parser = new JsonStreamParser(reader);
    String actualOne = zson.fromJson(parser.next(), String.class);
    assertEquals("one", actualOne);
    String actualTwo = zson.fromJson(parser.next(), String.class);
    assertEquals("two", actualTwo);
  }

  public void testReadWriteTwoObjects() throws IOException {
    Zson zson = new Zson();
    CharArrayWriter writer= new CharArrayWriter();
    TestTypes.BagOfPrimitives expectedOne = new TestTypes.BagOfPrimitives(1, 1, true, "one");
    writer.write(zson.toJson(expectedOne).toCharArray());
    TestTypes.BagOfPrimitives expectedTwo = new TestTypes.BagOfPrimitives(2, 2, false, "two");
    writer.write(zson.toJson(expectedTwo).toCharArray());
    CharArrayReader reader = new CharArrayReader(writer.toCharArray());
    JsonStreamParser parser = new JsonStreamParser(reader);
    TestTypes.BagOfPrimitives actualOne = zson.fromJson(parser.next(), TestTypes.BagOfPrimitives.class);
    assertEquals("one", actualOne.stringValue);
    TestTypes.BagOfPrimitives actualTwo = zson.fromJson(parser.next(), TestTypes.BagOfPrimitives.class);
    assertEquals("two", actualTwo.stringValue);
    assertFalse(parser.hasNext());
  }

  public void testTypeMismatchThrowsJsonSyntaxExceptionForStrings() {
    try {
      zson.fromJson("true", new TypeToken<Map<String, String>>() {}.getType());
      fail();
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testTypeMismatchThrowsJsonSyntaxExceptionForReaders() {
    try {
      zson.fromJson(new StringReader("true"), new TypeToken<Map<String, String>>() {}.getType());
      fail();
    } catch (JsonSyntaxException expected) {
    }
  }
}
