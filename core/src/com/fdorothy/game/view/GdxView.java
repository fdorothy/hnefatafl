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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import com.fdorothy.game.ViewModel;

public class GdxView extends ApplicationAdapter {

    // GDX objects for rendering
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera cam;

    Skin skin;
    Stage stage;
    Table table;
    TextButton forfeit;

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

    // last-move start/stop;
    private Vector2 lastMoveStart;
    private Vector2 lastMoveStop;

    private Rectangle bounds;
    private Rectangle whiteTitle;
    private Rectangle redTitle;
    private Move move;
    private Rectangle screen;
    private double spacing;
    private int rows;
    
    private ViewModel viewModel;
    private Settings settings;
    private long lastRender;
    private long duration;
    private Rectangle tmpBounds;

    protected class Animation
    {
	GdxPiece piece;
	void act() {}
	float animT;
    }

    protected class DeathAnimation extends Animation
    {
	@Override
	public void act()
	{
	    piece.setAlpha(1.0f - animT);
	}
    }

    protected class MoveAnimation extends Animation
    {
	Vector2 start;
	Vector2 stop;

	@Override
	public void act()
	{
	    float t=animT;
	    piece.bounds.setCenter((int)(start.x*(1.0f-t) + stop.x*t),
				   (int)(start.y*(1.0f-t) + stop.y*t));
	}
    }
    
    Array <Animation> moveAnimations;
    Array <Animation> deathAnimations;
    boolean animating;

    @Override
    public void create () {
	stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

	skin = new Skin(Gdx.files.internal("uiskin.json"));
	table = new Table();
	forfeit = new TextButton("forfeit",skin);
	forfeit.addListener(new ClickListener()
	    {
		public void clicked(InputEvent event, float x, float y)
		{
		    reset();
		}
	    });
	table.setFillParent(true);
	table.add(forfeit).expand().width(100).height(50).bottom();
	stage.addActor(table);
	batch = new SpriteBatch();
	shapeRenderer = new ShapeRenderer();
	res = new Resources();
	cursor = new Vector3();
	dragStart = new Vector2();
	dragEnd = new Vector2();
	dragOffset = new Vector2();
	lastMoveStart = new Vector2();
	lastMoveStop = new Vector2();
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
	moveAnimations = new Array<Animation>();
	deathAnimations = new Array<Animation>();
	animating = false;
	tmpBounds = new Rectangle();
	reset();
    }

    void reset()
    {
	if (viewModel != null) {
	    if (Gdx.app.getType() == ApplicationType.Desktop) {
		String s = viewModel.getLog();
		FileHandle file = Gdx.files.local("tafl_log.txt");
		file.writeString(s,false);
	    }
	}
	viewModel = new ViewModel();
	settings = new Settings(res,viewModel);
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
	// if (Gdx.app.getType() != ApplicationType.Desktop) {
	//     draw(res.wood, bounds);
	// } else {
	//     int rows = viewModel.getGame().rows();
	//     draw(res.wood, bounds, 0, 0, rows, rows);
	// }
	batch.end();

	renderGridLines();
    }

