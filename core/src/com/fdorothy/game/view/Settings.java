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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class Settings
{
    protected Resources res;
    protected Stage stage;
    protected ShapeRenderer shapeRenderer;
    protected Skin skin;
    protected ViewModel viewModel;

    // ui elements
    protected Table table;

    // labels
    protected Label settingsLabel;
    protected Label redLabel;
    protected Label whiteLabel;
    protected Label gameLabel;

    // button group for red's human/ai
    protected ButtonGroup redTypeGroup;
    protected CheckBox redHumanButton;
    protected CheckBox redAIButton;

    // button group for white's human/ai
    protected ButtonGroup whiteTypeGroup;
    protected CheckBox whiteHumanButton;
    protected CheckBox whiteAIButton;

    // list-box for the game type
    protected SelectBox gameTypeBox;

    // button to finally start the game!
    protected ImageButton startButton;

    protected boolean start;

    public class GameTypeViewModel
    {
	GameTypeViewModel(String name, GameTypes t)
	{
	    this.name = name;
	    this.gameType = t;
	}
	public String name;
	public GameTypes gameType;
	public String toString()
	{
	    return name;
	}
    }

    Array <GameTypeViewModel> gameTypes;
    
    public Settings(Resources r, ViewModel vm)
    {
	viewModel = vm;
	res = r;
	stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	Gdx.input.setInputProcessor(stage);
	shapeRenderer = new ShapeRenderer();
	start=false;
	
	// set up the UI skin
	skin = new Skin(Gdx.files.internal("uiskin.json"));
	setupUI();
    }

    protected void resize(int width, int height)
    {
	stage.getViewport().update(width,height,true);
    }

    protected void setupUI()
    {
	table = new Table(skin);
	table.setFillParent(true);
	stage.addActor(table);

	//  create the UI elements
	settingsLabel = new Label("settings", skin);
	redLabel = new Label("white", skin);
	whiteLabel = new Label("black", skin);
	gameLabel = new Label("game type", skin);
	redHumanButton = new CheckBox("human", skin);
	whiteHumanButton = new CheckBox("human", skin);
	redAIButton = new CheckBox("computer", skin);
	whiteAIButton = new CheckBox("computer", skin);
	gameTypeBox = new SelectBox(skin);
	gameTypes = new Array<GameTypeViewModel>();
	gameTypes.add(new GameTypeViewModel("hnefatafl",GameTypes.HNEFATAFL));
	gameTypes.add(new GameTypeViewModel("brandubh",GameTypes.BRANDUBH));
	gameTypes.add(new GameTypeViewModel("ard ri",GameTypes.ARD_RI));
	gameTypes.add(new GameTypeViewModel("tablut",GameTypes.TABLUT));
	gameTypeBox.setItems(gameTypes);

	startButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("start.png")))),
				      new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("start_down.png")))));

	table.add(settingsLabel).colspan(3).center().pad(10);
	table.row();
	table.add(redLabel).left().pad(10);
	table.add(redHumanButton).pad(10);
	table.add(redAIButton).pad(10);
	table.row();
	table.add(whiteLabel).left().pad(10);
	table.add(whiteHumanButton).pad(10);
	table.add(whiteAIButton).pad(10);
	table.row();
	table.add(gameLabel).left().pad(10);
	table.add(gameTypeBox).colspan(2).pad(10).right();
	table.row();
	table.add(startButton).width(200).height(50).colspan(3).center().bottom().pad(15).space(100);

	redTypeGroup = new ButtonGroup(redHumanButton, redAIButton);
	redTypeGroup.setMinCheckCount(1);
	redTypeGroup.setMaxCheckCount(1);
	redTypeGroup.setChecked("human");
	whiteTypeGroup = new ButtonGroup(whiteHumanButton, whiteAIButton);
	whiteTypeGroup.setMinCheckCount(1);
	whiteTypeGroup.setMaxCheckCount(1);
	whiteTypeGroup.setChecked("computer");

	startButton.addListener(new ClickListener()
	    {
		public void clicked(InputEvent event, float x, float y)
		{
		    start = true;
		}
	    });

	loadPreferences();
	updateUI();
    }

    /// sets the preferences based on the Gdx Preferences object
    public void loadPreferences()
    {
	Preferences prefs = Gdx.app.getPreferences("TaflPreferences");
	String gametypeStr = prefs.getString("gameType", "HNEFATAFL");
	try {
	    viewModel.setGame(GameTypes.valueOf(gametypeStr));
	} catch (Exception e) {
	}

	// which side is red
	String redStr = prefs.getString("redType", "HUMAN");
	Player p = viewModel.getRedPlayer();
	try {
	    p.playerType(PlayerType.valueOf(redStr));
	} catch (Exception e) {
	}

	// which side is white
	String whiteStr = prefs.getString("whiteType", "AI");
	p = viewModel.getWhitePlayer();
	try {
	    p.playerType(PlayerType.valueOf(whiteStr));
	} catch (Exception e) {
	}
    }

    public void updateUI()
    {
	Player redPlayer = viewModel.getRedPlayer();
	switch (redPlayer.playerType()) {
	default:
	case HUMAN:
	    redTypeGroup.setChecked("human");
	    break;
	case AI:
	    redTypeGroup.setChecked("computer");
	    break;
	}

	Player whitePlayer = viewModel.getWhitePlayer();
	switch (whitePlayer.playerType()) {
	default:
	case HUMAN:
	    whiteTypeGroup.setChecked("human");
	    break;
	case AI:
	    whiteTypeGroup.setChecked("computer");
	    break;
	}

	for (GameTypeViewModel t : gameTypes) {
	    if (t.gameType == viewModel.getGame().getGameType())
		gameTypeBox.setSelected(t);
	}

    }

    public void updateVM()
    {
	Preferences prefs = Gdx.app.getPreferences("TaflPreferences");
	Player p = viewModel.getRedPlayer();
	if (redHumanButton.isChecked()) {
	    p.playerType(PlayerType.HUMAN);
	} else
	    p.playerType(PlayerType.AI);
	prefs.putString("redType", p.playerType().toString());

	p = viewModel.getWhitePlayer();
	if (whiteHumanButton.isChecked())
	    p.playerType(PlayerType.HUMAN);
	else
	    p.playerType(PlayerType.AI);
	prefs.putString("whiteType", p.playerType().toString());
	
	GameTypeViewModel gt = (GameTypeViewModel)(gameTypeBox.getSelected());
	viewModel.getGame().setGameType(gt.gameType);
	viewModel.updateCurrentPlayer();
	prefs.putString("gameType", gt.gameType.toString());

	prefs.flush();
    }

    public boolean startGame()
    {
	updateVM();
	return start;
    }

    public void render()
    {
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	stage.act(Gdx.graphics.getDeltaTime());
	stage.draw();
    }
}
