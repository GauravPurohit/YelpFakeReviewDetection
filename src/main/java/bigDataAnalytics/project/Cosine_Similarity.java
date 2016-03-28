
package IDS561bigDataAnalytics.project;

import java.io.BufferedWriter;
import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

// IDS 561 Big data project- calculation of cosine similarity file 
public class Cosine_Similarity {
	public class values 
	{
		int values1;
		int values2;

		values(int v1, int v2) 
		{
			this.values1 = v1;
			this.values2 = v2;
		}

		public void Update_Values(int v1, int v2) 
		{
			this.values1 = v1;
			this.values2 = v2;
		}
	}

    
	

	public double Cosine_Similarity_Score(String Text1, String Text2) {
		double similarity_score = 0.0;
		
		String[] text1WordSequence = Text1.split(" ");
		String[] text2WordSequence = Text2.split(" ");
		Hashtable<String, values> wordFrequencyVector = new Hashtable<String, Cosine_Similarity.values>();
		LinkedList<String> distinctWordsFromBothTexts = new LinkedList<String>();

		// Word frequency vector preparation for Text1 
		for (int i = 0; i < text1WordSequence.length; i++) {
			String text1WordWithoutWhiteSpace = text1WordSequence[i].trim();
			if (text1WordWithoutWhiteSpace.length() > 0) {
				if (wordFrequencyVector.containsKey(text1WordWithoutWhiteSpace)) {
					values vals1 = wordFrequencyVector.get(text1WordWithoutWhiteSpace);
					int frequency1 = vals1.values1 + 1;
					int frequency2 = vals1.values2;
					vals1.Update_Values(frequency1, frequency2);
					wordFrequencyVector.put(text1WordWithoutWhiteSpace, vals1);
				} else {
					values vals1 = new values(1, 0);
					wordFrequencyVector.put(text1WordWithoutWhiteSpace, vals1);
					distinctWordsFromBothTexts.add(text1WordWithoutWhiteSpace);
				}
			}
		}

		// Word frequency vector preparation for Text2
		for (int i = 0; i < text2WordSequence.length; i++) {
			String text2WordWithoutWhiteSpace = text2WordSequence[i].trim();
			if (text2WordWithoutWhiteSpace.length() > 0) {
				if (wordFrequencyVector.containsKey(text2WordWithoutWhiteSpace)) {
					values vals1 = wordFrequencyVector.get(text2WordWithoutWhiteSpace);
					int frequency1 = vals1.values1;
					int frequency2 = vals1.values2 + 1;
					vals1.Update_Values(frequency1, frequency2);
					wordFrequencyVector.put(text2WordWithoutWhiteSpace, vals1);
				} else {
					values vals1 = new values(0, 1);
					wordFrequencyVector.put(text2WordWithoutWhiteSpace, vals1);
					distinctWordsFromBothTexts.add(text2WordWithoutWhiteSpace);
				}
			}
		}

		// Cosine Similarity score calculation.
		double VectorAB = 0.0;
		double VectorA_Sq = 0.0;
		double VectorB_Sq = 0.0;

		for (int i = 0; i < distinctWordsFromBothTexts.size(); i++) {
			values vals12 = wordFrequencyVector
					.get(distinctWordsFromBothTexts.get(i));

			double frequency1 = (double) vals12.values1;
			double frequency2 = (double) vals12.values2;

			VectorAB = VectorAB + (frequency1 * frequency2);

			VectorA_Sq = VectorA_Sq + frequency1 * frequency1;
			VectorB_Sq = VectorB_Sq + frequency2 * frequency2;
		}

		similarity_score = ((VectorAB) / (Math.sqrt(VectorA_Sq) * Math.sqrt(VectorB_Sq)));

		return (similarity_score);
	}

	public static void WriteToFile(String outputPath, double[] ratingSimilarity,
			double[] cosineSimilarity , String[] business_id , String[] user_id ) throws IOException 
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
		if (ratingSimilarity.length == cosineSimilarity.length) 
		{
			for (int i = 0; i < ratingSimilarity.length; i++) 
			{
				bw.write(ratingSimilarity[i] + "," + cosineSimilarity[i] + "," + business_id[i] + "," + user_id[i] + "," + "\n");		
			}
		}	
		bw.close();
	}

	
	public static void main(String[] args) throws IOException {

		String[] allFolders = GetDir.ProvideNamesOfFolders("D:\\Yelp\\reviews_dump\\"); 																			
		int size = allFolders.length;
		for (int num = 0; num < size; num++) {

			String[] allFilesInAFolder = GetDir.ProvideNamesOfFolders(allFolders[num]); 	
			String filePath = "D:\\Yelp\\ratings_dump\\"
					+ allFolders[num].substring(21, allFolders[num].length())
					+ ".txt";
			File filesize = new File("D:\\Yelp\\reviews_dump\\"
					+ allFolders[num].substring(21, allFolders[num].length()));
			
			System.out.print(filePath);
			
			File file = new File(filePath);
			double[] ratings = new double[100];
			long dirSize = FileUtils.sizeOfDirectory(filesize);
			
			if(file.exists() && ((dirSize/1024)<999))
		{		
		    ratings = Rating_Similarity.CalculateRatingSimilarity(filePath);

			int folderFileCount = allFilesInAFolder.length;

			String[] inFile = new String[folderFileCount];

			for (int num2 = 1; num2 < folderFileCount + 1; num2++) {
				String text = allFilesInAFolder[num2 - 1];
				byte[] encoded = Files.readAllBytes(Paths.get(text));
				inFile[num2 - 1] = new String(encoded);
			}

			Cosine_Similarity cs1 = new Cosine_Similarity();
			HashMap<keyPair, Double> CosineSimsMap = new HashMap<keyPair, Double>();
			for (int i = 0; i < folderFileCount - 1; i++) {
				for (int j = i + 1; j < folderFileCount; j++) {
					double similarity_score = cs1.Cosine_Similarity_Score(
							inFile[i].toString(), inFile[j].toString());
					keyPair temp = new keyPair(i, j);
					CosineSimsMap.put(temp, similarity_score);
				}
			}

			double sumOfCosineSimilarityScores = 0;
			double[] avgCosineSimilarityScore = new double[folderFileCount];
			String[] business_id = new String[folderFileCount]; 
			String[] user_id = new String[folderFileCount];
			
			for (int i = 0; i < folderFileCount; i++) {
				for (int j = 0; j < folderFileCount; j++) {
					if (i != j) {
						keyPair similarityScore = new keyPair(i, j);
						sumOfCosineSimilarityScores += CosineSimsMap.get(similarityScore);
					}
				}
				avgCosineSimilarityScore[i] = sumOfCosineSimilarityScores / (folderFileCount - 1);
				System.out.println(" User: " + i +" >>"+ " " + allFilesInAFolder[i].toString() + " | Review Similarity: "
						+ avgCosineSimilarityScore[i] + '\n');
				
				business_id[i] = allFilesInAFolder[i].toString().substring(allFilesInAFolder[i].toString().lastIndexOf("\\")+1, allFilesInAFolder[i].toString().indexOf("^"));
				user_id[i]= allFilesInAFolder[i].toString().substring(allFilesInAFolder[i].toString().indexOf("^")+1,allFilesInAFolder[i].toString().length()-4);
				user_id[i]=user_id[i].replaceAll("!", "");
				
				sumOfCosineSimilarityScores = 0;		
			    	
				
			}
		
			System.out.println(inFile);
			WriteToFile("D:\\Yelp\\output.csv", ratings, avgCosineSimilarityScore, business_id, user_id);

		  }
		}
	}
	

}
