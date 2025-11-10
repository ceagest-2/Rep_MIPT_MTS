package com.mipt.uriilesnikov.io;

import java.io.*;

public class TextFileAnalyzer {

    public static class AnalysisResult {
        private final long lineCount;  // количество строк в файле
        private final long wordCount; // количество слов в файле
        private final long charCount; // количество символов в файле

        public AnalysisResult(long lineCount, long wordCount, long charCount) {
            this.lineCount = lineCount;
            this.wordCount = wordCount;
            this.charCount = charCount;
        }

        public long getLineCount() {
            return lineCount;
        }

        public long getWordCount() {
            return wordCount;
        }

        public long getCharCount() {
            return charCount;
        }

        @Override
        public String toString() {
            return "Количество строк: " + lineCount + "; Количество слов: " + wordCount + "; Количество символов: " + charCount + ".";
        }
    }

    public AnalysisResult analyzeFile(String filePath) throws IOException {
        long lineCount = 0;
        long wordCount = 0;
        long charCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                charCount += line.length();
                String[] words = line.trim().split("\\s+");
                if (!line.trim().isEmpty()) {
                    wordCount += words.length;
                }
            }
        }

        return new AnalysisResult(lineCount, wordCount, charCount);
    }

    public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("Количество строк: " + result.getLineCount());
            writer.newLine();
            writer.write("Количество слов: " + result.getWordCount());
            writer.newLine();
            writer.write("Количество символов: " + result.getCharCount());
            writer.newLine();
        }
    }
}
