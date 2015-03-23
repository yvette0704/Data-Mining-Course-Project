package apriori;

/**
 * @author Yingda Huang (Lynda) ({@code yingda.huang@emory.edu})
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Apriori {
	private int minSup;
	private static List<Set<String>> fileTrans;
	
	public int getMinSup() {
		return minSup;
	}
	
	public void setMinSup(int minSup) {
		this.minSup = minSup;
	}
	
	/**
	 * @param args
	 */
	 public static void main(String[] args) throws IOException { 
		Apriori apriori = new Apriori();
		double threshold = 0.005;
		int minSupport;
		String srcFile = "datafile.dat";
		
		//Read from data file
		fileTrans = apriori.readFile(srcFile);
		
		long totalItem = 0;
		long totalTime = 0;
		
		//Set up the output file
		FileWriter outputWriter = new FileWriter("Output.txt");
		minSupport = (int)(fileTrans.size() * threshold);
		apriori.setMinSup(minSupport);
		long startTime = System.currentTimeMillis();
		
		//Get frequent 1-item
		Map<Set<String>, Integer> f1Map = apriori.findFP1Items(fileTrans);
		long endTime = System.currentTimeMillis();
		totalTime += endTime - startTime;

		/*Test
		 * for (Set<String> name: f1Map.keySet()){
        	String key =name.toString();
        	String value = f1Map.get(name).toString();  
        	System.out.println(key + " " + value);  
		} */
		// Print the frequent 1-item
		totalItem = apriori.printMap(f1Map, outputWriter);
		Map<Set<String>, Integer> result = f1Map;
		do {	
			startTime = System.currentTimeMillis();
			result = apriori.genNextKItem(result);
			endTime = System.currentTimeMillis();
			totalTime += endTime - startTime;
			totalItem += apriori.printMap(result, outputWriter);
			} while(result.size() != 0);
		outputWriter.close();
		System.out.println("Times in total:" + totalTime + "ms");
		System.out.println("There are " + totalItem + " frequent items");
		
	}

	 /*Generate k-item set from (k-1)-item*/
	private Map<Set<String>, Integer> genNextKItem(Map<Set<String>, Integer> preMap) {
		Map<Set<String>, Integer> result = new HashMap<Set<String>, Integer>();
		List<Set<String>> preSetArray = new ArrayList<Set<String>>();
		for(Map.Entry<Set<String>, Integer> preMapItem : preMap.entrySet()){
			preSetArray.add(preMapItem.getKey());
		}
		int preSetLength = preSetArray.size();
		for (int i = 0; i < preSetLength - 1; i++) {
			for (int j = i + 1; j < preSetLength; j++) {
				String[] strA1 = preSetArray.get(i).toArray(new String[0]);
				String[] strA2 = preSetArray.get(j).toArray(new String[0]);
				// check if this two k-1 items can be linked into a k-item　
				if (isCanLink(strA1, strA2)) { 
					Set<String> set = new TreeSet<String>();
					for (String str : strA1) {
						set.add(str);
					}
					set.add((String) strA2[strA2.length - 1]); // link two k-1 items together
					// Check whether this k-item need to be cut. If not, put it into k-item frequency item set.
					if (!isNeedCut(preMap, set)) {
						result.put(set, 0);
					}
				}
			}
		}
		return assertFP(result);
	}
	
	/**Check if this k-item set need to be cut.
	 * The condition is: all the subset of k-item need to be the frequency item map (preMap)
	 */
	private boolean isNeedCut(Map<Set<String>, Integer> preMap, Set<String> set) {
		// TODO Auto-generated method stub
		boolean flag = false;
		List<Set<String>> subSets = getSubSets(set);
		for(Set<String> subSet : subSets){
			if(!preMap.containsKey(subSet)){
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**Get the all subset of a k-item
	 */
	private List<Set<String>> getSubSets(Set<String> set) {
		// TODO Auto-generated method stub
		String[] setArray = set.toArray(new String[0]);
		List<Set<String>> result = new ArrayList<Set<String>>();
		for(int i = 0; i < setArray.length; i++){
			Set<String> subSet = new HashSet<String>();
			for(int j = 0; j < setArray.length; j++){
				if(j != i) subSet.add(setArray[j]);
			}
			result.add(subSet);
		}
		return result;
	}

	/**Assert the item is frequent item
	 */
	private Map<Set<String>, Integer> assertFP(
			Map<Set<String>, Integer> allKItem) {
		Map<Set<String>, Integer> result = new HashMap<Set<String>, Integer>();
		for(Set<String> kItem : allKItem.keySet()){
			for(Set<String> data : fileTrans){
				boolean flag = true;
				for(String str : kItem){
					if(!data.contains(str)){
						flag = false;
						break;
					}
				}
				if(flag) allKItem.put(kItem, allKItem.get(kItem) + 1);
			}
			if(allKItem.get(kItem) >= minSup) {
				result.put(kItem, allKItem.get(kItem));
			}
		}
		return result;
	}

	/**Check if two k-1 items can link, in the condition of only the last item is different
	 * */
	private boolean isCanLink(String[] strA1, String[] strA2) {
		boolean flag = true;
		if(strA1.length != strA2.length){
			return false;
		}else {
			for(int i = 0; i < strA1.length - 1; i++){
				if(!strA1[i].equals(strA2[i])){
					flag = false;
					break;
				}
			}
			if(strA1[strA1.length -1].equals(strA2[strA1.length -1])){
				flag = false;
			}
		}
		return flag;
	}

	/**Print out k-item list into the output file
	 * @param map - the printout frequent item list
	 * @param outputWriter 
	 * @return int - number of items
	 * @throws IOException 
	 */
	private int printMap(Map<Set<String>, Integer> map, FileWriter outputWriter) throws IOException {
		// TODO Auto-generated method stub
		for(Map.Entry<Set<String>, Integer> item : map.entrySet()){
			for(String p : item.getKey()){
				outputWriter.append(p + " ");
			}
			outputWriter.append(" (" + item.getValue() + ")\n");
		}
		//outputWriter.flush();
		return map.size();
	}
	
	/** Generate L1 Frequent Item
	 * @param List<Set<String>> - the list of all Transactions
	 * @return Map<String, Integer> - HashMap of L1 
	 */
	
	private Map<Set<String>, Integer> findFP1Items(List<Set<String>> wholeTrans) {
		Map<Set<String>, Integer> f1Map = new HashMap<Set<String>, Integer>();
		Map<String, Integer> itemCount = new HashMap<String, Integer>();
		//Count each item
		for(Set<String> ds : wholeTrans){
			for(String d : ds){
				if(itemCount.containsKey(d)){
					itemCount.put(d, itemCount.get(d) + 1);
				} else {
					itemCount.put(d, 1);
				}
			}
		}
		//Create the frequent item list
		for(Map.Entry<String, Integer> item : itemCount.entrySet()){
			if(item.getValue() >= minSup){
				Set<String> fs = new HashSet<String>();
				fs.add(item.getKey());
				f1Map.put(fs, item.getValue());
			}
		}
		return f1Map;
	}

	/**Read from the data file
	 * @param fileDir - file directory 
	 * @return List<Set<String>> - the list of all Transactions
	 * @throws IOException 
	 */
	
	private List<Set<String>> readFile(String fileDir) {
		
		List<Set<String>> records = new ArrayList<Set<String>>(); 
        try { 
            FileReader fr = new FileReader(new File(fileDir)); 
            @SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(fr); 
       
            String line = null; 
            while ((line = br.readLine()) != null) { 
                if (line.trim() != "") { 
                    Set<String> record = new HashSet<String>(); 
                    String[] items = line.split(" "); // Item in each transaction is split by " "
                    for (String item : items) { 
                        record.add(item); 
                    } 
                    records.add(record); 
                } 
            } 
        } catch (IOException e) { 
            System.out.println("Fail to read from the data file."); 
            System.exit(-2); 
        } 
        return records; 
	}
}
