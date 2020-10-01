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
import com.zeoflow.zson.JsonPrimitive;
import com.zeoflow.zson.JsonSyntaxException;
import com.zeoflow.zson.LongSerializationPolicy;
import com.zeoflow.zson.reflect.TypeToken;

import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

/**
 * Functional tests for Json primitive values: integers, and floating point numbers.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class PrimitiveTest extends TestCase {
  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new Zson();
  }

  public void testPrimitiveIntegerAutoboxedSerialization() {
    assertEquals("1", zson.toJson(1));
  }

  public void testPrimitiveIntegerAutoboxedDeserialization() {
    int expected = 1;
    int actual = zson.fromJson("1", int.class);
    assertEquals(expected, actual);

    actual = zson.fromJson("1", Integer.class);
    assertEquals(expected, actual);
  }

  public void testByteSerialization() {
    assertEquals("1", zson.toJson(1, byte.class));
    assertEquals("1", zson.toJson(1, Byte.class));
  }

  public void testShortSerialization() {
    assertEquals("1", zson.toJson(1, short.class));
    assertEquals("1", zson.toJson(1, Short.class));
  }

  public void testByteDeserialization() {
    Byte target = zson.fromJson("1", Byte.class);
    assertEquals(1, (byte)target);
    byte primitive = zson.fromJson("1", byte.class);
    assertEquals(1, primitive);
  }

  public void testPrimitiveIntegerAutoboxedInASingleElementArraySerialization() {
    int target[] = {-9332};
    assertEquals("[-9332]", zson.toJson(target));
    assertEquals("[-9332]", zson.toJson(target, int[].class));
    assertEquals("[-9332]", zson.toJson(target, Integer[].class));
  }

  public void testReallyLongValuesSerialization() {
    long value = 333961828784581L;
    assertEquals("333961828784581", zson.toJson(value));
  }

  public void testReallyLongValuesDeserialization() {
    String json = "333961828784581";
    long value = zson.fromJson(json, Long.class);
    assertEquals(333961828784581L, value);
  }

  public void testPrimitiveLongAutoboxedSerialization() {
    assertEquals("1", zson.toJson(1L, long.class));
    assertEquals("1", zson.toJson(1L, Long.class));
  }

  public void testPrimitiveLongAutoboxedDeserialization() {
    long expected = 1L;
    long actual = zson.fromJson("1", long.class);
    assertEquals(expected, actual);

    actual = zson.fromJson("1", Long.class);
    assertEquals(expected, actual);
  }

  public void testPrimitiveLongAutoboxedInASingleElementArraySerialization() {
    long[] target = {-23L};
    assertEquals("[-23]", zson.toJson(target));
    assertEquals("[-23]", zson.toJson(target, long[].class));
    assertEquals("[-23]", zson.toJson(target, Long[].class));
  }

  public void testPrimitiveBooleanAutoboxedSerialization() {
    assertEquals("true", zson.toJson(true));
    assertEquals("false", zson.toJson(false));
  }

  public void testBooleanDeserialization() {
    boolean value = zson.fromJson("false", boolean.class);
    assertEquals(false, value);
    value = zson.fromJson("true", boolean.class);
    assertEquals(true, value);
  }

  public void testPrimitiveBooleanAutoboxedInASingleElementArraySerialization() {
    boolean target[] = {false};
    assertEquals("[false]", zson.toJson(target));
    assertEquals("[false]", zson.toJson(target, boolean[].class));
    assertEquals("[false]", zson.toJson(target, Boolean[].class));
  }

  public void testNumberSerialization() {
    Number expected = 1L;
    String json = zson.toJson(expected);
    assertEquals(expected.toString(), json);

    json = zson.toJson(expected, Number.class);
    assertEquals(expected.toString(), json);
  }

  public void testNumberDeserialization() {
    String json = "1";
    Number expected = Integer.valueOf(json);
    Number actual = zson.fromJson(json, Number.class);
    assertEquals(expected.intValue(), actual.intValue());

    json = String.valueOf(Long.MAX_VALUE);
    expected = Long.valueOf(json);
    actual = zson.fromJson(json, Number.class);
    assertEquals(expected.longValue(), actual.longValue());

    json = "1.0";
    actual = zson.fromJson(json, Number.class);
    assertEquals(1L, actual.longValue());
  }

  public void testNumberAsStringDeserialization() {
    Number value = zson.fromJson("\"18\"", Number.class);
    assertEquals(18, value.intValue());
  }

  public void testPrimitiveDoubleAutoboxedSerialization() {
    assertEquals("-122.08234335", zson.toJson(-122.08234335D));
    assertEquals("122.08112002", zson.toJson(122.08112002D));
  }

  public void testPrimitiveDoubleAutoboxedDeserialization() {
    double actual = zson.fromJson("-122.08858585", double.class);
    assertEquals(-122.08858585D, actual);

    actual = zson.fromJson("122.023900008000", Double.class);
    assertEquals(122.023900008D, actual);
  }

  public void testPrimitiveDoubleAutoboxedInASingleElementArraySerialization() {
    double[] target = {-122.08D};
    assertEquals("[-122.08]", zson.toJson(target));
    assertEquals("[-122.08]", zson.toJson(target, double[].class));
    assertEquals("[-122.08]", zson.toJson(target, Double[].class));
  }

  public void testDoubleAsStringRepresentationDeserialization() {
    String doubleValue = "1.0043E+5";
    Double expected = Double.valueOf(doubleValue);
    Double actual = zson.fromJson(doubleValue, Double.class);
    assertEquals(expected, actual);

    double actual1 = zson.fromJson(doubleValue, double.class);
    assertEquals(expected.doubleValue(), actual1);
  }

  public void testDoubleNoFractAsStringRepresentationDeserialization() {
    String doubleValue = "1E+5";
    Double expected = Double.valueOf(doubleValue);
    Double actual = zson.fromJson(doubleValue, Double.class);
    assertEquals(expected, actual);

    double actual1 = zson.fromJson(doubleValue, double.class);
    assertEquals(expected.doubleValue(), actual1);
  }

  public void testDoubleArrayDeserialization() {
      String json = "[0.0, 0.004761904761904762, 3.4013606962703525E-4, 7.936508173034305E-4,"
              + "0.0011904761904761906, 0.0]";
      double[] values = zson.fromJson(json, double[].class);
      assertEquals(6, values.length);
      assertEquals(0.0, values[0]);
      assertEquals(0.004761904761904762, values[1]);
      assertEquals(3.4013606962703525E-4, values[2]);
      assertEquals(7.936508173034305E-4, values[3]);
      assertEquals(0.0011904761904761906, values[4]);
      assertEquals(0.0, values[5]);
  }

  public void testLargeDoubleDeserialization() {
    String doubleValue = "1.234567899E8";
    Double expected = Double.valueOf(doubleValue);
    Double actual = zson.fromJson(doubleValue, Double.class);
    assertEquals(expected, actual);

    double actual1 = zson.fromJson(doubleValue, double.class);
    assertEquals(expected.doubleValue(), actual1);
  }

  public void testBigDecimalSerialization() {
    BigDecimal target = new BigDecimal("-122.0e-21");
    String json = zson.toJson(target);
    assertEquals(target, new BigDecimal(json));
  }

  public void testBigDecimalDeserialization() {
    BigDecimal target = new BigDecimal("-122.0e-21");
    String json = "-122.0e-21";
    assertEquals(target, zson.fromJson(json, BigDecimal.class));
  }

  public void testBigDecimalInASingleElementArraySerialization() {
    BigDecimal[] target = {new BigDecimal("-122.08e-21")};
    String json = zson.toJson(target);
    String actual = extractElementFromArray(json);
    assertEquals(target[0], new BigDecimal(actual));

    json = zson.toJson(target, BigDecimal[].class);
    actual = extractElementFromArray(json);
    assertEquals(target[0], new BigDecimal(actual));
  }

  public void testSmallValueForBigDecimalSerialization() {
    BigDecimal target = new BigDecimal("1.55");
    String actual = zson.toJson(target);
    assertEquals(target.toString(), actual);
  }

  public void testSmallValueForBigDecimalDeserialization() {
    BigDecimal expected = new BigDecimal("1.55");
    BigDecimal actual = zson.fromJson("1.55", BigDecimal.class);
    assertEquals(expected, actual);
  }

  public void testBigDecimalPreservePrecisionSerialization() {
    String expectedValue = "1.000";
    BigDecimal obj = new BigDecimal(expectedValue);
    String actualValue = zson.toJson(obj);

    assertEquals(expectedValue, actualValue);
  }

  public void testBigDecimalPreservePrecisionDeserialization() {
    String json = "1.000";
    BigDecimal expected = new BigDecimal(json);
    BigDecimal actual = zson.fromJson(json, BigDecimal.class);

    assertEquals(expected, actual);
  }

  public void testBigDecimalAsStringRepresentationDeserialization() {
    String doubleValue = "0.05E+5";
    BigDecimal expected = new BigDecimal(doubleValue);
    BigDecimal actual = zson.fromJson(doubleValue, BigDecimal.class);
    assertEquals(expected, actual);
  }

  public void testBigDecimalNoFractAsStringRepresentationDeserialization() {
    String doubleValue = "5E+5";
    BigDecimal expected = new BigDecimal(doubleValue);
    BigDecimal actual = zson.fromJson(doubleValue, BigDecimal.class);
    assertEquals(expected, actual);
  }

  public void testBigIntegerSerialization() {
    BigInteger target = new BigInteger("12121211243123245845384534687435634558945453489543985435");
    assertEquals(target.toString(), zson.toJson(target));
  }

  public void testBigIntegerDeserialization() {
    String json = "12121211243123245845384534687435634558945453489543985435";
    BigInteger target = new BigInteger(json);
    assertEquals(target, zson.fromJson(json, BigInteger.class));
  }

  public void testBigIntegerInASingleElementArraySerialization() {
    BigInteger[] target = {new BigInteger("1212121243434324323254365345367456456456465464564564")};
    String json = zson.toJson(target);
    String actual = extractElementFromArray(json);
    assertEquals(target[0], new BigInteger(actual));

    json = zson.toJson(target, BigInteger[].class);
    actual = extractElementFromArray(json);
    assertEquals(target[0], new BigInteger(actual));
  }

  public void testSmallValueForBigIntegerSerialization() {
    BigInteger target = new BigInteger("15");
    String actual = zson.toJson(target);
    assertEquals(target.toString(), actual);
  }

  public void testSmallValueForBigIntegerDeserialization() {
    BigInteger expected = new BigInteger("15");
    BigInteger actual = zson.fromJson("15", BigInteger.class);
    assertEquals(expected, actual);
  }

  public void testBadValueForBigIntegerDeserialization() {
    try {
      zson.fromJson("15.099", BigInteger.class);
      fail("BigInteger can not be decimal values.");
    } catch (JsonSyntaxException expected) { }
  }

  public void testMoreSpecificSerialization() {
    Zson zson = new Zson();
    String expected = "This is a string";
    String expectedJson = zson.toJson(expected);

    Serializable serializableString = expected;
    String actualJson = zson.toJson(serializableString, Serializable.class);
    assertFalse(expectedJson.equals(actualJson));
  }

  private String extractElementFromArray(String json) {
    return json.substring(json.indexOf('[') + 1, json.indexOf(']'));
  }

  public void testDoubleNaNSerializationNotSupportedByDefault() {
    try {
      double nan = Double.NaN;
      zson.toJson(nan);
      fail("Zson should not accept NaN for serialization");
    } catch (IllegalArgumentException expected) {
    }
    try {
      zson.toJson(Double.NaN);
      fail("Zson should not accept NaN for serialization");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testDoubleNaNSerialization() {
    Zson zson = new ZsonBuilder().serializeSpecialFloatingPointValues().create();
    double nan = Double.NaN;
    assertEquals("NaN", zson.toJson(nan));
    assertEquals("NaN", zson.toJson(Double.NaN));
  }

  public void testDoubleNaNDeserialization() {
    assertTrue(Double.isNaN(zson.fromJson("NaN", Double.class)));
    assertTrue(Double.isNaN(zson.fromJson("NaN", double.class)));
  }

  public void testFloatNaNSerializationNotSupportedByDefault() {
    try {
      float nan = Float.NaN;
      zson.toJson(nan);
      fail("Zson should not accept NaN for serialization");
    } catch (IllegalArgumentException expected) {
    }
    try {
      zson.toJson(Float.NaN);
      fail("Zson should not accept NaN for serialization");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testFloatNaNSerialization() {
    Zson zson = new ZsonBuilder().serializeSpecialFloatingPointValues().create();
    float nan = Float.NaN;
    assertEquals("NaN", zson.toJson(nan));
    assertEquals("NaN", zson.toJson(Float.NaN));
  }

  public void testFloatNaNDeserialization() {
    assertTrue(Float.isNaN(zson.fromJson("NaN", Float.class)));
    assertTrue(Float.isNaN(zson.fromJson("NaN", float.class)));
  }

  public void testBigDecimalNaNDeserializationNotSupported() {
    try {
      zson.fromJson("NaN", BigDecimal.class);
      fail("Zson should not accept NaN for deserialization by default.");
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testDoubleInfinitySerializationNotSupportedByDefault() {
    try {
      double infinity = Double.POSITIVE_INFINITY;
      zson.toJson(infinity);
      fail("Zson should not accept positive infinity for serialization by default.");
    } catch (IllegalArgumentException expected) {
    }
    try {
      zson.toJson(Double.POSITIVE_INFINITY);
      fail("Zson should not accept positive infinity for serialization by default.");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testDoubleInfinitySerialization() {
    Zson zson = new ZsonBuilder().serializeSpecialFloatingPointValues().create();
    double infinity = Double.POSITIVE_INFINITY;
    assertEquals("Infinity", zson.toJson(infinity));
    assertEquals("Infinity", zson.toJson(Double.POSITIVE_INFINITY));
  }

  public void testDoubleInfinityDeserialization() {
    assertTrue(Double.isInfinite(zson.fromJson("Infinity", Double.class)));
    assertTrue(Double.isInfinite(zson.fromJson("Infinity", double.class)));
  }

  public void testFloatInfinitySerializationNotSupportedByDefault() {
    try {
      float infinity = Float.POSITIVE_INFINITY;
      zson.toJson(infinity);
      fail("Zson should not accept positive infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
    try {
      zson.toJson(Float.POSITIVE_INFINITY);
      fail("Zson should not accept positive infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testFloatInfinitySerialization() {
    Zson zson = new ZsonBuilder().serializeSpecialFloatingPointValues().create();
    float infinity = Float.POSITIVE_INFINITY;
    assertEquals("Infinity", zson.toJson(infinity));
    assertEquals("Infinity", zson.toJson(Float.POSITIVE_INFINITY));
  }

  public void testFloatInfinityDeserialization() {
    assertTrue(Float.isInfinite(zson.fromJson("Infinity", Float.class)));
    assertTrue(Float.isInfinite(zson.fromJson("Infinity", float.class)));
  }

  public void testBigDecimalInfinityDeserializationNotSupported() {
    try {
      zson.fromJson("Infinity", BigDecimal.class);
      fail("Zson should not accept positive infinity for deserialization with BigDecimal");
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testNegativeInfinitySerializationNotSupportedByDefault() {
    try {
      double negativeInfinity = Double.NEGATIVE_INFINITY;
      zson.toJson(negativeInfinity);
      fail("Zson should not accept negative infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
    try {
      zson.toJson(Double.NEGATIVE_INFINITY);
      fail("Zson should not accept negative infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testNegativeInfinitySerialization() {
    Zson zson = new ZsonBuilder().serializeSpecialFloatingPointValues().create();
    double negativeInfinity = Double.NEGATIVE_INFINITY;
    assertEquals("-Infinity", zson.toJson(negativeInfinity));
    assertEquals("-Infinity", zson.toJson(Double.NEGATIVE_INFINITY));
  }

  public void testNegativeInfinityDeserialization() {
    assertTrue(Double.isInfinite(zson.fromJson("-Infinity", double.class)));
    assertTrue(Double.isInfinite(zson.fromJson("-Infinity", Double.class)));
  }

  public void testNegativeInfinityFloatSerializationNotSupportedByDefault() {
    try {
      float negativeInfinity = Float.NEGATIVE_INFINITY;
      zson.toJson(negativeInfinity);
      fail("Zson should not accept negative infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
    try {
      zson.toJson(Float.NEGATIVE_INFINITY);
      fail("Zson should not accept negative infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testNegativeInfinityFloatSerialization() {
    Zson zson = new ZsonBuilder().serializeSpecialFloatingPointValues().create();
    float negativeInfinity = Float.NEGATIVE_INFINITY;
    assertEquals("-Infinity", zson.toJson(negativeInfinity));
    assertEquals("-Infinity", zson.toJson(Float.NEGATIVE_INFINITY));
  }

  public void testNegativeInfinityFloatDeserialization() {
    assertTrue(Float.isInfinite(zson.fromJson("-Infinity", float.class)));
    assertTrue(Float.isInfinite(zson.fromJson("-Infinity", Float.class)));
  }

  public void testBigDecimalNegativeInfinityDeserializationNotSupported() {
    try {
      zson.fromJson("-Infinity", BigDecimal.class);
      fail("Zson should not accept positive infinity for deserialization");
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testLongAsStringSerialization() throws Exception {
    zson = new ZsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
    String result = zson.toJson(15L);
    assertEquals("\"15\"", result);

    // Test with an integer and ensure its still a number
    result = zson.toJson(2);
    assertEquals("2", result);
  }

  public void testLongAsStringDeserialization() throws Exception {
    long value = zson.fromJson("\"15\"", long.class);
    assertEquals(15, value);

    zson = new ZsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
    value = zson.fromJson("\"25\"", long.class);
    assertEquals(25, value);
  }

  public void testQuotedStringSerializationAndDeserialization() throws Exception {
    String value = "String Blah Blah Blah...1, 2, 3";
    String serializedForm = zson.toJson(value);
    assertEquals("\"" + value + "\"", serializedForm);

    String actual = zson.fromJson(serializedForm, String.class);
    assertEquals(value, actual);
  }

  public void testUnquotedStringDeserializationFails() throws Exception {
    assertEquals("UnquotedSingleWord", zson.fromJson("UnquotedSingleWord", String.class));

    String value = "String Blah Blah Blah...1, 2, 3";
    try {
      zson.fromJson(value, String.class);
      fail();
    } catch (JsonSyntaxException expected) { }
  }

  public void testHtmlCharacterSerialization() throws Exception {
    String target = "<script>var a = 12;</script>";
    String result = zson.toJson(target);
    assertFalse(result.equals('"' + target + '"'));

    zson = new ZsonBuilder().disableHtmlEscaping().create();
    result = zson.toJson(target);
    assertTrue(result.equals('"' + target + '"'));
  }

  public void testDeserializePrimitiveWrapperAsObjectField() {
    String json = "{i:10}";
    ClassWithIntegerField target = zson.fromJson(json, ClassWithIntegerField.class);
    assertEquals(10, target.i.intValue());
  }

  private static class ClassWithIntegerField {
    Integer i;
  }

  public void testPrimitiveClassLiteral() {
    assertEquals(1, zson.fromJson("1", int.class).intValue());
    assertEquals(1, zson.fromJson(new StringReader("1"), int.class).intValue());
    assertEquals(1, zson.fromJson(new JsonPrimitive(1), int.class).intValue());
  }

  public void testDeserializeJsonObjectAsLongPrimitive() {
    try {
      zson.fromJson("{'abc':1}", long.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsLongWrapper() {
    try {
      zson.fromJson("[1,2,3]", Long.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsInt() {
    try {
      zson.fromJson("[1, 2, 3, 4]", int.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsInteger() {
    try {
      zson.fromJson("{}", Integer.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsShortPrimitive() {
    try {
      zson.fromJson("{'abc':1}", short.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsShortWrapper() {
    try {
      zson.fromJson("['a','b']", Short.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsDoublePrimitive() {
    try {
      zson.fromJson("[1,2]", double.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsDoubleWrapper() {
    try {
      zson.fromJson("{'abc':1}", Double.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsFloatPrimitive() {
    try {
      zson.fromJson("{'abc':1}", float.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsFloatWrapper() {
    try {
      zson.fromJson("[1,2,3]", Float.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsBytePrimitive() {
    try {
      zson.fromJson("{'abc':1}", byte.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsByteWrapper() {
    try {
      zson.fromJson("[1,2,3,4]", Byte.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsBooleanPrimitive() {
    try {
      zson.fromJson("{'abc':1}", boolean.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsBooleanWrapper() {
    try {
      zson.fromJson("[1,2,3,4]", Boolean.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsBigDecimal() {
    try {
      zson.fromJson("[1,2,3,4]", BigDecimal.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsBigDecimal() {
    try {
      zson.fromJson("{'a':1}", BigDecimal.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsBigInteger() {
    try {
      zson.fromJson("[1,2,3,4]", BigInteger.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsBigInteger() {
    try {
      zson.fromJson("{'c':2}", BigInteger.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsNumber() {
    try {
      zson.fromJson("[1,2,3,4]", Number.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsNumber() {
    try {
      zson.fromJson("{'c':2}", Number.class);
      fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializingDecimalPointValueZeroSucceeds() {
    assertEquals(1, (int) zson.fromJson("1.0", Integer.class));
  }

  public void testDeserializingNonZeroDecimalPointValuesAsIntegerFails() {
    try {
      zson.fromJson("1.02", Byte.class);
      fail();
    } catch (JsonSyntaxException expected) {
    }
    try {
      zson.fromJson("1.02", Short.class);
      fail();
    } catch (JsonSyntaxException expected) {
    }
    try {
      zson.fromJson("1.02", Integer.class);
      fail();
    } catch (JsonSyntaxException expected) {
    }
    try {
      zson.fromJson("1.02", Long.class);
      fail();
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testDeserializingBigDecimalAsIntegerFails() {
    try {
      zson.fromJson("-122.08e-213", Integer.class);
      fail();
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testDeserializingBigIntegerAsInteger() {
    try {
      zson.fromJson("12121211243123245845384534687435634558945453489543985435", Integer.class);
      fail();
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testDeserializingBigIntegerAsLong() {
    try {
      zson.fromJson("12121211243123245845384534687435634558945453489543985435", Long.class);
      fail();
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testValueVeryCloseToZeroIsZero() {
    assertEquals(0, (byte) zson.fromJson("-122.08e-2132", byte.class));
    assertEquals(0, (short) zson.fromJson("-122.08e-2132", short.class));
    assertEquals(0, (int) zson.fromJson("-122.08e-2132", int.class));
    assertEquals(0, (long) zson.fromJson("-122.08e-2132", long.class));
    assertEquals(-0.0f, zson.fromJson("-122.08e-2132", float.class));
    assertEquals(-0.0, zson.fromJson("-122.08e-2132", double.class));
    assertEquals(0.0f, zson.fromJson("122.08e-2132", float.class));
    assertEquals(0.0, zson.fromJson("122.08e-2132", double.class));
  }

  public void testDeserializingBigDecimalAsFloat() {
    String json = "-122.08e-2132332";
    float actual = zson.fromJson(json, float.class);
    assertEquals(-0.0f, actual);
  }

  public void testDeserializingBigDecimalAsDouble() {
    String json = "-122.08e-2132332";
    double actual = zson.fromJson(json, double.class);
    assertEquals(-0.0d, actual);
  }

  public void testDeserializingBigDecimalAsBigIntegerFails() {
    try {
      zson.fromJson("-122.08e-213", BigInteger.class);
      fail();
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testDeserializingBigIntegerAsBigDecimal() {
    BigDecimal actual =
      zson.fromJson("12121211243123245845384534687435634558945453489543985435", BigDecimal.class);
    assertEquals("12121211243123245845384534687435634558945453489543985435", actual.toPlainString());
  }

  public void testStringsAsBooleans() {
    String json = "['true', 'false', 'TRUE', 'yes', '1']";
    assertEquals(Arrays.asList(true, false, true, false, false),
        zson.<List<Boolean>>fromJson(json, new TypeToken<List<Boolean>>() {}.getType()));
  }
}
