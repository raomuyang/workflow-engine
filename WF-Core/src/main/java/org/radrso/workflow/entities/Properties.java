package org.radrso.workflow.entities;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * Created by Rao-Mengnan
 * on 2017/10/19.
 */
@Data
public class Properties {

    @SerializedName("SCHEMA_START_SIGN")
    String schemaStartSign;
    @SerializedName("SCHEMA_FINISH_SIGN")
    String schemaFinishSign;
    @SerializedName("SCHEMA_INSTANCE_ID_VALUE")
    String schemaInstanceIdValue;
    @SerializedName("OUTPUT_VALUE")
    String outputValue;
    @SerializedName("HEADER_PARAMS_ESCAPE")
    String headerParamsEscape;
    @SerializedName("VALUES_ESCAPE")
    String valuesEscape;
}
