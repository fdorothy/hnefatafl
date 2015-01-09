package com.fdorothy.game;

public class Tafl
{
    protected int width;
    protected Piece pieces[];
    protected Tile tiles[];
    protected Piece turn;
    protected Piece winner;
    protected boolean gameOver;
    protected int redScore;
    protected int whiteScore;

    public Tafl()
    {
	reset();
    }

    public Tafl(Tafl rhs)
    {
	width = rhs.width;
	pieces = new Piece[width*width];
	for (int i=0; i<width*width; i++)
	    pieces[i] = rhs.pieces[i];
	tiles = new Tile[width*width];
	for (int i=0; i<width*width; i++)
	    tiles[i] = rhs.tiles[i];
	turn = rhs.turn;
	winner = rhs.winner;
	gameOver = rhs.gameOver;
    }

    public void reset()
    {
	winner = Piece.EMPTY;
	redScore=0;
	whiteScore=0;
	gameOver=false;
	set_hnefatafl();
    }

    private void set_hnefatafl()
    {
	width = 11;
	pieces = new Piece[width*width];
	tiles = new Tile[width*width];

	//  set up tiles
	for (int i=0; i<width; i++)
	    for (int j=0; j<width; j++)
		if ((i==0 && (j==0 || j==width-1)) ||
		    (i==width-1 && (j==0 || j==width-1)))
		    tile(i,j,Tile.CORNER);
		else if (i == 5 && j == 5)
		    tile(i,j,Tile.CENTER);
		else
		    tile(i,j,Tile.BLANK);

	//  clear the board
	for (int i=0; i<width; i++)
	    for (int j=0; j<width; j++)
		piece(i,j,Piece.EMPTY);

	//  left side of board
	piece( 0, 3, Piece.WHITE);
	piece( 0, 4, Piece.WHITE);
	piece( 0, 5, Piece.WHITE);
	piece( 0, 6, Piece.WHITE);
	piece( 0, 7, Piece.WHITE);
	piece( 1, 5, Piece.WHITE);

	//  right side of board
	piece(10, 3, Piece.WHITE);
	piece(10, 4, Piece.WHITE);
	piece(10, 5, Piece.WHITE);
	piece(10, 6, Piece.WHITE);
	piece(10, 7, Piece.WHITE);
	piece( 9, 5, Piece.WHITE);

	//  bottom of board
	piece( 3, 0, Piece.WHITE);
	piece( 4, 0, Piece.WHITE);
	piece( 5, 0, Piece.WHITE);
	piece( 6, 0, Piece.WHITE);
	piece( 7, 0, Piece.WHITE);
	piece( 5, 1, Piece.WHITE);

	//  top of board
	piece( 3,10, Piece.WHITE);
	piece( 4,10, Piece.WHITE);
	piece( 5,10, Piece.WHITE);
	piece( 6,10, Piece.WHITE);
	piece( 7,10, Piece.WHITE);
	piece( 5, 9, Piece.WHITE);

	//  center of board
	piece( 5, 3, Piece.RED);
	piece( 4, 4, Piece.RED);
	piece( 5, 4, Piece.RED);
	piece( 6, 4, Piece.RED);
	piece( 3, 5, Piece.RED);
	piece( 4, 5, Piece.RED);
	piece( 5, 5, Piece.KING);
	piece( 6, 5, Piece.RED);
	piece( 7, 5, Piece.RED);
	piece( 4, 6, Piece.RED);
	piece( 5, 6, Piece.RED);
	piece( 6, 6, Piece.RED);
	piece( 5, 7, Piece.RED);

	turn = Piece.WHITE;
    }

    private void piece(int col, int row, Piece piece)
    {
	if (!(col < 0 || col >= width || row < 0 || row >= width))
	    pieces[col+row*width] = piece;
    }

    public Piece piece(int col, int row)
    {
	if (col < 0 || col >= width || row < 0 || row >= width)
	    return Piece.EMPTY;
	return pieces[col+row*width];
    }

    private void tile(int col, int row, Tile tile)
    {
	if (!(col < 0 || col >= width || row < 0 || row >= width))
	    tiles[col+row*width] = tile;
    }

    public Tile tile(int col, int row)
    {
	if (col < 0 || col >= width || row < 0 || row >= width)
	    return Tile.OUTER;
	return tiles[col+row*width];
    }

    public Piece turn()
    {
	return turn;
    }

