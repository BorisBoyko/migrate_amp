package com.atasya.migrate;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;

import com.atasya.migrate.versions.Amp_2_2_0_1_VersionConverter;
import com.atasya.migrate.versions.Amp_2_2_0_2_VersionConverter;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Application {

	public static final ImmutableList<VersionConverter> VERSIONS = ImmutableList.<VersionConverter>of(
			new Amp_2_2_0_1_VersionConverter(),
			new Amp_2_2_0_2_VersionConverter());

	public static void main(String[] args) throws Exception {
		final CommandLine cmdLine = CmdOptions.parseCommandLine(args).orNull();
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
			System.out.println(file.getName());

			final JsonObject root = new Gson().fromJson(new FileReader(file), JsonElement.class).getAsJsonObject();
			final String version = root.get("Aginity Version").getAsJsonObject().get("Metadata Version").getAsString();
			final Iterator<VersionConverter> versionsIterator = VERSIONS.iterator();
			VersionConverter versionConverter = null;
			while (versionsIterator.hasNext()) {
				versionConverter = versionsIterator.next();
				if (!versionConverter.getSourceVersion().equalsIgnoreCase(version)) {
					versionConverter = null;
					continue;
				}

				break;
			}

			Preconditions.checkArgument(versionConverter != null, "Not supported verion: " + version);

			versionConverter.migrate(root);
			while (versionsIterator.hasNext()) {
				versionConverter = versionsIterator.next();
				versionConverter.migrate(root);
			}

			final File destFile = new File(destFolder, file.getName());
			final FileWriter fileWriter = new FileWriter(destFile);
			fileWriter.write(new Gson().toJson(root));
			fileWriter.close();
		}
	}

}
