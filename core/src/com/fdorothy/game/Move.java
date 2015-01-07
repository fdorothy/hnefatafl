package com.fdorothy.game;


public class Move
{
    public Move()
    {
	_src_x = 0;
	_src_y = 0;
	_dst_x = 0;
	_dst_y = 0;
    }

    public Move(int src_x, int src_y,
		int dst_x, int dst_y)
    {
	_src_x = src_x;
	_src_y = src_y;
	_dst_x = dst_x;
	_dst_y = dst_y;
    }

    int srcX() {return _src_x;}
    int srcY() {return _src_y;}
    int dstX() {return _dst_x;}
    int dstY() {return _dst_y;}

    void srcX(int x) {_src_x = x;}
    void srcY(int y) {_src_y = y;}
    void dstX(int x) {_dst_x = x;}
    void dstY(int y) {_dst_y = y;}

    public String toString()
    {
	char srcx = (char)(_src_x + (int)'a');
	char dstx = (char)(_dst_x + (int)'a');
	return srcx + (char)_src_y + ":" + dstx + (char)_dst_y;
    }

    protected int _src_x;
    protected int _src_y;
    protected int _dst_x;
    protected int _dst_y;
}
