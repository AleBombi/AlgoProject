package mnkgame.PlayerNostro;
import mnkgame.MNKBoard;
import mnkgame.MNKCellState;
import java.util.*;

public class BooleanBoard extends MNKBoard{
    protected BooleanCombination[][] board;

    /*
        Inizializza due Board: 
            -MNKCell contenente gli stati
            -BooleanCombination con variabili ausiliarie
        O(M*N)
    */
    public BooleanBoard (int m, int n, int k){
        super(m,n,k);
        board = new BooleanCombination[m][n];


        initBoardBool();
    }

    
    //  O(M*N)
    void initBoardBool(){
        for(int i = 0; i < M; i++){
            for(int j = 0; j < N; j++){
                board[i][j] = new BooleanCombination(i, j);
            }
        }
    }

    /*
        Change modifica la varibile della cella (row,col)
        Get restituisce tale variabile 
        O(1)
    */

    public void ChangeVertical(int row, int col, boolean p){
        board[row][col].Vertical=p;
    }

    public boolean GetVertical(int row, int col){
        return board[row][col].Vertical;
    }

    public void ChangeOrizontal(int row, int col, boolean p){
        board[row][col].Orizontal=p;
    }

    public boolean GetOrizontal(int row, int col){
        return board[row][col].Orizontal;
    }

    public void ChangeDiagonal(int row, int col, boolean p){
        board[row][col].Diagonal=p;
    }

    public boolean GetDiagonal(int row, int col){
        return board[row][col].Diagonal;
    }

    public void ChangeAntidiagonal(int row, int col, boolean p){
        board[row][col].Antidiagonal=p;
    }

    public boolean GetAntidiagonal(int row, int col){
        return board[row][col].Antidiagonal;
    }


    /*
        Modifica il proximitycounter delle celle attorno alla cella data e ritorna una lista di queste con in testa la cella data
        O(1)
    */
    public LinkedList<BooleanCombination> addProximity(int row, int col, int mod){
        LinkedList<BooleanCombination> list = new LinkedList<BooleanCombination>();
		BooleanCombination newc = board[row][col];

        list.add(newc);

        if(row>0){
            board[row-1][col].proximityCounter += mod;
            newc = board[row -1][col];
            if(B[newc.i][newc.j] == MNKCellState.FREE){
                list.add(newc);
            } 

        }
        if(row<M-1){
            board[row+1][col].proximityCounter += mod;
            newc = board[row +1][col]; 
            if(B[newc.i][newc.j] == MNKCellState.FREE){
                list.add(newc);
            } 
        }
        if(col>0){
            board[row][col-1].proximityCounter += mod;
            newc = board[row][col-1]; 
            if(B[newc.i][newc.j] == MNKCellState.FREE){
                list.add(newc);
            } 
        }
        if(col<N-1){
            board[row][col+1].proximityCounter += mod;
            newc = board[row][col+1]; 
            if(B[newc.i][newc.j] == MNKCellState.FREE){
                list.add(newc);
            } 
        }
        if(row>0 && col>0){
            board[row-1][col-1].proximityCounter += mod;
            newc = board[row -1][col-1]; 
            if(B[newc.i][newc.j] == MNKCellState.FREE){
                list.add(newc);
            } 
        }
        if(row>0 && col<N-1){
            board[row-1][col+1].proximityCounter += mod;
            newc = board[row -1][col +1]; 
            if(B[newc.i][newc.j] == MNKCellState.FREE){
                list.add(newc);
            } 
        }
        if(row<M-1 && col>0){
            board[row+1][col-1].proximityCounter += mod;
            newc = board[row +1][col -1]; 
            if(B[newc.i][newc.j] == MNKCellState.FREE){
                list.add(newc);
            } 
        }
        if(row<M-1 && col<N-1){
            board[row+1][col+1].proximityCounter += mod;
            newc = board[row +1][col +1]; 
            if(B[newc.i][newc.j] == MNKCellState.FREE){
                list.add(newc);
            } 
        }
        return list;
    }


}