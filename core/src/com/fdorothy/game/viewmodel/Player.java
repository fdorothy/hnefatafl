package com.fdorothy.game;

public class Player
{
    public Player()
    {
    }

    public Player(Piece side, PlayerType type)
    {
	_side = side;
	_ready=false;
	_playerType = type;
	_name = "human";
    }

    public Piece side()
    {
	return _side;
    }

    public void side(Piece side)
    {
	_side = side;
    }

    public Move move()
    {
	return _move;
    }

    public boolean ready()
    {
	return _ready;
    }

    public PlayerType playerType()
    {
	return _playerType;
    }

    public void playerType(PlayerType t)
    {
	_playerType = t;
    }

    public String name()
    {
	return _name;
    }

    public void name(String val)
    {
	_name = val;
    }

    public void playerAI(AI ai)
    {
	_ai = ai;
    }

    public AI playerAI()
    {
	return _ai;
    }

    protected boolean _ready;
    protected Piece _side;
    protected Move _move;
    protected PlayerType _playerType;
    protected String _name;
    protected AI _ai;
}
