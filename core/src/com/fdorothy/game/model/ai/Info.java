package com.fdorothy.game;

public class Info
{
    public Info()
    {
    }

    public Info(Game game)
    {
	calc(game);
    }
    
    public void calc(Game game)
    {
	DistanceGraph kingGraph = new DistanceGraph(game);
	int idx = findKing(game);
	int rows = game.rows();
	if (idx >= 0) {
	    int x = idx % rows;
	    int y = idx / rows;
	    kingGraph.seed(x,y);
	    _kingAlive=true;
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

	    //  is the king in a strategic location?
	    _kingInCorner = inCorner(rows,x,y);
	    _kingInThirdRank = inThirdRank(rows,x,y);
	} else {
	    _kingDistance = DistanceGraph.inf;
	    _kingAlive=false;
	}
	//  calculate threats (imminent attacks)
	_whiteThreats = threats(game,Piece.WHITE);
	_redThreats = threats(game,Piece.RED);

	//  calculate the number of pieces on the board
	Piece p;
	_whitePieces=0;
	_redPieces=0;
	for (int i=0; i<rows; i++) {
	    for (int j=0; j<rows; j++) {
		p = game.piece(i,j);
		if (p == Piece.WHITE)
		    _whitePieces++;
		else if (p == Piece.RED)
		    _redPieces++;
	    }
	}

	//  seed red to see which corners are blocked
	DistanceGraph redGraph = new DistanceGraph(game);
	redGraph.seedSide(Piece.RED);
	int cornersBlocked = 0;
	if (redGraph.D(0,0) == DistanceGraph.inf) cornersBlocked++;
	if (redGraph.D(rows-1,0) == DistanceGraph.inf) cornersBlocked++;
	if (redGraph.D(rows-1,rows-1) == DistanceGraph.inf) cornersBlocked++;
	if (redGraph.D(0,rows-1) == DistanceGraph.inf) cornersBlocked++;
    }

    public int threats(Game game, Piece player)
    {
	DistanceGraph graph = new DistanceGraph(game);
	graph.seedSide(player == Piece.WHITE ? Piece.RED : Piece.WHITE);

	//  determine how many possible captures there will be
	// in 1 turn with this arrangement.
	int rows = game.rows();
	int threats=0;
	for (int i=0; i<rows; i++)
	    for (int j=0; j<rows; j++)
		if (game.pieceOwner(i,j) == player && threatened(game,i,j,graph))
		    threats++;
	return threats;
    }

    public boolean threatened(Game game, int x, int y, DistanceGraph graph)
    {
	Piece piece = game.piece(x,y);
	Piece owner = game.pieceOwner(x,y);
	boolean isKing = (piece == Piece.KING);
	Piece opponent = (owner == Piece.RED ? Piece.WHITE : Piece.RED);
	if (isKing) {
	    int threats = 0;
	    if (game.owner(x-1,y,true) == opponent) threats++;
	    if (game.owner(x,y-1,true) == opponent) threats++;
	    if (game.owner(x+1,y,true) == opponent) threats++;
	    if (game.owner(x,y+1,true) == opponent) threats++;
	    if (graph.D(x+1,y) == 1 || graph.D(x,y+1) == 1 ||
		graph.D(x-1,y) == 1 || graph.D(x,y-1) == 1)
		threats++;
	    if (threats >= 3) return true;
	} else {
	    if (game.owner(x-1,y,isKing) == opponent && graph.D(x+1,y) == 1) return true;
	    if (game.owner(x,y-1,isKing) == opponent && graph.D(x,y+1) == 1) return true;
	    if (game.owner(x+1,y,isKing) == opponent && graph.D(x-1,y) == 1) return true;
	    if (game.owner(x,y+1,isKing) == opponent && graph.D(x,y-1) == 1) return true;
	}
	return false;
    }

    protected boolean inCorner(int rows, int x, int y)
    {
	if (x+y <= 2) return true;
	if (rows-1-x+y <= 2) return true;
	if (rows-1-x+rows-1-y <= 2) return true;
	if (x+rows-1-y <= 2) return true;
	return false;
    }

    protected boolean inThirdRank(int rows, int x, int y)
    {
	if (x == 2 || x == rows-3 ||
	    y == 2 || y == rows-3)
	    return true;
	return false;
    }

    protected int findKing(Game game)
    {
	int rows=game.rows();
	for (int i=0; i<rows; i++) {
	    for (int j=0; j<rows; j++) {
		if (game.piece(i,j) == Piece.KING)
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

    public boolean kingInThirdRank()
    {
	return _kingInThirdRank;
    }

    public boolean kingInCorner()
    {
	return _kingInCorner;
    }

    public int cornersBlocked()
    {
	return _cornersBlocked;
    }

    public String toString()
    {
	StringBuilder sb = new StringBuilder();
	String n = "\n";
	sb.append("king distance = " + _kingDistance + n);
	sb.append("king alive = " + _kingAlive + n);
	sb.append("whiteThreats = " + _whiteThreats + n);
	sb.append("redThreats = " + _redThreats + n);
	sb.append("whitePieces = " + _whitePieces + n);
	sb.append("redPieces = " + _redPieces + n);
	sb.append("king in third rank = " + _kingInThirdRank + n);
	sb.append("king in corner = " + _kingInCorner + n);
	sb.append("corners blocked = " + _cornersBlocked + n);
	return sb.toString();
    }

    protected int _whiteThreats;
    protected int _redThreats;
    protected int _kingDistance;
    protected int _whitePieces;
    protected int _redPieces;
    protected boolean _kingAlive;
    protected boolean _kingInThirdRank;
    protected boolean _kingInCorner;
    protected int _cornersBlocked;
}
