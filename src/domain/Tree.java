package domain;

public class Tree {
    Node root;
    
    public Tree() {
    	root = new Node();
    }

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}  
   
}