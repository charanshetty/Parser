package edu.csl.prs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.LinkedHashMap;
import java.util.List;

import java.util.Random;

/**
 * This class generates and writes the polytopes into the files
 *
 */
public class TextFeed {
	
	//Constants
	private static final String EXTENSION = "txt";
	
	private static final String DOT = ".";
	
	private static final String UNDERSCORE = "_";
	
	private static final String FILENAME_PREFIX = "points";
	
	private static final String FILEPATH = "polytopes/";

	/**
	 * Generates and returns random sequence of values
	 * 
	 * @param min
	 * @param max
	 * @return int
	 * @throws IOException
	 */

	public int genRandomSeqs(int min, int max) throws IOException {

		Random rand = new Random();

		int randNum = rand.nextInt((max - min) + 1) + min;
		// System.out.println("number  "+ randNum);
		return randNum;
	}

	// Returns the date
	public String getDate(LinkedHashMap<String, List<ExchRate>> map, int day) {
		int itr = 0;

		for (String key : map.keySet()) {
			if (itr == day) {
				return key;
			}

			itr++;
		}
		return null;
	}

	/**
	 * Generate randomly distributed polytopes and writes them to the respective
	 * files.
	 * 
	 * @param map
	 * @param dim
	 * @throws IOException
	 */
	public void feedGenerate(LinkedHashMap<String, List<ExchRate>> map, int dim)
			throws IOException {
		String startDate = null;
		String endDate = null;
		int begin, end;
		begin = genRandomSeqs(0, map.size() - 1);
		end = genRandomSeqs(begin, map.size() - 1);

		startDate = getDate(map, begin);
		endDate = getDate(map, end);
		// System.out.println(startDate);
		// System.out.println(endDate);

		BufferedWriter writer = new BufferedWriter(new FileWriter(FILEPATH + FILENAME_PREFIX + UNDERSCORE
				+ startDate + UNDERSCORE + endDate + DOT + EXTENSION));

		int recNum = 0;
		// System.out.println(end - begin);
		writer = writeTemplate(writer, dim, begin, end);

		for (String key : map.keySet()) {
			if ((recNum >= begin) && (recNum <= end)) {

				List<ExchRate> values = map.get(key);

				for (int i = 0; i < values.size(); i++)
					writer.write(values.get(i).getValue().toString().trim()
							+ "  ");
				writer.write("\n");

			}
			recNum++;

		}
		writer.close();
	}

	// Initial default template for the file
	private BufferedWriter writeTemplate(BufferedWriter writer, int dim,
			int begin, int end) throws IOException {
		writer.write(dim + " RBOX C");
		writer.write("\n");
		writer.write(Integer.toString(end - begin + 1));
		writer.write("\n");
		return writer;
	}

	/**
	 * Generates and writes uniformly distributed polytopes to their respective
	 * files.
	 * 
	 * @param map
	 * @param dim
	 * @param nthptope
	 * @param period
	 * @param polytopes
	 * @throws IOException
	 */
	public void feedGenerateEqual(LinkedHashMap<String, List<ExchRate>> map,
			int dim, int nthptope, int period, int polytopes)
			throws IOException {
		// generate fixed ratio floor(365/100)

		String startDate = null;
		String endDate = null;
		int begin, end;

		begin = nthptope * period;
		if (nthptope != polytopes - 1)
			end = (nthptope + 1) * period - 1;
		else {
			end = map.size() - 1;
		}
		startDate = getDate(map, begin);
		endDate = getDate(map, end);
		// System.out.println(startDate);
		// System.out.println(endDate);

		BufferedWriter writer = new BufferedWriter(new FileWriter(FILEPATH + FILENAME_PREFIX + UNDERSCORE
				+ startDate + UNDERSCORE + endDate + DOT + EXTENSION));

		int recNum = 0;
		// System.out.println(end - begin);
		writer = writeTemplate(writer, dim, begin, end);

		for (String key : map.keySet()) {
			if ((recNum >= begin) && (recNum <= end)) {

				List<ExchRate> values = map.get(key);

				for (int i = 0; i < values.size(); i++)
					writer.write(values.get(i).getValue().toString().trim()
							+ "  ");
				writer.write("\n");

			}
			recNum++;

		}
		writer.close();

	}
}