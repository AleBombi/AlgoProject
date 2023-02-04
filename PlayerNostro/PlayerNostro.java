package mnkgame.playerNostro;

import mnkgame.MNKBoard;
import mnkgame.MNKGameState;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKPlayer;

import java.util.Random;
import java.lang.Integer;
import java.math.*;





public class PlayerNostro implements MNKPlayer{

    private BooleanBoard Board;
    private boolean first;
    private int timeOut;
    private MNKGameState mywin;
    private MNKGameState yourwin;
    private Random rand;
    private MNKCellState myCell;
    private MNKCellState yourCell;
    protected MNKCell combinazioni[]=new MNKCell[12];
    static final double ALP = 1;

    private int numeroTOTcell;

    public PlayerNostro(){

    };

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs){
        Board = new BooleanBoard(M, N, K);
        this.first = first;
        timeOut = timeout_in_secs;
        mywin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourwin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        myCell = first ? MNKCellState.P1 : MNKCellState.P2;
        yourCell = first ? MNKCellState.P2 : MNKCellState.P1;
        rand    = new Random(System.currentTimeMillis());
        numeroTOTcell=0;
    };

    boolean isInBoard(int row, int col){
        return(row>=0 && row < Board.M && col>=0 && col < Board.N);
    }

    boolean IsFree(int row, int col){
        if(isInBoard(row,col)){
            return (Board.cellState(row, col) == MNKCellState.FREE);
        } else return false;
    }

    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC){
            stampacasellevuote();
           // valutazione();
            long start = System.currentTimeMillis();
            if(MC.length > 0){
                MNKCell n = MC[MC.length - 1];
                Board.markCell(n.i, n.j);
            } else {
                MNKCell n = new MNKCell(Board.M /2, Board.N /2);
                Board.markCell(n.i, n.j);
                return n;
            }


            MNKCell drawCell = FC[rand.nextInt(FC.length)];
            for(int i=0; i<FC.length; i++){
                if(!enoughTime(start)){
                    break;
                }
                drawCell = iterDeep(FC, i);
            }

            Board.markCell(drawCell.i, drawCell.j);
            return drawCell;
    }
    
    int getThreat(int combo, int type, boolean p, MNKCell[] MC){
        //  1 -> halfopen,  2 -> open, 3 -> vittoria
        //  combo: quanti ne devi mettere in fila
        // p indica il tipo, X oppure O

        int totale=0;
        int index=0;
        if( (p && Board.cellState(MC[0].i, MC[0].j) != myCell) || (!p && Board.cellState(MC[0].i, MC[0].j) == myCell) ){ //true sono le X
            index++;
        }

        //CheckThreat controlla se c'è un allineamento di tipo verticale, orizzontale, diagonale e antidiagonale
        for(int i=index; i< MC.length; i=i+2){
            totale=totale+CheckThreatVertical(MC[i], combo, type)+ CheckThreatOrizontal(MC[i], combo, type)+CheckThreatDiagonal(MC[i], combo, type)+ CheckThreatAntidiagonal(MC[i], combo, type);
        }

        for(int i=index; i<MC.length; i=i+2){
            Board.ChangeOrizontal(MC[i].i, MC[i].j, false);
            Board.ChangeVertical(MC[i].i, MC[i].j, false);
            Board.ChangeDiagonal(MC[i].i, MC[i].j, false);
            Board.ChangeAntidiagonal(MC[i].i, MC[i].j, false);
        }

        return totale;
    }

    int CheckThreatVertical(MNKCell cell, int sizeThreat, int type){
        int lastCellx1=cell.i;
        int lastCellx2=cell.i;

        int posXaxis=cell.i;
        int tot=-1;
        //si contano le celle sopra
        while(Board.cellState(posXaxis, cell.j)==Board.cellState(cell.i, cell.j) && !Board.GetVertical(posXaxis, cell.j)){
            tot=tot+1;
            Board.ChangeVertical(posXaxis, cell.j, true);
            posXaxis=posXaxis-1;
            if(posXaxis < 0){
                break;
            }
        }
        lastCellx1=posXaxis;
        posXaxis=cell.i;
        Board.ChangeVertical(posXaxis, cell.j, false);
        //si contano le celle sotto
        while(Board.cellState(posXaxis, cell.j)==Board.cellState(cell.i, cell.j) && !Board.GetVertical(posXaxis, cell.j)){
            tot=tot+1;
            Board.ChangeVertical(posXaxis, cell.j, true);
            posXaxis=posXaxis+1;
            if(posXaxis >= Board.M){
                break;
            }
        }
        lastCellx2=posXaxis;

        int risultato=0;
        if(tot==sizeThreat){
            switch (type){
                case 1:
                    if ((IsFree(lastCellx1, cell.j) && !IsFree(lastCellx2, cell.j)) || !IsFree(lastCellx1, cell.j) && IsFree(lastCellx2, cell.j)) risultato=1;
                    break;
                case 2:
                    if ((IsFree(lastCellx1, cell.j) && IsFree(lastCellx2, cell.j))) risultato=1;
                    break;
                case 3:
                    if(tot==Board.K) risultato=1;
            }
        } else if(type == 3 && tot > Board.K){
            risultato = 1;
        }
        return risultato;
    }

    int CheckThreatOrizontal(MNKCell cell, int sizeThreat, int type){
        int lastCelly1=cell.j;
        int lastCelly2=cell.j;

        int posYaxis=cell.j;
        int tot=-1;
        //si contano le celle a sinistra
        while(Board.cellState(cell.i, posYaxis)==Board.cellState(cell.i, cell.j) && !Board.GetOrizontal(cell.i, posYaxis)){
            tot=tot+1;
            Board.ChangeOrizontal(cell.i, posYaxis, true);
            posYaxis=posYaxis-1;
            if(posYaxis < 0){
                break;
            }
        }

        lastCelly1=posYaxis;
        posYaxis= cell.j;
        Board.ChangeOrizontal(cell.i, posYaxis, false);

        //si contano le celle a destra
        while(Board.cellState(cell.i, posYaxis)==Board.cellState(cell.i, cell.j) && !Board.GetOrizontal(cell.i, posYaxis)){
            tot=tot+1;
            Board.ChangeOrizontal(cell.i, posYaxis, true);
            posYaxis=posYaxis+1;
            if(posYaxis >= Board.N){
                break;
            }
        }

        lastCelly2=posYaxis;
        int risultato=0;
        if(tot==sizeThreat){
            switch (type){
                case 1:
                    if ((IsFree(cell.i, lastCelly1) && !IsFree(cell.i, lastCelly2)) || (!IsFree(cell.i, lastCelly1) && IsFree(cell.i, lastCelly2))) risultato=1;
                    break;
                case 2:
                    if ((IsFree(cell.i, lastCelly1) && IsFree(cell.i, lastCelly2))) risultato=1;
                    break;
                case 3:
                    if(tot==Board.K) risultato=1;
            }
        } else if(type == 3 && tot > Board.K){
            risultato = 1;
        }
        return risultato;
    }

    int CheckThreatDiagonal(MNKCell cell, int sizeThreat, int type){
        int lastCellx1=cell.i;
        int lastCelly1=cell.j;
        int lastCellx2=cell.i;
        int lastCelly2=cell.j;

        int posXaxis=cell.i;
        int posYaxis=cell.j;
        int tot=-1;
        //verso in alto a destra
        while(Board.cellState(posXaxis, posYaxis)==Board.cellState(cell.i, cell.j) && !Board.GetDiagonal(posXaxis, posYaxis)){
            tot=tot+1;
            Board.ChangeDiagonal(posXaxis, posYaxis, true);
            posXaxis=posXaxis-1;
            posYaxis=posYaxis+1;

            if(posXaxis < 0 || posYaxis >= Board.N){
                break;
            }
        }

        lastCellx1=posXaxis;
        lastCelly1=posYaxis;
        posXaxis=cell.i;
        posYaxis=cell.j;
        Board.ChangeDiagonal(posXaxis, posYaxis, false);
        //verso in basso a sinistra
        while(Board.cellState(posXaxis, posYaxis)==Board.cellState(cell.i, cell.j) && !Board.GetDiagonal(posXaxis, posYaxis)){
            tot=tot+1;
            Board.ChangeDiagonal(posXaxis, posYaxis, true);
            posXaxis=posXaxis+1;
            posYaxis=posYaxis-1;
            if(posXaxis >= Board.M || posYaxis < 0){
                break;
            }
        }

        lastCellx2=posXaxis;
        lastCelly2=posYaxis;
        int risultato=0;
        if(tot==sizeThreat){
            switch (type){
                case 1:
                    if ((IsFree(lastCellx1, lastCelly1) && !IsFree(lastCellx2, lastCelly2)) || (!IsFree(lastCellx1, lastCelly1) && IsFree(lastCellx2, lastCelly2))) risultato=1;
                    break;
                case 2:
                    if ((IsFree(lastCellx1, lastCelly1) && IsFree(lastCellx2, lastCelly2))) risultato=1;
                    break;
                case 3:
                    if(tot==Board.K) risultato=1;
            }
        } else if(type == 3 && tot > Board.K){
            risultato = 1;
        }
        return risultato;
    }

    int CheckThreatAntidiagonal(MNKCell cell, int sizeThreat, int type){
        int lastCellx1=cell.i;
        int lastCelly1=cell.j;
        int lastCellx2=cell.i;
        int lastCelly2=cell.j;

        int posXaxis=cell.i;
        int posYaxis=cell.j;
        int tot=-1;
        //verso in alto a sinistra
        while(Board.cellState(posXaxis, posYaxis)==Board.cellState(cell.i, cell.j) && !Board.GetAntidiagonal(posXaxis, posYaxis)){
            tot=tot+1;
            Board.ChangeAntidiagonal(posXaxis, posYaxis, true);
            posXaxis=posXaxis-1;
            posYaxis=posYaxis-1;
            if(posXaxis < 0 || posYaxis < 0){
                break;
            }
        }
        lastCellx1=posXaxis;
        lastCelly1=posYaxis;
        posXaxis=cell.i;
        posYaxis=cell.j;
        Board.ChangeAntidiagonal(posXaxis, posYaxis, false);
        //verso in basso a destra
        while(Board.cellState(posXaxis, posYaxis)==Board.cellState(cell.i, cell.j) && !Board.GetAntidiagonal(posXaxis, posYaxis)){
            tot=tot+1;
            Board.ChangeAntidiagonal(posXaxis, posYaxis, true);
            posXaxis=posXaxis+1;
            posYaxis=posYaxis+1;
            if(posXaxis >= Board.M || posYaxis >= Board.N){
                break;
            }
        }
        lastCellx2=posXaxis;
        lastCelly2=posYaxis;
        int risultato=0;
        if(tot==sizeThreat){
            switch (type){
                case 1:
                    if ((IsFree(lastCellx1, lastCelly1) && !IsFree(lastCellx2, lastCelly2)) || (!IsFree(lastCellx1, lastCelly1) && IsFree(lastCellx2, lastCelly2))) risultato=1;
                    break;
                case 2:
                    if ((IsFree(lastCellx1, lastCelly1) && IsFree(lastCellx2, lastCelly2))) risultato=1;
                    break;
                case 3:
                    if(tot==Board.K) risultato=1;
            }
        } else if(type == 3 && tot > Board.K){
            risultato = 1;
        }
        return risultato;
    }

    protected int threats(boolean p, MNKCell[] MC){
        int tot=0;
        if(p){
            for(int i = 1; i < Board.K-2; i++){
                tot += ALP * (i*2 - 1) * getThreat(i, 1, p, MC) + ALP * (i*2) * getThreat(i, 2, p, MC);
            }
            tot += ALP * getThreat(Board.K-2, 1, p, MC) + 100 * getThreat(Board.K-2, 2, p, MC) + 80 * getThreat(Board.K-1, 1, p, MC) + 250 * getThreat(Board.K-1, 2, p, MC) +
                    1000000 * getThreat(Board.K, 3, p, MC);
        } else{
            for(int i = 1; i < Board.K-2; i++){
                tot += ALP * (i*2 - 1) * getThreat(i, 1, p, MC) + ALP * (i*2) * getThreat(i, 2, p, MC);
            }
            tot += ALP * getThreat(Board.K-2, 1, p, MC) + 1300 * getThreat(Board.K-2, 2, p, MC) + 2000 * getThreat(Board.K-1, 1, p, MC) + 5020 * getThreat(Board.K-1, 2, p, MC) +
                    1000000 * getThreat(Board.K, 3, p, MC);

        }
        return tot;
    }


    private int evaluateProMax(){

        MNKCell[] MC = Board.getMarkedCells();
        return threats(true, MC) - threats(false, MC);
    }

    // Valuta soltanto vittoria/sconfitta/pareggio nei gamestate finali (1/0/-1)
    private int evaluate(){
        int eval;
        mnkgame.MNKGameState end = Board.gameState();
        if(end == MNKGameState.DRAW){
            eval = 0;
        } else if(end == mywin){
            eval = 1;
        } else eval = -1;

        return eval;
    }

    // Algoritmo alphaBeta con profondità limite

    private int alphaBetaDepht(MNKCell[] FC, boolean myturn, int a, int b, int depht){
        int eval;
        if(Board.gameState() != MNKGameState.OPEN || depht == 0){
            eval = evaluateProMax();
          //  StampGame(Board.getMarkedCells(), Board);
            // System.out.print( eval + "\n");
        } else if(myturn){
            eval = Integer.MIN_VALUE;
            for (MNKCell d : FC){
                Board.markCell(d.i, d.j);
                eval = Math.max(eval, alphaBetaDepht(Board.getFreeCells(), !myturn, a, b, depht - 1));
                Board.unmarkCell();
                a = Math.max(eval, a);
                if(b <= a){
                    break;
                }
            }
        } else {
            eval = Integer.MAX_VALUE;
            for(MNKCell d : FC){
                Board.markCell(d.i, d.j);
                eval = Math.min(eval, alphaBetaDepht(Board.getFreeCells(), !myturn, a, b, depht - 1 ));
                Board.unmarkCell();
                b = Math.min(eval, b);
                if(b <= a){
                    break;
                }
            }
        }
        return eval;
    }

    private MNKCell iterDeep(MNKCell[] FC, int depht){
        int a = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int eval;
        MNKCell cell = FC[rand.nextInt(FC.length)];
        for(MNKCell d : FC){
            Board.markCell(d.i, d.j);
            eval = alphaBetaDepht(Board.getFreeCells(), false, a, b, 3);
            if(eval > max){
                cell = d;
                max = eval;
            }
            Board.unmarkCell();
        }
        return cell;
    }

    private boolean enoughTime(long st){
        // tempo usato in secondi > 99% del timeout
        //     if(System.currentTimeMillis()-st/1000.0 > timeOut*(90.0/100.0)){
        return true;
        //     } else return false;
    }

    public void stampacasellevuote(){
        MNKCell celle[] =Board.getFreeCells();
        for(MNKCell d: celle){
            if(numeroTOTcell>Board.M*Board.N){
                break;    
            }
            numeroTOTcell=numeroTOTcell+1;            
            System.out.println("cella numero: " + numeroTOTcell + " coordinate x: "+ d.i + " coordinate y: " + d.j + "\n");
        }
    }

    public void valutazione(){

        //per la configurazione 4 3 3
        combinazioni[0] = new MNKCell(0,0, MNKCellState.P1);
        Board.markCell(0,0);
        combinazioni[1] = new MNKCell(1,0, MNKCellState.P2);
        Board.markCell(1,0);
        combinazioni[2] = new MNKCell(2,0, MNKCellState.P1);
        Board.markCell(2,0);
        combinazioni[3] = new MNKCell(3,0, MNKCellState.P2);
        Board.markCell(3,0);
        combinazioni[4] = new MNKCell(0,1, MNKCellState.P1);
        Board.markCell(0,1);
        combinazioni[5] = new MNKCell(0,2, MNKCellState.P2);
        Board.markCell(0,2);
        combinazioni[6] = new MNKCell(1,1, MNKCellState.P1);
        Board.markCell(1,1);
        combinazioni[7] = new MNKCell(1,2, MNKCellState.P2);
        Board.markCell(1,2);

        combinazioni[8] = new MNKCell(2,1, MNKCellState.FREE);
        combinazioni[9] = new MNKCell(2,2, MNKCellState.FREE);
        combinazioni[10] = new MNKCell(3,1, MNKCellState.FREE);
        combinazioni[11] = new MNKCell(3,2, MNKCellState.FREE);

        System.out.print( "valutazione della configuarzione:\n"  );
        StampGame(combinazioni, Board);
        System.out.println("\n" + (threats(true, combinazioni) - threats(false, combinazioni) ));

    }

    public void StampGame(MNKCell[] MC, MNKBoard B) {
        MNKCell c1, c2;
        boolean found = false;
        for (int i = 0; i < B.M; i++) {
            for (int j = 0; j < B.N; j++) {
                c1 = new MNKCell(i, j, MNKCellState.P1);
                c2 = new MNKCell(i, j, MNKCellState.P2);
                for (MNKCell M : MC) {
                    if (M.i == c1.i && M.j == c1.j && M.state == c1.state) {
                        System.out.print("X ");
                        found = true;
                        break;
                    } else if (M.i == c1.i && M.j == c1.j && M.state == c2.state) {
                        System.out.print("O ");
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.print("_ ");
                }
                found = false;
            }
            System.out.print("\n");
        }
        System.out.println("------");
    }

    public String playerName(){
        return "sus";
    }
}

