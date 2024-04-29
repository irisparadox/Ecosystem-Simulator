package simulator.launcher;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.factories.Factory;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;

import simulator.control.Controller;

import java.util.List;
import java.util.ArrayList;
import simulator.control.Builder;
import simulator.control.SelectFirstBuilder;
import simulator.control.SelectClosestBuilder;
import simulator.control.SelectYoungestBuilder;
import simulator.control.BuilderBasedFactory;
import simulator.control.*;
import simulator.view.MainWindow;

import javax.swing.*;

public class Main {

	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String _tag;
		private String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return _tag;
		}

		public String get_desc() {
			return _desc;
		}
	}

	// default values for some parameters
	//
	private final static Double _default_time = 10.0; // in seconds
	private final static Double _default_delta_time = 0.03; // in seconds

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _time = null;
	private static String _in_file = null;
	public static double dt;
	// private static double time;
	private static String outFile;
	private static ExecMode mode;
	private static Controller controller;
	private static boolean sv;

	public static Factory<Animal> _animal_factory;
	public static Factory<SelectionStrategy> _strategy_factory;
	public static Factory<Region> _region_factory;

	private static void parse_args(String[] args) throws FileNotFoundException {

		// define the valid command line options
		//
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parse_mode_option(line);
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_time_option(line);
			parse_output_option(line);
			parse_simple_viewer_option(line);
			parse_delta_time_option(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// output file
		cmdLineOptions
				.addOption(Option.builder("o").longOpt("output").hasArg().desc("Where output is written.").build());

		// show
		cmdLineOptions.addOption(
				Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());

		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());

		// dt
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("An real number representing the simulation delta time in seconds. Default value: "
						+ _default_delta_time + ".")
				.build());

		return cmdLineOptions;
	}

	private static void parse_mode_option(CommandLine line) {
		if(line.hasOption("m")) {
			mode = ExecMode.valueOf(line.getOptionValue("m"));
		} else mode = ExecMode.GUI;
	}

	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (mode == ExecMode.BATCH && _in_file == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}

	private static void parse_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _default_time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}

	private static void parse_output_option(CommandLine line) throws ParseException {
		if (line.hasOption("o")) {
			outFile = line.getOptionValue("o");
		}
	}

	private static void parse_simple_viewer_option(CommandLine line) throws ParseException {
		sv = line.hasOption("sv");
		if (sv)
			mode = ExecMode.BATCH;
		else
			mode = ExecMode.GUI;
	}

	private static void parse_delta_time_option(CommandLine line) throws ParseException, FileNotFoundException {

		String t = line.getOptionValue("dt", _default_delta_time.toString());
		try {
			dt = Double.parseDouble(t);
			assert (dt >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for delta time: " + t);
		}
	}

	private static void init_factories() {
		// strategies factory
		List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>();
		selection_strategy_builders.add(new SelectFirstBuilder());
		selection_strategy_builders.add(new SelectClosestBuilder());
		selection_strategy_builders.add(new SelectYoungestBuilder());
		_strategy_factory = new BuilderBasedFactory<SelectionStrategy>(selection_strategy_builders);
		// animals factory
		List<Builder<Animal>> animals_builders = new ArrayList<>();
		animals_builders.add(new SheepBuilder(_strategy_factory));
		animals_builders.add(new WolfBuilder(_strategy_factory));
		_animal_factory = new BuilderBasedFactory<Animal>(animals_builders);
		// region factory
		List<Builder<Region>> region_builders = new ArrayList<>();
		region_builders.add(new DefaultRegionBuilder());
		region_builders.add(new DynamicSupplyRegionBuilder());
		_region_factory = new BuilderBasedFactory<Region>(region_builders);
	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}

	private static void start_batch_mode() throws Exception {
		InputStream is = new FileInputStream(new File(_in_file));
		// 1
		JSONObject json = load_JSON_file(is);

		// 2
		FileOutputStream out = new FileOutputStream(new File(outFile));
		// 3
		int width = json.getInt("width");
		int height = json.getInt("height");
		int rows = json.getInt("rows");
		int cols = json.getInt("cols");
		Simulator simulator = new Simulator(cols, rows, width, height, _animal_factory, _region_factory);
		// 4
		Controller controller = new Controller(simulator);
		// 5
		controller.load_data(json);
		// 6
		controller.run(_time, dt, sv, out);
		// 7
		is.close();
		out.close();
	}

	private static void start_GUI_mode() throws Exception {
		InputStream is = new FileInputStream(new File(_in_file));
		// 1
		JSONObject json = load_JSON_file(is);

		// 2
		FileOutputStream out = new FileOutputStream(new File(outFile));
		// 3
		int width = json.getInt("width");
		int height = json.getInt("height");
		int rows = json.getInt("rows");
		int cols = json.getInt("cols");
		Simulator simulator = new Simulator(cols, rows, width, height, _animal_factory, _region_factory);
		// 4
		Controller controller = new Controller(simulator);
		// 5
		controller.load_data(json);
		// 6
		is.close();
		out.close();
		SwingUtilities.invokeAndWait(() -> new MainWindow(controller));
	}

	private static void start(String[] args) throws Exception {
		init_factories();
		parse_args(args);
		switch (mode) {
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode();
			break;
		}
	}

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647l);
		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
