//Compute Cosine Similarity between different review texts

package bigDataAnalytics.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;

//Compare similarity between two text files

public class Cosine_Similarity {
	public class values {
		int val1;
		int val2;

		values(int v1, int v2) {
			this.val1 = v1;
			this.val2 = v2;
		}

		public void Update_VAl(int v1, int v2) {
			this.val1 = v1;
			this.val2 = v2;
		}
	}// end of class values

    
	

	public double Cosine_Similarity_Score(String Text1, String Text2) {
		double sim_score = 0.0000000;
		// 1. Identify distinct words from both documents
		String[] word_seq_text1 = Text1.split(" ");
		String[] word_seq_text2 = Text2.split(" ");
		Hashtable<String, values> word_freq_vector = new Hashtable<String, Cosine_Similarity.values>();
		LinkedList<String> Distinct_words_text_1_2 = new LinkedList<String>();

		// prepare word frequency vector by using Text1
		for (int i = 0; i < word_seq_text1.length; i++) {
			String tmp_wd = word_seq_text1[i].trim();
			if (tmp_wd.length() > 0) {
				if (word_freq_vector.containsKey(tmp_wd)) {
					values vals1 = word_freq_vector.get(tmp_wd);
					int freq1 = vals1.val1 + 1;
					int freq2 = vals1.val2;
					vals1.Update_VAl(freq1, freq2);
					word_freq_vector.put(tmp_wd, vals1);
				} else {
					values vals1 = new values(1, 0);
					word_freq_vector.put(tmp_wd, vals1);
					Distinct_words_text_1_2.add(tmp_wd);
				}
			}
		}

		// prepare word frequency vector by using Text2
		for (int i = 0; i < word_seq_text2.length; i++) {
			String tmp_wd = word_seq_text2[i].trim();
			if (tmp_wd.length() > 0) {
				if (word_freq_vector.containsKey(tmp_wd)) {
					values vals1 = word_freq_vector.get(tmp_wd);
					int freq1 = vals1.val1;
					int freq2 = vals1.val2 + 1;
					vals1.Update_VAl(freq1, freq2);
					word_freq_vector.put(tmp_wd, vals1);
				} else {
					values vals1 = new values(0, 1);
					word_freq_vector.put(tmp_wd, vals1);
					Distinct_words_text_1_2.add(tmp_wd);
				}
			}
		}

		// calculate the cosine similarity score.
		double VectAB = 0.0000000;
		double VectA_Sq = 0.0000000;
		double VectB_Sq = 0.0000000;

		for (int i = 0; i < Distinct_words_text_1_2.size(); i++) {
			values vals12 = word_freq_vector
					.get(Distinct_words_text_1_2.get(i));

			double freq1 = (double) vals12.val1;
			double freq2 = (double) vals12.val2;

			VectAB = VectAB + (freq1 * freq2);

			VectA_Sq = VectA_Sq + freq1 * freq1;
			VectB_Sq = VectB_Sq + freq2 * freq2;
		}

		sim_score = ((VectAB) / (Math.sqrt(VectA_Sq) * Math.sqrt(VectB_Sq)));

		return (sim_score);
	}

	public static void WriteToOneFile(String outputPath, double[] ratingSims,
			double[] cosineSims , String[] csvString) throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
		if (ratingSims.length == cosineSims.length) {
			for (int i = 0; i < ratingSims.length; i++) {
				bw.write(ratingSims[i] + "," + cosineSims[i] + "," + csvString[i] + "," + "\n");
				// System.out.println(i + ":" + ratingSims[i] + "," +
				// cosineSims[i] + "\n");
			}
		}
		bw.close();
	}

	// ------------------------------------------
	// Main Function
	// ------------------------------------------
	public static void main(String[] args) throws IOException {

	
		String[] allDirs = GetDir
				.ReturnFolderNames("C:\\Users\\e22789\\Desktop\\yelp\\reviews_dump\\reviews_dump\\"); // Get
																				// review
																				// texts
																				// directory

		int size = allDirs.length;
		for (int num = 0; num < size; num++) {

			
			String[] allFiles = GetDir.ReturnFolderNames(allDirs[num]); // return
																		// text
																		// folder
																		// names
			
			
			String filePath = "C:\\Users\\e22789\\Desktop\\yelp\\ratings_dump\\"
					+ allDirs[num].substring(55, allDirs[num].length())
					+ ".txt";
			File filesize = new File("C:\\Users\\e22789\\Desktop\\yelp\\reviews_dump\\reviews_dump\\"
					+ allDirs[num].substring(55, allDirs[num].length()));
			
			/*if(filePath.contains("Inc."))
            filePath = filePath.replaceAll("._","_");*/
			System.out.print(filePath);
			
			File file = new File(filePath);
			double[] ratings = new double[100];
			long dirSize = FileUtils.sizeOfDirectory(filesize);
			
			if(file.exists() && ((dirSize/1024)<999))
		{		
		    ratings = Rating_Similarity.CalRateSim(filePath);

			int numFile = allFiles.length;

			String[] inFile = new String[numFile];

			for (int num2 = 1; num2 < numFile + 1; num2++) {
				String text = allFiles[num2 - 1];
				byte[] encoded = Files.readAllBytes(Paths.get(text));
				inFile[num2 - 1] = new String(encoded);
			}

			Cosine_Similarity cs1 = new Cosine_Similarity();
			// Use Hash Map to store the data
			HashMap<keyPair, Double> CosineSims = new HashMap<keyPair, Double>();
			for (int i = 0; i < numFile - 1; i++) {
				for (int j = i + 1; j < numFile; j++) {
					double sim_score = cs1.Cosine_Similarity_Score(
							inFile[i].toString(), inFile[j].toString());
					keyPair temp = new keyPair(i, j);
					CosineSims.put(temp, sim_score);
				}
			}

			double sumCosineSims = 0;
			double[] avgCosineSims = new double[numFile];
			String[] csvString = new String[numFile]; 
			
			for (int i = 0; i < numFile; i++) {
				for (int j = 0; j < numFile; j++) {
					if (i != j) {
						keyPair sim = new keyPair(i, j);
						sumCosineSims += CosineSims.get(sim);
					}
				}
				avgCosineSims[i] = sumCosineSims / (numFile - 1);
				System.out.println(" User: " + i +" >>"+ " " + allFiles[i].toString() + " | Review Similarity: "
						+ avgCosineSims[i] + '\n');
				
				csvString[i] = " User: " + i +" >>"+ " " + allFiles[i].toString() + " | Review Similarity: "
						+ avgCosineSims[i];
				sumCosineSims = 0;		
			    
				
				
				
			}
			// Write our results to one file
			// Output is 2D variables of rating similarity and cosine(text)
			// similarity
			System.out.println(inFile);
			WriteToOneFile("C:\\Users\\e22789\\Desktop\\yelp\\output.csv", ratings, avgCosineSims, csvString);

		  }
		}
	}
	

}
