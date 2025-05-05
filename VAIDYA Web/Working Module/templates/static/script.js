// Anuja's Code
async function toggleInput(toggle){
    try {

        if(toggle)
            document.getElementById("user-input").disabled = true;
        else
          document.getElementById("user-input").disabled = false;   

    } catch (error) {
        console.log("Error:",error);
     }
}
// Add an event listener for when the DOM content is loaded
document.addEventListener("DOMContentLoaded", async function () {



    try {
        // toggleInput(true);
        // Get necessary elements from the DOM
        const chatContainer = document.getElementById("chat");
        const chatForm = document.getElementById("chat-form");
        const userInput = document.getElementById("user-input");

        // Function to add a message to the chat
        async function addMessage(message, isUser = false) {
            try {
                // Create a message element
                const messageElement = document.createElement("div");
                messageElement.className = "message" + (isUser ? " user" : "");
                messageElement.textContent = message;
                chatContainer.appendChild(messageElement);

                // Scroll to the bottom of the chat container
                chatContainer.scrollTop = chatContainer.scrollHeight;
            } catch (error) {
                console.error("Error adding message to chat:", error);
                throw error; // Propagate the error
            }
        }

        // Function to display the chatbot's response
        async function displayChatbotResponse(response) {
            try {
                const chatContainer = document.getElementById("chat");
                
  
                chatContainer.removeChild(chatContainer.lastChild)

                // Add the chatbot's response to the chat
                await addMessage("VAIDYA : " + response);
                document.getElementsByClassName("inner")[0].style.display = "none";
                document.getElementsByClassName("inner")[1].style.display = "none";
                document.getElementsByClassName("inner")[2].style.display = "none";
                // toggleInput(false)
                // Play audio response
                await playAudio();
                console.log("waited for playaudio");
                toggleRecognition();
                console.log("waited for Toggle");
            } catch (error) {
                console.error("Error displaying chatbot response:", error);
                throw error; // Propagate the error
            }
        }

        // Function to speak text
        function speak(text) {
            try {
                // Speech synthesis API to speak the text
                const synth = window.speechSynthesis;
                const utterance = new SpeechSynthesisUtterance(text);
                synth.speak(utterance);
            } catch (error) {
                console.error("Error speaking text:", error);
                throw error; // Propagate the error
            }
        }

        // Function to play audio
        async function playAudio() {
            try {
                const response = await fetch("/audio");
                const data = await response.blob();
                const audioBlob = new Blob([data], { type: 'audio/mpeg' });
                const audioUrl = URL.createObjectURL(audioBlob);
                const audio = new Audio(audioUrl);

                // Create a promise that resolves when the audio playback ends
                const audioPromise = new Promise(resolve => {
                    audio.addEventListener('ended', resolve);
                });

                // Start playback
                audio.play();

                // Wait for the playback to finish
                await audioPromise;
            } catch (error) {
                console.error("Error fetching or playing audio:", error);
                throw error; // Propagate the error
            }
        }

        // Event listener for form submission (sending a message)
        chatForm.addEventListener("submit", async function (e) {
            try {
                e.preventDefault();
                const userMessage = userInput.value;
                // Add user's message to the chat
                await addMessage("You : " + userMessage, true);
                document.getElementById("user-input").textContent = "";
                document.getElementsByClassName("inner")[0].style.display = "block";
                document.getElementsByClassName("inner")[1].style.display = "block";
                document.getElementsByClassName("inner")[2].style.display = "block";

                document.getElementById("user-input").innerHTML = "";
                // toggleInput(true);

                const chatContainer = document.getElementById("chat");
                const messageElement = document.createElement("div");
                messageElement.className = "message" ;
                // messageElement.textContent = "VAIDYA : Generating....";
                messageElement.style.height = "40px"
                messageElement.innerHTML = `VAIDYA : 
                <div id="load">
                    <div>G</div>
                    <div>N</div>
                    <div>I</div>
                    <div>T</div>
                    <div>A</div>
                    <div>R</div>
                    <div>E</div>
                    <div>N</div>
                    <div>E</div>
                    <div>G</div>
                </div>`;
                chatContainer.appendChild(messageElement);



                // Clear animation and hide elements
                const input = document.getElementById("user-input");
                input.style.animation = "none";
                const vanish = document.getElementsByClassName("vanish");
                for (let i = 0; i < vanish.length; i++) {
                    vanish[i].style.display = "none";
                }
                // Send user's message to the chatbot
                await sendUserMessage(userMessage);
                userInput.value = ""; // Clear the input field
            } catch (error) {
                console.error("Error submitting form:", error);
            }
        });

        // Function to send user's message to the server
        async function sendUserMessage(message) {
            try {
                // Send user's message to the server
                const response = await fetch("/adroit", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ question: message }),
                });
                const data = await response.json();
                // Display the chatbot's response
                await displayChatbotResponse(data.answer);
            } catch (error) {
                console.error("Error sending user message or displaying chatbot response:", error);
                throw error; // Propagate the error
            }
        }

        // Initialize speech recognition
        const recognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)();
        const textInput = document.getElementById('user-input');
        const microphoneIcon = document.getElementById('microphone-icon');

        recognition.continuous = false;
        recognition.lang = 'en-US';

        let isListening = false;

        // Event listener for speech recognition result
        recognition.onresult = async function (event) {
            try {
                const result = event.results[event.results.length - 1][0].transcript;
                textInput.value = result;

                // Add the recognized speech to the chat
                await addMessage("You: " + result, true);
                document.getElementsByClassName("inner")[0].style.display = "block";
                document.getElementsByClassName("inner")[1].style.display = "block";
                document.getElementsByClassName("inner")[2].style.display = "block";

                document.getElementById("user-input").innerHTML = "";
                // toggleInput(true);

                const chatContainer = document.getElementById("chat");
                const messageElement = document.createElement("div");
                messageElement.className = "message" ;
                messageElement.style.height = "40px"
                messageElement.innerHTML = `VAIDYA : 
                <div id="load">
                    <div>G</div>
                    <div>N</div>
                    <div>I</div>
                    <div>T</div>
                    <div>A</div>
                    <div>R</div>
                    <div>E</div>
                    <div>N</div>
                    <div>E</div>
                    <div>G</div>
                </div>`;
                chatContainer.appendChild(messageElement);

                const input = document.getElementById("user-input");
                input.style.animation = "none";
                const vanish = document.getElementsByClassName("vanish");
                for (let i = 0; i < vanish.length; i++) {
                    vanish[i].style.display = "none";
                }

                // Send the recognized speech to the chatbot
                await sendUserMessage(result);

                textInput.value = "";
            } catch (error) {
                console.error("Error handling speech recognition result:", error);
            }
        };

        // Event listener for speech recognition start
        recognition.onstart = function () {
            isListening = true;
        };

        // Event listener for speech recognition end
        recognition.onend = function () {
            isListening = false;
            const input = document.getElementById("user-input");
            input.style.animation = "none";
        };

        // Event listener for speech recognition error
        recognition.onerror = function (event) {
            console.error('Speech recognition error:', event.error);
            isListening = false;
        };

        // Event listener for microphone icon click (toggle speech recognition)

        microphoneIcon.onclick = toggleRecognition;

        // Function to toggle speech recognition
        function toggleRecognition() {
            const input = document.getElementById("user-input");
            if (isListening) {
                recognition.stop();
                input.style.animation = "none";
                console.log("stop");
            } else {
                recognition.start();
                input.style.animation = "runningBorder 1s linear infinite";
                console.log("start");
            }
        }

        // Function to speak Hindi text
        function speakHindi(text, speed) {
            try {
                if (!window.speechSynthesis) {
                    console.error("SpeechSynthesis API not supported by your browser.");
                    return;
                }

                const utterance = new SpeechSynthesisUtterance();
                utterance.text = text;
                utterance.lang = 'hi-IN';

                if (speed > 1) {
                    utterance.pitch = 1 + (speed - 1) * 0.2;
                    utterance.volume = 0.8;
                } else if (speed < 1) {
                    utterance.pitch = 1 - (1 - speed) * 0.2;
                    utterance.volume = 1;
                } else {
                    utterance.pitch = 1;
                    utterance.volume = 1;
                }

                window.speechSynthesis.speak(utterance);
            } catch (error) {
                console.error("Error speaking Hindi text:", error);
            }
        }
    } catch (error) {
        console.error("Error in DOMContentLoaded event listener:", error);
    }
});

