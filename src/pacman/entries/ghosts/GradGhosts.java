package pacman.entries.ghosts;
import java.util.EnumMap;
import java.util.Timer;
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
public class GradGhosts extends Controller<EnumMap<GHOST,MOVE>>
{
    // actual pacman is 224 x 288 with 8 x 8 tiles
    // this game is 114 x 130 with 4 x 4 tiles
    private int TILE = 4;
    private long totalTime = 0;
    private long lastTime = getSeconds();
    private boolean frightened = false;

	private EnumMap<GHOST, MOVE> myMoves=new EnumMap<GHOST, MOVE>(GHOST.class);
	
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue)
	{
        //System.out.println("frightened: " + frightened);

        // if any ghost is frightened, don't count time
        if (!frightened) {
            totalTime += getSeconds() - lastTime;
        }
        lastTime = getSeconds();

        // reset frightened to false
        frightened = false;

        myMoves.clear();

        // iterate over the ghosts
        for (GHOST ghostType : GHOST.values()) {
            frightened = frightened || game.isGhostEdible(ghostType);
            // if the ghost requires an action
            if (game.doesGhostRequireAction(ghostType)) {
                int moveIndex;
                // check for frightened mode
                
                if (game.isGhostEdible(ghostType)) {
                    moveIndex = Frighten(game, ghostType);
                }
                

                /*
                    check for scatter mode
                    1. Scatter for 7 seconds, then Chase for 20 seconds.
                    2. Scatter for 7 seconds, then Chase for 20 seconds.
                    3. Scatter for 5 seconds, then Chase for 20 seconds.
                    4. Scatter for 5 seconds, then switch to Chase mode permanently.
                 */
                
                else if (totalTime < 8
                        || (totalTime > 28 && totalTime < 36)
                        || (totalTime > 56 && totalTime < 61)
                        || (totalTime > 81 && totalTime < 86)) {
                    moveIndex = Scatter(game, ghostType);
                }
                        

                // default to chase mode
                else {
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
                    else {
                        moveIndex = SueChase(game, ghostType);
                    }
                }

                // calculate and add move using the Euclidean Distance Metric
                myMoves.put(ghostType, game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghostType),
                        moveIndex, game.getGhostLastMoveMade(ghostType), Constants.DM.EUCLID));
            }
        }
		
		return myMoves;
	}

    // Blinky chase mode - set target to pacman
    private int BlinkyChase(Game game, GHOST ghostType){
        int moveIndex = game.getPacmanCurrentNodeIndex();
        
        for (int i = 0; i < (TILE * 4f); i++) {
            int tempIndex = game.getNeighbour(moveIndex, MOVE.UP);
            if (tempIndex <= 0) break;
            moveIndex = tempIndex;
        }
        //get distance to pacman
        double pacDist = game.getDistance(
            game.getGhostCurrentNodeIndex(ghostType),
            game.getPacmanCurrentNodeIndex(),
            Constants.DM.EUCLID
        );
        // if withing 4 tiles of pacman go straight for him
        if (pacDist <= TILE * 4f) {
            return game.getPacmanCurrentNodeIndex();
        }
                
        return moveIndex;
    }

    // Pinky chase mode - set target to 4 moves ahead of pacman
    private int PinkyChase(Game game, GHOST ghostType) {
        int moveIndex = game.getPacmanCurrentNodeIndex();
        
        for (int i = 0; i < (TILE * 6f); i++) {
            int tempIndex = game.getNeighbour(moveIndex, MOVE.DOWN);
            if (tempIndex <= 0) break;
            moveIndex = tempIndex;
        }
        //get distance to pacman
        double pacDist = game.getDistance(
            game.getGhostCurrentNodeIndex(ghostType),
            game.getPacmanCurrentNodeIndex(),
            Constants.DM.EUCLID
        );
        // if withing 4 tiles of pacman go straight for him
        if (pacDist <= TILE * 4f) {
            return BlinkyChase(game, ghostType);
        }
        return moveIndex;
    }

    // Inky chase mode - set target to 2 moves ahead of pacman
    private int InkyChase(Game game, GHOST ghostType) { 
                int moveIndex = game.getPacmanCurrentNodeIndex();
        
        for (int i = 0; i < (TILE * 4f); i++) {
            int tempIndex = game.getNeighbour(moveIndex, MOVE.LEFT);
            if (tempIndex <= 0) break;
            moveIndex = tempIndex;
        }
        //get distance to pacman
        double pacDist = game.getDistance(
            game.getGhostCurrentNodeIndex(ghostType),
            game.getPacmanCurrentNodeIndex(),
            Constants.DM.EUCLID
        );
        // if withing 4 tiles of pacman go straight for him
        if (pacDist <= TILE * 4f) {
            for (int i = 0; i < (TILE * 2f); i++) {
                int tempIndex = game.getNeighbour(moveIndex, MOVE.RIGHT);
                if (tempIndex <= 0) break;
                moveIndex = tempIndex;
            }
        }
        return moveIndex;

    }
    // Sue chase mode - same as Blinky, but scatter if close to pacman
    private int SueChase(Game game, GHOST ghostType) {  
        int moveIndex = game.getPacmanCurrentNodeIndex();
        
        for (int i = 0; i < (TILE * 4f); i++) {
            int tempIndex = game.getNeighbour(moveIndex, MOVE.RIGHT);
            if (tempIndex <= 0) break;
            moveIndex = tempIndex;
        }
        //get distance to pacman
        double pacDist = game.getDistance(
            game.getGhostCurrentNodeIndex(ghostType),
            game.getPacmanCurrentNodeIndex(),
            Constants.DM.EUCLID
        );
        // if withing 4 tiles of pacman go straight for him
        if (pacDist <= TILE * 6f) {
            int newIndex = game.getNeighbour(moveIndex, MOVE.RIGHT);
            if(newIndex > 0) return newIndex;
        }
        return moveIndex;

    }
    
    
    private int GetDirectionalIndex(Game game, int moveIndex, MOVE mov) {
        int tempIndex = game.getNeighbour(moveIndex, mov);

        //if hitting a wall, break and try moving x distance
        if (tempIndex <= 0) {
            return moveIndex;
        }
        return tempIndex;
    }



    // Scatter mode - return to your corner
    private int Scatter(Game game, GHOST ghost){
        // Blinky owns the top right corner = 3
        if (ghost == GHOST.BLINKY) return game.getPowerPillIndices()[3];

        // Pinky owns the top left corner = 2
        else if (ghost == GHOST.PINKY){
            //System.out.println(game.isPowerPillStillAvailable(2));
            return game.getPowerPillIndices()[2];
        }
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

    private long getSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}