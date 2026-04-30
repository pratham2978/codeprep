package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.Submission;
import com.example.demo.repository.SubmissionRepository;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/compile")
@CrossOrigin(origins = "*")
public class CompilerController {

    @Autowired
    private SubmissionRepository submissionRepository;

    public static class CodeRequest {
        public String code;
        public String language;
        public String questionName;
        public String userEmail;
    }

    @PostMapping
    public Submission compileAndSave(@RequestBody CodeRequest request) {
        Submission submission = new Submission();
        submission.setCode(request.code);
        submission.setLanguage(request.language != null ? request.language : "java");
        submission.setQuestionName(request.questionName);
        submission.setUserEmail(request.userEmail != null ? request.userEmail : "anonymous@codeprep.com");

        String output = "";
        String status = "Error";
        
        if ("java".equalsIgnoreCase(submission.getLanguage())) {
            try {
                // Determine class name from code if provided, otherwise default to Main
                String className = "Main"; 
                // Creating isolated temporary directory
                Path tempDir = Files.createTempDirectory("java-exec");
                File sourceFile = new File(tempDir.toFile(), className + ".java");
                Files.writeString(sourceFile.toPath(), request.code);

                // Compile Phase
                Process compileProcess = new ProcessBuilder("javac", sourceFile.getAbsolutePath())
                        .directory(tempDir.toFile())
                        .redirectErrorStream(true)
                        .start();
                
                String compileOutput = readProcessOutput(compileProcess);
                boolean compiled = compileProcess.waitFor(10, TimeUnit.SECONDS);
                
                if (compileProcess.exitValue() != 0) {
                    output = "Compilation Error:\n" + compileOutput;
                    status = "Compilation Error";
                } else {
                    // Execution Phase
                    Process runProcess = new ProcessBuilder("java", className)
                            .directory(tempDir.toFile())
                            .redirectErrorStream(true)
                            .start();
                    
                    output = readProcessOutput(runProcess);
                    boolean finished = runProcess.waitFor(5, TimeUnit.SECONDS);
                    
                    if (!finished) {
                        runProcess.destroyForcibly();
                        output = "Time Limit Exceeded - Execution took > 5 seconds.";
                        status = "TLE";
                    } else if (runProcess.exitValue() != 0) {
                        status = "Runtime Error";
                    } else {
                        // Execution succeeded, but we need to verify logic structure
                        String verificationResult = verifySolution(submission.getQuestionName(), request.code, output);
                        if(verificationResult.equals("Success")) {
                            status = "Executed Successfully";
                        } else {
                            status = "Wrong Answer";
                            output += "\n\n[VERIFICATION FAILED]: " + verificationResult;
                        }
                    }
                }
                
                // Cleanup temp files
                cleanupDirectory(tempDir.toFile());
                
            } catch (Exception e) {
                output = "System Runtime Error: " + e.getMessage();
                status = "System Error";
            }
        } else if ("python".equalsIgnoreCase(submission.getLanguage())) {
            try {
                Path tempDir = Files.createTempDirectory("python-exec");
                File sourceFile = new File(tempDir.toFile(), "solution.py");
                Files.writeString(sourceFile.toPath(), request.code);

                Process runProcess = new ProcessBuilder("python", sourceFile.getAbsolutePath())
                        .directory(tempDir.toFile())
                        .redirectErrorStream(true)
                        .start();

                output = readProcessOutput(runProcess);
                boolean finished = runProcess.waitFor(5, TimeUnit.SECONDS);

                if (!finished) {
                    runProcess.destroyForcibly();
                    output = "Time Limit Exceeded - Execution took > 5 seconds.";
                    status = "TLE";
                } else if (runProcess.exitValue() != 0) {
                    status = "Runtime Error";
                } else {
                    String verificationResult = verifySolution(submission.getQuestionName(), request.code, output);
                    if(verificationResult.equals("Success")) {
                        status = "Executed Successfully";
                    } else {
                        status = "Wrong Answer";
                        output += "\n\n[VERIFICATION FAILED]: " + verificationResult;
                    }
                }
                
                cleanupDirectory(tempDir.toFile());
            } catch (Exception e) {
                output = "System Runtime Error: " + e.getMessage();
                status = "System Error";
            }
        } else if ("cpp".equalsIgnoreCase(submission.getLanguage())) {
            try {
                Path tempDir = Files.createTempDirectory("cpp-exec");
                File sourceFile = new File(tempDir.toFile(), "solution.cpp");
                Files.writeString(sourceFile.toPath(), request.code);

                Process compileProcess = new ProcessBuilder("g++", sourceFile.getAbsolutePath(), "-o", "solution.exe")
                        .directory(tempDir.toFile())
                        .redirectErrorStream(true)
                        .start();

                String compileOutput = readProcessOutput(compileProcess);
                boolean compiled = compileProcess.waitFor(10, TimeUnit.SECONDS);

                if (compileProcess.exitValue() != 0) {
                    output = "Compilation Error:\n" + compileOutput;
                    status = "Compilation Error";
                } else {
                    File exeFile = new File(tempDir.toFile(), "solution.exe");
                    Process runProcess = new ProcessBuilder(exeFile.getAbsolutePath())
                            .directory(tempDir.toFile())
                            .redirectErrorStream(true)
                            .start();

                    output = readProcessOutput(runProcess);
                    boolean finished = runProcess.waitFor(5, TimeUnit.SECONDS);

                    if (!finished) {
                        runProcess.destroyForcibly();
                        output = "Time Limit Exceeded - Execution took > 5 seconds.";
                        status = "TLE";
                    } else if (runProcess.exitValue() != 0) {
                        status = "Runtime Error";
                    } else {
                        String verificationResult = verifySolution(submission.getQuestionName(), request.code, output);
                        if(verificationResult.equals("Success")) {
                            status = "Executed Successfully";
                        } else {
                            status = "Wrong Answer";
                            output += "\n\n[VERIFICATION FAILED]: " + verificationResult;
                        }
                    }
                }

                cleanupDirectory(tempDir.toFile());
            } catch (Exception e) {
                output = "System Runtime Error: " + e.getMessage();
                status = "System Error";
            }
        } else {
             output = "Language not supported yet.";
             status = "Unsupported";
        }
        
        submission.setOutput(output);
        submission.setStatus(status);
        
        // Save the submission data to the MySQL Database!
        return submissionRepository.save(submission);
    }
    