    public void renderGridLines()
    {
	// draw lines on the board designating tiles
	//shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
	shapeRenderer.setColor(0.2f,0.2f,0.2f,1.0f);
	float spacing = bounds.width / rows;
	// for (int i=0; i<rows; i++) {
	//     shapeRenderer.line(i*spacing+bounds.x,bounds.y,i*spacing+bounds.x,bounds.y+bounds.height);
	//     shapeRenderer.line(bounds.x,i*spacing+bounds.y,bounds.x+bounds.width,i*spacing+bounds.y);
	// }

	//  draw the cross over corner and center pieces
	batch.begin();
	for (int i=0; i<rows; i++) {
	    for (int j=0; j<rows; j++) {
		Tile t = viewModel.tile(i,j);
		tmpBounds.x = i*spacing+bounds.x;
		tmpBounds.y = j*spacing+bounds.y;
		tmpBounds.width = spacing;
		tmpBounds.height = spacing;
		draw(res.tile,tmpBounds);
		if (t == Tile.CORNER || t == Tile.CENTER)
		    draw(res.xtile,tmpBounds);
	    }
	}
	batch.end();
	shapeRenderer.end();

	//  draw the line from the selection start to the selected piece
	shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
	if (selection != null) {
	    shapeRenderer.setColor(0.0f, 1.0f, 1.0f, 1.0f);
	    Rectangle b = selection.getBounds();
	    shapeRenderer.line(cursor.x+dragOffset.x, cursor.y+dragOffset.y, dragStart.x, dragStart.y);
	}
	shapeRenderer.end();

	//  draw the last move locations
	shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 0.5f);

    }

    public void renderPieces()
    {
	// draw pieces
	batch.begin();
	for (GdxPiece piece: pieces)
	    renderPiece(piece);
	for (Animation anim: moveAnimations)
	    renderPiece(anim.piece);
	for (Animation anim: deathAnimations)
	    renderPiece(anim.piece);
	batch.end();
    }

    public void renderPiece(GdxPiece piece)
    {
	Rectangle b = piece.getBounds();
	Piece p = piece.getPiece();
	Texture tex;
	switch (p) {
	case WHITE: tex=res.white; break;
	case RED: tex=res.red; break;
	case KING: tex=res.king; break;
	default: tex=res.white; break;
	}
	batch.setColor(1.0f,1.0f,1.0f,piece.alpha);
	draw(tex,b);
	batch.setColor(1.0f,1.0f,1.0f,1.0f);
    }

    @Override
    public void render () {
	if (lastRender != 0)
	    duration = TimeUtils.timeSinceMillis(lastRender);
	lastRender = TimeUtils.millis();
	if (settings != null) {
	    settings.render();
	    if (settings.startGame()) {
		settings = null;
		Gdx.input.setInputProcessor(stage);
		fillPieces();
	    }
	} else {
	    Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    stage.act(Gdx.graphics.getDeltaTime());
	    stage.draw();
	    cam.update();
	    batch.setProjectionMatrix(cam.combined);
	    shapeRenderer.setProjectionMatrix(cam.combined);
	    renderUI();
	    renderBoard();
	    renderPieces();
	    if (!animating)
		checkInput();
	    renderAnimations();
	}
    }

    public void renderAnimations()
    {
	if (moveAnimations.size > 0)
	    renderAnimations(moveAnimations);
	else if (deathAnimations.size > 0)
	    renderAnimations(deathAnimations);
	else if (animating) {
	    animating = false;
	    fillPieces();
	    if (viewModel.winner() != Piece.EMPTY) {
		reset();
	    }
	}
    }

    public void renderAnimations(Array<Animation> animations)
    {
	// render the anim
	for (Animation anim: animations) {
	    anim.act();
	}
	// update the animation time, remove old animations
	float inc = duration / 1000.0f;
	//float inc = .01f;
	for (int i=0; i<animations.size; i++) {
	    Animation anim = animations.get(i);
	    anim.animT += inc;
	    if (anim.animT > 1.0f)
		animations.removeIndex(i--);
	}
    }

    public void toScreen(int col, int row, Vector2 out)
    {
	int rows = viewModel.getGame().rows();
	out.x = (bounds.width / rows) * (0.5f + col) + bounds.x;
	out.y = (bounds.height / rows) * (0.5f + row) + bounds.y;
    }

    public void checkInput()
    {
	Player p = viewModel.getCurrentPlayer();
	if (p.playerType() == PlayerType.AI) {
	    Move m = viewModel.aiMove();
	    toScreen(m.srcX(), m.srcY(), lastMoveStart);
	    toScreen(m.dstX(), m.dstY(), lastMoveStop);
	    MoveAnimation ma = new MoveAnimation();
	    ma.start = lastMoveStart;
	    ma.stop = lastMoveStop;
	    ma.piece = getGdxPiece(m.srcX(), m.srcY());
	    moveAnimations.add(ma);
	    animating = true;

	    // find the dead
	    Array<GdxPiece> tmp = pieces;
	    fillPieces();
	    for (GdxPiece piece:deadPieces(tmp,pieces,ma.piece)) {
		DeathAnimation da = new DeathAnimation();
		da.animT = 0.0f;
		da.piece = piece;
		deathAnimations.add(da);
	    }
	    pieces = tmp;
	}
	else if (p.playerType() == PlayerType.HUMAN) {
	    processLocalMove();
	}
    }

    public Array<GdxPiece> deadPieces(Array <GdxPiece> oldPieces, Array <GdxPiece> newPieces, GdxPiece moved)
    {
	Array<GdxPiece> dead = new Array<GdxPiece>();
	for (GdxPiece oldPiece:oldPieces) {
	    if (!oldPiece.equals(moved)) {
		boolean found = false;
		for (GdxPiece newPiece:newPieces) {
		    if (newPiece.equals(oldPiece)) {
			found=true;
		    }
		}
		if (!found)
		    dead.add(oldPiece);
	    }
	}
	return dead;
    }

    public GdxPiece getGdxPiece(int x, int y)
    {
	for (GdxPiece piece: pieces)
	    if (piece.x == x && piece.y == y)
		return piece;
	return null;
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
		toScreen(move.srcX(), move.srcY(), lastMoveStart);
		toScreen(move.dstX(), move.dstY(), lastMoveStop);

		MoveAnimation ma = new MoveAnimation();
		ma.piece = selection;

		viewModel.humanMove(move);
		move = new Move();

		ma.start = lastMoveStart;
		ma.stop = lastMoveStop;
		moveAnimations.add(ma);
		animating = true;

		// find the dead
		Array<GdxPiece> tmp = pieces;
		fillPieces();
		for (GdxPiece piece:deadPieces(tmp,pieces,ma.piece)) {
		    DeathAnimation da = new DeathAnimation();
		    da.animT = 0.0f;
		    da.piece = piece;
		    deathAnimations.add(da);
		}
		pieces = tmp;
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
