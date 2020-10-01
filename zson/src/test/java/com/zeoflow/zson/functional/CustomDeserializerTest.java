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
import com.zeoflow.zson.JsonDeserializationContext;
import com.zeoflow.zson.JsonDeserializer;
import com.zeoflow.zson.JsonElement;
import com.zeoflow.zson.JsonObject;
import com.zeoflow.zson.JsonParseException;
import com.zeoflow.zson.common.TestTypes;

import junit.framework.TestCase;

import java.lang.reflect.Type;

/**
 * Functional Test exercising custom deserialization only. When test applies to both
 * serialization and deserialization then add it to CustomTypeAdapterTest.
 *
 * @author Joel Leitch
 */
public class CustomDeserializerTest extends TestCase {
  private static final String DEFAULT_VALUE = "test123";
  private static final String SUFFIX = "blah";

  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new ZsonBuilder().registerTypeAdapter(DataHolder.class, new DataHolderDeserializer()).create();
  }

  public void testDefaultConstructorNotCalledOnObject() throws Exception {
    DataHolder data = new DataHolder(DEFAULT_VALUE);
    String json = zson.toJson(data);

    DataHolder actual = zson.fromJson(json, DataHolder.class);
    assertEquals(DEFAULT_VALUE + SUFFIX, actual.getData());
  }

  public void testDefaultConstructorNotCalledOnField() throws Exception {
    DataHolderWrapper dataWrapper = new DataHolderWrapper(new DataHolder(DEFAULT_VALUE));
    String json = zson.toJson(dataWrapper);

    DataHolderWrapper actual = zson.fromJson(json, DataHolderWrapper.class);
    assertEquals(DEFAULT_VALUE + SUFFIX, actual.getWrappedData().getData());
  }

  private static class DataHolder {
    private final String data;

    // For use by Zson
    @SuppressWarnings("unused")
    private DataHolder() {
      throw new IllegalStateException();
    }

    public DataHolder(String data) {
      this.data = data;
    }

    public String getData() {
      return data;
    }
  }

  private static class DataHolderWrapper {
    private final DataHolder wrappedData;

    // For use by Zson
    @SuppressWarnings("unused")
    private DataHolderWrapper() {
      this(new DataHolder(DEFAULT_VALUE));
    }

    public DataHolderWrapper(DataHolder data) {
      this.wrappedData = data;
    }

    public DataHolder getWrappedData() {
      return wrappedData;
    }
  }

  private static class DataHolderDeserializer implements JsonDeserializer<DataHolder> {
    @Override
    public DataHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException
    {
      JsonObject jsonObj = json.getAsJsonObject();
      String dataString = jsonObj.get("data").getAsString();
      return new DataHolder(dataString + SUFFIX);
    }
  }

  public void testJsonTypeFieldBasedDeserialization() {
    String json = "{field1:'abc',field2:'def',__type__:'SUB_TYPE1'}";
    Zson zson = new ZsonBuilder().registerTypeAdapter(MyBase.class, new JsonDeserializer<MyBase>() {
      @Override public MyBase deserialize(JsonElement json, Type pojoType,
          JsonDeserializationContext context) throws JsonParseException {
        String type = json.getAsJsonObject().get(MyBase.TYPE_ACCESS).getAsString();
        return context.deserialize(json, SubTypes.valueOf(type).getSubclass());
      }
    }).create();
    SubType1 target = (SubType1) zson.fromJson(json, MyBase.class);
    assertEquals("abc", target.field1);
  }

  private static class MyBase {
    static final String TYPE_ACCESS = "__type__";
  }

  private enum SubTypes {
    SUB_TYPE1(SubType1.class),
    SUB_TYPE2(SubType2.class);
    private final Type subClass;
    private SubTypes(Type subClass) {
      this.subClass = subClass;
    }
    public Type getSubclass() {
      return subClass;
    }
  }

  private static class SubType1 extends MyBase {
    String field1;
  }

  private static class SubType2 extends MyBase {
    @SuppressWarnings("unused")
    String field2;
  }

  public void testCustomDeserializerReturnsNullForTopLevelObject() {
    Zson zson = new ZsonBuilder()
      .registerTypeAdapter(TestTypes.Base.class, new JsonDeserializer<TestTypes.Base>() {
        @Override
        public TestTypes.Base deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
          return null;
        }
      }).create();
    String json = "{baseName:'Base',subName:'SubRevised'}";
    TestTypes.Base target = zson.fromJson(json, TestTypes.Base.class);
    assertNull(target);
  }

  public void testCustomDeserializerReturnsNull() {
    Zson zson = new ZsonBuilder()
      .registerTypeAdapter(TestTypes.Base.class, new JsonDeserializer<TestTypes.Base>() {
        @Override
        public TestTypes.Base deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
          return null;
        }
      }).create();
    String json = "{base:{baseName:'Base',subName:'SubRevised'}}";
    TestTypes.ClassWithBaseField target = zson.fromJson(json, TestTypes.ClassWithBaseField.class);
    assertNull(target.base);
  }

  public void testCustomDeserializerReturnsNullForArrayElements() {
    Zson zson = new ZsonBuilder()
      .registerTypeAdapter(TestTypes.Base.class, new JsonDeserializer<TestTypes.Base>() {
        @Override
        public TestTypes.Base deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
          return null;
        }
      }).create();
    String json = "[{baseName:'Base'},{baseName:'Base'}]";
    TestTypes.Base[] target = zson.fromJson(json, TestTypes.Base[].class);
    assertNull(target[0]);
    assertNull(target[1]);
  }

  public void testCustomDeserializerReturnsNullForArrayElementsForArrayField() {
    Zson zson = new ZsonBuilder()
      .registerTypeAdapter(TestTypes.Base.class, new JsonDeserializer<TestTypes.Base>() {
        @Override
        public TestTypes.Base deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
          return null;
        }
      }).create();
    String json = "{bases:[{baseName:'Base'},{baseName:'Base'}]}";
    ClassWithBaseArray target = zson.fromJson(json, ClassWithBaseArray.class);
    assertNull(target.bases[0]);
    assertNull(target.bases[1]);
  }

  private static final class ClassWithBaseArray {
    TestTypes.Base[] bases;
  }
}
