package com.raghsonline.javadomainobjdifference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Map;

public class PrettyPrintJsonDiff {
    public static void main(String[] args) {
        // Assuming you have a Map<String, Object> containing the differences
        Map<String, Object> jsonDiffMap = getJsonDiffMap();

        // Convert the map to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty printing
        try {
            String jsonString = objectMapper.writeValueAsString(jsonDiffMap);

            // Print the pretty-printed JSON string
            System.out.println(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to simulate obtaining differences between two JSON files as a Map
    private static Map<String, Object> getJsonDiffMap() {
        // Simulate differences in JSON files as a Map
        // Replace this with your actual logic to obtain the differences
        // For demonstration, let's create a sample map
        Map<String, Object> jsonDiffMap = Map.of(
            "key1", "value1",
            "key2", Map.of("nestedKey", "nestedValue"),
            "key3", 123
        );

        System.out.println("jsonDiffMap = " + jsonDiffMap);
        return jsonDiffMap;
    }
}
