package com.fdorothy.game;

import java.lang.Integer;
import java.util.Queue;
import java.util.LinkedList;

public class DistanceGraph
{
    public DistanceGraph(Game game)
    {
	_rows = game.rows();
	_game = game;
	_D = new int[_rows*_rows];
	for (int i=0; i<_rows*_rows; i++)
	    _D[i] = inf;
    }
    
    public void seed(int x, int y)
    {
	_D[x+y*_rows] = 0;
	Queue <Integer> visit = new LinkedList <Integer>();
	visit.add(toIdx(x,y));
	findDistance(visit);
    }

    protected void findDistance(Queue <Integer> visit)
    {
	int pt, d, x, y;

	while (!visit.isEmpty()) {
	    pt = visit.poll();
	    d = _D[pt];
	    x = toX(pt);
	    y = toY(pt);

	    //  visit moves that are faster than what
	    // we already know about.
	    d++;
	    visitSweep(x,y,-1, 0,d,visit);
	    visitSweep(x,y, 1, 0,d,visit);
	    visitSweep(x,y, 0,-1,d,visit);
	    visitSweep(x,y, 0, 1,d,visit);
	}
    }

    public void seedSide(Piece side)
    {
	Queue <Integer> visit = new LinkedList <Integer>();
	for (int i=0; i<_rows; i++)
	    for (int j=0; j<_rows; j++)
		if (_game.pieceOwner(i,j) == side) {
		    _D[toIdx(i,j)] = 0;
		    visit.add(toIdx(i,j));
		} else
		    _D[toIdx(i,j)] = inf;
	findDistance(visit);
    }

    protected void visitSweep(int x, int y, int dir_x, int dir_y, int d, Queue <Integer> visit)
    {
	x+=dir_x;
	y+=dir_y;
	while (true) {
	    if (x < 0 || x >= _rows || y < 0 || y >= _rows)
		return;
	    if (_game.piece(x,y) != Piece.EMPTY)
		return;
	    int idx = this.toIdx(x,y);
	    if (_D[idx] == inf || d < _D[idx]) {
		_D[idx] = d;
		visit.add(idx);
	    }
	    x+=dir_x;
	    y+=dir_y;
	}
    }

    public int D(int x, int y)
    {
	if (x < 0 || y < 0 || x >= _rows || y >= _rows)
	    return inf;
	return _D[x+y*_rows];
    }

    public String toString()
    {
	StringBuilder sb = new StringBuilder();

	for (int j=_rows-1; j>=0; j--) {
	    for (int i=0; i<_rows; i++) {
		int d = D(i,j);
		if (i == _rows-1)
		    sb.append(" " + d);//String.format("%4d", d));
		else
		    sb.append(" " + d);//String.format("%4d,", d));
	    }
	    sb.append("\n");
	}
	return sb.toString();
    }

    protected int toIdx(int x, int y)
    {
	return x+y*_rows;
    }

    protected int toX(int idx)
    {
	return idx % _rows;
    }

    protected int toY(int idx)
    {
	return idx / _rows;
    }
    
    protected static final int inf = 999;
    protected int[] _D;
    protected int _rows;
    protected Game _game;
}
