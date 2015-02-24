package org.fdorothy.game;

import java.io.*;

import org.junit.Ignore;
import org.junit.Test;
import com.fdorothy.game.Game;
import com.fdorothy.game.Tile;
import com.fdorothy.game.Piece;
import com.fdorothy.game.Move;
import com.fdorothy.game.History;
import com.fdorothy.game.AI;

import static org.junit.Assert.*;

public class TaflTest
{
    public String DATA = "test_data/";
    public String RESULTS = "test_data/results/";

    public void writeResult(String filename, String str)
    {
	filename = RESULTS+filename;
	Writer writer = null;
	try {
	    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
	    writer.write(str);
	} catch (Exception e) {
	    assert(false);
	} finally {
	    try{writer.close(); } catch (Exception e) {}
	}
    }

    public String readAll(String filename)
    {
	try {
	    BufferedReader br = new BufferedReader(new FileReader(filename));
	    try {
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
		    sb.append(line);
		    sb.append("\n");
		    line = br.readLine();
		}
		return sb.toString();
	    } catch (IOException e) {
		assert(false);
	    } catch (Exception e) {
		assert(false);
	    } finally {
		br.close();
	    }
	} catch (Exception e) {
	    assert(false);
	}
	return "";
    }

    public void compareOutput(String filename, String str)
    {
	// write the output to the results folder with the
	// same filename for debugging
	writeResult(filename, str);

	// read and compare contents of test data on disk
	// with the data 'str' passed to this function
	String old = readAll(DATA+filename);
	assertEquals(old,str);//.equals(str));
    }

    @Test
    public void testSetup()
    {
	Game t = new Game();
	compareOutput("hnefataflSetup.txt", t.toString());
    }

    @Test
    public void testCapture()
    {
	Game t = new Game();
	
	// white moves 1,5 -> 1,1
	t.move(new Move(1,5,1,1));
	assertEquals(t.piece(1,5),Piece.EMPTY);
	assertEquals(t.piece(1,1),Piece.WHITE);
	assertEquals(t.turn(),Piece.RED);

	// red moves 3,5 -> 3,1
	t.move(new Move(3,5,3,1));
	assertEquals(t.piece(3,5),Piece.EMPTY);
	assertEquals(t.piece(3,1),Piece.RED);
	assertEquals(t.turn(),Piece.WHITE);

	// white captures 3,10 -> 3,2
	t.move(new Move(3,10,3,2));
	assertEquals(t.piece(3,10),Piece.EMPTY);
	assertEquals(t.piece(3,2),Piece.WHITE);
	assertEquals(t.piece(3,1),Piece.EMPTY);
	assertEquals(t.turn(),Piece.RED);
    }

    public History getGame(String filename)
    {
	String str = readAll(filename);
	String[] lines = str.split("\n");
	History history = new History();
	for (String line : lines) {
	    try {
		if (line.charAt(0) == '(')
		    history.addMove(Move.fromString(line));
	    } catch (Exception e) {
	    }
	}
	return history;
    }

    public Game testGame(String filename)
    {
	History history = getGame(DATA+filename);
	Game game = new Game();
	for (Move m : history.moves()) {
	    System.out.println("moving: " + m.toString());
	    game.move(m);
	}
	String str = history.toString();
	str += game.toString();
	compareOutput(filename,str);
	return game;
    }

    @Test
    public void testGames()
    {
	String[] games = readAll(DATA+"games.txt").split("\n");
	for (String game : games) {
	    if (game.trim().length() != 0 && game.charAt(0) != '#') {
		System.out.println("trying game " + game);
		try {
		    testGame(game);
		    System.out.println("game passed");
		} catch (Exception e) {
		    System.out.println("game failed");
		    assert(false);
		}
	    }
	}
    }

    @Test
    public void testAI()
    {
	History history = getGame(DATA+"ai_test1.txt");
	Game game = new Game();
	Game copy = new Game();
	AI red = new AI(Piece.RED);

	// go through each move, see if the AI makes the same
	// choices it did before.
	for (Move m : history.moves()) {
	    System.out.println(m.toString());
	    if (game.turn() == Piece.WHITE) {
		game.move(m);
	    } else {
		copy.lazyRestore(game);
		AI.MoveNode aiMove = red.move(copy);
		if (!m.equals(aiMove.move))
		    System.out.println(aiMove.move.toString() + "*");
		game.move(m);
	    }
	}
    }
}
