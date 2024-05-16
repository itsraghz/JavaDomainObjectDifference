package com.raghsonline.javadomainobjdifference.v2;

import com.raghsonline.javadomainobjdifference.Change;
import com.raghsonline.javadomainobjdifference.service.JsonChangeDifferentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class JsonDifferenceServiceTest {

    @Autowired
    private JsonChangeDifferentService jsonChangeDifferentService;

    @Test
    public void testJsonDifferences() throws IOException {

        System.out.println("-------------------------------------");
        System.out.println(" ********    V2 Testing    **********");
        System.out.println("-------------------------------------");

        String testResourcesPath = "src/test/resources";
        List<String> jsonFiles = getAllJsonFiles(testResourcesPath);

        for (int i = 0; i < jsonFiles.size() - 1; i++) {
            String file1 = jsonFiles.get(i);
            String file2 = jsonFiles.get(i + 1);

            String filePath1 = testResourcesPath + "/" + file1;
            String filePath2 = testResourcesPath + "/" + file2;

            System.out.println();
            System.out.println("-------------------------------------");
            System.out.println("[# " + i + "] Comparing differences between " + file1 + " and " + file2 + ":");
            System.out.println("-------------------------------------");
            List<Change> differences = jsonChangeDifferentService.findJsonDifferences(filePath1, filePath2);
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

    private List<String> getAllJsonFiles(String directoryPath) throws IOException {
        return Files.walk(Path.of(directoryPath))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(fileName -> fileName.matches("testcase\\d+\\.json"))
                .sorted()
                .collect(Collectors.toList());
    }
}
