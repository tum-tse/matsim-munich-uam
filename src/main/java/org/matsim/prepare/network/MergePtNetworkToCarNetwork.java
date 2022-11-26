package org.matsim.prepare.network;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;

import org.apache.log4j.Logger;

public class MergePtNetworkToCarNetwork {
    private static final Logger log = Logger.getLogger(MergePtNetworkToCarNetwork.class);

    public static void main(String[] args) {
        String carNetwork = "/Users/haowu/Documents/TSE/UAM/MSM/Carlos/travel_demand_2021/matsim/studyNetworkDense.xml.gz";
        String ptNetwork = "/Users/haowu/Documents/TSE/UAM/MSM/Carlos/travel_demand_2021/matsim/pt_network_schedule/network_pt_road.xml.gz";
        String mergedNetwork = "scenarios/privateTumTbBase/studyNetworkDense_pt.xml.gz";

        // Get car network
        Scenario carScenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        (new MatsimNetworkReader(carScenario.getNetwork())).readFile(carNetwork);

        // Get pt network
        Scenario ptScenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        (new MatsimNetworkReader(ptScenario.getNetwork())).readFile(ptNetwork);

        //merge networks
        // Get pt network
        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        Network network = scenario.getNetwork();
        MergeNetworks.merge(carScenario.getNetwork(), "", ptScenario.getNetwork(), "", network);

        //clean the final network
        (new NetworkCleaner()).run(network);

        //output the network
        new NetworkWriter(network).write(mergedNetwork);

        log.info("Please check if linkId start will blank space!");
    }
}
