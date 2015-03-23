package c45;
/**
 * @author Yingda Huang (Lynda) ({@code yingda.huang@emory.edu})
 */
public class Test {

	DecisionTree tree;
	Object[] testArray;
	String[] predictResult;
	double accuracy;
	
	/**
	 * Constructor
	 * @param tree - the derived decision tree
	 * @param array - the test date
	 */
	public Test(DecisionTree tree, Object[] array){
		this.tree = tree;
		this.testArray = array;
		predictResult =  new String[testArray.length];
		output();
		this.accuracy = accuracyTest();
	}
	
	/**
	 * Gain the predict for every tuple
	 * 
	 * @return result
	 */
	public void output(){
		for(int i = 0; i < testArray.length; i++){
			predictResult[i] = predict((String[])testArray[i]);
		}
	}
	
	/**
	 * Predict class value
	 * 
	 * @param (String) tuple
	 * @return the predicted class value
	 */
	public String predict(String[] tuple){
		DecisionTreeNode node = tree.getRoot();
		int index;
		DecisionTreeNode[] children;
		do{
			index = Integer.parseInt(node.nodeAttr);
			children = node.childrenNodes;
			for(int i = 0; i <  children.length ; i++){
					if( children[i].parentAttr.equals(tuple[index])){
						node = children[i];
						break;
					}
			}
		}while(node.childrenNodes != null);
		
		return node.nodeAttr;	
	}
	
	public double accuracyTest(){
		int matched = 0;
		for(int i = 0; i < testArray.length; i++){
			if(((String[])(testArray[i]))[0].equals(predictResult[i])){
				matched ++;
			}
				
		}
		return (double)matched/testArray.length;
	}
}

