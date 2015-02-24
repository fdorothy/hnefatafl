package com.fdorothy.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

public class Resources
{
    // textures used for rendering the game
    public Texture wood;
    public Texture red;
    public Texture king;
    public Texture white;
    public Texture tile;
    public Texture xtile;
    public Texture redTurn;
    public Texture whiteTurn;

    // font for text rendering
    public BitmapFont font;

    public Resources()
    {
	wood = new Texture("wood.png");
	wood.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
	red = new Texture("red.png");
	king = new Texture("red_king.png");
	white = new Texture("white.png");
	tile = new Texture("blank_tile.png");
	xtile = new Texture("cross_tile.png");
	redTurn = new Texture("red_turn.png");
	whiteTurn = new Texture("white_turn.png");
	font = new BitmapFont();
	font.setColor(Color.RED);
    }

    public void dispose()
    {
	wood.dispose();
	red.dispose();
	king.dispose();
	white.dispose();
	tile.dispose();
	xtile.dispose();
	redTurn.dispose();
	whiteTurn.dispose();
	font.dispose();
    }

}
