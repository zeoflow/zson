package com.zeoflow.zson.model;

import com.zeoflow.zson.JsonElement;

public class Attribute
{

    private String key;
    private JsonElement value;

    public <T> Attribute(String key, JsonElement value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public JsonElement getValue()
    {
        return value;
    }

    public String getValueType()
    {
        return printClass(value);
    }

    private String printClass(JsonElement je)
    {
        if (je.isJsonNull())
            return "null";

        if (je.isJsonPrimitive())
        {
            if (je.getAsJsonPrimitive().isBoolean())
                return "Boolean";
            if (je.getAsJsonPrimitive().isString())
                return "String";
            if (je.getAsJsonPrimitive().isNumber())
            {
                return "Number";
            }
        }

        if (je.isJsonArray())
        {
            return "Array";
        }

        if (je.isJsonObject())
        {
            return "Object";
        }
        return "Unknown";

    }

}
