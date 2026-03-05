package kg.attractor.java.server;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Utils {
    public static Map<String, String> parseUrlEncoded(String rawLines, String delimiter) {
        return Arrays.stream(rawLines.split(delimiter))
                .map(Utils::decode)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
    }

    static Optional<Map.Entry<String, String>> decode(String kv) {
        if (!kv.contains("=")) return Optional.empty();
        String[] pair = kv.split("=");
        if (pair.length != 2) return Optional.empty();
        String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
        String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
        return Optional.of(Map.entry(key, value));
    }
}