package org.matsim.uam.network.indirectRoutes;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This script add labels like road class to the links
 *
 * @author haowuintub (Hao Wu)
 */
public class AddLinkLabelsToNetwork {
    private static final Logger LOG = Logger.getLogger(AddLinkLabelsToNetwork.class);
    public static final String TYPE="type" ;

    public static void main(String[] args) {
        System.out.println("ARGS: network-input-file.xml.gz* network-reference-file.xml.gz* network-output-file.xml.gz*");
        System.out.println("(* required)");

        // ARGS
        int j = 0;
        String networkInputFile = args[j++];
        String networkReferenceFile = args[j++];
        String networkOutputFile = args[j++];

        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        Network network = scenario.getNetwork();
        MatsimNetworkReader reader = new MatsimNetworkReader(network);
        reader.readFile(networkInputFile);

        Scenario ptScenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        Network networkForReference = ptScenario.getNetwork();
        MatsimNetworkReader reader2 = new MatsimNetworkReader(networkForReference);
        reader2.readFile(networkReferenceFile);

        Scenario scenarioForOutput = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        Network networkForOutput = scenarioForOutput.getNetwork();
        MatsimNetworkReader reader3 = new MatsimNetworkReader(networkForOutput);
        reader3.readFile(networkInputFile);

        List<Id<Link>> carLinks = new ArrayList<>();
        List<Id<Link>> ptLinks = new ArrayList<>();
        for (Link link : network.getLinks().values()) {
            if (link.getId().toString().startsWith("pt")){
                ptLinks.add(link.getId());
            } else {
                carLinks.add(link.getId());
            }
        }
        LOG.info("finished saving the carLinks and ptLinks");

        for (Link link : networkForOutput.getLinks().values()) {
            if (carLinks.contains(link.getId())){
                Link linkInReferenceNetwork = networkForReference.getLinks().get(link.getId());
                //ToDo: may need to extract the type manually
                link.getAttributes().putAttribute(TYPE, linkInReferenceNetwork.getAttributes().getAttribute(TYPE));
            } else if (ptLinks.contains(link.getId())&link.getAllowedModes().contains("rail")) {
                String railwayType = "railway";
                link.getAttributes().putAttribute(TYPE, railwayType);
            }
        }
        LOG.info("finished attached labels");

        // Write modified network to file
        NetworkWriter writer = new NetworkWriter(networkForOutput);
        writer.write(networkOutputFile);
    }
}
