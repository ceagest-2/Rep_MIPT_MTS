package com.mipt.uriilesnikov.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {

    /**
     * Разбивает файл на части указанного размера
     * @param sourcePath путь к исходному файлу
     * @param outputDir директория для сохранения частей
     * @param partSize размер каждой части в байтах
     * @return список путей к созданным частям
     */
    public List<Path> splitFile(String sourcePath, String outputDir, int partSize) throws IOException {
        if (partSize <= 0) {
            throw new IllegalArgumentException("Размер части должен быть целым положительным числом");
        }

        Path source = Paths.get(sourcePath);

        if (!Files.exists(source)) {
            throw new IOException("Исходный файл не существует: " + source);
        }
        if (!Files.isReadable(source)) {
            throw new IOException("Невозможно прочитать исходный файл: " + source);
        }

        Path outputDirectory = Paths.get(outputDir);
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }

        String filename = source.getFileName().toString();
        List<Path> parts = new ArrayList<>();

        try (FileChannel inChannel = FileChannel.open(source, StandardOpenOption.READ)) {
            long fileSize = inChannel.size();
            long pos = 0;
            int partNum = 1;

            ByteBuffer buffer = ByteBuffer.allocate(partSize);

            while (pos < fileSize) {
                int bytesToRead = (int) Math.min(partSize, fileSize - pos);
                buffer.clear();
                if (bytesToRead < buffer.capacity()) {
                    buffer.limit(bytesToRead);
                }

                int bytesRead = inChannel.read(buffer);
                if (bytesRead == -1) {break;}

                String partName = filename + "." + partNum;
                Path pathPath = outputDirectory.resolve(partName);
                try (FileChannel outChannel = FileChannel.open(pathPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        outChannel.write(buffer);
                    }
                }

                parts.add(pathPath);
                pos += bytesRead;
                partNum++;
                buffer.clear();
            }
        }
        return parts;
    }

    /**
     * Объединяет части файла обратно в один файл
     * @param partPaths список путей к частям файла (в правильном порядке)
     * @param outputPath путь для результирующего файла
     */
    public void mergeFiles(List<Path> partPaths, String outputPath) throws IOException {
        if (partPaths == null || partPaths.isEmpty()) {
            throw new IllegalArgumentException("Список частей не может быть пуст");
        }

        Path output = Paths.get(outputPath);
        Path outputDir = output.getParent();
        if (outputDir != null && !Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        for (Path part : partPaths) {
            if (!Files.exists(part)) {
                throw new IOException("Часть не найдена: " + part);
            }
        }

        Files.deleteIfExists(output);

        try (FileChannel outChannel = FileChannel.open(output, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            ByteBuffer buffer = ByteBuffer.allocate(8192);

            for (Path part : partPaths) {
                try (FileChannel inChannel = FileChannel.open(part, StandardOpenOption.READ)) {
                    while (inChannel.read(buffer) != -1) {
                        buffer.flip();
                        while (buffer.hasRemaining()) {
                            outChannel.write(buffer);
                        }
                        buffer.clear();
                    }
                }
            }
        }
    }
}