    public boolean isValid(Move move)
    {
	try {
	    int srcx = move.srcX();
	    int srcy = move.srcY();
	    int dstx = move.dstX();
	    int dsty = move.dstY();

	    //  make sure we aren't moving 0 distance
	    if (srcx == dstx && srcy == dsty)
		return false;

	    //  make sure all values are within the range of the board
	    if (!onBoard(srcx,srcy) || !onBoard(dstx,dsty))
		return false;

	    //  make sure there isn't already a piece where we are moving
	    if (piece(dstx,dsty) != Piece.EMPTY)
		return false;

	    //  make sure we are moving a valid piece
	    Piece p = piece(srcx,srcy);
	    if ((p == Piece.WHITE && turn == Piece.RED) ||
		((p == Piece.RED || p == Piece.KING) && turn == Piece.WHITE))
		return false;

	    //  if we aren't moving the king then make sure we aren't
	    // moving to a corner
	    Tile dsttile = tile(dstx, dsty);
	    if (p != Piece.KING && (dsttile == Tile.CORNER || dsttile == Tile.CENTER))
		return false;
		
	    //  check that we are moving in a cardinal direction
	    if (!(srcx == dstx || srcy == dsty))
		return false;

	    //  check along cardinal direction to make sure we aren't jumping
	    // over any pieces
	    if (srcx == dstx) {
		int dir = (dsty-srcy) > 0 ? 1 : -1;
		int y = srcy+dir;
		while (y != dsty) {
		    if (piece(dstx,y) != Piece.EMPTY)
			return false;
		    y += dir;
		}
	    }
	    if (srcy == dsty) {
		int dir = (dstx-srcx) > 0 ? 1 : -1;
		int x = srcx+dir;
		while (x != dstx) {
		    if (piece(x,dsty) != Piece.EMPTY)
			return false;
		    x += dir;
		}
	    }
	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    public boolean onBoard(int x, int y)
    {
	return !(x < 0 || y < 0 || x >= width || y > width);
    }

    public boolean move(Move move)
    {
	if (gameOver)
	    return false;

	if (!isValid(move))
	    return false;

	try {
	    Piece p = piece(move.srcX(), move.srcY());
	    piece(move.srcX(), move.srcY(), Piece.EMPTY);
	    piece(move.dstX(), move.dstY(), p);
	} catch (Exception e) {
	    return false;
	}

	capture(move.dstX(),move.dstY());
	winner = checkForWin();
	if (winner != Piece.EMPTY) {
	    gameOver = true;
	    return true;
	}
	
	if (turn == Piece.WHITE)
	    turn = Piece.RED;
	else
	    turn = Piece.WHITE;

	return true;
    }

    public Piece owner(int x, int y, boolean isKingAsking)
    {
	Piece p = piece(x,y);
	if (p == Piece.KING)
	    p = Piece.RED;
	Tile t = tile(x,y);
	if (t == Tile.CORNER)
	    return turn;
	if (t == Tile.CENTER && isKingAsking)
	    return Piece.RED;
	return p;
    }

    protected boolean capture(int x, int y)
    {
	int oldRedScore = redScore;
	int oldWhiteScore = whiteScore;
	
	// who owns the moved piece and is it a king?
	Piece p = piece(x,y);
	boolean king=false;
	if (p == Piece.KING) {
	    king=true;
	    p=Piece.RED;
	}

	Piece opposing = (p == Piece.WHITE) ? Piece.RED : Piece.WHITE;

	//  check for any surrounding normal pieces
	if (owner(x-1,y,false) == opposing && owner(x-2,y,king) == p)
	    removePiece(x-1,y);
	if (owner(x+1,y,false) == opposing && owner(x+2,y,king) == p)
	    removePiece(x+1,y);
	if (owner(x,y-1,false) == opposing && owner(x,y-2,king) == p)
	    removePiece(x,y-1);
	if (owner(x,y+1,false) == opposing && owner(x,y+2,king) == p)
	    removePiece(x,y+1);

	if (redScore != oldRedScore || whiteScore != oldWhiteScore)
	    return true;
	return false;
    }

    protected void removePiece(int x, int y)
    {
	switch (piece(x,y)) {
	case RED:
	    redScore++;
	    piece(x,y,Piece.EMPTY);
	    break;
	case WHITE:
	    whiteScore++;
	    piece(x,y,Piece.EMPTY);
	    break;
	case KING:
	    // do nothing, king cannot be removed
	    break;
	default:break;
	}
    }

    public Piece winner()
    {
	if (gameOver)
	    return winner;
	return Piece.EMPTY;
    }

    protected Piece checkForWin()
    {
	int white=0;
	int red=0;
	int king=0;
	Piece p;
	for (int i=0; i<width; i++) {
	    for (int j=0; j<width; j++) {
		p = piece(i,j);
		if (p == Piece.KING) {
		    king++;
		    if (tile(i,j) == Tile.CORNER)
			return Piece.RED;
		    if (checkForKingCapture(i,j))
			return Piece.WHITE;
		}
		if (p == Piece.WHITE)
		    white++;
		if (p == Piece.RED)
		    red++;
		if (white != 0 && red != 0 && king != 0)
		    return Piece.EMPTY;
	    }
	}
	if (red == 0)
	    return Piece.WHITE;
	if (white == 0)
	    return Piece.RED;
	return Piece.EMPTY;
    }

    protected boolean checkForKingCapture(int i, int j)
    {
	int sides = 0;
	sides += (owner(i-1,j,false) == Piece.WHITE ? 1 : 0);
	sides += (owner(i+1,j,false) == Piece.WHITE ? 1 : 0);
	sides += (owner(i,j-1,false) == Piece.WHITE ? 1 : 0);
	sides += (owner(i,j+1,false) == Piece.WHITE ? 1 : 0);

	if (i==0 || i == width-1 || j == 0 || j == width-1)
	    if (sides == 3)
		return true;
	if (sides == 4)
	    return true;
	return false;
    }

    public int width()
    {
	return width;
    }
}
