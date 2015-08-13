package com.atasya.migrate;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Application {

	public static void main(String[] args) throws Exception {
		final CommandLine cmdLine = parseCommandLine(args).orNull();
		if (cmdLine == null) {
			return;
		}

		final File sourceFolder = new File(cmdLine.getOptionValue(CmdOptions.sourcePath.getOpt()));
		final File destFolder = new File(cmdLine.getOptionValue(CmdOptions.destPath.getOpt()));

		final File[] files = sourceFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".cimdbin");
			}
		});
		for (int i = 0; i < files.length; i++) {
			final File file = files[i];

			final JsonObject root = new Gson().fromJson(new FileReader(file), JsonElement.class).getAsJsonObject();
			root.get("Aginity Version").getAsJsonObject().addProperty("Metadata Version", "amp_2.2.0.2");

			for (JsonElement element : root.get("Fact Columns").getAsJsonArray()) {
				final JsonObject column = element.getAsJsonObject();
				column.addProperty("ColumnPhysicalName", column.get("ColumnName").getAsString());
			}

			for (JsonElement element : root.get("Fact Table").getAsJsonArray()) {
				final JsonObject table = element.getAsJsonObject();
				final String id = table.get("ID").getAsString();
				table.addProperty("ID", table.get("Name").getAsString());
				table.addProperty("Name", id);
			}

			final File destFile = new File(destFolder, file.getName());
			final FileWriter fileWriter = new FileWriter(destFile);
			fileWriter.write(new Gson().toJson(root));
			fileWriter.close();
		}
	}

	private static Optional<CommandLine> parseCommandLine(String[] args) {
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
