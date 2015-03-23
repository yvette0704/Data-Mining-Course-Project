package c45;

public class DecisionTreeNode {
	/** Node key*/
	String nodeAttr; // use the attribute index, as each attribute only appear in a node
	
	/** Classified value for this attribute */
	String[] attrValue;
	
	/** Possible attribute list for its children*/
	String[] attrList;
	
	/** Parent */
	String parentAttr; //parent attribute value
	DecisionTreeNode parentNode;
	
	/** Child */
	DecisionTreeNode[] childrenNodes;
	
	
}
