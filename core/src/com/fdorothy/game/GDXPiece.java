package com.fdorothy.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class GDXPiece {
    Piece piece;
    Rectangle bounds;
    boolean selected;

    GDXPiece()
    {
	piece = Piece.EMPTY;
	bounds = new Rectangle(0,0,32,32);
    }

    GDXPiece(Piece p, Rectangle b)
    {
	piece = p;
	bounds = b;
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

    boolean selected()
    {
	return selected;
    }

    public void selected(boolean v)
    {
	selected = v;
    }

}
