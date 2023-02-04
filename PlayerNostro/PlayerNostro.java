package mnkgame.PlayerNostro;

import mnkgame.MNKGameState;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKPlayer;

import java.util.LinkedList;
import java.lang.Integer;


public class PlayerNostro implements MNKPlayer{

    private BooleanBoard Board; //board contenente stati delle celle e variabili ausiliarie
    private int timeOut;    //timeout in secondi
    private double percTime;
    private MNKCellState myCell;    //CellState assegnato (x/O)
    private long start; //segna il momento di inizio del timer
    private boolean is_late;    //Variabile di controllo per il tempo
    private AVLtree AVLtree;    //Albero bilanciato per ordinamento mosse

    public PlayerNostro(){};

    /*
        Inizializzazione del giocatore
        O(M*N) 
    */
    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs){
        Board = new BooleanBoard(M, N, K);
        timeOut = timeout_in_secs;
        percTime = 90.0 - (M*N / 1000);
        myCell = first ? MNKCellState.P1 : MNKCellState.P2;
        AVLtree = new AVLtree();
    };

    /*
        Seleziona e ritorna la mossa migliore
        O() 
    */
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC){

        start = System.currentTimeMillis();
        is_late = false;

        if(MC.length > 0){  //recupera la mossa dell'avversario
            BooleanCombination n = new BooleanCombination(MC[MC.length - 1].i, MC[MC.length - 1].j);
            Board.markCell(n.i, n.j);
            AVLtree.updateTree(Board.addProximity(n.i, n.j, 1),1);
        } else {    //se ha la prima mossa gioca al centro della board
            BooleanCombination n = new BooleanCombination(Board.M /2, Board.N /2);
            Board.markCell(n.i, n.j);
            AVLtree.updateTree(Board.addProximity(n.i, n.j, 1),1);
            return n;
        }
       
        LinkedList<BooleanCombination> mosse = AVLtree.PossibleMoves(); //recupera le mosse prioritarie dall'albero
        BooleanCombination drawCell = mosse.getFirst();
        BooleanCombination tempCell = drawCell;

        for(int i=0; i<mosse.size(); i++){ //aumenta gradualmente la profondità di ricerca
            if((System.currentTimeMillis()-start)/1000.0  > ((percTime/100.0) * timeOut)){
                is_late = true;
                break;
            }

            tempCell = iterDeep(mosse, i);

            if(is_late && i>0){
                break;
            }
            drawCell = tempCell;
        }

        // marchia la cella, aggiorna albero e TransTable e ritorna la cella
        Board.markCell(drawCell.i, drawCell.j);
        AVLtree.updateTree(Board.addProximity(drawCell.i, drawCell.j, 1),1);

        changeTimer();

        return drawCell;
    }

    /*
        Esegue una ricerca alphabeta sulle mosse con profondità massima depht
        O(b^d) con b fattore di diramazione e d altezza dell’albero
    */
    private BooleanCombination iterDeep(LinkedList<BooleanCombination> moves, int depht){
        int a = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int eval;
        BooleanCombination cell=moves.getFirst();

        for(BooleanCombination d : moves){ //visita una per una le mosse della lista
            if((System.currentTimeMillis()-start)/1000.0  > ((percTime/100.0) * timeOut)){
                is_late = true;
                break;
            }
            Board.markCell(d.i, d.j);
            AVLtree.updateTree(Board.addProximity(d.i, d.j, 1),1);

            eval = alphaBetaDepht(AVLtree.PossibleMoves(), false, a, b, depht); //visita in profondità e ritorna una valutazione

            if(eval > max){
                cell = d;
                max = eval;
            }

            Board.unmarkCell();
            AVLtree.updateTree(Board.addProximity(d.i, d.j, -1), -1);

            if(is_late){
                break;
            }
        }

        return cell;
    }

    /*
        Ricerca alphaBetaPruning
        O()
    */
    private int alphaBetaDepht(LinkedList<BooleanCombination> moves, boolean myturn, int a, int b, int depht){
        int eval;

        //Controllo di interruzione per tempo, GameState finale o profondità richiesta raggiunta
        if(((System.currentTimeMillis()-start)/1000.0  > ((percTime/100.0) * timeOut)) || (Board.gameState() != MNKGameState.OPEN || depht == 0)){
            if((System.currentTimeMillis()-start)/1000.0  > ((percTime/100.0) * timeOut)){
                is_late=true;
                return Integer.MIN_VALUE;
            }

            eval = evaluateProMax();
            
        //altrimenti prosegui nella ricerca
        } else if(myturn){
            eval = Integer.MIN_VALUE;

            for (BooleanCombination d : moves){
                if((System.currentTimeMillis()-start)/1000.0  > ((percTime/100.0) * timeOut)){
                    is_late=true;
                    break;
                }
                Board.markCell(d.i, d.j);
                AVLtree.updateTree(Board.addProximity(d.i, d.j, 1), 1);

                eval = Math.max(eval, alphaBetaDepht(AVLtree.PossibleMoves(), !myturn, a, b, depht - 1));
                
                Board.unmarkCell();
                AVLtree.updateTree(Board.addProximity(d.i, d.j, -1), -1);

                a = Math.max(eval, a);
                if(b <= a){ //CutOff
                    break;
                }
            }
        } else {
            eval = Integer.MAX_VALUE;
            for(BooleanCombination d : moves){
                if((System.currentTimeMillis()-start)/1000.0  > ((percTime/100.0) * timeOut)){
                    is_late=true;
                    break;
                }
                Board.markCell(d.i, d.j);
                AVLtree.updateTree(Board.addProximity(d.i, d.j, 1), 1);

                eval = Math.min(eval, alphaBetaDepht(AVLtree.PossibleMoves(), !myturn, a, b, depht - 1 ));;
                
                Board.unmarkCell();
                AVLtree.updateTree(Board.addProximity(d.i, d.j, -1), -1);

                b = Math.min(eval, b);
                if(b <= a){ //CutOff
                    break;
                }
            }
        }
        if(is_late){
            return Integer.MIN_VALUE;
        }
        return eval;
    }

    /*
        Valuta la board per entrambi i giocatori e sottrae i punteggi
        O(M*N*K) 
    */
    private int evaluateProMax(){
        MNKCell[] MC = Board.getMarkedCells();
        return threats(true, MC) - threats(false, MC);
    }

    /*
        Valuta la board per il giocatore p e ritorna il valore
        O(M*N*K) 
    */
    protected int threats(boolean p, MNKCell[] MC){
        int tot=0;
        if(p){
            for(int i = 1; i < Board.K-2; i++){
                
                tot += (i*2 - 1) * getThreat(i, 1, p, MC) + (i*2) * getThreat(i, 2, p, MC);
            }
            tot += getThreat(Board.K-2, 1, p, MC) + 100 * getThreat(Board.K-2, 2, p, MC) + 80 * getThreat(Board.K-1, 1, p, MC) + 250 * getThreat(Board.K-1, 2, p, MC) +
                    1000000 * getThreat(Board.K, 3, p, MC);
        } else{
            for(int i = 1; i < Board.K-2; i++){
                tot += (i*2 - 1) * getThreat(i, 1, p, MC) + (i*2) * getThreat(i, 2, p, MC);
            }
            tot += getThreat(Board.K-2, 1, p, MC) + 1300 * getThreat(Board.K-2, 2, p, MC) + 2000 * getThreat(Board.K-1, 1, p, MC) + 5020 * getThreat(Board.K-1, 2, p, MC) +
                    1000000 * getThreat(Board.K, 3, p, MC);

        }
        return tot;
    }

    /*
        combo: quanti ne devi mettere in fila
        type: 1 -> halfopen,  2 -> open, 3 -> vittoria
        p indica il player: falso -> avversario
        Ritorna il numero di combinazioni di lunghezza combo e del tipo type
        O(nk)
    */
    int getThreat(int combo, int type, boolean p, MNKCell[] MC){


        int totale=0;
        int index=0;
        if( (p && Board.cellState(MC[0].i, MC[0].j) != myCell) || (!p && Board.cellState(MC[0].i, MC[0].j) == myCell) ){ 
            //osserva la prima mossa della partita, se non è del giocatore che si vuole valutare aumenta index
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

    /*
        Le funzioni CheckThreat controllano se è possibile da una cella ottenere una combinazione verticale diagonale o obliqua
        Si utilizzano le variabili Vertical/Orizontal/Diagonal/Antidiagonal per memorizzare quando una combinazione è già stata controllata
        O(K) 
    */

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
    
    /*
        Ritorna true se la cella è all'interno della board
        O(1) 
    */
    boolean isInBoard(int row, int col){
        return(row>=0 && row < Board.M && col>=0 && col < Board.N);
    }

    /*
        Restituisce true se la cella è libera e dentro la board 
        O(1)
    */
    boolean IsFree(int row, int col){
        if(isInBoard(row,col)){
            return (Board.cellState(row, col) == MNKCellState.FREE);
        } else return false;
    }

    private void changeTimer(){
        double tempTime = timeOut - (System.currentTimeMillis()-start)/1000.0;
        if(tempTime <= 0.3){
            percTime -= 3;
        } else if(tempTime >=0.6 && percTime < 97){
            percTime += 3;
        }
    }
    
    public String playerName(){
        return "Corrado il Conquistatore";
    }

}

