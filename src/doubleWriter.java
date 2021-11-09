import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class doubleWriter {
	public static void main(String[] args)
	{
		PrintWriter csvWriter;
		File csvFile = new File("data/doublesforskater.csv");
		Random rnd = new Random();
		try {
			csvFile.createNewFile();
		} catch (IOException e) {
			System.err.println("CSV file not created");
		}
		try {
			csvWriter = new PrintWriter(csvFile);

			// first row (output simulation params to the csv)
			for(int i = 0; i < 1000; i++)
			{
				csvWriter.printf("%f\n", rnd.nextDouble() * 100);
			}

			// close writer
			csvWriter.flush();
			csvWriter.close();

		} catch (FileNotFoundException e) {
			System.err.println("CSV file not found");
		}
	}
}
