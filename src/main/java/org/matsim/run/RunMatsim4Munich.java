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
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.qsim.AbstractQSimModule;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.replanning.PlanStrategyImpl;
import org.matsim.core.replanning.modules.ReRoute;
import org.matsim.core.replanning.modules.SubtourModeChoice;
import org.matsim.core.replanning.selectors.RandomPlanSelector;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultStrategy;
import org.matsim.core.router.TripRouter;
import org.matsim.core.scenario.ScenarioUtils;

import javax.inject.Inject;
import javax.inject.Provider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.matsim.core.config.groups.PlanCalcScoreConfigGroup.*;

/**
 * @author nagel
 *
 */
public class RunMatsim4Munich{
	// cf CNEMunich in ikaddoura playground

	private final String[] args;
	private Config config = null ;
	private Scenario scenario = null ;
	private Controler controler = null ;

	public static void main ( String [] args ) {
		new RunMatsim4Munich( args ).run() ;
	}

	public RunMatsim4Munich( String [] args ) {
		this.args = args ;
	}

	public final Config prepareConfig() {
		if ( args!=null && args.length > 0 ) {
			config = ConfigUtils.loadConfig( args[0] ) ;
		} else{
			throw new RuntimeException("need to provide path to config file. aborting ...") ;
		}

		// activities (alphabetic).  Presumably revealed activity durations from MidMUC.  This may explain why they extend to different maximum number of hours.  However, a
		// pickup activity of 22hours does not make sense, so it is not entirely clear.  kai, mar'19
		// ---
		{
			final ActivityParams params = new ActivityParams( "busi0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 1 ; ii <= 13; ii+=1 ) {
			final ActivityParams params = new ActivityParams( "busi" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		{
			final ActivityParams params = new ActivityParams( "educ0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 1 ; ii <= 23; ii+=1 ) {
			final ActivityParams params = new ActivityParams( "educ" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		// I don't know what the following is.  Maybe "visit friends"?  kai, mar'19
		{
			final ActivityParams params = new ActivityParams( "frie0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 4 ; ii <= 4; ii+=1 ) { // not sure why; other values were not in original config file.  kai, mar'19
			final ActivityParams params = new ActivityParams( "frie" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		{
			final ActivityParams params = new ActivityParams( "home0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 1 ; ii <= 27; ii+=1 ) {
			final ActivityParams params = new ActivityParams( "home" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		{
			final ActivityParams params = new ActivityParams( "leis0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 1 ; ii <= 24; ii+=1 ) {
			final ActivityParams params = new ActivityParams( "leis" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		{
			final ActivityParams params = new ActivityParams( "othe0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 1 ; ii <= 17; ii+=1 ) {
			final ActivityParams params = new ActivityParams( "othe" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		{
			final ActivityParams params = new ActivityParams( "pick0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 1 ; ii <= 22; ii+=1 ) {
			final ActivityParams params = new ActivityParams( "pick" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		{
			final ActivityParams params = new ActivityParams( "priv0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 1 ; ii <= 20; ii+=1 ) {
			final ActivityParams params = new ActivityParams( "priv" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		{
			final ActivityParams params = new ActivityParams( "shop0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 1 ; ii <= 22; ii+=1 ) {
			final ActivityParams params = new ActivityParams( "shop" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
//		{
//			final ActivityParams params = new ActivityParams( "spor0.5H" );
//			params.setTypicalDuration( 0.5 * 3600. );
//			config.planCalcScore().addActivityParams( params );
//		}
		for ( long ii = 2 ; ii <= 3; ii+=1 ) {  // only with these two values in original config file.  kai, mar'19
			final ActivityParams params = new ActivityParams( "spor" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		// Theoretically, "other" (above) means activity types that are known but are aggregated into the new type.  In contrast, "unknown" means that they are not known at
		// all.  But I don't know if it was designed like that.  kai, mar'19
		{
			final ActivityParams params = new ActivityParams( "unkn0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 1 ; ii <= 30; ii+=1 ) {
			final ActivityParams params = new ActivityParams( "unkn" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		{
			final ActivityParams params = new ActivityParams( "with0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 1 ; ii <= 20; ii+=1 ) {
			final ActivityParams params = new ActivityParams( "with" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		{
			final ActivityParams params = new ActivityParams( "work0.5H" );
			params.setTypicalDuration( 0.5 * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		for ( long ii = 1 ; ii <= 23; ii+=1 ) {
			final ActivityParams params = new ActivityParams( "work" + ii + ".0H" ) ;
			params.setTypicalDuration( ii * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		// presumably, the following are commuters from outside MUC to inside.  For these, we did not have MiD, so they were derived from Pendlerstatistik (or from BVWP).
		{
			final ActivityParams params = new ActivityParams( "pvHome" );
			params.setTypicalDuration( 6. * 3600. ); // not sure why this is set to 6hrs; does not make sense.  Maybe should have been 16 and is a typo.  kai, mar'19
			config.planCalcScore().addActivityParams( params );
		}
		{
			final ActivityParams params = new ActivityParams( "pvWork" );
			params.setTypicalDuration( 9. * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		// freight traffic ("Gueterverkehr").  Derived from BVWP.
		{
			final ActivityParams params = new ActivityParams( "gvHome" );  // the "home" does not make sense here. kai, mar'19
			params.setTypicalDuration( 6. * 3600. );
			config.planCalcScore().addActivityParams( params );
		}
		// ---
		// none of the above has opening/closing time, so one cannot use this with time mutation.


		return config ;
	}

	public final Scenario prepareScenario() {
		if ( config==null ) {
			prepareConfig() ;
		}
		scenario = ScenarioUtils.loadScenario( config ) ;

		return scenario ;
	}

	public final void addOverridingModule( AbstractModule controlerModule ) {
		if ( controler==null ){
			prepareControler();
		}
		controler.addOverridingModule( controlerModule ) ;
	}

	public final void addOverridingQSimModule( AbstractQSimModule qSimModule ) {
		if ( controler==null ){
			prepareControler();
		}
		controler.addOverridingQSimModule( qSimModule ) ;
	}

	public final void run() {
		if ( controler==null ) {
			prepareControler() ;
		}
		controler.run() ;
	}

	public final Controler prepareControler() {
		if ( scenario==null ) {
			prepareScenario() ;
		}
		controler = new Controler( scenario ) ;

		// use the (congested) car travel time for the teleported ride mode
		// Seems like a nice trick, but does not work so well: All ride trips found in the 0th iteration use the free speed travel time, which is much too fast.  And they
		// remember this forever. kai, mar'19
		controler.addOverridingModule( new AbstractModule() {
			@Override public void install() {
				addTravelTimeBinding( TransportMode.ride ).to( networkTravelTime() );
				addTravelDisutilityFactoryBinding( TransportMode.ride ).to( carTravelDisutilityFactoryKey() );
			}
		} );

		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				final Provider<TripRouter> tripRouterProvider = binder().getProvider(TripRouter.class );

				// this here "registers" an additional stratetegy under "SubtourModeChoice_COMMUTER_REV_COMMUTER".  We want the commuters have a different pt than the urban population, and this cannot be
				// configured by config alone.  See here: https://github.com/matsim-org/matsim-code-examples/issues/92 .
				addPlanStrategyBinding( "SubtourModeChoice_COMMUTER_REV_COMMUTER" ).toProvider( new Provider<PlanStrategy>() {
					final String[] availableModes = {"car", "pt_COMMUTER_REV_COMMUTER"};
					final String[] chainBasedModes = {"car", "bike"};
					@Inject Scenario sc;
					@Override public PlanStrategy get() {
						final PlanStrategyImpl.Builder builder = new PlanStrategyImpl.Builder(new RandomPlanSelector<>());
						builder.addStrategyModule(new SubtourModeChoice(sc.getConfig().global().getNumberOfThreads(), availableModes, chainBasedModes, false,
							  0.5, tripRouterProvider) );
						builder.addStrategyModule(new ReRoute(sc, tripRouterProvider) );
						return builder.build();
					}
				} );
			}
		});


		return controler ;
	}

}
