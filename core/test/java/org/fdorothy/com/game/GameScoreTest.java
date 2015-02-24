import com.fdorothy.game.DistanceGraph;
import com.fdorothy.game.Game;
import com.fdorothy.game.Piece;
import com.fdorothy.game.Info;
import com.fdorothy.game.Move;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameScoreTest
{
    @Test
    public void testGameScore()
    {
	Game game = new Game();
	Info score = new Info(game);
	System.out.println(score.toString());
    }

    @Test
    public void testThreats()
    {
	Game game = new Game();
	game.move(new Move(10,4,7,4));
	Info score = new Info(game);
	DistanceGraph graph = new DistanceGraph(game);
	graph.seedSide(Piece.WHITE);
	System.out.println(graph.toString());
	graph.seedSide(Piece.RED);
	System.out.println(graph.toString());
	System.out.println(score.toString());
    }
}

