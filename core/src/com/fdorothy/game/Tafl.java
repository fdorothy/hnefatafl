package com.fdorothy.game;

public class Tafl
{
    int width;
    Piece pieces[];
    Tile tiles[];

    public Tafl() throws Exception
    {
	reset();
    }

    public void reset() throws Exception
    {
	set_hnefatafl();
    }

    private void set_hnefatafl() throws Exception
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
    }

    private void piece(int col, int row, Piece piece) throws Exception
    {
	if (col < 0 || col > width || row < 0 || row > width)
	    throw new Exception("invalid request for setting tile contents: out of bounds");
	pieces[col+row*width] = piece;
    }

    public Piece piece(int col, int row)
    {
	if (col < 0 || col > width || row < 0 || row > width)
	    return Piece.EMPTY;
	return pieces[col+row*width];
    }

    private void tile(int col, int row, Tile tile) throws Exception
    {
	if (col < 0 || col > width || row < 0 || row > width)
	    throw new Exception("invalid request for setting tile: out of bounds");
	tiles[col+row*width] = tile;
    }

    public Tile tile(int col, int row)
    {
	if (col < 0 || col > width || row < 0 || row > width)
	    return Tile.OUTER;
	return tiles[col+row*width];
    }


    public int width()
    {
	return width;
    }
}
