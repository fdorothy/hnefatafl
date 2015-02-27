package com.fdorothy.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class GdxPiece {
    Piece piece;
    Rectangle bounds;
    int x, y;
    float alpha;

    GdxPiece()
    {
	piece = Piece.EMPTY;
	bounds = new Rectangle(0,0,32,32);
	alpha = 1.0f;
    }

    GdxPiece(Piece p, Rectangle b, int x, int y)
    {
	piece = p;
	bounds = b;
	alpha = 1.0f;
	this.x=x;
	this.y=y;
    }

    void setBounds(Rectangle rect)
    {
	bounds = rect;
    }

    Rectangle getBounds()
    {
	return bounds;
    }

    void setPiece(Piece p)
    {
	piece = p;
    }

    Piece getPiece()
    {
	return piece;
    }

    Piece owner()
    {
	if (piece == Piece.WHITE) return Piece.WHITE;
	return Piece.RED;
    }

    public float getAlpha()
    {
	return alpha;
    }

    public void setAlpha(float v)
    {
	alpha = v;
    }

    public boolean equals(GdxPiece rhs)
    {
	return (x == rhs.x && y == rhs.y && piece == rhs.piece);
    }
}
