package Chessbot3.GameBoard;

import Chessbot3.Move;
import Chessbot3.Tuple;
import Chessbot3.iPiece;

/**
 * IBoard
 */
public interface IBoard 
{

    public iPiece GetPice(Tuple<Integer,Integer> pos);

    public void MovePiece(iPiece pice, Move move);

    public boolean IsMate();
    
    public Move[] GenMoves(iPiece pice);

    public int GetScore(boolean isWhite);

}