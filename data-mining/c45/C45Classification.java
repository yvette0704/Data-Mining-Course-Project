package c45;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class C45Classification {
	
	public static void main(String[] args) throws IOException{
		String srcFile = "datafile.dat";
		String testFile = "testfile.dat";
		String outputFile = "Output.text";
		C45(srcFile,testFile,outputFile);
	} 
	
	public static void C45(String srcFile,String testFile,String outputFile) throws IOException{

		//Read from data file
		Object[] trainArray = readFile(srcFile);
		Object[] testArray = readFile(testFile);
	
		//Set up the output file
		FileWriter outputWriter = new FileWriter(outputFile);
		
		long startTime = System.currentTimeMillis();
		DecisionTree tree = new DecisionTree(trainArray,0);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		
		Test t = new Test(tree,testArray);
		outputWriter.append("The accuracy for this prediction is " + t.accuracy*100 + "%.\n");
		outputWriter.append("The total time for deriving the decision tree is " + totalTime + "ms.\n");
		for(int i = 0; i < t.testArray.length;i++){
			for(int j = 0; j < ((String[])t.testArray[i]).length; j++){
				outputWriter.append(((String[])t.testArray[i])[j]+ "\t");
			}
			outputWriter.append(t.predictResult[i] + "\n");
		}
		outputWriter.close();
		
	}
	
	public static Object[] readFile(String fileDir) {
		
		List<Object> records = new ArrayList<Object>(); 
        try { 
            FileReader fr = new FileReader(new File(fileDir)); 
            @SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(fr); 
       
            String line = null; 
            while ((line = br.readLine()) != null) { 
                if (line.trim() != "") { 
                    String[] items = line.split("	");
                    records.add(items); 
                } 
            } 
        } catch (IOException e) { 
            System.out.println("Fail to read from the data file."); 
            System.exit(-2); 
        } 
        Object[] result = records.toArray(new Object[] {});
        return result; 
	}
	
	
}
