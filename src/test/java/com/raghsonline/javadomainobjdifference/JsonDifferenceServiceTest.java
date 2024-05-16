package com.raghsonline.javadomainobjdifference;

import com.raghsonline.javadomainobjdifference.service.JsonChangeDifferentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
public class JsonDifferenceServiceTest {

    @Autowired
    private JsonChangeDifferentService jsonChangeDifferentService;

    private String readJsonFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }

    @Test
    public void testJsonDifferences() throws IOException {
        String[] originalFiles = {"testcase1.json", "testcase2.json",
                "testcase3.json", "testcase4.json",
                "testcase5.json", "testcase6.json",
                "testcase7.json"};

        for (int i = 0; i < originalFiles.length - 1; i++) {
            String file1 = originalFiles[i];
            String file2 = originalFiles[i + 1];

            String filePath1 = "src/test/resources/" + file1;
            String filePath2 = "src/test/resources/" + file2;

            // Load JSON files from resources directory
            String jsonString1 = readJsonFile(filePath1);
            String jsonString2 = readJsonFile(filePath2);

            System.out.println();
            System.out.println("-------------------------------------");
            System.out.println("[# " + i + "] Comparing differences between " + file1 + " and " + file2 + ":");
            System.out.println("-------------------------------------");

            List<Change> differences = jsonChangeDifferentService.findJsonDifferences(jsonString1, jsonString2);
            if (differences.isEmpty()) {
                System.out.println("No differences found.");
            } else {
                System.out.println("Differences found:");
                for (Change change : differences) {
                    System.out.println(change);
                }
            }
            System.out.println("-------------------------------------");
            System.out.println();
        }
    }
}

