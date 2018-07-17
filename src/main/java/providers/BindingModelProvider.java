package providers;

import io.elastic.api.JSON;
import io.elastic.api.SelectModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;

public class BindingModelProvider implements SelectModelProvider {

    private static final Logger logger = LoggerFactory.getLogger(OperationModelProvider.class);

    @Override
    public JsonObject getSelectModel(final JsonObject configuration) {

        logger.info("input model configuration", JSON.stringify(configuration));
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("getName", "getName");
        builder.add("getTime", "getTime");


        return builder.build();
    }
}
