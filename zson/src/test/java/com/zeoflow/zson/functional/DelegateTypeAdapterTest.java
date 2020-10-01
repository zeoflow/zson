/*
 * Copyright (C) 2020 ZeoFlow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zeoflow.zson.functional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.zeoflow.zson.Zson;
import com.zeoflow.zson.ZsonBuilder;
import com.zeoflow.zson.TypeAdapter;
import com.zeoflow.zson.TypeAdapterFactory;
import com.zeoflow.zson.reflect.TypeToken;
import com.zeoflow.zson.stream.JsonReader;
import com.zeoflow.zson.stream.JsonWriter;
import com.zeoflow.zson.common.TestTypes;

/**
 * Functional tests for {@link Zson#getDelegateAdapter(TypeAdapterFactory, TypeToken)} method.
 *
 * @author Inderjeet Singh
 */
public class DelegateTypeAdapterTest extends TestCase {

  private StatsTypeAdapterFactory stats;
  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    stats = new StatsTypeAdapterFactory();
    zson = new ZsonBuilder()
      .registerTypeAdapterFactory(stats)
      .create();
  }

  public void testDelegateInvoked() {
    List<TestTypes.BagOfPrimitives> bags = new ArrayList<TestTypes.BagOfPrimitives>();
    for (int i = 0; i < 10; ++i) {
      bags.add(new TestTypes.BagOfPrimitives(i, i, i % 2 == 0, String.valueOf(i)));
    }
    String json = zson.toJson(bags);
    bags = zson.fromJson(json, new TypeToken<List<TestTypes.BagOfPrimitives>>(){}.getType());
    // 11: 1 list object, and 10 entries. stats invoked on all 5 fields
    assertEquals(51, stats.numReads);
    assertEquals(51, stats.numWrites);
  }

  public void testDelegateInvokedOnStrings() {
    String[] bags = {"1", "2", "3", "4"};
    String json = zson.toJson(bags);
    bags = zson.fromJson(json, String[].class);
    // 1 array object with 4 elements.
    assertEquals(5, stats.numReads);
    assertEquals(5, stats.numWrites);
  }

  private static class StatsTypeAdapterFactory implements TypeAdapterFactory {
    public int numReads = 0;
    public int numWrites = 0;

    @Override public <T> TypeAdapter<T> create(Zson zson, TypeToken<T> type) {
      final TypeAdapter<T> delegate = zson.getDelegateAdapter(this, type);
      return new TypeAdapter<T>() {
        @Override
        public void write(JsonWriter out, T value) throws IOException {
          ++numWrites;
          delegate.write(out, value);
        }

        @Override
        public T read(JsonReader in) throws IOException {
          ++numReads;
          return delegate.read(in);
        }
      };
    }
  }
}
