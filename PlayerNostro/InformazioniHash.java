package mnkgame.playerNostro;

public class InformazioniHash{
    protected int valutazione_configurazione;
    protected int depht;
    protected MNKCell bestmove;
    protected long key;

    protected InformazioniHash(int val, int profonidita, MNKCell cell, long chaive){
        this.valutazione_configurazione=val;
        this.depht=profonidita;
        this.bestmove=cell;
        this.key=chiave;
    }
}