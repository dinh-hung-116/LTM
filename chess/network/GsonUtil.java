package chess.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;

/**
 * GsonUtil
 *
 * Dùng chung cho Client + Server
 * để Gson hiểu LocalDate.
 */
public class GsonUtil {

    private GsonUtil() {
        // utility class
    }

    public static Gson createGson() {

        return new GsonBuilder()

                // LocalDate -> JSON String
                .registerTypeAdapter(
                        LocalDate.class,
                        (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                                new JsonPrimitive(src.toString())
                )

                // JSON String -> LocalDate
                .registerTypeAdapter(
                        LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                                LocalDate.parse(json.getAsString())
                )
                .create();
    }
}