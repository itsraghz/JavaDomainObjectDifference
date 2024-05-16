package com.raghsonline.javadomainobjdifference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class JsonComparator {

    private final ObjectMapper objectMapper;

    public JsonComparator() {
        this.objectMapper = new ObjectMapper();
    }

    public void compareJson(String json1, String json2) throws IOException {
        JsonNode node1 = objectMapper.readTree(json1);
        JsonNode node2 = objectMapper.readTree(json2);
        findDifferencesRecursive(node1, node2, "");
    }

    private void findDifferencesRecursive(JsonNode node1, JsonNode node2, String path) {
        if (node1 == null && node2 == null) {
            return; // Both nodes are null, no differences to report
        }

        if (node1 == null) {
            System.out.println("ADD: " + path + " - " + node2);
            return; // Node1 is null, node2 is not null, so node2 is added
        }

        if (node2 == null) {
            System.out.println("REMOVE: " + path + " - " + node1);
            return; // Node2 is null, node1 is not null, so node1 is removed
        }

        if (node1.isObject() && node2.isObject()) {
            // Handle objects
            handleObjects(node1, node2, path);
        } else if (node1.isArray() && node2.isArray()) {
            // Handle arrays
            handleArrays(node1, node2, path);
        } else {
            // Handle leaf nodes
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

        // Find keys present in node1 but not in node2
        Set<String> keysOnlyInNode1 = new HashSet<>(keys1);
        keysOnlyInNode1.removeAll(keys2);
        keysOnlyInNode1.forEach(key -> System.out.println("REMOVE: " + (path.isEmpty() ? key : path + "." + key)));

        // Find keys present in node2 but not in node1
        Set<String> keysOnlyInNode2 = new HashSet<>(keys2);
        keysOnlyInNode2.removeAll(keys1);
        keysOnlyInNode2.forEach(key -> System.out.println("ADD: " + (path.isEmpty() ? key : path + "." + key)));

        // Recursively check for differences in nested objects
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
        // Compare leaf values
        if (!Objects.equals(node1, node2)) {
            System.out.println("MODIFY: " + path + " - {left=" + node1 + ", right=" + node2 + "}");
        }
    }

    public static void main(String[] args) throws IOException {
        String json1 = """
                {
                  "customer": {
                    "id": "CUST123",
                    "name": "Rajesh Kumar",
                    "email": "rajesh.kumar@example.com",
                    "phone": "+919876543210",
                    "address": {
                      "street": "45 Gandhi Road",
                      "city": "Chennai",
                      "state": "Tamil Nadu",
                      "postal_code": "600001",
                      "country": "India"
                    },
                    "orders": [
                      {
                        "order_id": "ORDER001",
                        "order_date": "2024-05-15T09:00:00+05:30",
                        "products": [
                          {
                            "product_id": "PROD001",
                            "name": "Traditional Saree",
                            "price": 1500,
                            "discount": 0.1,
                            "quantity": 2
                          },
                          {
                            "product_id": "PROD002",
                            "name": "Silk Dhoti",
                            "price": 1000,
                            "discount": 0.05,
                            "quantity": 1
                          }
                        ],
                        "total_amount": 3050,
                        "payment": {
                          "method": "Credit Card",
                          "amount_paid": 3050,
                          "transaction_id": "TRANS001",
                          "payment_date": "2024-05-15T10:00:00+05:30"
                        },
                        "tracking_info": {
                          "created_date": "2024-05-15T09:00:00+05:30",
                          "created_by": "admin",
                          "updated_date": "2024-05-15T09:30:00+05:30",
                          "updated_by": "admin"
                        }
                      },
                      {
                        "order_id": "ORDER002",
                        "order_date": "2024-05-16T10:00:00+05:30",
                        "products": [
                          {
                            "product_id": "PROD003",
                            "name": "Handcrafted Jewelry Set",
                            "price": 2500,
                            "discount": 0.2,
                            "quantity": 1
                          }
                        ],
                        "total_amount": 2000,
                        "payment": {
                          "method": "UPI",
                          "amount_paid": 2000,
                          "transaction_id": "TRANS002",
                          "payment_date": "2024-05-16T11:00:00+05:30"
                        },
                        "tracking_info": {
                          "created_date": "2024-05-16T10:00:00+05:30",
                          "created_by": "admin",
                          "updated_date": "2024-05-16T10:30:00+05:30",
                          "updated_by": "admin"
                        }
                      }
                    ]
                  }
                }""";

        String json2 = """
                {
                  "customer": {
                    "id": "CUST123",
                    "name": "Rajesh Kumar",
                    "email": "rajesh.kumar@example.com",
                    "phone": "+919876543210",
                    "address": {
                      "street": "45 Gandhi Road",
                      "city": "Chennai",
                      "state": "Tamil Nadu",
                      "postal_code": "600001",
                      "country": "India"
                    },
                    "orders": [
                      {
                        "order_id": "ORDER001",
                        "order_date": "2024-05-15T09:00:00+05:30",
                        "products": [
                          {
                            "product_id": "PROD001",
                            "name": "Traditional Saree",
                            "price": 1500,
                            "discount": 0.1,
                            "quantity": 2
                          },
                          {
                            "product_id": "PROD002",
                            "name": "Silk Dhoti",
                            "price": 1000,
                            "discount": 0.05,
                            "quantity": 1
                          }
                        ],
                        "total_amount": 3050,
                        "payment": {
                          "method": "Credit Card",
                          "amount_paid": 3050,
                          "transaction_id": "TRANS001",
                          "payment_date": "2024-05-15T10:00:00+05:30"
                        },
                        "tracking_info": {
                          "created_date": "2024-05-15T09:00:00+05:30",
                          "created_by": "admin",
                          "updated_date": "2024-05-15T09:30:00+05:30",
                          "updated_by": "admin"
                        }
                      }
                    ]
                  }
                }""";

        System.out.println("Input JSON 1:");
        System.out.println(json1);
        System.out.println("\nInput JSON 2:");
        System.out.println(json2);

        JsonComparator comparator = new JsonComparator();
        System.out.println("\nComparison Output:");
        comparator.compareJson(json1, json2);
    }
}
