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

import com.zeoflow.zson.FieldNamingPolicy;
import com.zeoflow.zson.FieldNamingStrategy;
import com.zeoflow.zson.Zson;
import com.zeoflow.zson.ZsonBuilder;
import com.zeoflow.zson.annotations.SerializedName;
import com.zeoflow.zson.common.TestTypes;

import junit.framework.TestCase;

import java.lang.reflect.Field;

/**
 * Functional tests for naming policies.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class NamingPolicyTest extends TestCase {
  private ZsonBuilder builder;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    builder = new ZsonBuilder();
  }

  public void testZsonWithNonDefaultFieldNamingPolicySerialization() {
    Zson zson = builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
    TestTypes.StringWrapper target = new TestTypes.StringWrapper("blah");
    assertEquals("{\"SomeConstantStringInstanceField\":\""
        + target.someConstantStringInstanceField + "\"}", zson.toJson(target));
  }

  public void testZsonWithNonDefaultFieldNamingPolicyDeserialiation() {
    Zson zson = builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
    String target = "{\"SomeConstantStringInstanceField\":\"someValue\"}";
    TestTypes.StringWrapper deserializedObject = zson.fromJson(target, TestTypes.StringWrapper.class);
    assertEquals("someValue", deserializedObject.someConstantStringInstanceField);
  }

  public void testZsonWithLowerCaseDashPolicySerialization() {
    Zson zson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
    TestTypes.StringWrapper target = new TestTypes.StringWrapper("blah");
    assertEquals("{\"some-constant-string-instance-field\":\""
        + target.someConstantStringInstanceField + "\"}", zson.toJson(target));
  }

  public void testZsonWithLowerCaseDotPolicySerialization() {
    Zson zson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DOTS).create();
    TestTypes.StringWrapper target = new TestTypes.StringWrapper("blah");
    assertEquals("{\"some.constant.string.instance.field\":\""
          + target.someConstantStringInstanceField + "\"}", zson.toJson(target));
  }

  public void testZsonWithLowerCaseDotPolicyDeserialiation() {
    Zson zson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DOTS).create();
    String target = "{\"some.constant.string.instance.field\":\"someValue\"}";
    TestTypes.StringWrapper deserializedObject = zson.fromJson(target, TestTypes.StringWrapper.class);
    assertEquals("someValue", deserializedObject.someConstantStringInstanceField);
  }

  public void testZsonWithLowerCaseDashPolicyDeserialiation() {
    Zson zson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
    String target = "{\"some-constant-string-instance-field\":\"someValue\"}";
    TestTypes.StringWrapper deserializedObject = zson.fromJson(target, TestTypes.StringWrapper.class);
    assertEquals("someValue", deserializedObject.someConstantStringInstanceField);
  }

  public void testZsonWithLowerCaseUnderscorePolicySerialization() {
    Zson zson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();
    TestTypes.StringWrapper target = new TestTypes.StringWrapper("blah");
    assertEquals("{\"some_constant_string_instance_field\":\""
        + target.someConstantStringInstanceField + "\"}", zson.toJson(target));
  }

  public void testZsonWithLowerCaseUnderscorePolicyDeserialiation() {
    Zson zson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();
    String target = "{\"some_constant_string_instance_field\":\"someValue\"}";
    TestTypes.StringWrapper deserializedObject = zson.fromJson(target, TestTypes.StringWrapper.class);
    assertEquals("someValue", deserializedObject.someConstantStringInstanceField);
  }

  public void testZsonWithSerializedNameFieldNamingPolicySerialization() {
    Zson zson = builder.create();
    TestTypes.ClassWithSerializedNameFields expected = new TestTypes.ClassWithSerializedNameFields(5, 6);
    String actual = zson.toJson(expected);
    assertEquals(expected.getExpectedJson(), actual);
  }

  public void testZsonWithSerializedNameFieldNamingPolicyDeserialization() {
    Zson zson = builder.create();
    TestTypes.ClassWithSerializedNameFields expected = new TestTypes.ClassWithSerializedNameFields(5, 7);
    TestTypes.ClassWithSerializedNameFields actual =
        zson.fromJson(expected.getExpectedJson(), TestTypes.ClassWithSerializedNameFields.class);
    assertEquals(expected.f, actual.f);
  }

  public void testZsonDuplicateNameUsingSerializedNameFieldNamingPolicySerialization() {
    Zson zson = builder.create();
    try {
      ClassWithDuplicateFields target = new ClassWithDuplicateFields(10);
      zson.toJson(target);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testZsonWithUpperCamelCaseSpacesPolicySerialiation() {
    Zson zson = builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES)
        .create();
    TestTypes.StringWrapper target = new TestTypes.StringWrapper("blah");
    assertEquals("{\"Some Constant String Instance Field\":\""
        + target.someConstantStringInstanceField + "\"}", zson.toJson(target));
  }

  public void testZsonWithUpperCamelCaseSpacesPolicyDeserialiation() {
    Zson zson = builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES)
        .create();
    String target = "{\"Some Constant String Instance Field\":\"someValue\"}";
    TestTypes.StringWrapper deserializedObject = zson.fromJson(target, TestTypes.StringWrapper.class);
    assertEquals("someValue", deserializedObject.someConstantStringInstanceField);
  }

  public void testDeprecatedNamingStrategy() throws Exception {
    Zson zson = builder.setFieldNamingStrategy(new UpperCaseNamingStrategy()).create();
    ClassWithDuplicateFields target = new ClassWithDuplicateFields(10);
    String actual = zson.toJson(target);
    assertEquals("{\"A\":10}", actual);
  }

  public void testComplexFieldNameStrategy() throws Exception {
    Zson zson = new Zson();
    String json = zson.toJson(new ClassWithComplexFieldName(10));
    String escapedFieldName = "@value\\\"_s$\\\\";
    assertEquals("{\"" + escapedFieldName + "\":10}", json);

    ClassWithComplexFieldName obj = zson.fromJson(json, ClassWithComplexFieldName.class);
    assertEquals(10, obj.value);
  }

  /** http://code.google.com/p/google-Zson/issues/detail?id=349 */
  public void testAtSignInSerializedName() {
    assertEquals("{\"@foo\":\"bar\"}", new Zson().toJson(new AtName()));
  }

  static final class AtName {
    @SerializedName("@foo") String f = "bar";
  }

  private static final class UpperCaseNamingStrategy implements FieldNamingStrategy {
    @Override
    public String translateName(Field f) {
      return f.getName().toUpperCase();
    }
  }

  @SuppressWarnings("unused")
  private static class ClassWithDuplicateFields {
    public Integer a;
    @SerializedName("a") public Double b;

    public ClassWithDuplicateFields(Integer a) {
      this(a, null);
    }

    public ClassWithDuplicateFields(Double b) {
      this(null, b);
    }

    public ClassWithDuplicateFields(Integer a, Double b) {
      this.a = a;
      this.b = b;
    }
  }

  private static class ClassWithComplexFieldName {
    @SerializedName("@value\"_s$\\") public final long value;

    ClassWithComplexFieldName(long value) {
      this.value = value;
    }
  }
}