// Virendra's Code

// Function to create a timeout
let timeout = async () => {
    try {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve()
            }, 1000);
        });
    } catch (error) {
        console.error("Error in timeout function:", error);
    }
};

// Function to execute when the page is loaded
async function OnLoad() {
    try {
        let loader = document.getElementsByClassName("loader")[0];
        await timeout();
        loader.style.display = "none";
    } catch (error) {
        console.error("Error in OnLoad function:", error);
    }
}

// Event listener for question 1
document.getElementById("box-1").addEventListener("click", e => {
    try {
        document.getElementById("user-input").value = document.getElementById("box-1").innerHTML;
    } catch (error) {
        console.error("Error in box-1  event listener:", error);
    }
});

// Event listener for question 2
document.getElementById("box-2").addEventListener("click", e => {
    try {
        document.getElementById("user-input").value = document.getElementById("box-2").innerHTML;
    } catch (error) {
        console.error("Error in box-2 click event listener:", error);
    }
});

// Event listener for question 3
document.getElementById("box-3").addEventListener("click", e => {
    try {
        document.getElementById("user-input").value = document.getElementById("box-3").innerHTML;
    } catch (error) {
        console.error("Error in box-3 click event listener:", error);
    }
});

// Event listener for question 4
document.getElementById("box-4").addEventListener("click", e => {
    try {
        document.getElementById("user-input").value = document.getElementById("box-4").innerHTML;
    } catch (error) {
        console.error("Error in box-4 click event listener:", error);
    }
});



