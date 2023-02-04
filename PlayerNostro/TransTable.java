package mnkgame.playerNostro;

import java.util.HashMap;
import java.util.Random;

/* Cosa salvare:
    zobrist key
    valutazione
    profondità
    mossa migliore
*/




public class TransTable {

    protected long [][] zobrist;
    private Random rand;
    private HashMap<Integer, Integer> tt;

    public TransTable(int m, int n){
        rand = new Random(System.currentTimeMillis());
        zobrist = new long[m*n][2];
        for(int cell=0; cell < zobrist.length; cell++){
            for(int player=0; player < 2; player++){
                zobrist[cell][player] = rand.nextLong();
            }
        }
        tt = new HashMap<Integer,Integer>(65536);
        
  }


}