package com.fdorothy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.fdorothy.game.ViewModel;

public class GdxView extends ApplicationAdapter {

    // GDX objects for rendering
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera cam;

    // GDX objects for images, sound, etc. for the game
    private Resources res;

    //  stores the GDX piece representations, for picking and rendering
    private Array <GdxPiece> pieces;

    //  selection and interaction objects
    private GdxPiece selection;
    private Vector2 dragStart;
    private Vector2 dragEnd;
    private Vector2 dragOffset;
    private Vector3 cursor;

    private Rectangle bounds;
    private Rectangle whiteTitle;
    private Rectangle redTitle;
    private Move move;
    private Rectangle screen;
    private double spacing;
    private int rows;
    
    private ViewModel viewModel;
    //private SettingsHandler handler;

    @Override
    public void create () {
	batch = new SpriteBatch();
	shapeRenderer = new ShapeRenderer();
	res = new Resources();
	cursor = new Vector3();
	dragStart = new Vector2();
	dragEnd = new Vector2();
	dragOffset = new Vector2();
	move = new Move();
	screen = new Rectangle(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	cam = new OrthographicCamera();
	cam.setToOrtho(false, screen.width, screen.height);
	int w = (int)Math.min(screen.width, screen.height);
	bounds = new Rectangle((screen.width - w)/2,
			       (screen.height - w)/2,
			       w,w);
	redTitle = new Rectangle(screen.width/2 - res.redTurn.getWidth()/2,
				 screen.height-res.redTurn.getHeight(),
				 res.redTurn.getWidth(),
				 res.redTurn.getHeight());
	whiteTitle = new Rectangle(screen.width/2 - res.whiteTurn.getWidth()/2,
				   screen.height-res.whiteTurn.getHeight(),
				   res.whiteTurn.getWidth(),
				   res.whiteTurn.getHeight());
	reset();
	viewModel = new ViewModel();
	loadPreferences();
    }

    /// sets the preferences based on the Gdx Preferences object
    public void loadPreferences()
    {
	Preferences prefs = Gdx.app.getPreferences("TaflPreferences");
	String gametypeStr = prefs.getString("gametype", "HNEFATAFL");
	if (gametypeStr.equals("HNEFATAFL"))
	    viewModel.setGame(GameTypes.HNEFATAFL);

	// which side (white/red) are we?
	String humanSideStr = prefs.getString("humanSide", "RED");
	Piece humanSide = Piece.RED;
	Piece opponentSide = Piece.WHITE;
	if (humanSideStr.equals("WHITE")) {
	    humanSide = Piece.WHITE;
	    opponentSide = Piece.RED;
	}

	// who is our opponent (AI/Human/Remote)?
	String opponentTypeStr = prefs.getString("opponentType", "AI");
	PlayerType opponentType = PlayerType.AI;
	if (opponentTypeStr.equals("HUMAN")) {
	    opponentType = PlayerType.HUMAN;
	} else if (opponentTypeStr.equals("REMOTE")) {
	    opponentType = PlayerType.REMOTE;
	}

	// set the players based on the preferences we just loaded
	Player[] players = new Player[2];

	// player 1
	players[0] = new Player();
	players[0].side(Piece.RED);
	if (humanSide == Piece.RED)
	    players[0].playerType(PlayerType.HUMAN);
	else
	    players[0].playerType(opponentType);

	// player 2
	players[1] = new Player();
	players[1].side(Piece.WHITE);
	if (humanSide == Piece.WHITE)
	    players[1].playerType(PlayerType.HUMAN);
	else
	    players[1].playerType(opponentType);

	// fill in AI objects if we are battling an AI
	if (opponentType == PlayerType.AI) {
	    if (opponentSide == Piece.RED)
		players[0].playerAI(new AI(Piece.RED));
	    else
		players[1].playerAI(new AI(Piece.WHITE));
	}

	viewModel.setPlayers(players);
    }

    void reset()
    {
	if (viewModel != null) {
	    if (Gdx.app.getType() == ApplicationType.Desktop) {
		String s = viewModel.getLog();
		FileHandle file = Gdx.files.local("tafl_log.txt");
		file.writeString(s,false);
	    }
	} else
	    viewModel = new ViewModel();
	loadPreferences();
	fillPieces();
    }

    //  fills in pieces from the game into the 'pieces' array
    void fillPieces()
    {
	pieces = new Array <GdxPiece>();
	rows = viewModel.rows();
	spacing = (double)bounds.width / rows;
	for (int i=0; i<rows; i++) {
	    for (int j=0; j<rows; j++) {
		Piece p = viewModel.piece(i,j);
		if (p != Piece.EMPTY) {
		    Texture tex;
		    if (p == Piece.KING)
			tex = res.king;
		    else if (p == Piece.RED)
			tex = res.red;
		    else if (p == Piece.WHITE)
			tex = res.white;
		    else
			tex = res.white;
		    float x = (int)(bounds.x + spacing*(i+0.5f) - tex.getWidth()/2.0f);
		    float y = (int)(bounds.y + spacing*(j+0.5f) - tex.getHeight()/2.0f);
		    Rectangle r = new Rectangle(x, y, tex.getWidth(), tex.getHeight());
		    pieces.add(new GdxPiece(p, r, i, j));
		}
	    }
	}
    }

    public void draw(Texture tex, Rectangle bounds, float u1, float v1, float u2, float v2)
    {
	batch.draw(tex,bounds.x,bounds.y,bounds.width,bounds.height,u1,v1,u2,v2);
    }

    public void draw(Texture tex, Rectangle bounds)
    {
	batch.draw(tex,bounds.x,bounds.y,bounds.width,bounds.height);
    }

    public void renderUI()
    {
	// draw the red/white turn indicators
	batch.begin();
	if (viewModel.turn() == Piece.RED)
	    draw(res.redTurn, redTitle);
	else
	    draw(res.whiteTurn, whiteTitle);
	batch.end();
    }

    public void renderBoard()
    {
	// draw the board
	batch.begin();
	if (Gdx.app.getType() != ApplicationType.Desktop) {
	    draw(res.wood, bounds);
	} else {
	    draw(res.wood, bounds, 0, 0, 11, 11);
	}
	batch.end();

	renderGridLines();
    }

    public void renderGridLines()
    {
	// draw lines on the board designating tiles
	shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
	shapeRenderer.setColor(0.2f,0.2f,0.2f,1.0f);
	float spacing = bounds.width / rows;
	for (int i=0; i<rows; i++) {
	    shapeRenderer.line(i*spacing+bounds.x,bounds.y,i*spacing+bounds.x,bounds.y+bounds.height);
	    shapeRenderer.line(bounds.x,i*spacing+bounds.y,bounds.x+bounds.width,i*spacing+bounds.y);
	}

	//  draw the cross over corner and center pieces
	for (int i=0; i<rows; i++) {
	    for (int j=0; j<rows; j++) {
		Tile t = viewModel.tile(i,j);
		if (t == Tile.CORNER || t == Tile.CENTER) {
		    shapeRenderer.line(i*spacing+bounds.x, j*spacing+bounds.y, (i+1)*spacing+bounds.x, (j+1)*spacing+bounds.y);
		    shapeRenderer.line(i*spacing+bounds.x, (j+1)*spacing+bounds.y, (i+1)*spacing+bounds.x, j*spacing+bounds.y);
		}
	    }
	}

	//  draw the line from the selection start to the selected piece
	if (selection != null) {
	    shapeRenderer.setColor(0.0f, 0.0f, 1.0f, 1.0f);
	    Rectangle b = selection.getBounds();
	    shapeRenderer.line(cursor.x+dragOffset.x, cursor.y+dragOffset.y, dragStart.x, dragStart.y);
	}
	shapeRenderer.end();
    }

    public void renderPieces()
    {
	// draw pieces
	batch.begin();
	for (GdxPiece piece: pieces) {
	    Rectangle b = piece.getBounds();
	    Piece p = piece.getPiece();
	    Texture tex;
	    switch (p) {
	    case WHITE: tex=res.white; break;
	    case RED: tex=res.red; break;
	    case KING: tex=res.king; break;
	    default: tex=res.white; break;
	    }
	    draw(tex,b);
	}
	batch.end();
    }

    @Override
    public void render () {
	Gdx.gl.glClearColor(0, 0, 0, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	cam.update();
	batch.setProjectionMatrix(cam.combined);
	shapeRenderer.setProjectionMatrix(cam.combined);
	renderUI();
	renderBoard();
	renderPieces();
	checkInput();
    }

    public void checkInput()
    {
	Player p = viewModel.getCurrentPlayer();
	if (p.playerType() == PlayerType.AI) {
	    viewModel.aiMove();
	    fillPieces();
	}
	else if (p.playerType() == PlayerType.HUMAN) {
	    processLocalMove();
	}
	if (viewModel.winner() != Piece.EMPTY)
	    reset();
    }

    public void processLocalMove()
    {
	// check for user input
	if (Gdx.input.isTouched()) {

	    // get cursor x,y
	    cursor.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	    cam.unproject(cursor);

	    // if we just touched the board then check for hits
	    if (Gdx.input.justTouched()) {
		int s = (int)spacing;
		Rectangle area = new Rectangle(cursor.x-s/2, cursor.y-s/2, s, s);
		// try to find the piece we are touching
		for (GdxPiece piece: pieces) {
		    if (piece.owner() == viewModel.turn() && piece.bounds.overlaps(area)) {
			selection = piece;
			selection.bounds.getCenter(dragStart);
			if (Gdx.app.getType() == ApplicationType.Android) {
			    dragOffset.x = dragStart.x - cursor.x;
			    dragOffset.y = dragStart.y - cursor.y;
			}
		    }
		}
	    }
	    if (selection != null) {
		selection.bounds.x = (int)(cursor.x-selection.bounds.width/2+dragOffset.x);
		selection.bounds.y = (int)(cursor.y-selection.bounds.height/2+dragOffset.y);
	    }
	} else {
	    if (selection != null) {
		//  figure out the destination x,y coordinates
		cursor.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(cursor);
		move.srcX(selection.x);
		move.srcY(selection.y);
		move.dstX((int)((cursor.x-bounds.x+dragOffset.x)/spacing));
		move.dstY((int)((cursor.y-bounds.y+dragOffset.y)/spacing));
		viewModel.humanMove(move);
		move = new Move();
		fillPieces();
	    }
	    selection=null;
	}
    }

    @Override
    public void dispose()
    {
	batch.dispose();
	shapeRenderer.dispose();
	res.dispose();
	res=null;
    }

}