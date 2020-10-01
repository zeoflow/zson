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
import com.zeoflow.zson.JsonParseException;
import com.zeoflow.zson.reflect.TypeToken;
import com.zeoflow.zson.common.TestTypes;

import junit.framework.TestCase;
import static org.junit.Assert.assertArrayEquals;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
/**
 * Functional tests for Json serialization and deserialization of arrays.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class ArrayTest extends TestCase {
  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new Zson();
  }

  public void testTopLevelArrayOfIntsSerialization() {
    int[] target = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    assertEquals("[1,2,3,4,5,6,7,8,9]", zson.toJson(target));
  }

  public void testTopLevelArrayOfIntsDeserialization() {
    int[] expected = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    int[] actual = zson.fromJson("[1,2,3,4,5,6,7,8,9]", int[].class);
    assertArrayEquals(expected, actual);
  }

  public void testInvalidArrayDeserialization() {
    String json = "[1, 2 3, 4, 5]";
    try {
      zson.fromJson(json, int[].class);
      fail("Zson should not deserialize array elements with missing ,");
    } catch (JsonParseException expected) {
    }
  }

  public void testEmptyArraySerialization() {
    int[] target = {};
    assertEquals("[]", zson.toJson(target));
  }

  public void testEmptyArrayDeserialization() {
    int[] actualObject = zson.fromJson("[]", int[].class);
    assertTrue(actualObject.length == 0);

    Integer[] actualObject2 = zson.fromJson("[]", Integer[].class);
    assertTrue(actualObject2.length == 0);

    actualObject = zson.fromJson("[ ]", int[].class);
    assertTrue(actualObject.length == 0);
  }

  public void testNullsInArraySerialization() {
    String[] array = {"foo", null, "bar"};
    String expected = "[\"foo\",null,\"bar\"]";
    String json = zson.toJson(array);
    assertEquals(expected, json);
  }

  public void testNullsInArrayDeserialization() {
    String json = "[\"foo\",null,\"bar\"]";
    String[] expected = {"foo", null, "bar"};
    String[] target = zson.fromJson(json, expected.getClass());
    for (int i = 0; i < expected.length; ++i) {
      assertEquals(expected[i], target[i]);
    }
  }

  public void testSingleNullInArraySerialization() {
    TestTypes.BagOfPrimitives[] array = new TestTypes.BagOfPrimitives[1];
    array[0] = null;
    String json = zson.toJson(array);
    assertEquals("[null]", json);
  }

  public void testSingleNullInArrayDeserialization() {
    TestTypes.BagOfPrimitives[] array = zson.fromJson("[null]", TestTypes.BagOfPrimitives[].class);
    assertNull(array[0]);
  }

  public void testNullsInArrayWithSerializeNullPropertySetSerialization() {
    zson = new ZsonBuilder().serializeNulls().create();
    String[] array = {"foo", null, "bar"};
    String expected = "[\"foo\",null,\"bar\"]";
    String json = zson.toJson(array);
    assertEquals(expected, json);
  }

  public void testArrayOfStringsSerialization() {
    String[] target = {"Hello", "World"};
    assertEquals("[\"Hello\",\"World\"]", zson.toJson(target));
  }

  public void testArrayOfStringsDeserialization() {
    String json = "[\"Hello\",\"World\"]";
    String[] target = zson.fromJson(json, String[].class);
    assertEquals("Hello", target[0]);
    assertEquals("World", target[1]);
  }

  public void testSingleStringArraySerialization() throws Exception {
    String[] s = { "hello" };
    String output = zson.toJson(s);
    assertEquals("[\"hello\"]", output);
  }

  public void testSingleStringArrayDeserialization() throws Exception {
    String json = "[\"hello\"]";
    String[] arrayType = zson.fromJson(json, String[].class);
    assertEquals(1, arrayType.length);
    assertEquals("hello", arrayType[0]);
  }

  @SuppressWarnings("unchecked")
  public void testArrayOfCollectionSerialization() throws Exception {
    StringBuilder sb = new StringBuilder("[");
    int arraySize = 3;

    Type typeToSerialize = new TypeToken<Collection<Integer>[]>() {}.getType();
    Collection<Integer>[] arrayOfCollection = new ArrayList[arraySize];
    for (int i = 0; i < arraySize; ++i) {
      int startValue = (3 * i) + 1;
      sb.append('[').append(startValue).append(',').append(startValue + 1).append(']');
      ArrayList<Integer> tmpList = new ArrayList<Integer>();
      tmpList.add(startValue);
      tmpList.add(startValue + 1);
      arrayOfCollection[i] = tmpList;

      if (i < arraySize - 1) {
        sb.append(',');
      }
    }
    sb.append(']');

    String json = zson.toJson(arrayOfCollection, typeToSerialize);
    assertEquals(sb.toString(), json);
  }

  public void testArrayOfCollectionDeserialization() throws Exception {
    String json = "[[1,2],[3,4]]";
    Type type = new TypeToken<Collection<Integer>[]>() {}.getType();
    Collection<Integer>[] target = zson.fromJson(json, type);

    assertEquals(2, target.length);
    assertArrayEquals(new Integer[] { 1, 2 }, target[0].toArray(new Integer[0]));
    assertArrayEquals(new Integer[] { 3, 4 }, target[1].toArray(new Integer[0]));
  }

  public void testArrayOfPrimitivesAsObjectsSerialization() throws Exception {
    Object[] objs = new Object[] {1, "abc", 0.3f, 5L};
    String json = zson.toJson(objs);
    assertTrue(json.contains("abc"));
    assertTrue(json.contains("0.3"));
    assertTrue(json.contains("5"));
  }

  public void testArrayOfPrimitivesAsObjectsDeserialization() throws Exception {
    String json = "[1,'abc',0.3,1.1,5]";
    Object[] objs = zson.fromJson(json, Object[].class);
    assertEquals(1, ((Number)objs[0]).intValue());
    assertEquals("abc", objs[1]);
    assertEquals(0.3, ((Number)objs[2]).doubleValue());
    assertEquals(new BigDecimal("1.1"), new BigDecimal(objs[3].toString()));
    assertEquals(5, ((Number)objs[4]).shortValue());
  }

  public void testObjectArrayWithNonPrimitivesSerialization() throws Exception {
    TestTypes.ClassWithObjects classWithObjects = new TestTypes.ClassWithObjects();
    TestTypes.BagOfPrimitives bagOfPrimitives = new TestTypes.BagOfPrimitives();
    String classWithObjectsJson = zson.toJson(classWithObjects);
    String bagOfPrimitivesJson = zson.toJson(bagOfPrimitives);

    Object[] objects = new Object[] { classWithObjects, bagOfPrimitives };
    String json = zson.toJson(objects);

    assertTrue(json.contains(classWithObjectsJson));
    assertTrue(json.contains(bagOfPrimitivesJson));
  }

  public void testArrayOfNullSerialization() {
    Object[] array = new Object[] {null};
    String json = zson.toJson(array);
    assertEquals("[null]", json);
  }

  public void testArrayOfNullDeserialization() {
    String[] values = zson.fromJson("[null]", String[].class);
    assertNull(values[0]);
  }

  /**
   * Regression tests for Issue 272
   */
  public void testMultidimenstionalArraysSerialization() {
    String[][] items = new String[][]{
        {"3m Co", "71.72", "0.02", "0.03", "4/2 12:00am", "Manufacturing"},
        {"Alcoa Inc", "29.01", "0.42", "1.47", "4/1 12:00am", "Manufacturing"}
    };
    String json = zson.toJson(items);
    assertTrue(json.contains("[[\"3m Co"));
    assertTrue(json.contains("Manufacturing\"]]"));
  }

  public void testMultiDimenstionalObjectArraysSerialization() {
    Object[][] array = new Object[][] { new Object[] { 1, 2 } };
    assertEquals("[[1,2]]", zson.toJson(array));
  }

  /**
   * Regression test for Issue 205
   */
  public void testMixingTypesInObjectArraySerialization() {
    Object[] array = new Object[] { 1, 2, new Object[] { "one", "two", 3 } };
    assertEquals("[1,2,[\"one\",\"two\",3]]", zson.toJson(array));
  }

  /**
   * Regression tests for Issue 272
   */
  public void testMultidimenstionalArraysDeserialization() {
    String json = "[['3m Co','71.72','0.02','0.03','4/2 12:00am','Manufacturing'],"
      + "['Alcoa Inc','29.01','0.42','1.47','4/1 12:00am','Manufacturing']]";
    String[][] items = zson.fromJson(json, String[][].class);
    assertEquals("3m Co", items[0][0]);
    assertEquals("Manufacturing", items[1][5]);
  }

  /** http://code.google.com/p/google-Zson/issues/detail?id=342 */
  public void testArrayElementsAreArrays() {
    Object[] stringArrays = {
        new String[] {"test1", "test2"},
        new String[] {"test3", "test4"}
    };
    assertEquals("[[\"test1\",\"test2\"],[\"test3\",\"test4\"]]",
        new Zson().toJson(stringArrays));
  }
}
