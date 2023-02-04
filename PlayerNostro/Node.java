package mnkgame.PlayerNostro;

import java.util.*;

/*
   Nodo dell'albero AVLtree per l'ordinamento delle mosse
*/

public class Node {
   protected int key;
   protected Node left;
   protected Node right;
   protected int height;
   protected int count;
   protected LinkedList<BooleanCombination> ListNodi;	

   public Node(int chiave, BooleanCombination cell){
      key=chiave;
      left=null;
      right=null;
      height=1; //nuovo nodo aggiunto come foglia
      count=1;
      ListNodi = new LinkedList<BooleanCombination>();
      ListNodi.add(cell);
   }

}