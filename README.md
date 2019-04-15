# The MATSim Open Berlin Scenario

### About this project

This repository provides a MATSim transport model for Munich, provided by the [Transport Systems Planning and Transport Telematics group](https://www.vsp.tu-berlin.de) of [Technische Universität Berlin](http://www.tu-berlin.de).

### Note

Handling of large files within git is not without problems (git lfs files are not included in the zip download; we have to pay; ...).  In order to avoid this, large files, both on the input and on the output side, reside at https://svn.vsp.tu-berlin.de/repos/shared-svn/projects/matsim-munich .  You need to have permission to access that repository.

### Simple things (without installing/running MATSim)

##### Run VIA on output files

1. Get VIA from https://www.simunto.com/via/.  (There is a free license for a small number of agents; that will probably work but only display a small number of vehicles/agents.)
1. Go to http://svn.vsp.tu-berlin.de/repos/shared-svn/projects/matsim-munich/scenarios/v2/output/2019-03-26-1pct-1it/ .
1. Download `*.output_network.xml.gz` and `*.output_events.xml.gz`.  Best make sure that they do not uncompress, e.g. by "Download linked file as ...".
1. Get these files into VIA.  This can be achieved in various ways; one is to open VIA and then drag the files from a file browser into VIA.
1. Run VIA and enjoy.

### Downloading the repository alternative 1: Download ZIP

1. Click on `Clone or download` and then on `Download ZIP`.
1. Unzip the repository.
1. Go to "Download the data" below.

### Downloading the repository alternative 2: Clone the repository

##### git clone the code

1. Install git for the command line.
1. Type `git clone https://github.com/matsim-vsp/matsim-munich.git` in the command line.

(Or use your IDE, e.g. Eclipse, IntelliJ, to clone the repository.)

This will result in a new `matsim-munich` directory.  Memorize where you have put it.  You can move it, as a whole, to some other place.

##### Update your local clone of the repository.

1. Go into the `matsim-munich` directory.
1. Type `git pull`

(Or use your IDE, e.g. Eclipse, IntelliJ, to update the repository.)

This will update your repository to the newest version.

### Download the data

##### svn checkout the data

1. Install svn for the command line.
1. `svn checkout https://svn.vsp.tu-berlin.de/repos/shared-svn/projects/matsim-munich`

##### update your local copy of the data

1. Go into the matsim-munich data directory.
1. Type `svn update`.

### Run the MATSim Munich scenario ...
(Requires either cloning or downloading the repository.)

##### ... using an IDE, e.g. Eclipse, IntelliJ
1. Set up the project in your IDE.
1. Make sure the project is configured as maven project.
1. Run the JAVA class `src/main/java/org/matsim/gui/MATSimGUI.java`.  A simple GUI should open.
1. In the GUI, click on the "Choose" button for configuration file.  Navigate to the `scenario/equil` or the `scenario/v2` directory in the data repo and load one of the configuration files.
1. Increase memory in the GUI.
1. Press the "Start MATSim" button.  This should run MATSim.  
1. "Open" the output directory.  You can drag files into VIA as was already done above.
1. Edit the config file or adjust the run class.  In particular, it makes sense to reduce the lastIteration number for trying around.  Re-run MATSim.

##### !! Path convention: !!

To work outside the GUI, you need
* the code (from github) in `<someDirectoryRoot>/<something>/matsim-munich`
* the data (from svn) in `<someDirectoryRoot>/<somethingElse>/shared-svn/projects/matsim-munich`

More technically, the code searches, from the java root, for the data by `../../shared-svn/projects/matsim-munich` .  This is a bit annoying, but everything else (e.g. git lsf, git fat) is worse.

So please move the code directory and the data directory accordingly.  





### More information

For more information about MATSim, see here: https://www.matsim.org/.
