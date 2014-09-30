package pacman.entries.ghosts;

import java.util.EnumMap;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class MyGhosts extends Controller<EnumMap<GHOST,MOVE>>
{
    // actual pacman size is 224 x 288 with 8 x 8 tiles
    // this game is 114 x 130 with 4 x 4 tiles
    private int TILE_SIZE = 4;

	private EnumMap<GHOST, MOVE> myMoves=new EnumMap<GHOST, MOVE>(GHOST.class);
	
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue)
	{
		myMoves.clear();

        // iterate over the ghosts
        for (GHOST ghostType : GHOST.values()) {
            // if the ghost requires an action
            if (game.doesGhostRequireAction(ghostType)) {
                int moveIndex = game.getGhostCurrentNodeIndex(ghostType);

                // check for frightened mode

                // check for scatter mode
                moveIndex = Scatter(game, ghostType);
                /*

                // Blinky chase mode
                if (ghostType == GHOST.BLINKY) {
                    moveIndex = BlinkyChase(game, ghostType);
                }

                // Pinky chase mode
                else if (ghostType == GHOST.PINKY) {
                    moveIndex = PinkyChase(game, ghostType);
                }

                // Inky chase mode
                else if (ghostType == GHOST.INKY) {
                    moveIndex = InkyChase(game, ghostType);
                }

                // Sue chase mode
                else if (ghostType == GHOST.SUE) {
                    moveIndex = SueChase(game, ghostType);
                }*/

                // calculate and add move using the Euclidean Distance Metric
                myMoves.put(ghostType, game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghostType),
                        moveIndex, game.getGhostLastMoveMade(ghostType), Constants.DM.EUCLID));
            }
        }
		
		return myMoves;
	}

    // Blinky chase mode - set target to pacman
    private int BlinkyChase(Game game, GHOST ghostType){
        return game.getPacmanCurrentNodeIndex();
    }

    // Pinky chase mode - set target to 4 moves ahead of pacman
    private int PinkyChase(Game game, GHOST ghostType) {
        int moveIndex = game.getPacmanCurrentNodeIndex();
        for (int i = 0; i < (TILE_SIZE * 4); i++) {
            int tempIndex = game.getNeighbour(moveIndex, game.getPacmanLastMoveMade());
            if (tempIndex <= 0) break;
            moveIndex = tempIndex;
        }
        return moveIndex;
    }

    // Inky chase mode -
    private int InkyChase(Game game, GHOST ghostType) {
        int moveIndex = game.getPacmanCurrentNodeIndex();
        return moveIndex;
    }

    // Sue chase mode -
    private int SueChase(Game game, GHOST ghostType) {
        int moveIndex = game.getPacmanCurrentNodeIndex();
        return moveIndex;
    }

    // Scatter mode - return to your corner
    private int Scatter(Game game, GHOST ghost){
        // Blinky owns the top right corner = 3
        if (ghost == GHOST.BLINKY) return game.getPowerPillIndices()[3];

        // Pinky owns the top left corner = 2
        else if (ghost == GHOST.PINKY) return game.getPowerPillIndices()[2];

        // Inky owns the bottom right corner = 0
        else if (ghost == GHOST.INKY) return game.getPowerPillIndices()[0];

        // Sue owns the bottom left corner = 1
        else return game.getPowerPillIndices()[1];
    }

    // Frightened mode - randomly target a neighboring node
    private int Frighten(Game game, GHOST ghostType){
        // get array of neighboring nodes
        int[] nodes = game.getNeighbouringNodes(game.getGhostCurrentNodeIndex(ghostType));
        // return one randomly
        return nodes[(int) Math.floor(Math.random() * nodes.length)];
    }
}