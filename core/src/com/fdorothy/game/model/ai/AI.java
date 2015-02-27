package com.fdorothy.game;

import java.util.ArrayList;

public class AI
{

    public class MoveNode
    {
	public MoveNode(Move m, double s) {move = m; score = s;}
	public Move move;
	public double score;
    }

    public class GameStack
    {
	GameStack(Game game, int depth)
	{
	    _depth = depth;
	    _cur = 0;
	    _stack = new Game[depth];
	    _info = new Info[depth];
	    _moves = new ArrayList<ArrayList <MoveNode>>();
	    for (int i=0; i<depth; i++) {
		_stack[i] = new Game(game);
		_info[i] = new Info(_stack[i]);
		_moves.add(new ArrayList<MoveNode>());
	    }
	}

	public void push()
	{
	    _cur++;
	    _stack[_cur].lazyRestore(_stack[_cur-1]);
	    _moves.get(_cur).clear();
	}

	public void pop()
	{
	    _cur--;
	}

	public Info info()
	{
	    return _info[_cur];
	}

	public ArrayList <MoveNode> moves()
	{
	    return _moves.get(_cur);
	}

	public Game peek()
	{
	    return _stack[_cur];
	}

	public void reset(Game game)
	{
	    for (int i=0; i<_depth; i++) {
		_stack[i].lazyRestore(game);
		_moves.get(i).clear();
	    }
	    _cur=0;
	}

	int _cur;
	int _depth;
	Game[] _stack;
	Info[] _info;
	ArrayList <ArrayList <MoveNode>> _moves;
    }


    public AI()
    {
    }

    public Piece player()
    {
	return _player;
    }

    public MoveNode move(Game game)
    {
	_player = game.turn();
	_stack = new GameStack(game,10);
	MoveNode n = think(1,1,false);
	MoveNode n2= think(2,1,true);
	if (n2 == null || n.score > n2.score) {
	    game.move(n.move);
	    return n;
	} else {
	    game.move(n2.move);
	    return n2;
	}
    }

    public MoveNode think(int depth, int opponentDepth, boolean kingOnly)
    {
	_stack.moves().clear();
	if (kingOnly)
	    findKingMoves();
	else
	    findAllMoves();

	//  find the best move without lookahead
	if (depth <= 1)
	    return findBestMove();

	//  find best move based on recusively seeing which way
	// the game will go
	Piece player = _player;
	Piece opp = (_player == Piece.WHITE ? Piece.RED : Piece.WHITE);
	MoveNode best = null;
	ArrayList <MoveNode> moves = _stack.moves();
	for (MoveNode m : moves) {

	    _stack.push();

	    // make our move, return if we won
	    Game game = _stack.peek();
	    game.move(m.move);
	    if (game.winner() != Piece.EMPTY) {
		m.score = score();
		_stack.pop();
		return m;
	    }

	    // figure out what the likely move is from the opponent,
	    // using recursion. If they are going to win then don't
	    // consider going down this path.
	    _player = opp;
	    MoveNode node = think(opponentDepth, 0, false);
	    _player = player;
	    if (node != null) {
		game.move(node.move);
		if (game.winner() != Piece.EMPTY) {
		    if (best == null) {
			m.score = score();
			best = m;
		    }
		} else {

		    // now do some more recursive thinking to determine
		    // how we should respond to the opponent's move
		    node = think(depth-1,opponentDepth,kingOnly);
		    if (node != null && (best == null || node.score > best.score)) {
			m.score = node.score;
			best = m;
		    }
		}
	    }

	    _stack.pop();
	}
	return best;
    }

    public MoveNode findBestMove()
    {
	ArrayList <MoveNode> moves = _stack.moves();
	MoveNode best = null;
	for (MoveNode m : moves) {
	    _stack.push();
	    _stack.peek().move(m.move);
	    m.score = score();
	    if (best == null || m.score > best.score)
		best = m;
	    _stack.pop();
	}
	return best;
    }

    public double score()
    {
	Info info = _stack.info();
	info.calc(_stack.peek());
	if (_player == Piece.RED)
	    return redScore(info);
	else
	    return whiteScore(info);
    }

    public double redScore(Info info)
    {
	double score = 0.0;

	if (_stack.peek().winner() == Piece.RED)
	    return 999.0;

	score += info.redPieces();
	score -= info.whitePieces();
	score += info.whiteThreats() * 0.5;
	score -= info.redThreats();
	score -= info.cornersBlocked() * 2.0;
	if (info.kingInThirdRank())
	    score += 1.0;
	if (info.kingInCorner())
	    score += 6.0;

	//  check if king is about to get into win position, we
	// want to avoid this.
	int kingDistance = info.kingDistance();
	if (kingDistance == 3)
	    score += 2.0;
	else if (kingDistance == 2)
	    score += 3.0;
	else if (kingDistance == 1)
	    score += 20.0;
	else if (kingDistance == 0)
	    score += 100.0;
	else if (kingDistance >= DistanceGraph.inf)
	    score -= 1.0;
	else
	    score -= kingDistance - 3;

	return score;
    }

    public double whiteScore(Info info)
    {
	double score = 0.0;

	if (_stack.peek().winner() == Piece.WHITE)
	    return 999.0;

	score -= info.redPieces();
	score += info.whitePieces();
	score -= info.whiteThreats();
	score += info.redThreats() * 0.5;

	//  check if king is about to get into win position, we
	// want to avoid this.
	int kingDistance = info.kingDistance();
	if (kingDistance == 3)
	    score -= 5.0;
	else if (kingDistance == 2)
	    score -= 15.0;
	else if (kingDistance == 1)
	    score -= 25.0;
	else if (kingDistance == 0)
	    score -= 50.0;
	else if (kingDistance >= 999)
	    score += 10.0;
	else
	    score += kingDistance - 3;

	return score;
    }

    public void findAllMoves()
    {
	Game game = _stack.peek();
	int rows = game.rows();
	for (int i=0; i<rows; i++) {
	    for (int j=0; j<rows; j++) {
		if (game.pieceOwner(i,j) == _player) {
		    findMoves(i,j,-1,0);
		    findMoves(i,j,1,0);
		    findMoves(i,j,0,-1);
		    findMoves(i,j,0,1);
		}
	    }
	}
    }

    public void findKingMoves()
    {
	Game game = _stack.peek();
	int rows = game.rows();
	for (int i=0; i<rows; i++) {
	    for (int j=0; j<rows; j++) {
		if (game.piece(i,j) == Piece.KING) {
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
	Game game = _stack.peek();
	ArrayList <MoveNode> moves = _stack.moves();
	int rows = game.rows();
	int x=srcX+dirX;
	int y=srcY+dirY;
	
	while (true) {
	    if (x < 0 || x >= rows || y < 0 || y >= rows)
		return;
	    if (game.piece(x,y) != Piece.EMPTY)
		return;
	    Move m = new Move(srcX,srcY,x,y);
	    if (!game.isValid(m))
		return;
	    moves.add(new MoveNode(m,0.0));
	    x+=dirX;
	    y+=dirY;
	}
    }

    protected Piece _player;
    protected GameStack _stack;
}
