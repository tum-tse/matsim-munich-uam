package org.matsim.analysis.environment.noise;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.noise.MergeNoiseCSVFile;
import org.matsim.contrib.noise.NoiseConfigGroup;
import org.matsim.contrib.noise.NoiseOfflineCalculation;
import org.matsim.contrib.noise.ProcessNoiseImmissions;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

public class RunNoiseOfflineAnalysis {

    public RunNoiseOfflineAnalysis() {
    }

    public static void main(String[] args) {
        System.out.println(
                "ARGS: run_directory* output_directory* receiver_point_gap(default: 100)* grid_max_x* grid_max_y* grid_min_x* grid_min_y* run_id(of the matsim scenario)* write-description");
        System.out.println("(* required)");

        // ARGS
        int j = 0;
        final String runDirectory = args[j++];
        String outputDirectory = args[j++];
        final double receiverPointGap = Integer.parseInt(args[j++]);
        final double gridMaxX = Double.parseDouble(args[j++]);
        final double gridMaxY = Double.parseDouble(args[j++]);
        final double gridMinX = Double.parseDouble(args[j++]);
        final double gridMinY = Double.parseDouble(args[j++]);
        final String runId = args.length == 8 ? args[j++] : "";


        // settings for noise modeling
        Config config = ConfigUtils.createConfig(new NoiseConfigGroup());
        config.global().setCoordinateSystem("EPSG:31468");
        config.controler().setRunId(runId);
        config.network().setInputFile(runDirectory + runId  + "." + "output_network.xml.gz");
        config.plans().setInputFile(runDirectory + runId  + "." + "output_plans.xml.gz");
        config.controler().setOutputDirectory(runDirectory);
        NoiseConfigGroup noiseParameters = ConfigUtils.addOrGetModule(config, NoiseConfigGroup.class);
        noiseParameters.setReceiverPointGap(receiverPointGap);
        noiseParameters.setReceiverPointsGridMaxY(gridMaxY);
        noiseParameters.setReceiverPointsGridMinY(gridMinY);
        noiseParameters.setReceiverPointsGridMaxX(gridMaxX);
        noiseParameters.setReceiverPointsGridMinX(gridMinX);
        //noiseParameters.setHgvIdPrefixes("departure"); //ToDo: need to investigate a little bit!

        Scenario scenario = ScenarioUtils.loadScenario(config);
        NoiseOfflineCalculation noiseCalculation = new NoiseOfflineCalculation(scenario, outputDirectory);
        noiseCalculation.run();
        if (!outputDirectory.endsWith("/")) {
            outputDirectory = outputDirectory + "/";
        }

        String outputFilePath = outputDirectory + "noise-analysis/";
        ProcessNoiseImmissions process = new ProcessNoiseImmissions(outputFilePath + "immissions/", outputFilePath + "receiverPoints/receiverPoints.csv", noiseParameters.getReceiverPointGap());
        process.run();
        String[] labels = new String[]{"immission", "consideredAgentUnits", "damages_receiverPoint"};
        String[] workingDirectories = new String[]{outputFilePath + "/immissions/", outputFilePath + "/consideredAgentUnits/", outputFilePath + "/damages_receiverPoint/"};
        MergeNoiseCSVFile merger = new MergeNoiseCSVFile();
        merger.setReceiverPointsFile(outputFilePath + "receiverPoints/receiverPoints.csv");
        merger.setOutputDirectory(outputFilePath);
        merger.setTimeBinSize(noiseParameters.getTimeBinSizeNoiseComputation());
        merger.setWorkingDirectory(workingDirectories);
        merger.setLabel(labels);
        merger.run();
    }
}