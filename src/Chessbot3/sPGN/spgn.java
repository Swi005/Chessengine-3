package Chessbot3.sPGN;

import Chessbot3.MiscResources.Move;

import java.io.File;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

public class spgn implements Ispgn
{
    private int index = 0;
    private int score;
    private String name;
    private int type; //0 for save| 1 for lookup table
    private Move[] moves;
    private String path;

    //Constructor for making a new file
    public spgn(int score, int type, Move[] moves)
    {

        this.score = score;
        this.type = type;
        this.moves = moves;
        if(type == 0)
            path = "Chessbot3/src/Chessbot3/files/saves/" + "save:"+ new Date().toString();//TODO: Find out a naming scheme
        else if(type == 1)
            path = "Chessbot3/src/Chessbot3/files/lookuptables/"+ "lookup:"+ new Date().toString();//TODO: Find out a naming scheme
    }

    //Constructor for retriving a file as an spgn
    public spgn(File file)
    {
        spgnIO spgnController = new spgnIO();
        spgn temp = spgnController.GetSPGN(file);
        this.moves = temp.moves;
        this.score = temp.score;
        this.type = temp.type;
        path = file.getPath();
        name = file.getName();
    }
    public spgn(String name)
    {
        spgnIO spgnController = new spgnIO();
        spgn temp = spgnController.GetSPGN(new File(path + name));
        this.moves = temp.moves;
        this.score = temp.score;
        this.type = temp.type;
        this.path = temp.path;
    }

    @Override
    public int GetScore()
    {
        return score;
    }

    @Override
    public int GetType()
    {
        return type;
    }

    @Override
    public Move GetMove(int index)
    {
        return moves[index];
    }

    @Override
    public Move GetNextMove()
    {
        index++;
        return GetMove(index);
    }

    @Override
    public Move[] GetAllMoves() {
        return moves;
    }

    @Override
    public String GetPathToFile() {
        return path;
    }

    @Override
    public String GetName() {
        return null;
    }

    @Override
    public String toString()//TODO: Find a string representation of an spgn object
    {
        return "spgn{" +
                "index=" + index +
                ", score=" + score +
                ", type=" + type +
                ", moves=" + Arrays.toString(moves) +
                ", path='" + path + '\'' +
                '}';
    }
}
