package edu.csl.prs.test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import edu.csl.prs.ReadCsv;

/**
 * Junit test cases to validate the working of the service
 *
 */
public class ReadCsvTest {

	/**
	 * Test case to check parsing of input file
	 */
	@Test
	public void TestParseInputparameters() {
		ReadCsv readCsv = new ReadCsv();
		try {
			readCsv.parseInputparameters(6, 100, "23-Oct-1998", "23-Oct-1999");
			File file = new File("polytopes");
			Assert.assertEquals("Number of polytopes", 100,
					file.listFiles().length);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ReadCsvTest readCsvTest =new ReadCsvTest();
		readCsvTest.TestParseInputparameters();
	}
}
