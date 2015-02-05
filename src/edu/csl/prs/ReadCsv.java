package edu.csl.prs;

import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import edu.csl.enumeration.ExchangeRateCurrencyEnum;

/**
 * The main class to initiate the parsing of the ExchangeRate data and convert
 * into polytopes.
 *
 */
public class ReadCsv {
	// read csv file method
	// get random columns

	// Resource bundle containing configurable values
	private ResourceBundle rb = ResourceBundle.getBundle(this.getClass()
			.getName());

	// Constants
	private static final String DEFAULT_START_DATE = "startDate";

	private static final String DEFAULT_END_DATE = "endDate";

	private static final String FILE_PATH = "filepath";

	private LinkedHashMap<String, List<ExchRate>> parseCsvFileLineByLine(
			String filePath, LinkedHashMap<String, List<ExchRate>> map,
			int dimension, int skipLines, String[] countries, int defDim,
			String startDate, String endDate) throws IOException,
			ParseException {
		java.util.Date BDate = getDates(startDate);
		java.util.Date EDate = getDates(endDate);

		// initialize variables
		String[] record = null;
		int index = 0;
		String Date = null;
		// create CSVReader object

		CSVReader reader = new CSVReader(new FileReader(filePath), ',',
				CSVParser.DEFAULT_QUOTE_CHARACTER, skipLines);
		List<ExchRate> PrevRates = new ArrayList<ExchRate>();
		PrevRates = intializetoZero(PrevRates, dimension); // initialize to 0
		boolean genCols = false;
		try {
			// skip header row
			record = reader.readNext();
			int rand[] = new int[dimension];
			rand = getCountryCols(countries, rand, record, defDim);
			// read line by line
			while (((record = reader.readNext()) != null)
					&& (!record[0].isEmpty())) {
				index = 0;
				if (((getDates(record[index]).after(BDate)) && (getDates(record[index])
						.before(EDate)))
						|| getDates(record[index]).equals(BDate)
						|| getDates(record[index]).equals(EDate)) {
					if ((map.isEmpty()) && !genCols) {
						rand = getRandomCols(rand, record.length - 1,
								dimension, defDim, record);
						Arrays.sort(rand);
						for (int i = 0; i < rand.length; i++)
							System.out.println("setting " + i
									+ "column indexes " + rand[i]);
						genCols = true;
					}

					List<ExchRate> eRates = new ArrayList<ExchRate>();
					Date = record[index++].trim();
					if (Date.isEmpty())
						break;
					while (dimension + 1 != index) {
						ExchRate exch = new ExchRate();
						if ((record[rand[index - 1]].toString().isEmpty())
								|| (record[rand[index - 1]].toString()
										.matches("N/A|NA"))) {
							exch.setValue(PrevRates.get(index - 1).getValue());
							// System.out.println(PrevRates.get(dim-2).getValue());
						} else {
							exch.setValue(record[rand[index - 1]].toString()
									.trim());
						}

						index++;
						eRates.add(exch);

					}
					// System.out.println(eRates.get(0).getValue());
					map.put(Date, eRates);
					PrevRates = eRates;
				}
			}
			checkAndCorrectInitialZeroPolytopes(map, startDate, endDate);
		} catch (IOException e) {
			System.out.println("no data");
			e.printStackTrace();
		}

		reader.close();

		return map;
	}

