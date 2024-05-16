package com.raghsonline.javadomainobjdifference.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JsonDiffService {

    private final ObjectMapper objectMapper;

    @Autowired
    public JsonDiffService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private void printJson(String jsonFilePath) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(jsonFilePath)) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
        }
    }

    private void findDifferencesRecursive(JsonNode node1, JsonNode node2, String path) {
        if (node1 == null && node2 == null) {
            return;
        }

        if (node1 == null) {
            System.out.println("ADD: " + path + " - " + node2);
            return;
        }

        if (node2 == null) {
            System.out.println("REMOVE: " + path + " - " + node1);
            return;
        }

        if (node1.isObject() && node2.isObject()) {
            handleObjects(node1, node2, path);
        } else if (node1.isArray() && node2.isArray()) {
            handleArrays(node1, node2, path);
        } else {
            handleLeafNodes(node1, node2, path);
        }
    }

    private void handleObjects(JsonNode node1, JsonNode node2, String path) {
        Iterator<String> fieldNames1 = node1.fieldNames();
        Iterator<String> fieldNames2 = node2.fieldNames();

        Set<String> keys1 = new HashSet<>();
        Set<String> keys2 = new HashSet<>();

        while (fieldNames1.hasNext()) {
            keys1.add(fieldNames1.next());
        }
        while (fieldNames2.hasNext()) {
            keys2.add(fieldNames2.next());
        }

        Set<String> keysOnlyInNode1 = new HashSet<>(keys1);
        keysOnlyInNode1.removeAll(keys2);
        keysOnlyInNode1.forEach(key -> System.out.println("REMOVE: " + (path.isEmpty() ? key : path + "." + key)));

        Set<String> keysOnlyInNode2 = new HashSet<>(keys2);
        keysOnlyInNode2.removeAll(keys1);
        keysOnlyInNode2.forEach(key -> System.out.println("ADD: " + (path.isEmpty() ? key : path + "." + key)));

        keys1.retainAll(keys2);
        for (String key : keys1) {
            findDifferencesRecursive(node1.get(key), node2.get(key), path.isEmpty() ? key : path + "." + key);
        }
    }

    private void handleArrays(JsonNode node1, JsonNode node2, String path) {
        int size1 = node1.size();
        int size2 = node2.size();

        int maxSize = Math.max(size1, size2);
        for (int i = 0; i < maxSize; i++) {
            findDifferencesRecursive(i < size1 ? node1.get(i) : null, i < size2 ? node2.get(i) : null, path + "[" + i + "]");
        }
    }

    private void handleLeafNodes(JsonNode node1, JsonNode node2, String path) {
        if (!Objects.equals(node1, node2)) {
            System.out.println("MODIFY: " + path + " - {left=" + node1 + ", right=" + node2 + "}");
        }
    }

    public void findJsonDifferences(String jsonString1, String jsonString2) throws IOException {
        // Convert JSON strings to JSON objects
        JsonNode jsonNode1 = objectMapper.readTree(jsonString1);
        JsonNode jsonNode2 = objectMapper.readTree(jsonString2);

        findDifferencesRecursive(jsonNode1, jsonNode2, "");
    }
    private Map<String, Object> convertJsonNodeToMapSimple(JsonNode jsonNode) {
        if (jsonNode instanceof ObjectNode) {
            return objectMapper.convertValue(jsonNode, Map.class);
        }
        return new HashMap<>();
    }

    private Map<String, Object> convertJsonNodeToMapRecursive(JsonNode jsonNode) {
        Map<String, Object> map = new HashMap<>();
        jsonNode.fields().forEachRemaining(entry -> {
            if (entry.getValue().isObject()) {
                map.put(entry.getKey(), convertJsonNodeToMap(entry.getValue()));
            } else {
                map.put(entry.getKey(), entry.getValue());
            }
        });
        return map;
    }

    public void findJsonDifferencesMap(String jsonString1, String jsonString2) throws IOException {
        // Convert JSON strings to JSON objects
        JsonNode jsonNode1 = objectMapper.readTree(jsonString1);
        JsonNode jsonNode2 = objectMapper.readTree(jsonString2);

        // Convert JSON nodes to Maps
        Map<String, Object> map1 = convertJsonNodeToMap(jsonNode1);
        Map<String, Object> map2 = convertJsonNodeToMap(jsonNode2);

        System.out.println("Keys in Map 1");
        System.out.println("----------------");
        map1.keySet().forEach(System.out::println);
        System.out.println();

        System.out.println("Keys in Map 2");
        System.out.println("----------------");
        map1.keySet().forEach(System.out::println);
        System.out.println();

        // Find keys that exist in both maps
        Set<String> commonKeys = map1
                .keySet()
                .stream()
                .filter(map2::containsKey)
                .collect(Collectors.toSet());

        System.out.println("commonKeys = " + commonKeys);
        System.out.println("commonKeys.size() = " + commonKeys.size());

        // Compare values of common keys
        for (String key : commonKeys) {
            Object value1 = map1.get(key);
            Object value2 = map2.get(key);
            if (!Objects.equals(value1, value2)) {
                System.out.println("MODIFY: " + key
                        + " - \n{left=" + value1
                        + ", \nright=" + value2 + "}");
            }
        }

        // Find keys only in map1
        Set<String> keysOnlyInMap1 = map1
                .keySet()
                .stream()
                .filter(key -> !map2.containsKey(key))
                .collect(Collectors.toSet());
        keysOnlyInMap1.forEach(key -> System.out.println(
                "REMOVE: " + key + " - " + map1.get(key)));

        // Find keys only in map2
        Set<String> keysOnlyInMap2 = map2
                .keySet()
                .stream()
                .filter(key -> !map1.containsKey(key))
                .collect(Collectors.toSet());
        keysOnlyInMap2.forEach(key -> System.out.println(
                "ADD: " + key + " - " + map2.get(key)));
    }

    private Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
        Map<String, Object> map = new HashMap<>();
        jsonNode.fields().forEachRemaining(entry -> {
            if (entry.getValue().isObject()) {
                map.put(entry.getKey(), convertJsonNodeToMap(entry.getValue()));
            } else {
                map.put(entry.getKey(), entry.getValue().asText());
            }
        });
        return map;
    }

    public void findJsonDifferencesOld2(String jsonString1, String jsonString2) throws IOException {
        // Convert JSON strings to JSON objects
        JsonNode jsonNode1 = objectMapper.readTree(jsonString1);
        JsonNode jsonNode2 = objectMapper.readTree(jsonString2);

        // Convert JSON nodes to Maps
        Map<String, Object> map1 = convertJsonNodeToMap(jsonNode1);
        Map<String, Object> map2 = convertJsonNodeToMap(jsonNode2);

        // Find differences between two maps
        MapDifference<String, Object> difference = Maps.difference(map1, map2);

        // Log the differences to console
        System.out.println("Differences:");
        difference.entriesDiffering().forEach((key, value) -> {
            System.out.println("MODIFY: " + key + " - " + value);
        });
        difference.entriesOnlyOnLeft().forEach((key, value) -> {
            System.out.println("REMOVE: " + key + " - " + value);
        });
        difference.entriesOnlyOnRight().forEach((key, value) -> {
            System.out.println("ADD: " + key + " - " + value);
        });

        difference.entriesDiffering().forEach((key, value) -> {
            String leftValue = null;
            String rightValue = null;

            // Extract actual values from JSON nodes
            JsonNode leftNode = jsonNode1.get(key);
            JsonNode rightNode = jsonNode2.get(key);

            // Check if JSON nodes are not null
            if (leftNode != null && rightNode != null) {
                // Extract actual values based on the type
                if (leftNode.isBoolean()) {
                    leftValue = String.valueOf(leftNode.asBoolean());
                } else if (leftNode.isInt()) {
                    leftValue = String.valueOf(leftNode.asInt());
                } else if (leftNode.isDouble()) {
                    leftValue = String.valueOf(leftNode.asDouble());
                } else {
                    leftValue = leftNode.asText();
                }

                if (rightNode.isBoolean()) {
                    rightValue = String.valueOf(rightNode.asBoolean());
                } else if (rightNode.isInt()) {
                    rightValue = String.valueOf(rightNode.asInt());
                } else if (rightNode.isDouble()) {
                    rightValue = String.valueOf(rightNode.asDouble());
                } else {
                    rightValue = rightNode.asText();
                }
            }
            System.out.println("MODIFY: " + key + " - {left=" + leftValue + ", right=" + rightValue + "}");
        });
        difference.entriesOnlyOnLeft().forEach((key, value) -> {
            System.out.println("REMOVE: " + jsonNode1.get(key) + " - " + value);
        });
        difference.entriesOnlyOnRight().forEach((key, value) -> {
            System.out.println("ADD: " + key + " - " + value);
        });
    }

    // Helper method to convert JsonNode value to string
    private String getValueAsString(JsonNode node) {
        if (node.isTextual()) {
            return node.asText();
        } else if (node.isBoolean()) {
            return Boolean.toString(node.asBoolean());
        } else if (node.isNumber()) {
            return node.asText(); // Assuming numbers are represented as strings
        } else {
            return node.toString(); // Fallback to toString for other types
        }
    }

    public void findJsonDifferencesOld(String jsonString1, String jsonString2) throws IOException {
        // Convert JSON strings to JSON objects
        JsonNode jsonNode1 = objectMapper.readTree(jsonString1);
        JsonNode jsonNode2 = objectMapper.readTree(jsonString2);

        // Convert JSON objects to Maps
        //Map<String, Object> map1 = objectMapper.convertValue(jsonNode1, Map.class);
        //Map<String, Object> map2 = objectMapper.convertValue(jsonNode2, Map.class);

        // Convert JSON objects to Maps
        Map<String, Object> map1 = objectMapper.convertValue(jsonNode1, new TypeReference<>() {});
        Map<String, Object> map2 = objectMapper.convertValue(jsonNode2, new TypeReference<Map<String, Object>>() {});


        // Find differences between two maps
        MapDifference<String, Object> difference = Maps.difference(map1, map2);

        // Print the JSON structures
        System.out.println("JSON 1:");
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode1));
        System.out.println("JSON 2:");
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode2));

        // Log the differences to console
        System.out.println("Differences:");
        difference.entriesDiffering().forEach((key, value) -> System.out.println("MODIFY: " + key + " - " + value));
        difference.entriesOnlyOnLeft().forEach((key, value) -> System.out.println("REMOVE: " + key + " - " + value));
        difference.entriesOnlyOnRight().forEach((key, value) -> System.out.println("ADD: " + key + " - " + value));
    }
}
