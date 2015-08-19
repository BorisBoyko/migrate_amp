package com.atasya.migrate;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

public class CmdOptions {

	public static final Option sourcePath = createRequired("s", "source-path" , "Source path");
	public static final Option destPath = createRequired("d", "dest-path" , "Destination path");

	public static final ImmutableList<Option> optionList = ImmutableList.of(sourcePath, destPath);

	public static final Options options;
	static {
		options = new Options();

		for (final Option option : optionList)
			options.addOption(option);
	}

	public static Option createRequired(String opt, String longOpt, String description) {
		final Option result = new Option(opt, longOpt, true, description);
		result.setArgName(longOpt);
		result.setRequired(true);

		return result;
	}

	public static Optional<CommandLine> parseCommandLine(String[] args) {
		try {
			final CommandLineParser parser = new PosixParser();
			final CommandLine cmdLine = parser.parse(CmdOptions.options, args);
			return Optional.of(cmdLine);
		} catch (ParseException e) {
			try (final PrintWriter printer = new PrintWriter(System.out)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.setOptionComparator(Ordering.explicit(CmdOptions.optionList));
				formatter.printUsage(printer, 200, "migrate.bat", CmdOptions.options);
				formatter.printOptions(printer, 200, CmdOptions.options, 2, 3);
			}
			return Optional.absent();
		}
	}
}