package com.fdorothy.game;

import java.util.ArrayList;

public class AI
{
    public AI(Tafl game, Piece player)
    {
	_game = game;
	_rows = _game.rows();
	_player = player;
    }

    public Piece player()
    {
	return _player;
    }

    public void move()
    {
	findAllMoves();
	Move m = findBestMove();
	System.out.println("ai chose move = " + m.toString());
	_game.move(m);
    }

    public Move findBestMove()
    {
	Tafl tmp = new Tafl(_game);
	GameScore gs = new GameScore(tmp);
	GameScore best = new GameScore(tmp);
	Move bestMove = null;
	double bestScore=0.0;
	for (Move m : _moves) {
	    tmp.move(m);
	    if (bestMove == null) {
		bestMove = m;
		gs.calculateScore();
		bestScore=score(gs);
	    } else {
		gs.calculateScore();
		double tmpScore = score(gs);
		if (tmpScore > bestScore) {
		    bestMove = m;
		    best = new GameScore(tmp);
		    bestScore = tmpScore;
		}
	    }
	    tmp.lazyRestore(_game);
	}
	System.out.println(best.toString());
	System.out.println(score(best));
	return bestMove;
    }

    public double score(GameScore gs)
    {
	if (_player == Piece.RED)
	    return redScore(gs);
	else
	    return whiteScore(gs);
    }

    public double redScore(GameScore gs)
    {
	return 0;
    }

    public double whiteScore(GameScore gs)
    {
	double score = 0.0;

	score -= gs.redPieces();
	score += gs.whitePieces();
	score -= gs.whiteThreats();
	score += gs.redThreats() * 0.5;

	//  check if king is about to get into win position, we
	// want to avoid this.
	int kingDistance = gs.kingDistance();
	if (kingDistance == 3)
	    score -= 5.0;
	else if (kingDistance == 2)
	    score -= 15.0;
	else if (kingDistance == 1)
	    score -= 25.0;
	else if (kingDistance >= 999)
	    score += 10.0;
	else
	    score += kingDistance - 3;

	return score;
    }

    public void findAllMoves()
    {
	_moves = new ArrayList <Move>();
	for (int i=0; i<_rows; i++) {
	    for (int j=0; j<_rows; j++) {
		if (_game.pieceOwner(i,j) == _player) {
		    findMoves(i,j,-1,0);
		    findMoves(i,j,1,0);
		    findMoves(i,j,0,-1);
		    findMoves(i,j,0,1);
		}
	    }
	}
    }

    protected void findMoves(int srcX, int srcY, int dirX, int dirY)
    {
	int x=srcX+dirX;
	int y=srcY+dirY;
	
	while (true) {
	    if (x < 0 || x >= _rows || y < 0 || y >= _rows)
		return;
	    if (_game.piece(x,y) != Piece.EMPTY)
		return;
	    Move m = new Move(srcX,srcY,x,y);
	    if (!_game.isValid(m))
		return;
	    _moves.add(m);
	    x+=dirX;
	    y+=dirY;
	}
    }

    protected ArrayList <Move> _moves;
    protected Tafl _game;
    protected Piece _player;
    protected int _rows;
}
