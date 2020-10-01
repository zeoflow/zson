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

import static com.zeoflow.zson.FieldNamingPolicy.IDENTITY;
import static com.zeoflow.zson.FieldNamingPolicy.LOWER_CASE_WITH_DASHES;
import static com.zeoflow.zson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
import static com.zeoflow.zson.FieldNamingPolicy.UPPER_CAMEL_CASE;
import static com.zeoflow.zson.FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES;

import com.zeoflow.zson.FieldNamingPolicy;
import com.zeoflow.zson.Zson;
import com.zeoflow.zson.ZsonBuilder;
import com.zeoflow.zson.annotations.SerializedName;

import junit.framework.TestCase;

public final class FieldNamingTest extends TestCase {
  public void testIdentity() {
    Zson zson = getZsonWithNamingPolicy(IDENTITY);
    assertEquals("{'lowerCamel':1,'UpperCamel':2,'_lowerCamelLeadingUnderscore':3," +
        "'_UpperCamelLeadingUnderscore':4,'lower_words':5,'UPPER_WORDS':6," +
        "'annotatedName':7,'lowerId':8,'_9':9}",
        zson.toJson(new TestNames()).replace('\"', '\''));
  }

  public void testUpperCamelCase() {
    Zson zson = getZsonWithNamingPolicy(UPPER_CAMEL_CASE);
    assertEquals("{'LowerCamel':1,'UpperCamel':2,'_LowerCamelLeadingUnderscore':3," +
        "'_UpperCamelLeadingUnderscore':4,'Lower_words':5,'UPPER_WORDS':6," +
        "'annotatedName':7,'LowerId':8,'_9':9}",
        zson.toJson(new TestNames()).replace('\"', '\''));
  }

  public void testUpperCamelCaseWithSpaces() {
    Zson zson = getZsonWithNamingPolicy(UPPER_CAMEL_CASE_WITH_SPACES);
    assertEquals("{'Lower Camel':1,'Upper Camel':2,'_Lower Camel Leading Underscore':3," +
        "'_ Upper Camel Leading Underscore':4,'Lower_words':5,'U P P E R_ W O R D S':6," +
        "'annotatedName':7,'Lower Id':8,'_9':9}",
        zson.toJson(new TestNames()).replace('\"', '\''));
  }

  public void testLowerCaseWithUnderscores() {
    Zson zson = getZsonWithNamingPolicy(LOWER_CASE_WITH_UNDERSCORES);
    assertEquals("{'lower_camel':1,'upper_camel':2,'_lower_camel_leading_underscore':3," +
        "'__upper_camel_leading_underscore':4,'lower_words':5,'u_p_p_e_r__w_o_r_d_s':6," +
        "'annotatedName':7,'lower_id':8,'_9':9}",
        zson.toJson(new TestNames()).replace('\"', '\''));
  }

  public void testLowerCaseWithDashes() {
    Zson zson = getZsonWithNamingPolicy(LOWER_CASE_WITH_DASHES);
    assertEquals("{'lower-camel':1,'upper-camel':2,'_lower-camel-leading-underscore':3," +
        "'_-upper-camel-leading-underscore':4,'lower_words':5,'u-p-p-e-r_-w-o-r-d-s':6," +
        "'annotatedName':7,'lower-id':8,'_9':9}",
        zson.toJson(new TestNames()).replace('\"', '\''));
  }

  private Zson getZsonWithNamingPolicy(FieldNamingPolicy fieldNamingPolicy){
    return new ZsonBuilder()
      .setFieldNamingPolicy(fieldNamingPolicy)
        .create();
  }

  @SuppressWarnings("unused") // fields are used reflectively
  private static class TestNames {
    int lowerCamel = 1;
    int UpperCamel = 2;
    int _lowerCamelLeadingUnderscore = 3;
    int _UpperCamelLeadingUnderscore = 4;
    int lower_words = 5;
    int UPPER_WORDS = 6;
    @SerializedName("annotatedName") int annotated = 7;
    int lowerId = 8;
    int _9 = 9;
  }
}
