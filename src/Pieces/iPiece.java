package Pieces;

import Chessbot3.Color;
import Chessbot3.Move;
import Chessbot3.Tuple;

import java.util.ArrayList;

public interface iPiece {
    public Boolean isWhite();
    public Boolean isBlack();
    public Color getColor();
    public Boolean isOppositeColor(iPiece p);
    public Boolean canSprint();

    public Integer getX();
    public Integer getY();
    public Tuple<Integer, Integer> getCoords();

    public int getValue();
    public Character getSymbol();

    ArrayList<Move> getMoves();
}
