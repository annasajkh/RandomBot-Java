package com.github.annasajkh;

public class Api
{
    public static enum Type
    {
        MOTIVATION,
        JOKE,
        ADVICE,
        QUOTE,
        IMAGE
    }

    public String link;
    public Type type;

    public Api(String link, Type type)
    {
        this.link = link;
        this.type = type;
    }

}





