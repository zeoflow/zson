package com.zeoflow.zson.functional;

import com.zeoflow.zson.Zson;

import junit.framework.TestCase;

/**
 * Functional tests for Json serialization and deserialization of strings.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class StringTest extends TestCase {
  private Zson zson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    zson = new Zson();
  }

  public void testStringValueSerialization() throws Exception {
    String value = "someRandomStringValue";
    assertEquals('"' + value + '"', zson.toJson(value));
  }

  public void testStringValueDeserialization() throws Exception {
    String value = "someRandomStringValue";
    String actual = zson.fromJson("\"" + value + "\"", String.class);
    assertEquals(value, actual);
  }

  public void testSingleQuoteInStringSerialization() throws Exception {
    String valueWithQuotes = "beforeQuote'afterQuote";
    String jsonRepresentation = zson.toJson(valueWithQuotes);
    assertEquals(valueWithQuotes, zson.fromJson(jsonRepresentation, String.class));
  }

  public void testEscapedCtrlNInStringSerialization() throws Exception {
    String value = "a\nb";
    String json = zson.toJson(value);
    assertEquals("\"a\\nb\"", json);
  }

  public void testEscapedCtrlNInStringDeserialization() throws Exception {
    String json = "'a\\nb'";
    String actual = zson.fromJson(json, String.class);
    assertEquals("a\nb", actual);
  }

  public void testEscapedCtrlRInStringSerialization() throws Exception {
    String value = "a\rb";
    String json = zson.toJson(value);
    assertEquals("\"a\\rb\"", json);
  }

  public void testEscapedCtrlRInStringDeserialization() throws Exception {
    String json = "'a\\rb'";
    String actual = zson.fromJson(json, String.class);
    assertEquals("a\rb", actual);
  }

  public void testEscapedBackslashInStringSerialization() throws Exception {
    String value = "a\\b";
    String json = zson.toJson(value);
    assertEquals("\"a\\\\b\"", json);
  }

  public void testEscapedBackslashInStringDeserialization() throws Exception {
    String actual = zson.fromJson("'a\\\\b'", String.class);
    assertEquals("a\\b", actual);
  }

  public void testSingleQuoteInStringDeserialization() throws Exception {
    String value = "beforeQuote'afterQuote";
    String actual = zson.fromJson("\"" + value + "\"", String.class);
    assertEquals(value, actual);
  }

  public void testEscapingQuotesInStringSerialization() throws Exception {
    String valueWithQuotes = "beforeQuote\"afterQuote";
    String jsonRepresentation = zson.toJson(valueWithQuotes);
    String target = zson.fromJson(jsonRepresentation, String.class);
    assertEquals(valueWithQuotes, target);
  }

  public void testEscapingQuotesInStringDeserialization() throws Exception {
    String value = "beforeQuote\\\"afterQuote";
    String actual = zson.fromJson("\"" + value + "\"", String.class);
    String expected = "beforeQuote\"afterQuote";
    assertEquals(expected, actual);
  }

  public void testStringValueAsSingleElementArraySerialization() throws Exception {
    String[] target = {"abc"};
    assertEquals("[\"abc\"]", zson.toJson(target));
    assertEquals("[\"abc\"]", zson.toJson(target, String[].class));
  }

  public void testStringWithEscapedSlashDeserialization() {
    String value = "/";
    String json = "'\\/'";
    String actual = zson.fromJson(json, String.class);
    assertEquals(value, actual);
  }

  /**
   * Created in response to http://groups.google.com/group/google-Zson/browse_thread/thread/2431d4a3d0d6cb23
   */
  public void testAssignmentCharSerialization() {
    String value = "abc=";
    String json = zson.toJson(value);
    assertEquals("\"abc\\u003d\"", json);
  }

  /**
   * Created in response to http://groups.google.com/group/google-Zson/browse_thread/thread/2431d4a3d0d6cb23
   */
  public void testAssignmentCharDeserialization() {
    String json = "\"abc=\"";
    String value = zson.fromJson(json, String.class);
    assertEquals("abc=", value);

    json = "'abc\u003d'";
    value = zson.fromJson(json, String.class);
    assertEquals("abc=", value);
  }

  public void testJavascriptKeywordsInStringSerialization() {
    String value = "null true false function";
    String json = zson.toJson(value);
    assertEquals("\"" + value + "\"", json);
  }

  public void testJavascriptKeywordsInStringDeserialization() {
    String json = "'null true false function'";
    String value = zson.fromJson(json, String.class);
    assertEquals(json.substring(1, json.length() - 1), value);
  }
}
