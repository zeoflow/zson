// Copyright (C) 2014 Trymph Inc.
package com.zeoflow.zson.functional;

import java.io.IOException;

import junit.framework.TestCase;

import com.zeoflow.zson.Zson;
import com.zeoflow.zson.annotations.SerializedName;

@SuppressWarnings("serial")
public final class ThrowableFunctionalTest extends TestCase {
  private final Zson zson = new Zson();

  public void testExceptionWithoutCause() {
    RuntimeException e = new RuntimeException("hello");
    String json = zson.toJson(e);
    assertTrue(json.contains("hello"));

    e = zson.fromJson("{'detailMessage':'hello'}", RuntimeException.class);
    assertEquals("hello", e.getMessage());
  }

  public void testExceptionWithCause() {
    Exception e = new Exception("top level", new IOException("io error"));
    String json = zson.toJson(e);
    assertTrue(json.contains("{\"detailMessage\":\"top level\",\"cause\":{\"detailMessage\":\"io error\""));

    e = zson.fromJson("{'detailMessage':'top level','cause':{'detailMessage':'io error'}}", Exception.class);
    assertEquals("top level", e.getMessage());
    assertTrue(e.getCause() instanceof Throwable); // cause is not parameterized so type info is lost
    assertEquals("io error", e.getCause().getMessage());
  }

  public void testSerializedNameOnExceptionFields() {
    MyException e = new MyException();
    String json = zson.toJson(e);
    assertTrue(json.contains("{\"my_custom_name\":\"myCustomMessageValue\""));
  }

  public void testErrorWithoutCause() {
    OutOfMemoryError e = new OutOfMemoryError("hello");
    String json = zson.toJson(e);
    assertTrue(json.contains("hello"));

    e = zson.fromJson("{'detailMessage':'hello'}", OutOfMemoryError.class);
    assertEquals("hello", e.getMessage());
  }

  public void testErrornWithCause() {
    Error e = new Error("top level", new IOException("io error"));
    String json = zson.toJson(e);
    assertTrue(json.contains("top level"));
    assertTrue(json.contains("io error"));

    e = zson.fromJson("{'detailMessage':'top level','cause':{'detailMessage':'io error'}}", Error.class);
    assertEquals("top level", e.getMessage());
    assertTrue(e.getCause() instanceof Throwable); // cause is not parameterized so type info is lost
    assertEquals("io error", e.getCause().getMessage());
  }

  private static final class MyException extends Throwable {
    @SerializedName("my_custom_name") String myCustomMessage = "myCustomMessageValue";
  }
}
