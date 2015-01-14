package com.fdorothy.game;

public class Utility
{
    public static String padLeft(String str, int size)
    {
	StringBuffer buf = new StringBuffer(str);
	while (buf.length() < size)
	    buf.insert(0,' ');
	return buf.toString();
    }
}
