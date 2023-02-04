package mnkgame.PlayerNostro;

import mnkgame.MNKCell;

/*
  Un estensione di MNKCell per inserire altre informazioni usate nella valutazione e nell'ordinamento delle mosse
*/

public class BooleanCombination extends MNKCell{
  protected boolean Vertical;
  protected boolean Orizontal;
  protected boolean Diagonal;     // diagonal è la diagonale da in basso a sinistra verso in alto a destra 
  protected boolean Antidiagonal; // antidiagonal è la diagonale da in basso a destra verso in alto a sinistra  
  protected int proximityCounter;    //numero di celle segnate nell'intorno


  public BooleanCombination(int i, int j){
    super(i, j);
    proximityCounter = 0;
    initBool();
  }

  public void initBool(){
    this.Vertical=false;
    this.Orizontal=false;
    this.Diagonal=false;
    this.Antidiagonal=false;
  }

}