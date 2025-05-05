package com.example.chatbot;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HindiActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private StringBuilder sentenceBuffer = new StringBuilder();
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    // List to store conversation history in the format expected by the chat API
    private List<JSONObject> conversationHistory;

    // Speech Recognition components
    private static final int RECORD_AUDIO_PERMISSION_CODE = 1;
    private SpeechRecognizer speechRecognizer;
    private ImageButton micButton;
    private TextView buttonPress, pp, bb;
    private ImageView topImage;
    private boolean isListening = false;

    // Logging tag
    private static final String TAG = "HindiActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hindi);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.forLanguageTag("hi-IN")); // Hindi voice
                textToSpeech.setSpeechRate(0.8f);     // 30% faster
                textToSpeech.setPitch(1.0f);          // slightly lower pitch
            }
        });

        // Initialize UI components
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        micButton = findViewById(R.id.micButton);
        buttonPress = findViewById(R.id.buttonpress);
        pp = findViewById(R.id.pp);
        bb = findViewById(R.id.bb);
        topImage = findViewById(R.id.topImage);

        // Initialize chat components
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Initialize conversation history
        conversationHistory = new ArrayList<>();

        // Check and request audio permission
        checkPermission();

        // Initialize speech recognizer
        initializeSpeechRecognizer();

        // Set up mic button click listener
        micButton.setOnClickListener(v -> {
            if (!isListening) {
                startListening();
            } else {
                stopListening();
            }
        });

        // Set up message edit text action listener
        messageEditText.setOnEditorActionListener((v, actionId, event) -> {
            String userMessage = messageEditText.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                sendMessage(userMessage);
                messageEditText.setText("");
            }
            return true;
        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_CODE);
        }
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    Log.d(TAG, "Ready for speech");
                    isListening = true;
                    // Hide the specified views
                    buttonPress.setVisibility(View.GONE);
                    pp.setVisibility(View.GONE);
                    bb.setVisibility(View.GONE);
                    topImage.setVisibility(View.GONE);

                    // Visual feedback for mic button
                    micButton.setImageResource(android.R.drawable.ic_btn_speak_now);
                }

                @Override
                public void onBeginningOfSpeech() {
                    Log.d(TAG, "Beginning of speech");
                }

                @Override
                public void onRmsChanged(float rmsdB) {
                    // Can be used to show volume level
                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                    // Not used in this implementation
                }

                @Override
                public void onEndOfSpeech() {
                    Log.d(TAG, "End of speech");
                    isListening = false;
                    // Reset mic button to original state
                    micButton.setImageResource(android.R.drawable.ic_btn_speak_now);
                }

                @Override
                public void onError(int error) {
                    String errorMessage;
                    switch (error) {
                        case SpeechRecognizer.ERROR_AUDIO:
                            errorMessage = "Audio recording error";
                            break;
                        case SpeechRecognizer.ERROR_CLIENT:
                            errorMessage = "Client side error";
                            break;
                        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                            errorMessage = "Insufficient permissions";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK:
                            errorMessage = "Network error";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                            errorMessage = "Network timeout";
                            break;
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            errorMessage = "No speech input";
                            break;
                        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                            errorMessage = "RecognitionService busy";
                            break;
                        case SpeechRecognizer.ERROR_SERVER:
                            errorMessage = "Server error";
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            errorMessage = "No speech input";
                            break;
                        default:
                            errorMessage = "Unknown error";
                            break;
                    }
                    Log.e(TAG, "Error: " + errorMessage);
                    Toast.makeText(HindiActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    isListening = false;
                    micButton.setImageResource(android.R.drawable.ic_btn_speak_now);
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String recognizedText = matches.get(0);
                        Log.d(TAG, "Recognized: " + recognizedText);
                        // Automatically send the recognized text
                        sendMessage(recognizedText);
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    // Not used in this implementation
                }

                @Override
                public void onEvent(int eventType, Bundle params) {
                    // Not used in this implementation
                }
            });
        } else {
            Toast.makeText(this, "Speech recognition not available on this device", Toast.LENGTH_LONG).show();
        }
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN"); // Hindi language
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true); // Try to use offline recognition if available

        try {
            speechRecognizer.startListening(intent);
            // Visual feedback
            micButton.setImageResource(android.R.drawable.ic_media_pause);
        } catch (Exception e) {
            Toast.makeText(this, "Error starting speech recognition: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error starting speech recognition", e);
        }
    }

    private void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            isListening = false;
            micButton.setImageResource(android.R.drawable.ic_btn_speak_now);
        }
    }

    private void chatWithHistory(String userMessage) {
        OkHttpClient client = new OkHttpClient();

        try {
            // Add the new user message to conversation history
            JSONObject userMessageObj = new JSONObject();
            userMessageObj.put("role", "user");
            userMessageObj.put("content", userMessage);
            conversationHistory.add(userMessageObj);

            // Create messages array for the API request
            JSONArray messagesArray = new JSONArray();
            for (JSONObject message : conversationHistory) {
                messagesArray.put(message);
            }

            // Create the request JSONs
            JSONObject requestJson = new JSONObject();
            requestJson.put("model", "hf.co/nawazadroit/vaidya_midnight_q4_K_m"); // Use your model name here
            requestJson.put("messages", messagesArray);
            requestJson.put("stream", true);

            // Create request body
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    requestJson.toString()
            );

            // Build the request
            Request request = new Request.Builder()
                    .url("http://localhost:11434/api/chat")
                    .post(body)
                    .build();

            // Execute the request
            client.newCall(request).enqueue(new Callback() {
                StringBuilder fullResponse = new StringBuilder();
                StringBuilder sentenceBuffer = new StringBuilder();
                int messageIndex = -1;
                ExecutorService ttsExecutor = Executors.newSingleThreadExecutor(); // Sequential TTS execution

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("CHAT_API", "Error: " + e.getMessage());
                    runOnUiThread(() -> {
                        Toast.makeText(HindiActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e("CHAT_API", "Unsuccessful response: " + response.code());
                        return;
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        try {
                            JSONObject obj = new JSONObject(line);

                            // Handle the streaming response
                            if (obj.has("message")) {
                                JSONObject messageObj = obj.getJSONObject("message");
                                String content = messageObj.getString("content");

                                // Append to full response
                                fullResponse.append(content);
                                sentenceBuffer.append(content);

                                // Sentence-level TTS logic with delimiters
                                String sentenceText = sentenceBuffer.toString();
                                int splitIndex = findDelimiterIndex(sentenceText);

                                while (splitIndex != -1) {
                                    String completeSentence = sentenceText.substring(0, splitIndex + 1).trim();
                                    String remaining = sentenceText.substring(splitIndex + 1);

                                    String finalSentence = completeSentence;
                                    ttsExecutor.execute(() -> speakOffline(finalSentence));

                                    sentenceBuffer = new StringBuilder(remaining);
                                    sentenceText = sentenceBuffer.toString();
                                    splitIndex = findDelimiterIndex(sentenceText);
                                }

                                // UI update
                                runOnUiThread(() -> {
                                    if (messageIndex == -1) {
                                        ChatMessage msg = new ChatMessage(content, false);
                                        chatMessages.add(msg);
                                        messageIndex = chatMessages.size() - 1;
                                    } else {
                                        chatMessages.get(messageIndex).setMessage(fullResponse.toString());
                                    }
                                    chatAdapter.notifyItemChanged(messageIndex);
                                    chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                                });
                            }

                            // Check if this is the final message
                            if (obj.has("done") && obj.getBoolean("done")) {
                                // Add the assistant's message to the conversation history
                                JSONObject assistantMessage = new JSONObject();
                                assistantMessage.put("role", "assistant");
                                assistantMessage.put("content", fullResponse.toString());
                                conversationHistory.add(assistantMessage);

                                // Speak any remaining text
                                if (sentenceBuffer.length() > 0) {
                                    String finalRemaining = sentenceBuffer.toString().trim();
                                    if (!finalRemaining.isEmpty()) {
                                        ttsExecutor.execute(() -> speakOffline(finalRemaining));
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            Log.e("CHAT_API", "JSON parsing error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (JSONException e) {
            Log.e("CHAT_API", "JSON error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        buttonPress.setVisibility(View.GONE);
        pp.setVisibility(View.GONE);
        bb.setVisibility(View.GONE);
        topImage.setVisibility(View.GONE);

        // Add user's message to the UI
        chatMessages.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);

        // Call the chat API with conversation history
        chatWithHistory(message);
    }

    private int findDelimiterIndex(String text) {
        int minIndex = -1;
        for (char c : new char[]{'.', ',', '?', '।','!',':'}) {
            int index = text.indexOf(c);
            if (index != -1 && (minIndex == -1 || index < minIndex)) {
                minIndex = index;
            }
        }
        return minIndex;
    }

    private void speakOffline(String sentence) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(sentence, TextToSpeech.QUEUE_ADD, null, null);
        } else {
            textToSpeech.speak(sentence, TextToSpeech.QUEUE_ADD, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}