package mnkgame.playerNostro;
import mnkgame.MNKBoard;
import mnkgame.MNKCellState;

public class BooleanBoard extends MNKBoard{
    protected BooleanCombination[][] board;
    protected int i;

    public BooleanBoard (int m, int n, int k){
        super(m,n,k);
        board = new BooleanCombination[m][n];


        initBoardBool();
    }

    //USARE UNO SWITCH
    //USARE UNO SWITCH

    void initBoardBool(){
        for(int i = 0; i < M; i++){
            for(int j = 0; j < N; j++){
                board[i][j] = new BooleanCombination(i, j, MNKCellState.FREE);
            }
        }
    }

    void resetBool(){
        for(int i = 0; i < M; i++){
            for(int j = 0; j < N; j++){
                board[i][j].initBool();
            }
        }
    }


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

}
