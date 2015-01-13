import com.fdorothy.game.DistanceGraph;
import com.fdorothy.game.Tafl;
import com.fdorothy.game.Piece;
import com.fdorothy.game.GameScore;
import com.fdorothy.game.Move;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameScoreTest
{
    @Test
    public void testGameScore()
    {
	Tafl game = new Tafl();
	GameScore score = new GameScore(game);
	System.out.println(score.toString());
    }

    @Test
    public void testThreats()
    {
	Tafl game = new Tafl();
	game.move(new Move(10,4,7,4));
	GameScore score = new GameScore(game);
	DistanceGraph graph = new DistanceGraph(game);
	graph.seedSide(Piece.WHITE);
	System.out.println(graph.toString());
	graph.seedSide(Piece.RED);
	System.out.println(graph.toString());
	System.out.println(score.toString());
    }
}

