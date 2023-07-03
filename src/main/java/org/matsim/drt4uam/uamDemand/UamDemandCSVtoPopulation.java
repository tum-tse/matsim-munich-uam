package org.matsim.drt4uam.uamDemand;

import org.apache.commons.csv.*;
import org.matsim.api.core.v01.*;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationWriter;

import java.io.*;
import java.util.*;

public class UamDemandCSVtoPopulation {
    public static void main(String[] args) {
        System.out.println(
                "ARGS: uamDemandCsvFile* populationFile* skippedRowsFile*");
        System.out.println("(* required)");

        // ARGS
        int j = 0;
        final String uamDemandCsvFile = args[j++];
        final String populationFile = args[j++];
        final String skippedRowsFile = args[j++];

        Population population = PopulationUtils.createPopulation(ConfigUtils.createConfig());
        Map<String, Person> existingPersons = new HashMap<>();

        try {
            Reader reader = new FileReader(uamDemandCsvFile);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT/*.withDelimiter(';').withFirstRecordAsHeader()*/.withHeader().withIgnoreHeaderCase().withTrim()); // CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader()
            FileWriter writerSkippedRows = new FileWriter(skippedRowsFile);

            for (CSVRecord record : csvParser) {
                String endTime = record.get("endTime");
                if (endTime == null || endTime.isEmpty()) {
                    writerSkippedRows.append(record.toString() + "\n");
                    continue;
                }

                String peronId = record.get("peronId");
                double originCoordX = Double.parseDouble(record.get("originCoordX"));
                double originCoordY = Double.parseDouble(record.get("originCoordY"));
                double destinationCoordX = Double.parseDouble(record.get("destinationCoordX"));
                double destinationCoordY = Double.parseDouble(record.get("destinationCoordY"));
                double startTime = Double.parseDouble(record.get("startTime"));
                double endTimeVal = Double.parseDouble(endTime);

                Person person;
                Plan plan;
                if (existingPersons.containsKey(peronId)) {
                    person = existingPersons.get(peronId);
                    plan = person.getSelectedPlan();
                } else {
                    person = population.getFactory().createPerson(Id.createPersonId(peronId));
                    plan = population.getFactory().createPlan();
                }

                Activity activity1 = population.getFactory().createActivityFromCoord("origin-activity", new Coord(originCoordX, originCoordY));
                activity1.setEndTime(startTime);
                plan.addActivity(activity1);

                Leg leg = population.getFactory().createLeg("uam");
                leg.setDepartureTime(startTime);
                plan.addLeg(leg);

                Activity activity2 = population.getFactory().createActivityFromCoord("destination-activity", new Coord(destinationCoordX, destinationCoordY));
                activity2.setStartTime(endTimeVal);
                plan.addActivity(activity2);

                if (!existingPersons.containsKey(peronId)) {
                    person.addPlan(plan);
                    population.addPerson(person);
                    existingPersons.put(peronId, person);
                }
            }

            csvParser.close();
            writerSkippedRows.flush();
            writerSkippedRows.close();

            PopulationWriter writer = new PopulationWriter(population);
            writer.write(populationFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String secondsToTime(double time) {
        int h = (int) (time / 3600);
        int m = (int) ((time % 3600) / 60);
        int s = (int) (time % 60);
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}

