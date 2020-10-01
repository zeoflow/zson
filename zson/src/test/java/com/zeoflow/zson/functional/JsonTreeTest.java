package com.zeoflow.zson.functional;

import com.zeoflow.zson.Zson;
import com.zeoflow.zson.JsonElement;
import com.zeoflow.zson.JsonObject;
import com.zeoflow.zson.JsonPrimitive;
import com.zeoflow.zson.common.TestTypes;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import junit.framework.TestCase;

/**
 * Functional tests for {@link Zson#toJsonTree(Object)} and
 * {@link Zson#toJsonTree(Object, java.lang.reflect.Type)}
 * 
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class JsonTreeTest extends TestCase {
  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new Zson();
  }

  public void testToJsonTree() {
    TestTypes.BagOfPrimitives bag = new TestTypes.BagOfPrimitives(10L, 5, false, "foo");
    JsonElement json = zson.toJsonTree(bag);
    assertTrue(json.isJsonObject());
    JsonObject obj = json.getAsJsonObject();
    Set<Entry<String, JsonElement>> children = obj.entrySet();
    assertEquals(4, children.size());
    assertContains(obj, new JsonPrimitive(10L));
    assertContains(obj, new JsonPrimitive(5));
    assertContains(obj, new JsonPrimitive(false));
    assertContains(obj, new JsonPrimitive("foo"));
  }

  public void testToJsonTreeObjectType() {
    SubTypeOfBagOfPrimitives bag = new SubTypeOfBagOfPrimitives(10L, 5, false, "foo", 1.4F);
    JsonElement json = zson.toJsonTree(bag, TestTypes.BagOfPrimitives.class);
    assertTrue(json.isJsonObject());
    JsonObject obj = json.getAsJsonObject();
    Set<Entry<String, JsonElement>> children = obj.entrySet();
    assertEquals(4, children.size());
    assertContains(obj, new JsonPrimitive(10L));
    assertContains(obj, new JsonPrimitive(5));
    assertContains(obj, new JsonPrimitive(false));
    assertContains(obj, new JsonPrimitive("foo"));
  }

  public void testJsonTreeToString() {
    SubTypeOfBagOfPrimitives bag = new SubTypeOfBagOfPrimitives(10L, 5, false, "foo", 1.4F);
    String json1 = zson.toJson(bag);
    JsonElement jsonElement = zson.toJsonTree(bag, SubTypeOfBagOfPrimitives.class);
    String json2 = zson.toJson(jsonElement);
    assertEquals(json1, json2);
  }

  public void testJsonTreeNull() {
    TestTypes.BagOfPrimitives bag = new TestTypes.BagOfPrimitives(10L, 5, false, null);
    JsonObject jsonElement = (JsonObject) zson.toJsonTree(bag, TestTypes.BagOfPrimitives.class);
    assertFalse(jsonElement.has("stringValue"));
  }

  private void assertContains(JsonObject json, JsonPrimitive child) {
    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
      JsonElement node = entry.getValue();
      if (node.isJsonPrimitive()) {
        if (node.getAsJsonPrimitive().equals(child)) {
          return;
        }
      }
    }
    fail();
  }
  
  private static class SubTypeOfBagOfPrimitives extends TestTypes.BagOfPrimitives
  {
    @SuppressWarnings("unused")
    float f = 1.2F;
    public SubTypeOfBagOfPrimitives(long l, int i, boolean b, String string, float f) {
      super(l, i, b, string);
      this.f = f;
    }
  }
}
