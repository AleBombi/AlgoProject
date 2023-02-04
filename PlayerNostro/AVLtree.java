package mnkgame.PlayerNostro;

import java.util.*;

/*
	Un albero bilanciato utilizzato per l'ordinamento delle mosse
*/
public class AVLtree {

	protected Node tree;
	
	/*
		Inizializza un albero vuoto
		O(1) 
	*/
	public AVLtree(){
		tree=null;
	}

	/*
		Restituisce l'altezza di un nodo N
		O(1) 
	*/
	public int height(Node N){
        if (N == null)
            return 0;
        return N.height;
    }

	/*
		Ruota verso destra il sottoalbero con radice y e restituisce la nuova radice
		O(1)
	*/
	public Node rightRotate(Node y){
		Node x = y.left;
		Node T2 = x.right;

		// ruota
		x.right = y;
		y.left = T2;

		// aggiorna altezza
		y.height = Math.max(height(y.left), height(y.right)) + 1;
		x.height = Math.max(height(x.left), height(x.right)) + 1;

		return x;
	}

	/*
		Ruota verso sinistra il sottoalbero con radice x e restituisce la nuova radice
		O(1)
	*/
	public Node leftRotate(Node x){
		Node y = x.right;
		Node T2 = y.left;

		// ruota
		y.left = x;
		x.right = T2;

		// aggiorna altezza
		x.height = Math.max(height(x.left), height(x.right)) + 1;
		y.height = Math.max(height(y.left), height(y.right)) + 1;

		return y;
	}

	/*
		Restituisce il fattore di bilanciamento del nodo
		O(1) 
	*/
	public int getBalance(Node node){
		if (node == null)
			return 0;
		return height(node.left) - height(node.right);
	}

	/*
		Inserisce nel sottoalbero la nuova cella e ribilancia
		O(log n) 
	*/
	public Node insertNode(Node node, int key, BooleanCombination cell){
		if (node == null){
			node = new Node(key, cell);
			return node;
		}

		// se esiste già un nodo con la stessa chiave aggiungi la cella in lista
		if (key == node.key){
			(node.count)++;
			node.ListNodi.add(cell);
			return node;
		}

		// scorri l'albero
		if (key < node.key)
			node.left = insertNode(node.left, key, cell);
		else
			node.right = insertNode(node.right, key, cell);

		// aggiorna l'altezza
		node.height = Math.max(height(node.left), height(node.right)) + 1;

		// se il nodo si è sbilanciato ribilancia
		int balance = getBalance(node);

		// Left Left Case
		if (balance > 1 && key < node.left.key)
			return rightRotate(node);

		// Right Right Case
		if (balance < -1 && key > node.right.key)
			return leftRotate(node);

		// Left Right Case
		if (balance > 1 && key > node.left.key) {
			node.left = leftRotate(node.left);
			return rightRotate(node);
		}

		// Right Left Case
		if (balance < -1 && key < node.right.key) {
			node.right = rightRotate(node.right);
			return leftRotate(node);
		}

		// ritorna la radice
		return node;
	}

	/*
		Scorre l'albero a sinistra e restituisce il nodo con chiave più piccola 
		O(h) h=altezza dell'albero
	*/
	public Node minValueNode(Node node)	{
		Node current = node;

		while (current.left != null)
			current = current.left;
		return current;
	}

	/*
		Ritorna una lista ordinata delle mosse
		O(n) n=numero totale dei nodi
	*/
	public LinkedList<BooleanCombination> PossibleMoves(){
		LinkedList<BooleanCombination> mosse = new LinkedList<>();
		NotInOrder(tree, mosse);
		return mosse;
	}

	/*
		visita in ordine, dal più grande al più piccolo, e aggiunge le celle a una lista
		O(n) n=numero totale dei nodi 
	*/
	public void NotInOrder(Node node, LinkedList<BooleanCombination> mosse){
		if(node!=null){
			NotInOrder(node.right, mosse);
			for( BooleanCombination d: node.ListNodi){
				mosse.add(d);	
			}
			NotInOrder(node.left, mosse);
		}
	}

