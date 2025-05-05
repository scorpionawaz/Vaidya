import speech_recognition as sr
from gtts import gTTS
from playsound import playsound

def speech_to_audio():
    """Combines speech recognition and text-to-speech to convert voice input to audio.
     
     hi-IN
     hi
     
     mr
     
     en
     
    Returns:
        None
    """

    # Initialize the recognizer for speech recognition
    recognizer = sr.Recognizer()

    while True:
        try:
            # Capture audio from the microphone
            print("Speak something in Hindi:")
            with sr.Microphone() as source:
                audio = recognizer.listen(source)

            # Use Google Web Speech API for recognition
            text = recognizer.recognize_google(audio, language='en-IN')
            print("You said:", text)

            # Convert recognized text to Hindi speech (if desired)
            # if input("Convert text to audio (y/n)? ").lower() == 'y':
            if True:
                tts = gTTS(text, lang='en')
                tts.save("output.mp3")
                playsound("output.mp3")

        except sr.UnknownValueError:
            print("Google Web Speech API could not understand audio.")
        except sr.RequestError as e:
            print(f"Could not request results from Google Web Speech API; {e}")

# Start the speech-to-audio process
speech_to_audio()
