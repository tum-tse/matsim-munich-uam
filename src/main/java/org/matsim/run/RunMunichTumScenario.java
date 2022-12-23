/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.run;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author nagel
 */
public class RunMunichTumScenario {

    private final String[] args;
    private Config config = null;
    private Scenario scenario = null;
    private Controler controler = null;

    public static void main(String[] args) {
        new RunMunichTumScenario(args).run();
    }

    public RunMunichTumScenario(String[] args) {
        this.args = args;
    }

    public final void run() {
        if (args != null && args.length > 0) {
            config = ConfigUtils.loadConfig(args[0]);
        } else {
            throw new RuntimeException("need to provide path to config file. aborting ...");
        }
        config.plansCalcRoute().setInsertingAccessEgressWalk(true);
        scenario = ScenarioUtils.loadScenario(config);
        controler = new Controler(scenario);
        controler.run();
    }


}
