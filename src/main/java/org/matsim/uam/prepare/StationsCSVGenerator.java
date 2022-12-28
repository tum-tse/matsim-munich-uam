package org.matsim.uam.prepare;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.geotools.data.FeatureReader;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class StationsCSVGenerator {
    public static void main(String[] args) throws IOException {
        System.out.println(
                "ARGS: vertiports.shp* outputfile-name.csv* write-description");
        System.out.println("(* required)");

        // ARGS
        int j = 0;
        final String shapePathname = args[j++];
        Path outputTripsPath = Path.of(args[j++]);

        final char delimiter = ',';


        //prepare csv writter
        CSVPrinter csvWriter = new CSVPrinter(new FileWriter(outputTripsPath.toString()), CSVFormat.DEFAULT.withDelimiter(delimiter).withFirstRecordAsHeader());
        List<String> tripsTitleRow = Arrays.asList
                ("station_id", "station_name", "x", "y", "z", "vtol_z", "ground_access_capacity", "ground_access_freespeed", "flight_access_capacity", "flight_access_freespeed", "preflighttime", "postflighttime", "defaultwaittime");
        csvWriter.printRecord(tripsTitleRow);
        int numOfStationsServed = 0;


        //read vertiport shape file
        FileDataStore store = FileDataStoreFinder.getDataStore( new File( shapePathname ) );

        FeatureReader<SimpleFeatureType, SimpleFeature> reader = store.getFeatureReader();

        List<SimpleFeature> features = new ArrayList<>();
        for ( ; reader.hasNext(); ) {
            SimpleFeature result = reader.next();
            features.add(  result );
        }

        reader.close();

        //int stationId = 0;
        for( SimpleFeature feature : features ){
            Geometry point = (Point) feature.getAttributes().get( 0 );

                //write output
                List<String> outputRow = new ArrayList<>();

                outputRow.add((String) feature.getAttributes().get( 2 )); //outputRow.add(Double.toString(stationId++));
                outputRow.add((String) feature.getAttributes().get( 1 ));
                outputRow.add(Double.toString(point.getCoordinate().x));
                outputRow.add(Double.toString(point.getCoordinate().y));
                outputRow.add(Integer.toString(0));
                outputRow.add(Integer.toString(600));
                outputRow.add(Double.toString(999));
                outputRow.add(Double.toString(35));
                outputRow.add(Double.toString(999));
                outputRow.add(Double.toString(35));
                outputRow.add(Integer.toString(900));
                outputRow.add(Integer.toString(300));
                outputRow.add(Integer.toString(480));


                if (tripsTitleRow.size() != outputRow.size()) {
                    throw new RuntimeException("tripsTitleRow.size() != outputRow.size()");
                }

                csvWriter.printRecord(outputRow);

                numOfStationsServed++;
        }

        csvWriter.close();
        System.out.println("numOfStationsServed: " + numOfStationsServed);

    }
}
