package Chessbot3.GameBoard;

import static Chessbot3.GuiMain.Chess.gui;
import static Chessbot3.Pieces.WhiteBlack.BLACK;
import static Chessbot3.Pieces.WhiteBlack.WHITE;
import static java.lang.StrictMath.abs;

import java.util.ArrayList;
import java.util.List;

import Chessbot3.MiscResources.Move;
import Chessbot3.MiscResources.Tuple;
import Chessbot3.Pieces.*;

/**
 * Board
 */
public class Board {

    private static final char[][] initialBoard = new char[][]{
            new char[]{'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
            new char[]{'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
            new char[]{'.', '.', '.', '.', '.', '.', '.', '.'},
            new char[]{'.', '.', '.', '.', '.', '.', '.', '.'},
            new char[]{'.', '.', '.', '.', '.', '.', '.', '.'},
            new char[]{'.', '.', '.', '.', '.', '.', '.', '.'},
            new char[]{'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
            new char[]{'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
    };

    private static final Tuple A8 = new Tuple(0,0);
    private static final Tuple H8 = new Tuple(7,0);
    private static final Tuple A1 = new Tuple(0,7);
    private static final Tuple H1 = new Tuple(7,7);

    private static final Tuple E1 = new Tuple(4,7);
    private static final Tuple E8 = new Tuple(4,0);
    private static final Tuple G1 = new Tuple(6,7);
    private static final Tuple C1 = new Tuple(2,7);
    private static final Tuple G8 = new Tuple(6,0);
    private static final Tuple C8 = new Tuple(2,0);


    iPiece[][] grid;

    private int wScore;
    private int bScore;
    private WhiteBlack colorToMove = WHITE;
    //private boolean isWhitesTurn = true;
    public Tuple<Boolean, Boolean> wCastle;
    public Tuple<Boolean, Boolean> bCastle;
    private Tuple<Integer, Integer> passantPos;

    public Board() {
        //Det initielle brettet. Plasserer brikker med hensyn på initialBoard.
        grid = new iPiece[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                iPiece pie = PieceFactory.getPiece(initialBoard[y][x]);
                grid[x][y] = pie;
            }
        }
        wCastle = new Tuple<>(true, true);
        bCastle = new Tuple<>(true, true);
        wScore = 0;
        bScore = 0;
        passantPos = new Tuple(-1, -1);
    }

    public Board(iPiece[][] customBoard, WhiteBlack colorToMove, int wScore, int bScore, Tuple<Boolean, Boolean> wCastle, Tuple<Boolean, Boolean> bCastle, Tuple<Integer, Integer> passantPos)
    //En konstruktør som kun skal brukes for å opprette en kopi av et tidligere brett.
    //Denne tar inn score, rokadebetingelser, osv fra det forrige brettet.
    {
        this.grid = customBoard;
        this.colorToMove = colorToMove;
        this.wCastle = wCastle;
        this.bCastle = bCastle;
        this.wScore = wScore;
        this.bScore = bScore;
        this.passantPos = passantPos;
    }

    public List<Move> GenMoves(WhiteBlack c){
        //Tar inn en farge og gir deg en liste over alle trekk den spilleren kan ta akkurat nå,
        // inkludert rokader og en passant.
        ArrayList<Move> ret = new ArrayList();
        for(iPiece pie : GetPieceList(c)){
            ret.addAll(pie.getMoves(this));
        }
        return ret;
    }

    public Tuple<Integer, Integer> GetCoordsOfPiece(iPiece piece) throws IllegalArgumentException {
        //Søker etter en brikke og returnerer koordinatene dens.
        //Kræsjer om den ikke finner den.
        for (int i = 0; i < grid.length; i++) {
            iPiece[] subRow = grid[i];
            for (int j = 0; j < subRow.length; j++) {
                if (subRow[j] == piece)
                    return new Tuple<Integer, Integer>(i, j);
            }
        }
        throw new IllegalArgumentException("Fant ikke brikken!");
    }

    public int AddScore(iPiece piece) {
        if(piece == null) return 0;
        if (piece.getColor() == WHITE)
            bScore += piece.getValue();
        else
            wScore += piece.getValue();
        return piece.getValue();
    }
    public void AddScore(Integer x, WhiteBlack c){
        if(c == WHITE) wScore += x;
        else bScore += x;
    }

    public void MovePiece(Move move, Boolean isHumanPlayer) {
        //Flytter en brikke. Denne oppdaterer rokadebetingelser og en passant.
        //Denne driter i om trekket er lovlig eller ikke, det må sjekkes med checkPlayerMove/GenMoves.
        //Om isHumanPlayer=true, og trekket er at en bonde blir flyttet til enden av brettet,
        //vil denne lage et pupup-vindu om hvilken brikke bonden skal promoteres til.
        //Hvis ikke, spawnes bare en dronning.

        Tuple<Integer, Integer> fra = move.getX();
        Tuple<Integer, Integer> til = move.getY();
        iPiece pie = GetPiece(fra);
        iPiece target = GetPiece(til);

        //Oppdaterer rokadebetingelser
        //Om et tårn blir tatt eller flyttet, kan ikke lenger spilleren rokere den veien.
        if(fra.equals(A1) || til.equals(A1)) wCastle.setX(false);
        else if(fra.equals(H1) || til.equals(H1)) wCastle.setY(false);
        else if(fra.equals(A8) || til.equals(A8)) bCastle.setX(false);
        else if(fra.equals(H8) || til.equals(H8)) bCastle.setY(false);

        else if(fra.equals(E1)) wCastle = new Tuple(false, false); //Om kongen flytter seg, kan den aldri rokere.
        else if(fra.equals(E8)) bCastle = new Tuple(false, false);

        //Flytter tårnet, om kongen rokerer.
        if(pie instanceof King){
            if(fra.equals(E1) && til.equals(G1)){
                grid[5][7] = grid[7][7]; //Flytter tårnet når hvit rokerer kort.
                grid[7][7] = null;
            }else if(fra.equals(E1) && til.equals(C1)){
                grid[3][7] = grid[0][7]; //Flytter tårnet når hvit rokerer langt.
                grid[0][7] = null;
            }else if(fra.equals(E8) && til.equals(G8)){
                grid[5][0] = grid[7][0]; //Flytter tårnet når svart rokerer kort.
                grid[7][0] = null;
            }else if(fra.equals(E8) && til.equals(C8)){
                grid[3][0] = grid[0][0]; //Flytter tårnet når svart rokerer langt.
                grid[0][0] = null;
            }
        }

        //Flytter faktisk brikken.
        grid[til.getX()][til.getY()] = pie;
        grid[fra.getX()][fra.getY()] = null;

        //Dreper en bonde, om trekket er en passant.
        if(til.equals(passantPos)){
            if(til.getY() == 2) grid[til.getX()][3] = null;
            else grid[til.getX()][4] = null;
        }
        //oppdaterer passantbetingelser.
        if(passantPos.getX() != -1) passantPos.setX(-1);
        if(pie instanceof Pawn){
            if(abs(fra.getY()-til.getY()) == 2){
                passantPos.setX(fra.getX());
                passantPos.setY((fra.getY()+til.getY())/2);
            }
            //Promoterer bønder.
            else if(til.getY() == 0 || til.getY() == 7){
                if(!isHumanPlayer) grid[til.getX()][til.getY()] = new Queen(pie.getColor());
                else grid[til.getX()][til.getY()] = gui.promotePawn();
            }
        }
        colorToMove = GetOppositeColor(colorToMove); //Bytter farge på hvem sin tur det er.

        // TODO: 26.03.2020 Legg til score eller noe sånt her? 

    }

    public Boolean CheckPlayerMove(Move playerMove) {
        //Sjekker om spillerens trekk er lovlig.
        // Tar hensyn til om trekket setter seg selv i sjakk.
        // Returnerer true om det er lov.
        boolean ret = false;
        for (Move move : GenMoves(GetColorToMove())) {
            if (move.equals(playerMove)) {
                ret = true;
            }
        }//Om spillerens trekk er på listen, er det kanskje lovlig.
        //Da må vi simulere at det trekket blir gjort, og se om motstanderen har noen trekk han kan gjøre for å umiddelbart ta kongen.
        //Hvis ja, betyr det at spilleren har satt seg selv i sjakk, eller at han sto i sjakk og ingorerte det.
        //Da er trekket ulovlig, og returnerer false;
        if(ret) {
            Board copy = this.Copy();
            copy.MovePiece(playerMove, false);
            List<Move> counterMoves = copy.GenMoves(GetOppositeColorToMove());
            for (Move counter : counterMoves) {
                iPiece target = copy.GetGrid()[counter.getY().getX()][counter.getY().getY()];
                if (target instanceof King) return false; //Om motstanderen kan ta kongen
            }
            return true; //Om motstanderen ikke har noen trekk som kan ta kongen
        }
        return false; //Om trekket ikke engang er på den originale listen.
    }

    public Boolean CheckCheckMate(){
        //Sjekker om brettet er sjakkmatt.
        //Returnerer true om det matt, null om det er patt (uavgjort) og false ellers.
        List<Move> legals = new ArrayList<>();
        for(Move move : GenMoves(GetColorToMove())){
            if(CheckPlayerMove(move)) legals.add(move);
        }
        if(legals.size()>0) return false; //Om spilleren har lovlige trekk.
        else{
            for(Move move : GenMoves(GetOppositeColorToMove())){
                iPiece target = GetGrid()[move.getY().getX()][move.getY().getY()];
                if(target instanceof King) return true; //Om spilleren ikke har noen lovlige trekk, og kongen blir truet.
            }
            return null; //Om spilleren ikke har noen lovlige trekk, men heller ikke blir truet. Da er det patt.
        }
    }

    public int GetScore(boolean isWhite)
    {
        if (isWhite)
            return wScore;
        else
            return bScore;
    }
    public int GetScore(WhiteBlack color){
        //Returnerer scoren til en farge.
        if(color == WHITE) return wScore;
        else return bScore;
    }
    public iPiece[][] GetGrid()
    {
        //Returnerer en kopi av selve rutenettet av brikker.
        iPiece[][] retgrid = new iPiece[8][8];
        for(int y=0; y<8; y++){
            for(int x=0; x<8; x++){
                retgrid[y][x] = grid[y][x];
            }
        }
        return retgrid;
    }

    public Board Copy()
    {
        //Returnerer en kopi av brettet, og husker hvem som kan rokerer hvor, og scoren til hver spiller.
        return new Board(this.GetGrid(), this.colorToMove, this.wScore, this.bScore, this.wCastle.copy(), this.bCastle.copy(), this.passantPos.copy());
    }

    //Returnerer hvilken farge som skal flytte.
    public WhiteBlack GetColorToMove(){ return colorToMove; }

    //Returnerer den andre fargen, den fargen som ikke skal flytte.
    public WhiteBlack GetOppositeColorToMove(){ return GetOppositeColor(colorToMove); }
    
    public static WhiteBlack GetOppositeColor(WhiteBlack c){
        //Tar en farge, og returnerer den andre fargen.
        //Dette er litt det samme som å sette et ikke-tegn foran en farge.
        if(c == WHITE) return BLACK;
        else return WHITE;
    }

    public List<iPiece> GetPieceList(WhiteBlack c){
        //Lager en liste over alle brikkene til en farge.
        List<iPiece> ret = new ArrayList<>();
        for(int y=0; y<8; y++){
            for(int x=0; x<8; x++){
                iPiece pie = grid[y][x];
                if(pie != null && pie.getColor() == c) ret.add(pie);
            }
        }
        return ret;
    }
    public Tuple<Integer, Integer> GetPassantPos(){ return passantPos; }

    public iPiece GetPiece(Tuple<Integer, Integer> pos) { return grid[pos.getX()][pos.getY()]; }

    public iPiece GetPiece(int x, int y){ return grid[x][y]; }

    @Override
    public String toString(){
        String ret = "";
        for(int y=0; y<8; y++){
            String rekke = "";
            for(int x=0; x<8; x++){
                iPiece pie = GetPiece(x, y);
                if(pie == null) rekke += ".";
                else if(pie.isWhite()) rekke += pie.getSymbol();
                else rekke += Character.toLowerCase(pie.getSymbol());
                rekke += "";
            }
            ret += rekke + "\n";
        }
        ret += "White: " + wScore + " Black: " + bScore;
        return ret;
    }
}