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
import com.zeoflow.zson.JsonArray;
import com.zeoflow.zson.JsonElement;
import com.zeoflow.zson.JsonObject;
import com.zeoflow.zson.JsonSerializationContext;
import com.zeoflow.zson.JsonSerializer;
import com.zeoflow.zson.common.TestTypes;

import junit.framework.TestCase;

import java.lang.reflect.Type;

/**
 * Functional Test exercising custom serialization only.  When test applies to both
 * serialization and deserialization then add it to CustomTypeAdapterTest.
 *
 * @author Inderjeet Singh
 */
public class CustomSerializerTest extends TestCase {

   public void testBaseClassSerializerInvokedForBaseClassFields() {
     Zson zson = new ZsonBuilder()
         .registerTypeAdapter(TestTypes.Base.class, new TestTypes.BaseSerializer())
         .registerTypeAdapter(TestTypes.Sub.class, new TestTypes.SubSerializer())
         .create();
     TestTypes.ClassWithBaseField target = new TestTypes.ClassWithBaseField(new TestTypes.Base());
     JsonObject json = (JsonObject) zson.toJsonTree(target);
     JsonObject base = json.get("base").getAsJsonObject();
     assertEquals(TestTypes.BaseSerializer.NAME, base.get(TestTypes.Base.SERIALIZER_KEY).getAsString());
   }

   public void testSubClassSerializerInvokedForBaseClassFieldsHoldingSubClassInstances() {
     Zson zson = new ZsonBuilder()
         .registerTypeAdapter(TestTypes.Base.class, new TestTypes.BaseSerializer())
         .registerTypeAdapter(TestTypes.Sub.class, new TestTypes.SubSerializer())
         .create();
     TestTypes.ClassWithBaseField target = new TestTypes.ClassWithBaseField(new TestTypes.Sub());
     JsonObject json = (JsonObject) zson.toJsonTree(target);
     JsonObject base = json.get("base").getAsJsonObject();
     assertEquals(TestTypes.SubSerializer.NAME, base.get(TestTypes.Base.SERIALIZER_KEY).getAsString());
   }

   public void testSubClassSerializerInvokedForBaseClassFieldsHoldingArrayOfSubClassInstances() {
     Zson zson = new ZsonBuilder()
         .registerTypeAdapter(TestTypes.Base.class, new TestTypes.BaseSerializer())
         .registerTypeAdapter(TestTypes.Sub.class, new TestTypes.SubSerializer())
         .create();
     TestTypes.ClassWithBaseArrayField target = new TestTypes.ClassWithBaseArrayField(new TestTypes.Base[] {new TestTypes.Sub(), new TestTypes.Sub()});
     JsonObject json = (JsonObject) zson.toJsonTree(target);
     JsonArray array = json.get("base").getAsJsonArray();
     for (JsonElement element : array) {
       JsonElement serializerKey = element.getAsJsonObject().get(TestTypes.Base.SERIALIZER_KEY);
      assertEquals(TestTypes.SubSerializer.NAME, serializerKey.getAsString());
     }
   }

   public void testBaseClassSerializerInvokedForBaseClassFieldsHoldingSubClassInstances() {
     Zson zson = new ZsonBuilder()
         .registerTypeAdapter(TestTypes.Base.class, new TestTypes.BaseSerializer())
         .create();
     TestTypes.ClassWithBaseField target = new TestTypes.ClassWithBaseField(new TestTypes.Sub());
     JsonObject json = (JsonObject) zson.toJsonTree(target);
     JsonObject base = json.get("base").getAsJsonObject();
     assertEquals(TestTypes.BaseSerializer.NAME, base.get(TestTypes.Base.SERIALIZER_KEY).getAsString());
   }

   public void testSerializerReturnsNull() {
     Zson zson = new ZsonBuilder()
       .registerTypeAdapter(TestTypes.Base.class, new JsonSerializer<TestTypes.Base>() {
         public JsonElement serialize(TestTypes.Base src, Type typeOfSrc, JsonSerializationContext context) {
           return null;
         }
       })
       .create();
       JsonElement json = zson.toJsonTree(new TestTypes.Base());
       assertTrue(json.isJsonNull());
   }
}
