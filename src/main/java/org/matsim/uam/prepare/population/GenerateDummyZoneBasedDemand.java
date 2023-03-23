package org.matsim.uam.prepare.population;

import org.matsim.core.utils.misc.Time;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.geotools.data.FeatureReader;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.MultiPolygon;
import org.matsim.uam.utils.TimeUtils;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateDummyZoneBasedDemand {

    public static void main(String[] args) throws IOException {
        System.out.println(
                "ARGS: start_time[s]* zones.shp* trips.csv* measurePointX* measurePointY* write-description");
        System.out.println("(* required)");

        // ARGS
        int j = 0;
        final int START_TIME = Integer.parseInt(args[j++]);
        final String shapePathName = args[j++];
        Path outputTripsPath = Path.of(args[j++]);
        final double measurePointX = Double.parseDouble(args[j++]);
        final double measurePointY = Double.parseDouble(args[j++]);

        final char delimiter = ',';


        //prepare csv writter
        CSVPrinter csvWriter = new CSVPrinter(new FileWriter(outputTripsPath.toString()), CSVFormat.DEFAULT.withDelimiter(delimiter).withFirstRecordAsHeader());
        List<String> tripsTitleRow = Arrays.asList
                ("from_x", "from_y", "to_x", "to_y", "start_time");
        csvWriter.printRecord(tripsTitleRow);
        CSVPrinter csvWriter2 = new CSVPrinter(new FileWriter(outputTripsPath.toString() + "reference.csv"), CSVFormat.DEFAULT.withDelimiter(delimiter).withFirstRecordAsHeader());
        List<String> tripsTitleRow2 = Arrays.asList
                ("from_x", "from_y", "to_x", "to_y", "start_time", "zone_id");
        csvWriter2.printRecord(tripsTitleRow2);
        int numOfTripsCreated = 0;


        //read vertiport shape file
        FileDataStore store = FileDataStoreFinder.getDataStore( new File( shapePathName ) );

        FeatureReader<SimpleFeatureType, SimpleFeature> reader = store.getFeatureReader();

        List<SimpleFeature> features = new ArrayList<>();
        for ( ; reader.hasNext(); ) {
            SimpleFeature result = reader.next();
            features.add(  result );
        }

        reader.close();

        for( SimpleFeature feature : features ){
            Geometry multiPolygon = (MultiPolygon) feature.getAttributes().get( 0 );
            //System.out.println("The handled multi-polygon contains " + multiPolygon.getNumGeometries() + " polygon(s)");
            if (multiPolygon.getNumGeometries() != 1) {
                throw new RuntimeException("The handled multi-polygon contains 0 polygon or more than 1 polygons!");
            }
            Geometry polygon = (Polygon) multiPolygon.getGeometryN(0);
            double tripAnotherEndX = polygon.getCentroid().getX();
            double tripAnotherEndY = polygon.getCentroid().getY();

            {
                //from measure point to the zone
                List<String> outputRow = new ArrayList<>();

                outputRow.add(Double.toString(measurePointX));
                outputRow.add(Double.toString(measurePointY));
                outputRow.add(Double.toString(tripAnotherEndX));
                outputRow.add(Double.toString(tripAnotherEndY));
                outputRow.add((new TimeUtils.DefaultTimeWriter()).writeTime(START_TIME));


                if (tripsTitleRow.size() != outputRow.size()) {
                    throw new RuntimeException("tripsTitleRow.size() != outputRow.size()");
                }

                csvWriter.printRecord(outputRow);

                //for the reference csv
                outputRow.add(Double.toString((double) feature.getAttributes().get( 1 )));
                if (tripsTitleRow2.size() != outputRow.size()) {
                    throw new RuntimeException("tripsTitleRow2.size() != outputRow.size()");
                }
                csvWriter2.printRecord(outputRow);

                numOfTripsCreated++;
            }

            {
                //from the zone to measure point
                List<String> outputRow = new ArrayList<>();

                outputRow.add(Double.toString(tripAnotherEndX));
                outputRow.add(Double.toString(tripAnotherEndY));
                outputRow.add(Double.toString(measurePointX));
                outputRow.add(Double.toString(measurePointY));
                outputRow.add((new TimeUtils.DefaultTimeWriter()).writeTime(START_TIME));


                if (tripsTitleRow.size() != outputRow.size()) {
                    throw new RuntimeException("tripsTitleRow.size() != outputRow.size()");
                }

                csvWriter.printRecord(outputRow);

                //for the reference csv
                outputRow.add(Double.toString((double) feature.getAttributes().get( 1 )));
                if (tripsTitleRow2.size() != outputRow.size()) {
                    throw new RuntimeException("tripsTitleRow2.size() != outputRow.size()");
                }
                csvWriter2.printRecord(outputRow);

                numOfTripsCreated++;
            }
        }

        csvWriter.close();
        csvWriter2.close();
        System.out.println("numOfTripsServed: " + numOfTripsCreated);
    }

}
