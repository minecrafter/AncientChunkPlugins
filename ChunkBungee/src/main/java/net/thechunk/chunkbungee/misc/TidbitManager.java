package net.thechunk.chunkbungee.misc;

import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TidbitManager {
    @Getter
    private static Map<String, String> tidbits = new HashMap<>();

    public static void loadTidbitsFrom(File file) throws IOException {
        // This is a case where using SnakeYAML does make sense.
        Yaml yaml = new Yaml();
        Object tmp;
        try (FileInputStream fis = new FileInputStream(file)) {
            tmp = yaml.load(fis);
        }
        if (tmp instanceof Map) {
            Map<?, ?> map = (Map) tmp;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                    tidbits.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }
    }
}
