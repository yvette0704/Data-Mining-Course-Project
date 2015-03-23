package c45;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class DecisionTree {
	
	private DecisionTreeNode root = new DecisionTreeNode();;
	/** List of all predicted attribute*/
	private String[] allAttr;
	
	/** Training Data Array including all tuples*/
	private Object[] trainingArray;

	/** Index of class attribute (the attribute containing class of each record)*/
	private int nodeIndex;
	
	/** the classified class attribute*/
	private String[] classValue;


	/** @return root*/
	public DecisionTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Constructor: Initialize the C4.5 Decision Tree
	 * @param array - training data in each tuple
	 * @param index - the index number of class attribute
	 */
	public DecisionTree(Object[] array, int index) {
		this.trainingArray = array;
		int length= ((String[]) array[0]).length;

		this.allAttr = new String[length-1];
		int j = 0;
		for( int i = 0 ; i < length; i++)
		{
			if( i != index)
				this.allAttr[j++] = String.valueOf(i);
		}
		
		/**Initialize the "toCompute": mark the class attribute as TRUE*/
		classValue = getAttrValue(nodeIndex);
		createDecisionTree(this.trainingArray);
		//printDecisionTree(root);
	}
	
/********************* Construct Decision Tree **************************/	

	/**
	 * initialize the root node and create the whole tree
	 * 
	 * @param array - training data
	 */
	public void createDecisionTree(Object[] array) {
		
		// Compute Max Gain Ratio
		Object[] maxGain = getMaxGainRatio(array,this.allAttr);
		
		root.parentNode = null;
		root.attrValue = getAttrValue(((Integer) maxGain[1]).intValue());
		root.nodeAttr = String.valueOf(((Integer) maxGain[1]).intValue());
		root.childrenNodes = new DecisionTreeNode[root.attrValue.length];
		root.attrList = getAttrList(((Integer) maxGain[1]).intValue(),root);
		//Create whole tree
		branchDecisionTree(array, root);
		}

	
	/**
	 * Create following Decision Tree
	 * 
	 * @param array - training data
	 * @param parentNode
	 */
	public void branchDecisionTree(Object[] array, DecisionTreeNode parentNode) {
		String[] arrtibutes = parentNode.attrValue;
		
		// New Decision Tree is a branch from each possible value of parent node attribute
		for (int i = 0; i < arrtibutes.length; i++) {
			
			/*
			 * Derive sub array for each branch from parent node
			 * attributes[i] - the parent attribute value for this branch
			 * Integer.parseInt(parentNode.nodeAttr) - parent attribute index
			 */
			Object[] subArray = createSubArray(array, arrtibutes[i],
					Integer.parseInt(parentNode.nodeAttr));
			
			// Get the split and Max Gain Ratio for the sub array
			Object[] info = getMaxGainRatio(subArray,parentNode.attrList);
			
			double gainRatio = ((Double) info[0]).doubleValue();//Max Gain Ratio
			if (gainRatio != 0) {
				int index = ((Integer) info[1]).intValue();//Selected Attribute Index
				DecisionTreeNode currentNode = new DecisionTreeNode();
				currentNode.parentNode = parentNode;
				currentNode.parentAttr = arrtibutes[i];
				currentNode.attrValue = getAttrValue(index);
				currentNode.attrList = getAttrList(index,currentNode);
				currentNode.nodeAttr = String.valueOf(index);
				currentNode.childrenNodes = new DecisionTreeNode[currentNode.attrValue.length];
			    System.out.println(currentNode.parentNode.nodeAttr +"  - " +currentNode.parentAttr+ "-  "+currentNode.nodeAttr);
				parentNode.childrenNodes[i] = currentNode;
				
				branchDecisionTree(subArray, currentNode);
			} else {
				DecisionTreeNode leafNode = new DecisionTreeNode();
				
				leafNode.parentNode = parentNode;
				leafNode.parentAttr = arrtibutes[i];
				leafNode.attrValue = null;
				leafNode.attrList = null;
				leafNode.nodeAttr = getLeafNodeValue(subArray);
				leafNode.childrenNodes = null;
			    System.out.println(leafNode.parentNode.nodeAttr + "  - " +leafNode.parentAttr+ "-  "+ leafNode.nodeAttr);
				parentNode.childrenNodes[i] = leafNode;
			}
		}
	}

	/**
	 * Create sub-array for each branch
	 *	 
	 * @param array - all training data at this level
	 * @param arrtibute - the parent attribute value for this branch
	 * @param index - parent attribute index 
	 * @return Object[]
	 */
	public Object[] createSubArray(Object[] array, String arrtibute,
			int index) {
		ArrayList<String[]> list = new ArrayList<String[]>();
		for (int i = 0; i < array.length; i++) {
			String[] strs = (String[]) array[i];
			if (strs[index].equals(arrtibute)) {
				list.add(strs);
			}
		}
		return list.toArray();
	}

/**
 * Get the possible attribute list for its children
 * 
 * @param index - the index of attribute selected for this node
 * @param currentNode - currentNode
 * @return
 */
public String[] getAttrList(int index, DecisionTreeNode currentNode){
	List<String> a;
	if(currentNode.parentNode != null){
		String[] parentList = currentNode.parentNode.attrList;
		a = Arrays.asList(parentList);
	}
	else {
		a = Arrays.asList(allAttr);
	}
	Iterator<String> i = a.iterator();
	while(i != null)
	{
		if(i.equals(String.valueOf(index)))
			i.remove();		
		if(i.hasNext())
			i.next();
		else 
			break;
	}
	String[] currentList = (String[]) a.toArray();
	return currentList;
}

	/**
	 * Get possible value in this attribute
	 * 
	 *@param index - attribute's index
	 * @return String[] - the value set of the attribute
	 */
	public String[] getAttrValue(int index) {
	
		/** Construct an sorted tree set to store the attribute's value*/
		TreeSet<String> set = new TreeSet<String>(new Comparator<Object>(){
			@Override
			public int compare(Object obj1, Object obj2) {
				String str1 = (String) obj1;
				String str2 = (String) obj2;
				return str1.compareTo(str2);
			}
		});
		for (int i = 0; i < trainingArray.length; i++) {
			String[] strs = (String[]) trainingArray[i];
			set.add(strs[index]);
		}
		String[] result = new String[set.size()];
	
		return set.toArray(result);
	}


	/**
	 * @param array - training data
	 * @return String - node value for leaf
	 */
	public String getLeafNodeValue(Object[] array) {
		if (array != null && array.length > 0) {
			String[] strs = (String[]) array[0];
			return strs[this.nodeIndex];
		}
		return null;
	}


/***********************2. Get Max Gain Ratio ****************************/	


	/**
	 * Max Gain Ratio and the Selected Attribute to split
	 * 
	 * @param array - training data
	 * @return Object[] - return the (maxGainRatio, index of selected attribute)
	 */
	public Object[] getMaxGainRatio(Object[] array, String[] attrList) {
		Object[] result = new Object[2];
		double gain = 0;
		int index = -1; // The selected attribute with max gain ratio
		double value;
		
		for (int i = 0; i < attrList.length; i++) {
		    // Compute all possible attribute
			value = gainRatio(array, Integer.parseInt(attrList[i]), this.nodeIndex);
				if (gain < value) {
					gain = value;
					index = Integer.parseInt(attrList[i]);
			}
		}
		result[0] = gain;
		result[1] = index;
		
		return result;
	}

	/**
	 * Compute Gain Ratio_A(D) = Gain(A)/splitInfo_A(D)
	 * 
	 * @param array - training data
	 * @param index - the index of the attribute to compute
	 * @param nodeIndex - the class attribute
	 * @return GainRatio(A)
	 */
	public double gainRatio(Object[] array, int index, int nodeIndex) {
		
		/** Gain(A) */
		double gain = gain(array, index, nodeIndex);
		int[] counts = countEachClass(array, index);
		
		/** splitInfo_A(D) */
		double splitInfo = splitInfo_A(array, counts);
		
		/** Gain Ratio_A(D) */
		double gainRatio = 0;
		if (splitInfo != 0) 
			gainRatio = gain / splitInfo;
		
		return gainRatio;
	}

	/**
	 * Gain(A)
	 * 
	 * @param array - training data
	 * @param index - the index of the attribute to compute
	 * @param nodeIndex - the class attribute
	 * @return Gain(A)
	 */
	public double gain(Object[] array, int index, int nodeIndex) {
		int[] counts = countEachClass(array, nodeIndex);
		String[] attributes = getAttrValue(index);
		double gain = 0;
		double infoD = info_D(array, counts, true);
		double infoaD = infoA_D(array, index, nodeIndex, attributes);
		gain = infoD - infoaD;
		return gain;
	}

	/**
	 * @param array - training data
	 * @param nodeIndex
	 * @return pi - the counts of each class
	 */
	public int[] countEachClass(Object[] array, int nodeIndex) {
		int[] counts = new int[classValue.length];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = 0;
		}
		for (int i = 0; i < array.length; i++) {
			String[] strs = (String[]) array[i];
			for (int j = 0; j < classValue.length; j++) {
				if (strs[nodeIndex].equals(classValue[j])) {
					counts[j]++;
				}
			}
		}
		return counts;
	}

	/**
	 * infoD = -sum(pi*log2 pi)
	 * 
	 * @param array - training data
	 * @param counts - counts for each class
	 * @return
	 */
	public double info_D(Object[] array, int[] counts, boolean entropyS) {
		double pi = 0;
		double infoD = 0;
		for (int i = 0; i < counts.length; i++) {
			pi = calculate_entropy(counts[i], array.length);
			infoD += pi;
		}
		return (-infoD);
	}

	/**
	 * Info_A(D) = sum(|Dj| / |D|) * info(Dj)
	 * 
	 * @param array - training data
	 * @param index - current j
	 * @param attributes - array of classified value in this attribute
	 * 				     - attribute [j] correspond to Dj
	 * @return
	 */
	public double infoA_D(Object[] array, int index, int nodeIndex, String[] attributes) {
		double sum = 0;
		double Dj = 0;
		for (int i = 0; i < attributes.length; i++) {
			Dj = info_Dj(array, index, nodeIndex, attributes[i],array.length);
			sum += Dj;
		}
		return sum;
	}

	/**
	 * ((|Dj| / |D|) * Info(Dj))
	 * 
	 * @param array - training data
	 * @param index - current j
	 * @param arrtibute - attribute value for Dj
	 * @param allTotal - amount of training data
	 * @return double
	 */
	public double info_Dj(Object[] array, int index, int nodeIndex, String attribute, int allTotal) {
		int[] counts = new int[classValue.length];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = 0;
		}
		// Counts according to class in each Dj
		for (int i = 0; i < array.length; i++) {
			String[] strs = (String[]) array[i];
			if (strs[index].equals(attribute)) {
				for (int k = 0; k < classValue.length; k++) {
					if (strs[nodeIndex].equals(classValue[k])) {
						counts[k]++;
					}
				}
			}
		}
		
		int total = 0;
		for(int k = 0; k < counts.length; k++)
			total += counts[k];
		
		double infoDj = 0;
		double classi = 0;
		
		for (int i = 0; i < counts.length; i++) {
			classi = calculate_entropy(counts[i], total);
			infoDj += classi;
		}
		double result = getPi(total, allTotal) * (-infoDj);
		return result;
	}

	/**
	 * splitE_A = -sum（i=1-k）
	 * 
	 * @param array
	 * @param counts
	 * @return
	 */
	public double splitInfo_A(Object[] array, int[] counts) {
		return info_D(array, counts, false);
	}

	/**
	 * pi*log（2）pi
	 * 
	 * @param x - count the tuple in this class
	 * @param total - total amount of training data
	 * @return double - the entropy for this class
	 */
	public double calculate_entropy(int x, int total) {
		double info = 0;
		if (x == 0) {
			return 0;
		}
		double x_pi = getPi(x, total);
		info = (x_pi * logBase2(x_pi));
		return info;
	}

	/**
	 * log_2(X)
	 * 
	 * @param x
	 * @return double
	 */
	public static double logBase2(double x) {
		double log2 = Math.log(x) / Math.log(2);
		return log2;
	}

	/**
	 * pi=|C(i,d)|/|D|
	 * 
	 * @param x - count the tuple in this class
	 * @param total - total amount of training data
	 * @return pi
	 */
	public static double getPi(int x, int total) {
		double pi = x / (double) total;
		return pi; 
	}

}
