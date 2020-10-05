package com.zeoflow.test.models;

public class ModelClassOld
{

    private boolean error = false;
    private int errorID = 23435;
    private String errorContent = "ssafdg";
    private int count = 2;

    public ModelClassOld()
    {

    }

    public boolean isError()
    {
        return error;
    }

    public int getErrorID()
    {
        return errorID;
    }

    public String getErrorContent()
    {
        return errorContent;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public int getCount()
    {
        return count;
    }

}
