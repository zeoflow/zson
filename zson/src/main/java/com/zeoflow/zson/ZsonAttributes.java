package com.zeoflow.zson;

import com.zeoflow.zson.model.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ZsonAttributes
{

    private JsonElement zElement;
    private List<Attribute> zAttributes;

    public ZsonAttributes(String jsonObject)
    {
        this.zElement = JsonParser.parseString(jsonObject);
        JsonObject obj = this.zElement.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
        List<Attribute> list = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : entries)
        {
            list.add(new Attribute(entry.getKey(), entry.getValue()));
        }
        zAttributes = list;
    }

    public static ZsonAttributes withObject(Object object)
    {
        return new ZsonAttributes(new Zson().toJson(object));
    }

    public static ZsonAttributes withJson(String jsonObject)
    {
        return new ZsonAttributes(jsonObject);
    }

    public JsonElement getJsonElement()
    {
        return this.zElement;
    }

    public List<Attribute> getAllAttributes()
    {
        return zAttributes;
    }

    public Attribute getAttribute(String... keys)
    {
        Attribute zJsonElement = null;
        for (String key : keys)
        {
            if (zJsonElement == null)
            {
                zJsonElement = new Attribute(key, this.zElement.getAsJsonObject().get(key));
            } else
            {
                if (zJsonElement.getValue() == null || !zJsonElement.getValue().isJsonObject())
                {
                    return new Attribute(keys[keys.length - 1], null);
                }
                if (zJsonElement.getValue().isJsonObject())
                {
                    zJsonElement = new Attribute(key, zJsonElement.getValue().getAsJsonObject().get(key));
                }
            }
        }
        return zJsonElement;
    }

}
