import com.fdorothy.game.DistanceGraph;
import com.fdorothy.game.Game;
import com.fdorothy.game.Piece;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class DistanceGraphTest
{
    @Test
    public void testPieceMovement()
    {
	Game game = new Game();
	DistanceGraph path = new DistanceGraph(game);
	path.seed(1,5);
	System.out.println(path.toString());
    }

    @Test
    public void testTotalMovement()
    {
	Game game = new Game();
	DistanceGraph path = new DistanceGraph(game);
	path.seedSide(Piece.WHITE);
	System.out.println("white can move:");
	System.out.println(path.toString());

	path = new DistanceGraph(game);
	path.seedSide(Piece.RED);
	System.out.println("red can move:");
	System.out.println(path.toString());
    }
}

