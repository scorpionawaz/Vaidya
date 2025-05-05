 
import openai

openai.api_key = "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"
def register(user_question,register_classifier, history):
    messages = [{"role": "system", "content": register_classifier}]
    messages.extend(history)
    messages.append({"role": "user", "content": user_question})
    print("messages ---------------------------------------------------------------------------\n",messages)
    completion = openai.ChatCompletion.create(
        model="gpt-3.5-turbo",
        messages=messages,
        temperature=0,
        max_tokens=700,
        top_p=0.5,
        frequency_penalty=0,
        presence_penalty=0,
    )

    response = str(completion.choices[0].message.content)
    print("REGISTRATION LLM RESPONSE :",response)
    
    if '{' and '}' in response:
        import json
         
        json_string = response
        json_object = json.loads(json_string)
        print("the json onj is ",json_object)
        
        from datetime import datetime
        
        def generate_patient_id(patient_count):
            return f"RGN{patient_count + 1}"
        
        def calculate_age(date_of_birth):
            # Convert the input string to a datetime object
            try:
                dob = datetime.strptime(date_of_birth, "%d/%m/%Y")
            except ValueError:
                return "Invalid date"
        
        
            # Get the current date
            current_date = datetime.now()
        
            # Calculate the age
            age = current_date.year - dob.year - ((current_date.month, current_date.day) < (dob.month, dob.day))
        
            return age
        
        
        def insert_into_json(name, dob, addhar_no, gender):
            
            try:
                with open("id_count.json", 'r') as f:
                    data = json.load(f)
            except FileNotFoundError:
                print("id_count.json file not found")
        
            # Generate new patient ID
            patient_id = generate_patient_id(data["patient_count"])
        
            try:
                with open("patient_info.json", 'r') as f:
                    existing_data = json.load(f)
            except FileNotFoundError:
                existing_data = {}
        
            existing_data[str(patient_id)] = {
                "name": str(name),
                "dob": str(dob),
                "addhar_no": int(addhar_no),
                "age": calculate_age(dob),
                "consulted_to": "",
                "diagnosed_with": "",
                "reg_date": datetime.now().strftime("%d/%m/%Y"),
                "gender": str(gender),
                "last_time_rep": {
                    "blood_pressure": "",
                    "respiration": "",
                    "spo2": ""
                }
            }
        
            with open("id_count.json", 'w') as f:
                data["patient_count"] += 1
                json.dump(data, f, indent=4)
        
            with open("patient_info.json", 'w') as f:
                json.dump(existing_data, f, indent=4)
            
        name = json_object["name"]
        gender = json_object["gender"]
        dob = json_object["dob"]
        addhar_no = json_object["aadhaar_number"]
        insert_into_json(name, dob, addhar_no, gender)
        
        
        
        return "YOUR ARE REGISTERED AND YOUR SLIP IS CREATED!! Printing the recipet"
    
    return response
