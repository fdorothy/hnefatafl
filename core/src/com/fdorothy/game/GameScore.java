package com.fdorothy.game;

public class GameScore
{
    public GameScore(Tafl game)
    {
	_game = game;
	calculateScore();
    }
    
    public void calculateScore()
    {
	DistanceGraph kingGraph = new DistanceGraph(_game);
	int idx = findKing();
	int rows = _game.rows();
	if (idx > 0) {
	    int x = idx % rows;
	    int y = idx / rows;
	    kingGraph.seed(x,y);
	    _kingAlive=true;
	    _kingDistance = DistanceGraph.inf;
	    _kingDistance = kingGraph.D(0,0);
	    int d2 = kingGraph.D(rows-1,0);
	    int d3 = kingGraph.D(rows-1,rows-1);
	    int d4 = kingGraph.D(0,rows-1);
	    if (_kingDistance > d2)
		_kingDistance = d2;
	    if (_kingDistance > d3)
		_kingDistance = d3;
	    if (_kingDistance > d4)
		_kingDistance = d4;
	} else {
	    _kingDistance = DistanceGraph.inf;
	    _kingAlive=false;
	}
	//  calculate threats (imminent attacks)
	_whiteThreats = threats(Piece.WHITE);
	_redThreats = threats(Piece.RED);

	//  calculate the number of pieces on the board
	Piece p;
	_whitePieces=0;
	_redPieces=0;
	for (int i=0; i<rows; i++) {
	    for (int j=0; j<rows; j++) {
		p = _game.piece(i,j);
		if (p == Piece.WHITE)
		    _whitePieces++;
		else if (p == Piece.RED)
		    _redPieces++;
	    }
	}
    }

    public int threats(Piece player)
    {
	DistanceGraph graph = new DistanceGraph(_game);
	graph.seedSide(player == Piece.WHITE ? Piece.RED : Piece.WHITE);

	//  determine how many possible captures there will be
	// in 1 turn with this arrangement.
	int rows = _game.rows();
	int threats=0;
	for (int i=0; i<rows; i++)
	    for (int j=0; j<rows; j++)
		if (_game.pieceOwner(i,j) == player && threatened(i,j,graph))
		    threats++;
	return threats;
    }

    public boolean threatened(int x, int y, DistanceGraph graph)
    {
	Piece piece = _game.piece(x,y);
	Piece owner = _game.pieceOwner(x,y);
	boolean isKing = (piece == Piece.KING);
	Piece opponent = (owner == Piece.RED ? Piece.WHITE : Piece.RED);
	if (_game.owner(x-1,y,isKing) == opponent && graph.D(x+1,y) == 1) return true;
	if (_game.owner(x,y-1,isKing) == opponent && graph.D(x,y+1) == 1) return true;
	if (_game.owner(x+1,y,isKing) == opponent && graph.D(x-1,y) == 1) return true;
	if (_game.owner(x,y+1,isKing) == opponent && graph.D(x,y-1) == 1) return true;
	return false;
    }

    protected int findKing()
    {
	int rows=_game.rows();
	for (int i=0; i<rows; i++) {
	    for (int j=0; j<rows; j++) {
		if (_game.piece(i,j) == Piece.KING)
		    return i+j*rows;
	    }
	}
	return -1;
    }

    public int whiteThreats()
    {
	return _whiteThreats;
    }

    public int redThreats()
    {
	return _redThreats;
    }

    public int kingDistance()
    {
	return _kingDistance;
    }

    public int whitePieces()
    {
	return _whitePieces;
    }

    public int redPieces()
    {
	return _redPieces;
    }

    public boolean kingAlive()
    {
	return _kingAlive;
    }

    public String toString()
    {
	StringBuilder sb = new StringBuilder();
	String n = System.lineSeparator();
	sb.append("king distance = " + _kingDistance + n);
	sb.append("king alive = " + _kingAlive + n);
	sb.append("whiteThreats = " + _whiteThreats + n);
	sb.append("redThreats = " + _redThreats + n);
	sb.append("whitePieces = " + _whitePieces + n);
	sb.append("redPieces = " + _redPieces + n);
	return sb.toString();
    }

    protected Tafl _game;
    protected int _whiteThreats;
    protected int _redThreats;
    protected int _kingDistance;
    protected int _whitePieces;
    protected int _redPieces;
    protected boolean _kingAlive;
}