// Event listener for English language selection
document.getElementsByClassName("eng")[0].addEventListener("click", e => {
    try {
        // Change text content to English
        document.getElementsByClassName("hello")[0].innerHTML = "HELLO,";
        document.getElementsByClassName("inograte")[0].innerHTML = "HOW CAN I HELP YOU WITH THIS?";
        document.getElementById("box-1").innerHTML = "CAN I BOOK YOUR APPOINTMENT";
        document.getElementById("box-2").innerHTML = "NAVIGATE TO PATAIENT";
        document.getElementById("box-3").innerHTML = "please tell INFORMATION ABOUT HEALTH CARE SCHEMES";
        document.getElementById("box-4").innerHTML = "I WANT TO REGISTER";
        toggleInput(false);
    } catch (error) {
        console.error("Error in English language selection event listener:", error);
    }
});

// Event listener for Hindi language selection
document.getElementsByClassName("hindi")[0].addEventListener("click", e => {
    try {
        // Change text content to Hindi
        document.getElementsByClassName("hello")[0].innerHTML = "नमस्ते,";
        document.getElementsByClassName("inograte")[0].innerHTML = "में आपकी क्या सहायता कर सकती हूँ?";
        document.getElementById("box-1").innerHTML = "क्या मैं आपका नियुक्ति बुकिंग कर सकती हूँ";
        document.getElementById("box-2").innerHTML = "रोगी के पास जाएँ";
        document.getElementById("box-3").innerHTML = "स्वास्थ्य देखभाल योजनाओं के बारे में जानकारी";
        document.getElementById("box-4").innerHTML = "मैं पंजीकरण करना चाहता हूँ";
        toggleInput(false);
    } catch (error) {
        console.error("Error in Hindi language selection event listener:", error);
    }
});

// Event listener for Marathi language selection
document.getElementsByClassName("marathi")[0].addEventListener("click", e => {
    try {
        // Change text content to Marathi
        document.getElementsByClassName("hello")[0].innerHTML = "नमस्कार,";
        document.getElementsByClassName("inograte")[0].innerHTML = "मी तुम्हाला कशी मदत करू शकते?";
        document.getElementById("box-1").innerHTML = "मी तुमची अपॉइंटमेंट बुक करू शकती का";
        document.getElementById("box-2").innerHTML = "रुग्णाला भेट द्या";
        document.getElementById("box-3").innerHTML = "आरोग्य सेवा योजनांची माहिती";
        document.getElementById("box-4").innerHTML = "मला नोंदणी करायची आहे";
        toggleInput(false);
    } catch (error) {
        console.error("Error in Marathi language selection event listener:", error);
    }

    
});

// Event listener for language selection buttons
document.addEventListener("DOMContentLoaded", function () {
    try {
        // Get all language buttons
        const langButtons = document.querySelectorAll('.lang');

        // Add click event listener to each button
        langButtons.forEach(function (button) {
            button.addEventListener('click', function () {
                // Get the language parameter based on the button's text content
                let langParam;
                switch (this.textContent.trim()) {
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
                    .catch(error => console.error('Error fetching language:', error));
            });
        });
    } catch (error) {
        console.error("Error in language selection buttons event listener:", error);
    }
});