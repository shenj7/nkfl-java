import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVWriterExample {

	// writeToCSV()
    public void writeToCSV() {

        // instantiate writer and file
        PrintWriter csvWriter;
        File csvFile = new File("source_code/plot_data.csv");

        // create file, disregard overwriting
        try {
            csvFile.createNewFile();
        } catch (IOException e) {
            System.err.println("CSV file not created");
        }

        // write data
        try {
            csvWriter = new PrintWriter(csvFile);

            // first row
            csvWriter.print("generation,bestFitness,averageFitness,worstFitness\n");

            // // each row is a generation
            // for (int genNum = 1; genNum <= this.allGenerations.size(); genNum++) {
            //     AlgoGeneration thisGen = this.allGenerations.get(genNum - 1);

            //     csvWriter.printf("%d,%d,%d,%d\n", genNum, thisGen.algoScoreMap.get(thisGen.bestAlgo), 
            //     thisGen.getAverageFitness(), thisGen.algoScoreMap.get(thisGen.worstAlgo));
            // }

            //
            System.out.println();
            System.out.println("Evolution data successfully written to plot_data.csv");
            System.out.println();

            // close writer
            csvWriter.flush();
            csvWriter.close();

        } catch (FileNotFoundException e) {
            System.err.println("CSV file not found");
        }
    }
}