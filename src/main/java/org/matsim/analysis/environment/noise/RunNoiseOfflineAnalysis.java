package org.matsim.analysis.environment.noise;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.noise.MergeNoiseCSVFile;
import org.matsim.contrib.noise.NoiseConfigGroup;
import org.matsim.contrib.noise.NoiseOfflineCalculation;
import org.matsim.contrib.noise.ProcessNoiseImmissions;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

public class RunNoiseOfflineAnalysis {

    private static String runDirectory = "outputExpressBus/";
    private static String outputDirectory = "outputExpressBus/noise/";

    public RunNoiseOfflineAnalysis() {
    }

    public static void main(String[] args) {
        Config config = ConfigUtils.createConfig(new ConfigGroup[]{new NoiseConfigGroup()});
        config.network().setInputFile(runDirectory + "output_network.xml.gz");
        config.plans().setInputFile(runDirectory + "output_plans.xml.gz");
        config.controler().setOutputDirectory(runDirectory);
        NoiseConfigGroup noiseParameters = (NoiseConfigGroup)ConfigUtils.addOrGetModule(config, NoiseConfigGroup.class);
        noiseParameters.setReceiverPointGap(100);
        noiseParameters.setReceiverPointsGridMaxY(3000);
        noiseParameters.setReceiverPointsGridMinY(-3000);
        noiseParameters.setReceiverPointsGridMaxX(7000);
        noiseParameters.setReceiverPointsGridMinX(-100);

        noiseParameters.setHgvIdPrefixes("departure");

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