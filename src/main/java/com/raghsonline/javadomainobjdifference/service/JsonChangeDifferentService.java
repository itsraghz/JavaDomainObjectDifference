package com.raghsonline.javadomainobjdifference.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raghsonline.javadomainobjdifference.Change;
import com.raghsonline.javadomainobjdifference.ChangeType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class JsonChangeDifferentService {

    private final ObjectMapper objectMapper;

    public JsonChangeDifferentService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Change> findJsonDifferences(String jsonString1, String jsonString2) throws IOException {
        List<Change> changes = new ArrayList<>();
        //JsonNode jsonNode1 = objectMapper.readTree(new File(filePath1));
        //JsonNode jsonNode2 = objectMapper.readTree(new File(filePath2));
        JsonNode jsonNode1 = objectMapper.readTree(jsonString1);
        JsonNode jsonNode2 = objectMapper.readTree(jsonString2);
        findDifferenceRecursive(jsonNode1, jsonNode2, "", changes);
        return changes;
    }

    private void findDifferenceRecursive(JsonNode node1, JsonNode node2, String path, List<Change> changes) {
        if (node1 == null && node2 != null) {
            changes.add(Change.builder()
                    .changeType(ChangeType.ADDED)
                    .componentName(path)
                    .currentValue(node2.toString())
                    .build());
            return;
        } else if (node1 != null && node2 == null) {
            changes.add(Change.builder()
                    .changeType(ChangeType.REMOVED)
                    .componentName(path)
                    .previousValue(node1.toString())
                    .build());
            return;
        }

        if (node1 != null && node1.isObject() && node2.isObject()) {
            handleObjects(node1, node2, path, changes);
        } else if ((node1 != null && node1.isArray()) && node2.isArray()) {
            handleArrays(node1, node2, path, changes);
        } else {
            handleLeafNodes(node1, node2, path, changes);
        }
    }

    private void handleObjects(JsonNode node1, JsonNode node2, String path, List<Change> changes) {
        Iterator<Map.Entry<String, JsonNode>> fields1 = node1.fields();
        Iterator<Map.Entry<String, JsonNode>> fields2 = node2.fields();

        while (fields1.hasNext()) {
            Map.Entry<String, JsonNode> field1 = fields1.next();
            String fieldName = field1.getKey();
            JsonNode value1 = field1.getValue();
            JsonNode value2 = node2.get(fieldName);

            findDifferenceRecursive(value1, value2, path.isEmpty() ? fieldName : path + "." + fieldName, changes);
        }

        while (fields2.hasNext()) {
            Map.Entry<String, JsonNode> field2 = fields2.next();
            String fieldName = field2.getKey();
            JsonNode value2 = field2.getValue();
            JsonNode value1 = node1.get(fieldName);

            if (value1 == null) {
                changes.add(Change.builder()
                        .changeType(ChangeType.ADDED)
                        .componentName(path.isEmpty() ? fieldName : path + "." + fieldName)
                        .currentValue(value2.toString())
                        .build());
            }
        }
    }

    private void handleArrays(JsonNode node1, JsonNode node2, String path, List<Change> changes) {
        int size1 = node1.size();
        int size2 = node2.size();

        for (int i = 0; i < Math.max(size1, size2); i++) {
            JsonNode childNode1 = i < size1 ? node1.get(i) : null;
            JsonNode childNode2 = i < size2 ? node2.get(i) : null;
            findDifferenceRecursive(childNode1, childNode2, path + "[" + i + "]", changes);
        }
    }

    private void handleLeafNodes(JsonNode node1, JsonNode node2, String path, List<Change> changes) {
        if (!node1.equals(node2)) {
            if (node1.isNull() && node2.isNull()) {
                return;
            }
            changes.add(Change.builder()
                    .changeType(ChangeType.CHANGE)
                    .componentName(path)
                    .currentValue(node2.toString())
                    .previousValue(node1.toString())
                    .build());
        }
    }
}
