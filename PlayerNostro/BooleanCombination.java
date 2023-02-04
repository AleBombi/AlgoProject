package mnkgame.playerNostro;

import mnkgame.MNKCellState;
import mnkgame.MNKCell;

public class BooleanCombination extends MNKCell{
protected boolean Vertical;
protected boolean Orizontal;
protected boolean Diagonal;
  /* diagonal è la diagonale da in basso a sinistra
  verso in alto a destra  */
protected boolean Antidiagonal;

  public BooleanCombination(int i, int j, MNKCellState state){
    super(i,j,state);
    initBool();
  }

  public void initBool(){
    this.Vertical=false;
    this.Orizontal=false;
    this.Diagonal=false;
    this.Antidiagonal=false;
  }

}