	/*
		Elimina il nodo corrispondente alla chiave nel sottoalbero root e ribilancia
		O(log n) 
	*/
	public Node deleteNode(Node root, int key, BooleanCombination cell){
		if (root == null)
			return root;

		//scorre l'albero 
		if (key < root.key)
			root.left = deleteNode(root.left, key, cell);
		else if (key > root.key)
			root.right = deleteNode(root.right, key, cell);

		// se la chiave corrisponde nodo trovato
		else {
			// se sono presenti più celle cerca la cella da eliminare e ritorna
			
			if (root.count > 1) {
				boolean trovato = false;
				int iter=0;
				while(!trovato && iter<root.ListNodi.size()){
					if(root.ListNodi.get(iter).i == cell.i && root.ListNodi.get(iter).j == cell.j){
						root.ListNodi.remove(cell);
						trovato=true;
					}else{
						iter++;
					}
				}
				(root.count)--;
				return root;
			}

			// altrimenti elimina il nodo e sistema i figli
			if ((root.left == null) || (root.right == null)) {
				Node temp = root.left != null ? root.left : root.right;

				// nodo senza figli
				if (temp == null) {
					temp = root;
					root = null;
				}
				else // nodo con un figlio
					root = temp;
			}
			else {
				// nodo con due figli: seleziona il nodo più piccolo nel sottoalbero destro
				Node temp = minValueNode(root.right);

				root.key = temp.key;
				root.count = temp.count;
				root.ListNodi=temp.ListNodi;
				temp.count = 1;

				root.right = deleteNode(root.right, temp.key, cell);
			}
		}

		// se l'albero aveva solo un nodo ritorna
		if (root == null)
			return root;

		// aggiorna l'altezza
		root.height = Math.max(height(root.left), height(root.right)) + 1;

		// controlla il fattore di bilanciamento del nodo e ribilancia
		int balance = getBalance(root);

		// Left Left Case
		if (balance > 1 && getBalance(root.left) >= 0)
			return rightRotate(root);

		// Left Right Case
		if (balance > 1 && getBalance(root.left) < 0) {
			root.left = leftRotate(root.left);
			return rightRotate(root);
		}

		// Right Right Case
		if (balance < -1 && getBalance(root.right) <= 0)
			return leftRotate(root);

		// Right Left Case
		if (balance < -1 && getBalance(root.right) > 0) {
			root.right = rightRotate(root.right);
			return leftRotate(root);
		}

		return root;
	}

	/*
		Inserisci nell'albero una nuova cella 
		O(log n)
	*/
	public void InsertTree(int key, BooleanCombination cell){
		tree = insertNode(tree, key, cell);
	}

	/*
		Elimina dall'albero una cella 
		O(log n)
	*/
	public void DeleteTree(int key, BooleanCombination cell){
		tree = deleteNode(tree, key, cell);
	}
	
	/*
		Data una lista di celle a cui è stato modificato il proximitycounter aggiorna l'albero 
		eliminando le vecchie celle e aggiungendo le nuove 
		O(c log n) dove c è la cardinalità di ListCell e n il numero di nodi
	*/
	public void updateTree(LinkedList<BooleanCombination> ListCell, int mod){
		// il primo elemento di ListCell è la casella che abbiamo markato/unmarkato precedentemente
		// in caso di markCell occorre eliminarla dall'albero
		// in caso di unmarkCell occorre reinserirla nell'albero
		BooleanCombination modCell = ListCell.getFirst();
		ListCell.removeFirst();
		if(mod == 1){
			DeleteTree(modCell.proximityCounter, modCell);
		} else{
			InsertTree(modCell.proximityCounter, modCell);
		}
		for(BooleanCombination d : ListCell){
			DeleteTree(d.proximityCounter-mod, d);
			InsertTree(d.proximityCounter, d);
		}
	}
	
	
}		