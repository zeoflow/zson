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
package com.zeoflow.zson.regression;

import com.zeoflow.zson.Zson;
import com.zeoflow.zson.TypeAdapter;
import com.zeoflow.zson.TypeAdapterFactory;
import com.zeoflow.zson.annotations.JsonAdapter;
import com.zeoflow.zson.reflect.TypeToken;

import junit.framework.TestCase;

public class JsonAdapterNullSafeTest extends TestCase {
  private final Zson zson = new Zson();

  public void testNullSafeBugSerialize() throws Exception {
    Device device = new Device("ec57803e");
    zson.toJson(device);
  }

  public void testNullSafeBugDeserialize() throws Exception {
    Device device = zson.fromJson("{'id':'ec57803e2'}", Device.class);
    assertEquals("ec57803e2", device.id);
  }

  @JsonAdapter(Device.JsonAdapterFactory.class)
  private static final class Device {
    String id;
    Device(String id) {
      this.id = id;
    }

    static final class JsonAdapterFactory implements TypeAdapterFactory {
      // The recursiveCall in {@link Device.JsonAdapterFactory} is the source of this bug
      // because we use it to return a null type adapter on a recursive call.
      private static final ThreadLocal<Boolean> recursiveCall = new ThreadLocal<Boolean>();

      @Override public <T> TypeAdapter<T> create(final Zson zson, TypeToken<T> type) {
        if (type.getRawType() != Device.class || recursiveCall.get() != null) {
          recursiveCall.set(null); // clear for subsequent use
          return null;
        }
        recursiveCall.set(Boolean.TRUE);
        return zson.getDelegateAdapter(this, type);
      }
    }
  }
}
