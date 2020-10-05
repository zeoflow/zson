/**
 * Copyright 2020 ZeoFlow SRL
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zeoflow.test;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.zeoflow.test.models.ModelClassNew;
import com.zeoflow.test.models.ModelClassOld;
import com.zeoflow.zson.JsonElement;
import com.zeoflow.zson.Zson;
import com.zeoflow.zson.ZsonAttributes;
import com.zeoflow.zson.ZsonCast;
import com.zeoflow.zson.model.Attribute;

import java.util.List;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ModelClassOld zModelClassOld = new ModelClassOld();
//        zModelClassOld.setCount(124);
//        ModelClassNew zModelClassNew = ZsonCast.fromObject(zModelClassOld)
//            .toObject(ModelClassNew.class)
//            .cast();
//
//        Zson zson = new Zson();
//        zson.newBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");

//        String jsonString = "[{strUserData=strUserData, user_id=12}]";
//        ZsonAttributes
//            .withJson(jsonString);

//        List<Attribute> entries = ZsonAttributes
//            .withObject(zModelClassNew)
//            .getAllAttributes();

//        Attribute zJsonElement = ZsonAttributes
//            .withObject(zModelClassNew)
//            .getAttribute("errorContent");
//        Log.d("zJsonElement", String.valueOf(zJsonElement.getValue()));
//        zJsonElement = ZsonAttributes
//            .withObject(zModelClassNew)
//            .getAttribute("zModelClassNewS", "errorContent");
//        Log.d("zJsonElement", String.valueOf(zJsonElement.getValue()));
//        zJsonElement = ZsonAttributes
//            .withObject(zModelClassNew)
//            .getAttribute("zModelClassNewS", "zModelClassNewS", "errorContent");
//        Log.d("zJsonElement", String.valueOf(zJsonElement.getValue()));

    }

}