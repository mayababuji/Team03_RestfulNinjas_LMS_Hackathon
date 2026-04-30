package utils;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
    private Map<String, Object> scenarioData = new HashMap<>();

    public void setContext(String key, Object value) {
        scenarioData.put(key, value);
    }

    public Object getContext(String key) {
        return scenarioData.get(key);
    }
}