	/*
	 * This method tries to correct any zero values in the Exchange Rate at the
	 * beginning by replacing it with the next available exchange rate value
	 */
	private LinkedHashMap<String, List<ExchRate>> checkAndCorrectInitialZeroPolytopes(
			LinkedHashMap<String, List<ExchRate>> map, String startDate,
			String endDate) {
		DateFormat df = new SimpleDateFormat("d-MMM-yyyy");
		Calendar cal = Calendar.getInstance();
		List<ExchRate> exchangeRate = null;
		String currentDate = null;
		String completionDate = null;
		ExchRate zeroValueRate = new ExchRate();
		zeroValueRate.setValue("0");
		try {
			cal.setTime(df.parse(startDate));

			
			// Date currentDate = Date.valueOf(startDate);
			//completionDate = df.format(df.parse(endDate));

			while (!cal.after(df.parse(endDate))) {
				currentDate = df.format(cal.getTime());
				if (map.containsKey(currentDate)
						&& !map.get(currentDate).contains(zeroValueRate)) {
					exchangeRate = map.get(currentDate);
					break;
				}
				cal.add(Calendar.DATE, 1);
			}

			System.out.println(exchangeRate);
			System.out.println(currentDate);

			cal.setTime(df.parse(startDate));
			completionDate = currentDate;
			currentDate = df.format(cal.getTime());
			int index = 0;
			while (!currentDate.equals(completionDate)) {
				index = 0;
				if (map.containsKey(currentDate)) {
					for (ExchRate currentRate : map.get(currentDate)) {
						if (currentRate.equals(zeroValueRate)) {
							currentRate.setValue(exchangeRate.get(index)
									.getValue());
						}
						index++;
					}

					exchangeRate = map.get(currentDate);
				}
				cal.add(Calendar.DATE, 1);
				currentDate = df.format(cal.getTime());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return map;
	}

	/*
	 * Convert String date into java.util.Date date
	 */
	private java.util.Date getDates(String date) throws ParseException {

		java.util.Date tdate = new SimpleDateFormat("dd-MMM-yyyy",
				Locale.ENGLISH).parse(date);
		return tdate;
	}

	/*
	 * Returns the columns that are linked to the countries required.
	 */
	private int[] getCountryCols(String[] countries, int[] rand,
			String[] record, int defDim) {

		int i = 0;
		for (i = 0; i < defDim; i++) {
			for (int j = 0; j < record.length; j++) {

				if (countries[i].trim().equalsIgnoreCase(record[j].trim())) {
					rand[i] = j;
					System.out.println(record[j] + "column" + rand[i]);
					break;
				}
			}
		}
		return rand;
	}

	private int[] getRandomCols(int rand[], int size, int count, int cl,
			String[] record) throws IOException {
		// generate random columns
		int begin = 1;
		int tmp = 0;

		TextFeed tfeed = new TextFeed();
		// System.out.println(size + " " + count + "  " + cl);
		for (int i = 0; i < count - cl; i++) {
			tmp = tfeed.genRandomSeqs(begin, size);
			if (!numberExists(tmp, rand) && (!record[tmp].toString().isEmpty())) {
				rand[i + cl] = tmp;
			} else if (i != 0) {

				i--;
			}

		}
		return rand;
	}

	private boolean numberExists(int tmp, int[] rand) {
		for (int i = 0; i < rand.length; i++) {
			if (rand[i] == tmp)
				return true;
		}
		return false;
	}

	private List<ExchRate> intializetoZero(List<ExchRate> prevRates, int count) {
		ExchRate eRate = new ExchRate();
		int defaultValue = 0;
		eRate.setValue(Integer.toString(defaultValue));
		for (int i = 0; i < count; i++)
			prevRates.add(eRate);
		return prevRates;
	}

	// public static void main(String[] args) throws IOException,
	// NumberFormatException, ParseException {
	// // Example input
	// // 6 100 04-Jan-2011 28-Dec-2012
	// // i/p has only 6 columns
	// parseInputparameters(args);
	//
	// }

	/**
	 * Entry point for parsing. Use this method when the data is in array of
	 * String format. Example input: 6 100 04-Jan-2011 28-Dec-2012 Input has 4
	 * String objects in the array. The order of parsing is :
	 * <ul>
	 * <li>Number of dimensions(max 6)</li>
	 * <li>Number of polytopes</li>
	 * <li>Start Date</li>
	 * <li>End Date</li>
	 * </ul>
	 * 
	 * @param args
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws ParseException
	 */
	public void parseInputparameters(String[] args)// setting default values of
			// inputs
			throws NumberFormatException, IOException, ParseException {

		int dimension = 6;
		int polytopes = 100;
		String filePath = rb.getString(FILE_PATH);
		int itr = 0;
		String startDate = rb.getString(DEFAULT_START_DATE);
		String endDate = rb.getString(DEFAULT_END_DATE);
		if (args.length != 0) {
			if (args.length >= 1)
				dimension = Integer.parseInt((args[0].trim().isEmpty()) ? "6"
						: args[0].trim());
			if (args.length >= 2)
				polytopes = Integer.parseInt((args[1].trim().isEmpty()) ? "100"
						: args[1].trim());
			if (args.length >= 3)
				startDate = ((args[2].trim().isEmpty()) ? startDate : args[2]
						.trim());
			if (args.length >= 4)
				endDate = ((args[3].trim().isEmpty()) ? endDate : args[3]
						.trim());

		}

		String countries[] = new String[dimension];

		// set the default countries
		ExchangeRateCurrencyEnum[] tokens = ExchangeRateCurrencyEnum.values();
		while (itr < dimension) {
			countries[itr] = tokens[itr].getValue();
			itr++;
		}

		System.out.println("Dimensions" + dimension + ", polytopes "
				+ polytopes + ",  filePath  " + filePath
				+ ",no.of defaultCountries " + (itr));
		generatePolytopes(filePath, dimension, polytopes, countries, itr,
				startDate, endDate);

	}

	/**
	 * Entry point for parsing. Use this method when the data is separate
	 * parameters for the method.
	 * 
	 * @param dim
	 * @param polytp
	 * @param startDate
	 * @param endDate
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws ParseException
	 */
	public void parseInputparameters(Integer dim, Integer polytp,
			String startDate, String endDate) throws NumberFormatException,
			IOException, ParseException {

		// setting default values of inputs
		int dimension = 6;
		int polytopes = 100;
		String filePath = rb.getString(FILE_PATH);
		int itr = 0;

		if (dim != null) {
			dimension = dim.intValue();
		}
		if (polytp != null) {
			polytopes = polytp.intValue();
		}
		if (startDate == null || startDate.isEmpty()) {
			startDate = rb.getString(DEFAULT_START_DATE);
		}
		if (endDate == null || endDate.isEmpty()) {
			endDate = rb.getString(DEFAULT_END_DATE);
		}

		String countries[] = new String[dimension];

		// set the default countries
		ExchangeRateCurrencyEnum[] tokens = ExchangeRateCurrencyEnum.values();
		while (itr < dimension) {
			countries[itr] = tokens[itr].getValue();
			itr++;
		}

		System.out.println("Dimensions" + dimension + ", polytopes "
				+ polytopes + ",  filePath  " + filePath
				+ ",no.of defaultCountries " + (itr));
		generatePolytopes(filePath, dimension, polytopes, countries, itr,
				startDate, endDate);

	}

	private void generatePolytopes(String filePath, int dim, int polytopes,
			String[] countries, int defDim, String startDate, String endDate)
			throws NumberFormatException, IOException, ParseException {
		// main function to be called
		// String filePath,String dim,String polytopes
		final int skipLines = 2;
		int period = 0;
		LinkedHashMap<String, List<ExchRate>> map = new LinkedHashMap<String, List<ExchRate>>();
		map = parseCsvFileLineByLine(filePath, map, (dim), skipLines,
				countries, defDim, startDate, endDate);
		TextFeed feed = new TextFeed();
		if (!map.isEmpty()) {
			if ((period = map.size() / polytopes) >= 1) {

				for (int j = 0; j < (polytopes); j++)
					feed.feedGenerateEqual(map, (dim), j, period, polytopes);
			} else if (period < 1) {

				for (int j = 0; j < map.size(); j++)
					feed.feedGenerateEqual(map, (dim), j, 1, polytopes);

				for (int j = 0; j < polytopes - map.size(); j++)

					feed.feedGenerate(map, (dim));

			}

		}
	}
}
