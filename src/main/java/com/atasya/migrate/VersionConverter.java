package com.atasya.migrate;

import com.google.gson.JsonObject;

public interface VersionConverter {
	String getSourceVersion();
	void migrate(JsonObject object);
}
