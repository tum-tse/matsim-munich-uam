package org.matsim.uam.travelTimes;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.matsim.core.utils.misc.Time;
import org.matsim.uam.utils.TimeUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author haowu
 */
public class TripsAsInputGenerator {
//    private static boolean writeDescription = true;

    public static void main(String[] args) throws IOException {
        System.out.println(
                "ARGS: trips.csv* outputfile-name.csv* write-description");
        System.out.println("(* required)");

        // ARGS
        int j = 0;
        Path inputTripsPath = Path.of(args[j++]);
        Path outputTripsPath = Path.of(args[j++]);
        final char delimiter = ',';

/*        if (args.length > 2)
            writeDescription = Boolean.parseBoolean(args[j]);*/



        //process trips
        CSVPrinter csvWriter = new CSVPrinter(new FileWriter(outputTripsPath.toString()), CSVFormat.DEFAULT.withDelimiter(delimiter).withFirstRecordAsHeader());
        List<String> tripsTitleRow = Arrays.asList
                ("from_x", "from_y", "to_x", "to_y", "start_time");
        csvWriter.printRecord(tripsTitleRow);

        int numOfTripsServed = 0;
        try (CSVParser parser = new CSVParser(Files.newBufferedReader(inputTripsPath),
                CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader())) {
            for (CSVRecord record : parser.getRecords()) {
                double fromX = Double.parseDouble(record.get("start_x"));
                double fromY = Double.parseDouble(record.get("start_y"));
                double toX = Double.parseDouble(record.get("end_x"));
                double toY = Double.parseDouble(record.get("end_y"));
                TimeUtils.CustomTimeWriter customTimeWriter = new TimeUtils.DefaultTimeWriter();
                double departureTime = Time.parseTime(record.get("dep_time"));


                //write output
                List<String> outputRow = new ArrayList<>();

                outputRow.add(Double.toString(fromX));
                outputRow.add(Double.toString(fromY));
                outputRow.add(Double.toString(toX));
                outputRow.add(Double.toString(toY));
                outputRow.add(customTimeWriter.writeTime(departureTime));


                csvWriter.printRecord(outputRow);

                numOfTripsServed++;
            }
        }
        csvWriter.close();
        System.out.println("numOfTripsServed: " + numOfTripsServed);

    }

}
