package com.atasya.migrate.versions;

import com.atasya.migrate.VersionConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Amp_2_2_0_1_VersionConverter implements VersionConverter {

	@Override
	public String getSourceVersion() {
		return "amp_2.2.0.1";
	}

	@Override
	public void migrate(JsonObject object) {
		object.get("Aginity Version").getAsJsonObject().addProperty("Metadata Version", "amp_2.2.0.2");

		for (JsonElement element : object.get("Fact Columns").getAsJsonArray()) {
			final JsonObject column = element.getAsJsonObject();
			column.addProperty("ColumnPhysicalName", column.get("ColumnName").getAsString());
		}

		for (JsonElement element : object.get("Fact Table").getAsJsonArray()) {
			final JsonObject table = element.getAsJsonObject();
			final String id = table.get("ID").getAsString();
			table.addProperty("ID", table.get("Name").getAsString());
			table.addProperty("Name", id);
		}
	}

}
