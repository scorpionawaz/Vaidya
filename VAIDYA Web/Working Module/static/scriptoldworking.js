

//Anuja

document.addEventListener("DOMContentLoaded", function () {
    const chatContainer = document.getElementById("chat");
    const chatForm = document.getElementById("chat-form");
    const userInput = document.getElementById("user-input");

    function addMessage(message, isUser = false) {
        const messageElement = document.createElement("div");
        messageElement.className = "message" + (isUser ? " user" : "");
        messageElement.textContent = message;
        chatContainer.appendChild(messageElement);

        chatContainer.scrollTop = chatContainer.scrollHeight;
    }

    function displayChatbotResponse(response) {
        // speakHindi(response,0.5);
        addMessage("Machinix: " + response);
        // Convert the chatbot's response to audio
        // speak(response);
        playAudio() ;
        
    }

    //depreacted funtion
    function speak(text) {
        const synth = window.speechSynthesis;
        const utterance = new SpeechSynthesisUtterance(text);
        synth.speak(utterance);
    }
    
    // calls the latest generated audio file 
    function playAudio() {
        fetch("/audio")
            .then(response => response.blob())
            .then(data => {
                var audioBlob = new Blob([data], { type: 'audio/mpeg' });
                var audioUrl = URL.createObjectURL(audioBlob);
                var audio = new Audio(audioUrl);
                audio.play();
            })
            .catch((error) => {
                console.error("Error fetching audio:", error);
            });
    }
    chatForm.addEventListener("submit", function (e) {
        e.preventDefault();
        const userMessage = userInput.value;
        addMessage("You: " + userMessage, true);

        sendUserMessage(userMessage);

        // Clear the input field after sending the user's message
        userInput.value = "";
    });

    function sendUserMessage(message) {
        fetch("/adroit", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ question: message }),

        })
            .then((response) => response.json())
            .then((data) => {
                // Display the chatbot's response
                displayChatbotResponse(data.answer);
            })
            .catch((error) => {
                console.error("Error:", error);
            });
    }

    const recognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)();
    const textInput = document.getElementById('user-input');
    const microphoneIcon = document.getElementById('microphone-icon');

    recognition.continuous = false;
    recognition.lang = 'en-US';

    let isListening = false;

    recognition.onresult = function(event) {
        const result = event.results[event.results.length - 1][0].transcript;
        textInput.value = result;

        // Display the speech-recognized message in the chat
        addMessage("You: " + result, true);
        let input = document.getElementById("user-input");
        input.style.animation ="none";

        // Send the speech-recognized message to the chatbot
        sendUserMessage(result);


        textInput.value = "";
    };

    recognition.onstart = function() {
        isListening = true;
    };

    recognition.onend = function() {
        isListening = false;
    };

    recognition.onerror = function(event) {
        console.error('Speech recognition error:', event.error);
        isListening = false;
    };

    microphoneIcon.onclick = toggleRecognition;

    function toggleRecognition() {
        let input = document.getElementById("user-input");
        if (isListening) {
            recognition.stop();
        } else {
            recognition.start();
            input.style.animation = "runningBorder 1s linear infinite";
        }
    }
    //depreacted funtion
    
    function speakHindi(text, speed) {
        // Check browser support for SpeechSynthesis API
        if (!window.speechSynthesis) {
          console.error("SpeechSynthesis API not supported by your browser.");
          return;
        }
      
        // Create a new SpeechSynthesisUtterance object
        const utterance = new SpeechSynthesisUtterance();
      
        // Set utterance properties
        utterance.text = text;
        utterance.lang = 'hi-IN'; // Hindi language code
      
        // Adjust pitch and volume based on desired speed
        if (speed > 1) {
          utterance.pitch = 1 + (speed - 1) * 0.2; // Increase pitch slightly for faster speeds
          utterance.volume = 0.8; // Lower volume slightly to avoid distortion
        } else if (speed < 1) {
          utterance.pitch = 1 - (1 - speed) * 0.2; // Decrease pitch slightly for slower speeds
          utterance.volume = 1; // Maintain volume for slower speeds
        } else {
          utterance.pitch = 1;
          utterance.volume = 1;
        }
      
        // Speak the utterance
        window.speechSynthesis.speak(utterance);
      }

    
});






//Virendra

let timeout = async ()=>{
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve()
        }, 1000);
    })
}


async function OnLoad(){
    let loader = document.getElementsByClassName("loader")[0];
    await timeout()
    loader.style.display = "none";
}




// nawaz language setter 
document.addEventListener("DOMContentLoaded", function() {
    // Get all language buttons
    const langButtons = document.querySelectorAll('.lang');

    // Add click event listener to each button
    langButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            // Get the language parameter based on the button's text content
            let langParam;
            switch(this.textContent.trim()) {
                case 'मराठी':
                    langParam = 'mr';
                    break;
                case 'ENGLISH':
                    langParam = 'en';
                    break;
                case 'हिन्दी':
                    langParam = 'hi';
                    break;
                default:
                    langParam = 'en'; // Default to English if language is not recognized
            }

            // Make a fetch request to /language endpoint with the selected language parameter
            fetch(`/lang?lang=${langParam}`)
                .then(response => response.text())
                .then(data => {
                    // Update the content of an element (e.g., a div) with the response from the server
                    // Replace '#target-element' with the actual ID of the element you want to update
                    document.querySelector('#target-element').innerHTML = data;
                })
                .catch(error => console.error('Error:', error));
        });
    });
});
