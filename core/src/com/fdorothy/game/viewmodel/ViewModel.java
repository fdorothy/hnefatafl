package com.fdorothy.game;

public class ViewModel
{
    public ViewModel()
    {
	game = new Game();
	history = new History();
	players = new Player[2];
	players[0] = new Player(Piece.RED, PlayerType.HUMAN);
	players[1] = new Player(Piece.WHITE, PlayerType.AI);
	for (int i=0; i<2; i++)
	    players[i].playerAI(new AI());
    }

    public void setGame(Game g)
    {
	game = g;
    }

    public void setGame(GameTypes gameType)
    {
	game = new Game();
	game.setGameType(gameType);
    }

    public Game getGame()
    {
	return game;
    }

    public void setHistory(History h)
    {
	history = h;
    }

    public History getHistory()
    {
	return history;
    }

    public Player[] getPlayers()
    {
	return players;
    }

    public Player getPlayer(int i)
    {
	return players[i];
    }

    public Player getRedPlayer()
    {
	return players[0];
    }

    public Player getWhitePlayer()
    {
	return players[1];
    }

    public void setPlayers(Player[] p)
    {
	players = p;
	updateCurrentPlayer();
    }

    public void setPlayer(int i, Player p)
    {
	players[i] = p;
    }

    public Player getCurrentPlayer()
    {
	return currentPlayer;
    }

    public void newGame()
    {
	history = new History();
	game = new Game();
	game.reset();
	updateCurrentPlayer();
    }

    public int rows()
    {
	return game.rows();
    }

    public String getLog()
    {
	if (history != null && game != null)
	    return history.toString() + "\n" + game.toString();
	return "";
    }

    public Piece piece(int row, int col)
    {
	return game.piece(row,col);
    }

    public Tile tile(int row, int col)
    {
	return game.tile(row,col);
    }

    public Piece turn()
    {
	return game.turn();
    }

    public boolean humanMove(Move m)
    {
	if (game.isValid(m)) {
	    game.move(m);
	    history.addMove(m);
	    updateCurrentPlayer();
	    return true;
	}
	return false;
    }

    public Move aiMove()
    {
	AI.MoveNode node = currentPlayer.playerAI().move(game);
	history.addMove(node.move);
	updateCurrentPlayer();
	return node.move;
    }

    protected void updateCurrentPlayer()
    {
	if (players[0].side() == turn())
	    currentPlayer = players[0];
	else
	    currentPlayer = players[1];
    }

    public Piece winner()
    {
	return game.winner();
    }

    private Game game;
    private History history;
    private Player[] players;
    private Player currentPlayer;
}
