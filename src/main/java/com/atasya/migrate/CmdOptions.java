package com.atasya.migrate;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.google.common.collect.ImmutableList;

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
}