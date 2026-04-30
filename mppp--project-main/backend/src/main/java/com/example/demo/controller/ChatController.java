package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    // Plug in your Gemini API Key in application.properties!
    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<?> processVoiceChat(@RequestBody Map<String, String> payload) {
        String userTranscript = payload.get("message");
        
        if (userTranscript == null || userTranscript.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No message provided"));
        }
        
        if (geminiApiKey == null || geminiApiKey.isEmpty() || geminiApiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
            return ResponseEntity.ok(Map.of("reply", "I am currently running in a simulated mode because the API key has not been configured yet. Please open application.properties and add your Gemini API Key to enable real time processing."));
        }

        try {
            // Construct the context for the Interviewer Persona
            String prompt = "You are Alex, an expert software engineer conducting a technical mock interview with a candidate for a Software Engineering role. " +
                            "The candidate is solving the 'Two Sum' problem in Java. Speak naturally, concisely, and conversationally in 1-3 sentences. " +
                            "Do not use markdown, emojis, asterisks, or complex formatting because your text will be read aloud by a text-to-speech engine. " +
                            "Here is what the candidate just said to you: '" + userTranscript + "'";

            // Build the Gemini-compatible payload structure natively
            Map<String, Object> parts = new HashMap<>();
            parts.put("text", prompt);
            
            Map<String, Object> contents = new HashMap<>();
            contents.put("parts", new Object[]{parts});
            
            Map<String, Object> body = new HashMap<>();
            body.put("contents", new Object[]{contents});

            String jsonBody = objectMapper.writeValueAsString(body);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-lite-latest:generateContent?key=" + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Gemini API Error: " + response.body());
                return ResponseEntity.ok(Map.of("reply", "I'm sorry, I'm having trouble connecting to my neural network right now. Please check the backend console."));
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            String aiReply = rootNode.path("candidates")
                                     .get(0)
                                     .path("content")
                                     .path("parts")
                                     .get(0)
                                     .path("text")
                                     .asText();

            // Stripping any potential markdown generated out of habit by the LLM
            aiReply = aiReply.replaceAll("[*#_`~]", "");

            return ResponseEntity.ok(Map.of("reply", aiReply));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("reply", "Sorry, my brain encountered a server error processing that."));
        }
    }
}