    private String readProcessOutput(Process process) throws IOException {
        StringBuilder out = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line).append("\n");
            }
        }
        return out.toString();
    }
    
    private void cleanupDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                cleanupDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    private String verifySolution(String questionName, String code, String output) {
        if (questionName == null) return "Success"; // Fallback for generic raw IDE bounds

        String lq = questionName.toLowerCase();
        String lc = code.toLowerCase();

        // 1. Linked Lists
        if (lq.contains("linked list") || lq.contains("list ") || lq.contains("nodes")) {
            if (!lc.contains("next") && !lc.contains("head") && !lc.contains("dummy")) {
                return "Your algorithm is missing standard Linked List operations (e.g., node 'next' pointers).";
            }
        }
        
        // 2. Trees / BST
        if (lq.contains("tree") || lq.contains("bst") || lq.contains("ancestor") || lq.contains("traversal")) {
            if (!lc.contains("left") && !lc.contains("right") && !lc.contains("root")) {
                return "Tree problems expect traversal logic using 'left' and 'right' child node references.";
            }
        }
        
        // 3. Binary Search
        if (lq.contains("binary search") || lq.contains("search in") || lq.contains("insert position")) {
            if (!lc.contains("mid") && (!lc.contains("/") || !lc.contains("2"))) {
                return "Binary search requires splitting the search dimension (mid calculation).";
            }
        }
        
        // 4. Graphs, BFS, DFS
        if (lq.contains("graph") || lq.contains("bfs") || lq.contains("dfs") || lq.contains("island") || lq.contains("schedule") || lq.contains("ocean")) {
            if (!lc.contains("visited") && !lc.contains("queue") && !lc.contains("stack") && !lc.contains("boolean[][]")) {
                return "Graph traversals require cycle-prevention (like 'visited' sets) and a Queue/Stack structure.";
            }
        }
        
        // 5. Sliding Window & Two Pointers
        if (lq.contains("window") || lq.contains("substring") || lq.contains("consecutive")) {
            if (!lc.contains("left") && !lc.contains("right") && !lc.contains("max") && !lc.contains("min")) {
                return "Sliding window problems implicitly expect a two-pointer boundary (left/right).";
            }
        }
        
        // 6. Generic Arrays, Stack, and Math (Catch-all for remaining basic ones)
        if (questionName.equalsIgnoreCase("Implement Stack using Arrays")) {
            if (!lc.contains("push") || !lc.contains("pop") || !lc.contains("[")) {
                return "Your class does not contain the required 'push'/'pop' methods using a primitive array.";
            }
        } else if (lq.contains("array") || lq.contains("sum") || lq.contains("water") || lq.contains("stock")) {
            if (!lc.contains("for") && !lc.contains("while") && !lc.contains("stream")) {
                return "Expected an algorithm using an iterative loop approach on the dataset.";
            }
        }

        return "Success";
    }
}
