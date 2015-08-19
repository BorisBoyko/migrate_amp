package com.atasya.migrate.versions;

import com.atasya.migrate.VersionConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
CHANGES
MappingParameters
  rename
    MappingKey -> MetadataObjectKey
  default value
    MetadataObjectTypeCode = 'PM'
  as is
    Cardinality
    Code
    DataType
    Description
    Key
    ParamValue
    Scope

PubParameterValues
  rename
    ConfigurationName -> InheritedConfigurationName
    MetadataObjectKey -> InheritedMetadataObjectKey
    MetadataObjectTypeCode -> InheritedMetadataObjectTypeCode
    SubjectAreaID -> InheritedSubjectAreaID
    TemplateName -> InheritedTemplateName
    MappingParameterKey -> MetadataObjectParameterKey
  calculate
    NVL(PubDimensionKey, PubFactKey) -> LocalMetadataObjectKey
    CASE WHEN PubDimensionKey IS NOT NULL THEN 'PD' WHEN PubFactKey IS NOT NULL THEN 'PF' ELSE NULL END -> LocalMetadataObjectTypeCode
  as is
    DataType
    Key
    ParamCode
    ParamValue
    ParamValueCardinality
    ParamValueType
 */
public class Amp_2_2_0_2_VersionConverter implements VersionConverter {

	@Override
	public String getSourceVersion() {
		return "amp_2.2.0.2";
	}

	@Override
	public void migrate(JsonObject object) {
		object.get("Aginity Version").getAsJsonObject().addProperty("Metadata Version", "amp_2.2.0.3");

		for (JsonElement element : object.get("MappingParameters").getAsJsonArray()) {
			final JsonObject column = element.getAsJsonObject();
			rename(column, "MappingKey", "MetadataObjectKey");
			column.addProperty("MetadataObjectTypeCode", "PM");
		}

		for (JsonElement element : object.get("PubParameterValues").getAsJsonArray()) {
			final JsonObject table = element.getAsJsonObject();
			rename(table, "ConfigurationName", "InheritedConfigurationName");
			rename(table, "MetadataObjectKey", "InheritedMetadataObjectKey");
			rename(table, "MetadataObjectTypeCode", "InheritedMetadataObjectTypeCode");
			rename(table, "SubjectAreaID", "InheritedSubjectAreaID");
			rename(table, "TemplateName", "InheritedTemplateName");
			rename(table, "MappingParameterKey", "MetadataObjectParameterKey");

			Long localMetadataObjectKey = null;
			String localMetadataObjectTypeCode = null;
			final JsonElement pubDimensionKeyElement = table.get("PubDimensionKey");
			final JsonElement pubFactKeyElement = table.get("PubFactKey");
			if (pubDimensionKeyElement != null && !pubDimensionKeyElement.isJsonNull()) {
				localMetadataObjectKey = pubDimensionKeyElement.getAsLong();
				localMetadataObjectTypeCode = "PD";
			} else if (pubFactKeyElement != null && !pubFactKeyElement.isJsonNull()) {
				localMetadataObjectKey = pubFactKeyElement.getAsLong();
				localMetadataObjectTypeCode = "PF";
			}

			table.addProperty("LocalMetadataObjectKey", localMetadataObjectKey);
			table.addProperty("LocalMetadataObjectTypeCode", localMetadataObjectTypeCode);
		}
	}

	public static void rename(JsonObject column, final String oldName, final String newName) {
		final JsonElement value = column.get(oldName);
		column.add(newName, value);
		column.remove(oldName);
	}
}