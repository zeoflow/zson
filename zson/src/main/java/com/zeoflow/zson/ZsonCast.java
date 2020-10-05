package com.zeoflow.zson;

import java.lang.reflect.Type;

public class ZsonCast
{

    private Object oldObject;
    private Object classOfT;

    public ZsonCast()
    {

    }

    public ZsonCast fromObject(Object oldObject)
    {
        this.oldObject = oldObject;
        return this;
    }

    public <T> ZsonCast toObject(Class<T> classOfT)
    {
        this.classOfT = classOfT;
        return this;
    }

    public <T> T cast()
    {
        Zson zson = new Zson();
        String json = zson.toJson(this.oldObject);
        Object object = zson.fromJson(json, (Type) this.classOfT);
        return (T) object;
    }

}
