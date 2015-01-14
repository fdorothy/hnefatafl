package com.fdorothy.game;

import java.util.ArrayList;

public class History
{
    protected ArrayList <Move> moves;

    public History()
    {
	moves = new ArrayList <Move>();
    }

    public void addMove(Move move)
    {
	moves.add(move);
    }

    public ArrayList <Move> moves()
    {
	return this.moves;
    }

    public String toString()
    {
	StringBuilder sb = new StringBuilder();
	for (Move m : moves) {
	    sb.append(m.toString() + "\n");
	}
	return sb.toString();
    }

    public static History fromString(String s) throws Exception
    {
	History h = new History();
	String[] parts = s.split("\n");
	for (String p : parts) {
	    h.addMove(Move.fromString(p));
	}
	return h;
    }
}
