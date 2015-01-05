package com.fdorothy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class HnefataflGame extends ApplicationAdapter {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    Texture red;
    Texture redKing;
    Texture white;
    Texture blankTile;
    Texture crossTile;
    Tafl game;
    Array <GDXPiece> pieces;
    GDXPiece selection;
    Rectangle bounds;
    Texture wood;
    private OrthographicCamera camera;
    private Vector3 touchPos;
    Vector2 selectionStart;

    @Override
    public void create () {
	batch = new SpriteBatch();
	shapeRenderer = new ShapeRenderer();
	red = new Texture("red.png");
	redKing = new Texture("red_king.png");
	white = new Texture("white.png");
	blankTile = new Texture("blank_tile.png");
	crossTile = new Texture("cross_tile.png");
	wood = new Texture("wood.png");
	wood.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
	touchPos = new Vector3();
	selectionStart = new Vector2();

	camera = new OrthographicCamera();
	camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	try {
	    reset();
	} catch (Exception e) {
	    Gdx.app.log("Hnefatafl", "error when creating game: " + e);
	}
    }

    @Override
    public void render () {
	Gdx.gl.glClearColor(0, 0, 0, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	camera.update();
	batch.setProjectionMatrix(camera.combined);
	batch.begin();


	// draw the board
	batch.draw(wood, bounds.x, bounds.y, bounds.width, bounds.height, 0, 0, 11, 11);

	batch.end();

	// draw lines on the board designating tiles
	shapeRenderer.setProjectionMatrix(camera.combined);
	shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
	shapeRenderer.setColor(0,0,0,1);
	int width = game.width();
	float inc = bounds.width / width;
	for (int i=0; i<width; i++) {
	    shapeRenderer.line(i*inc+bounds.x,bounds.y,i*inc+bounds.x,bounds.y+bounds.height);
	    shapeRenderer.line(bounds.x,i*inc+bounds.y,bounds.x+bounds.width,i*inc+bounds.y);
	}

	//  draw the cross over corner and center pieces
	for (int i=0; i<width; i++) {
	    for (int j=0; j<width; j++) {
		Tile t = game.tile(i,j);
		if (t == Tile.CORNER || t == Tile.CENTER) {
		    shapeRenderer.line(i*inc+bounds.x, j*inc+bounds.y, (i+1)*inc+bounds.x, (j+1)*inc+bounds.y);
		    shapeRenderer.line(i*inc+bounds.x, (j+1)*inc+bounds.y, (i+1)*inc+bounds.x, j*inc+bounds.y);
		}
	    }
	}

	//  draw the line from the selection start to the selected piece
	if (selection != null && selectionStart != null) {
	    shapeRenderer.setColor(0.0f, 0.0f, 1.0f, 1.0f);
	    Rectangle b = selection.getBounds();
	    shapeRenderer.line(selectionStart.x, selectionStart.y, (int)(b.x+b.width/2.0f), (int)(b.y+b.height/2.0f));
	}

	shapeRenderer.end();


	// draw pieces
	batch.begin();
	for (GDXPiece piece: pieces) {
	    Rectangle b = piece.getBounds();
	    Piece p = piece.getPiece();
	    switch (p) {
	    case WHITE:
		batch.draw(white, b.x, b.y, b.width, b.height);
		break;
	    case RED:
		batch.draw(red, b.x, b.y, b.width, b.height);
		break;
	    case KING:
		batch.draw(redKing, b.x, b.y, b.width, b.height);
		break;
	    default:break;
	    }
	}
	batch.end();


	// check for user input
	if (Gdx.input.isTouched()) {
	    touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	    camera.unproject(touchPos);
	    if (Gdx.input.justTouched()) {
		Rectangle area = new Rectangle(touchPos.x-inc/2, touchPos.y-inc/2, inc, inc);
		// try to find the piece we are touching
		for (GDXPiece piece: pieces) {
		    if (piece.bounds.overlaps(area)) {
			selection = piece;
			selection.bounds.getCenter(selectionStart);
		    }
		}
	    }
	    if (selection != null && selectionStart != null) {
		selection.bounds.x = (int)(touchPos.x-selection.bounds.width/2.0f);
		selection.bounds.y = (int)(touchPos.y-selection.bounds.height/2.0f);
	    }
	} else {
	    selection=null;
	}

    }

    void reset() throws Exception
    {
	fillBoard();
	fillPieces();
    }

    void fillBoard() throws Exception
    {
	game = new Tafl();

	//  calculate the bounding box of the board
	final float SCREEN_WIDTH = Gdx.graphics.getWidth();
	final float SCREEN_HEIGHT = Gdx.graphics.getHeight();
	float width = SCREEN_WIDTH > SCREEN_HEIGHT ? SCREEN_HEIGHT : SCREEN_WIDTH;
	bounds = new Rectangle((SCREEN_WIDTH - width)/2.0f,
			       (SCREEN_HEIGHT - width)/2.0f,
			       width,width);
    }

    //  fills in pieces from the Tafl board into the 'pieces' array
    void fillPieces()
    {
	pieces = new Array <GDXPiece>();
	int w = game.width();
	float inc = bounds.width / w;
	for (int i=0; i<w; i++) {
	    for (int j=0; j<w; j++) {
		Piece p = game.piece(i,j);
		if (p != Piece.EMPTY) {
		    Texture tex = getPieceTexture(p);
		    float x = (int)(bounds.x + inc*(i+0.5f) - tex.getWidth()/2.0f);
		    float y = (int)(bounds.y + inc*(j+0.5f) - tex.getHeight()/2.0f);
		    Rectangle r = new Rectangle(x, y, tex.getWidth(), tex.getHeight());
		    pieces.add(new GDXPiece(p, r));
		}
	    }
	}
    }

    Texture getPieceTexture(Piece p)
    {
	switch (p) {
	case WHITE:
	    return white;
	case RED:
	    return red;
	case KING:
	    return redKing;
	default:return null;
	}
    }
}
