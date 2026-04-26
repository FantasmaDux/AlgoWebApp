package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.api.dto.TestResultDto;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JavaCompilerService implements CompilerService {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/solver/";
    private static final long TIMEOUT_SECONDS = 5;

    @Override
    public ExecutionResult compileAndRun(String code, String language, List<TestCase> testCases) {
        if (!"java".equalsIgnoreCase(language)) {
            return new ExecutionResult(false, null, "Unsupported language: " + language, 0L, List.of());
        }

        try {
            Files.createDirectories(Paths.get(TEMP_DIR));
            String className = extractClassName(code);
            String fileName = className + ".java";
            Path sourcePath = Paths.get(TEMP_DIR, fileName);
            Files.writeString(sourcePath, code);

            // Компиляция
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourcePath.toFile());
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

            boolean success = task.call();
            fileManager.close();

            if (!success) {
                String errors = diagnostics.getDiagnostics().stream()
                        .map(d -> d.toString())
                        .collect(Collectors.joining("\n"));
                return new ExecutionResult(false, null, "Compilation error:\n" + errors, 0L, List.of());
            }

            // Запуск тестов
            long startTime = System.nanoTime();
            List<TestResultDto> testResults = new ArrayList<>();
            int passed = 0;

            for (int i = 0; i < testCases.size(); i++) {
                CompilerService.TestCase testCase = testCases.get(i);
                TestResultDto result = runSingleTest(className, testCase.input(), testCase.expected(), i + 1);
                testResults.add(result);
                if (result.isPassed()) passed++;
            }

            long executionTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            boolean allPassed = passed == testCases.size();

            return new ExecutionResult(
                    allPassed,
                    allPassed ? "All tests passed!" : String.format("Passed %d/%d tests", passed, testCases.size()),
                    null,
                    executionTime,
                    testResults
            );

        } catch (Exception e) {
            log.error("Execution error", e);
            return new ExecutionResult(false, null, "Runtime error: " + e.getMessage(), 0L, List.of());
        }
    }

    private TestResultDto runSingleTest(String className, String input, String expected, int testNumber) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "java", "-cp", TEMP_DIR, className
            );
            pb.redirectErrorStream(true);

            if (input != null && !input.isEmpty()) {
                pb.redirectInput(ProcessBuilder.Redirect.PIPE);
            }

            Process process = pb.start();

            if (input != null && !input.isEmpty()) {
                try (OutputStream os = process.getOutputStream()) {
                    os.write(input.getBytes());
                    os.flush();
                }
            }

            StringBuilder output = new StringBuilder();
            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return TestResultDto.builder()
                        .testNumber(testNumber)
                        .passed(false)
                        .input(input)
                        .expectedOutput(expected)
                        .actualOutput("Timeout (" + TIMEOUT_SECONDS + "s)")
                        .errorMessage("Execution timeout")
                        .build();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            String actualOutput = output.toString().trim();
            boolean passed = actualOutput.equals(expected.trim());

            return TestResultDto.builder()
                    .testNumber(testNumber)
                    .passed(passed)
                    .input(input)
                    .expectedOutput(expected)
                    .actualOutput(actualOutput)
                    .errorMessage(passed ? null : "Output mismatch")
                    .build();

        } catch (Exception e) {
            return TestResultDto.builder()
                    .testNumber(testNumber)
                    .passed(false)
                    .input(input)
                    .expectedOutput(expected)
                    .actualOutput(null)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    private String extractClassName(String code) {
        var matcher = java.util.regex.Pattern.compile("public\\s+class\\s+(\\w+)").matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Main";
    }
}