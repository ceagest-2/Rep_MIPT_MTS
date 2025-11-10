package com.mipt.uriilesnikov.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessorTest {

    @Test
    void testSplitAndMergeFile() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile("test", ".dat");
        byte[] testData = new byte[1500];
        new Random().nextBytes(testData);
        Files.write(testFile, testData);

        String outputDir = Files.createTempDirectory("parts").toString();
        List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

        assertEquals(3, parts.size(), "Должно быть 3 части для 1500 байт при размере части 500");
        assertTrue(Files.exists(parts.get(0)), "Часть 1 должна существовать");
        assertTrue(Files.exists(parts.get(1)), "Часть 2 должна существовать");
        assertTrue(Files.exists(parts.get(2)), "Часть 3 должна существовать");

        long sizePart1 = Files.size(parts.get(0));
        long sizePart2 = Files.size(parts.get(1));
        long sizePart3 = Files.size(parts.get(2));
        assertEquals(500, sizePart1, "Размер части 1 должен быть 500 байт");
        assertEquals(500, sizePart2, "Размер части 2 должен быть 500 байт");
        assertEquals(500, sizePart3, "Размер части 3 должен быть 500 байт");

        Path mergedFile = Files.createTempFile("merged", ".dat");
        processor.mergeFiles(parts, mergedFile.toString());

        assertArrayEquals(Files.readAllBytes(testFile), Files.readAllBytes(mergedFile), "Исходный и объединённый файлы должны быть идентичны");
    }
}
