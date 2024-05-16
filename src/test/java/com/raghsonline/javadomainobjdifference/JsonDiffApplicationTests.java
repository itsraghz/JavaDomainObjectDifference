package com.raghsonline.javadomainobjdifference;

import com.raghsonline.javadomainobjdifference.service.JsonChangeDifferentService;
import com.raghsonline.javadomainobjdifference.service.JsonDiffService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
class JsonDiffApplicationTests {

    @Autowired
    private JsonDiffService jsonDiffService;

    @Autowired
    private JsonChangeDifferentService jsonChangeDiffService;

    @Test
    void testJsonDiff() throws IOException {
        // Load JSON files from resources directory
        String jsonString1 = readJsonFile("src/test/resources/json1.json");
        String jsonString2 = readJsonFile("src/test/resources/json2.json");

        // Calculate and print JSON differences
        //jsonDiffService.findJsonDifferences(jsonString1, jsonString2);
        List<Change> changes = jsonChangeDiffService.findJsonDifferences(jsonString1, jsonString2);
        System.out.println("changes = " + changes);
        System.out.println("changes size = " + changes.size());
        changes.forEach(System.out::println);
    }

    private String readJsonFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }
}
