package providers;


import io.elastic.api.SelectModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;

/**
 * Implementation of {@link SelectModelProvider} providing a select model for the pet status select.
 * The provide sends a HTTP request to the Petstore API and returns a JSON object as shown below.
 *
 * <pre>
 *     {
 *         "available": "Available",
 *         "sold": "Sold",
 *         "pending": "Pending"
 *     }
 * </pre>
 *
 * The value in the returned JSON object are used to display option's labels.
 */
public class AuthModelProvider implements SelectModelProvider {

    private static final Logger logger = LoggerFactory.getLogger(AuthModelProvider.class);

    @Override
    public JsonObject getSelectModel(final JsonObject configuration) {

        logger.info("input model configuration");
        final JsonObjectBuilder builder = Json.createObjectBuilder();



        return builder.build();
    }
}
