import json
import os
from flask import Flask, render_template, request, jsonify , send_file
from gtts import gTTS
# from pydub import AudioSegment
from llamacall import call_llama_api , case_paper_auth
from registering import register
from prompts import topicclassifier,registration_prompt_generator
from addhar import onetimescan
from addhar_no_touching import send_to_json
from fpdf import FPDF
# GETTING NEW PROMPTS

from printerprint import print_registration_details,print_patient_details
# from appointmentbooking import appointmentbooking
app = Flask(__name__)

def generate_pdf(name, aadhar, dob, gender, filename="registration_details.pdf"):
    pdf = FPDF()
    pdf.add_page()
    pdf.set_font("Arial", size=12)
    
    pdf.cell(200, 10, txt="Registration Details", ln=True, align='C')
    pdf.ln(10)
    
    pdf.cell(200, 10, txt=f"Name: {name}", ln=True)
    pdf.cell(200, 10, txt=f"Aadhar: {aadhar}", ln=True)
    pdf.cell(200, 10, txt=f"DOB: {dob}", ln=True)
    pdf.cell(200, 10, txt=f"Gender: {gender}", ln=True)
    
    pdf.output(filename)
    print(f"PDF saved as {filename}")

# function to save data in excel file 
def save_data_to_file(data, file_name="data.json"):
    # Check if the file already exists
    if os.path.exists(file_name):
        # If the file exists, load the current data from it
        with open(file_name, 'r') as file:
            try:
                existing_data = json.load(file)  # Load the existing data
            except json.JSONDecodeError:
                existing_data = []  # If file is empty or corrupted, start fresh
    else:
        existing_data = []  # No file exists, start fresh

    # Append the new data
    existing_data.append(data)

    # Write the updated data back to the file
    with open(file_name, 'w') as file:
        json.dump(existing_data, file, indent=4)


@app.route("/")
def index():
    return render_template("index.html")


firstprompt = ""
registration_prompt = ""

lang="en"
@app.route('/lang')
def langg():
    global lang
    global registration_prompt
    lang = request.args.get('lang')  # Get the language parameter from the request
    if lang is None:
        lang = 'en'  # Set default language to English if parameter is missing
    global firstprompt 
    if appointmentbooking == False and registering == False:
       firstprompt = topicclassifier(lang)
       print("language is set to " + lang)
       return firstprompt
    elif registering ==True:
       registration_prompt = registration_prompt_generator(lang)
       return registration_prompt
        

history = []
response = ""
appointmentbooking = False
registering = False
docscan = False

@app.route("/adroit", methods=["POST"])
def ask():
    global appointmentbooking
    global registering
    global response
    global docscan
    user_question = request.json.get("question") 
    
    if appointmentbooking == False and docscan == False:
        global responses
        print("CALLING THE MAIN API ")
        response = call_llama_api(user_question,history) 
        print(response)
        # scanning an data 
        if  'SCANOBJ001' in response :
            docscan = True 
            print("RESPONSE TO SCAN NOW")
            response = "Please keep Adhar infront of the scanner :"
            tts = gTTS(response, lang="hi", slow=False)
            tts.save("audio.mp3")
            return jsonify({"answer": response})
        
        if 'bookappdoc' in response :
            appointmentbooking = True
            response = response
            print_patient_details(response)
            appointmentbooking = False             
            tts = gTTS(response, lang="hi", slow=False)
            tts.save("audio.mp3")
            return jsonify({"answer": response})

        
        history.append({"user": user_question})  # Add the current user question to history
        history.append({"vaidya:":response})  # Add the system response to history
        print(history)
        tts = gTTS(response, lang="hi", slow=False)
        tts.save("audio.mp3")
        return jsonify({"answer": response})
    
    if docscan == True:
        try:
            print("DOCSCAN TRUE : OPENING CAMERA ")
            json_data = onetimescan()

            # Extract the first (and only) key from the JSON
            timestamp_key = list(json_data.keys())[0]
            patient_data = json_data[timestamp_key]
            
            # Handle missing or incorrect values
            name = patient_data.get("name", "").strip()
            aadhar = str(patient_data.get("aadhaar_number", "Not Available"))
            dob = patient_data.get("dob", "Not Available")
            gender_list = patient_data.get("gender", [])
            
            # Convert gender list to a readable format
            gender = gender_list[0] if gender_list else "Not Specified"
            
            # # Ensure 'dob' is in a valid format
            # if dob.isdigit() and len(dob) == 8:  # Assuming format should be YYYYMMDD
            #     formatted_dob = f"{dob[:4]}-{dob[4:6]}-{dob[6:]}"
            # else:
            #     formatted_dob = "Invalid DOB"
            
        
            
            # Print final values for debugging
            print(f"Name: {name or 'Not Available'}")
            print(f"Aadhar: {aadhar}")
            print(f"DOB: {dob}")
            print(f"Gender: {gender}")
            
            # Pass values to the print function
            print_registration_details(name or "Not Available", aadhar, dob, gender)

            save_data_to_file( json_data )
            generate_pdf(name, aadhar, dob, gender)
            docscan = False
            response = str(json_data)
            
            tts = gTTS("Thank you !!! You are Registered . please take the print!", lang="hi", slow=False)
            tts.save("audio.mp3")
            return jsonify({"answer": response+"Thank you !!! You are Registered"})
        except Exception as e:
            return jsonify({"answer": "Image is not clear error is"+str(e)})

   
        
    
@app.route('/audio')
def audio():
    return send_file("audio.mp3",mimetype="audio/mpeg")
if __name__ == "__main__":
    app.run(debug="True",host="0.0.0.0")