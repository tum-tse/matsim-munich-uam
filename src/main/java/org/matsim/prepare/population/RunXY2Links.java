package org.matsim.prepare.population;

import org.matsim.run.XY2Links;

public class RunXY2Links {
    public static void main(String[] args) {
        XY2Links xy2Links = new XY2Links();
        String [] argString = new String[]{"src/main/java/org/matsim/prepare/population/input/config.xml", "src/main/java/org/matsim/prepare/population/input/matsimPlans_5_percent_xy2links.xml.gz"};
        xy2Links.run(argString);
    }
}
