import gspread
from google.oauth2.service_account import Credentials
import datetime

# Setting up credentials and Google Sheets access
scopes = ["https://www.googleapis.com/auth/spreadsheets"]
access_creds = Credentials.from_service_account_file("VaidyaDSAD_kunal.json", scopes=scopes)
client = gspread.authorize(access_creds)

# Open the spreadsheet by key
sheet = client.open_by_key("===")

# Access the main worksheet
worksheet = sheet.sheet1

# Function to initialize a new day's schedule
def initialize_day_schedule(date):
    # Add the date as a header
    worksheet.append_row([f"------------------------------------{date}------------------------------------------"])
    
    # Add time slot headers
    headers = ["TIME SLOT", "NEUROLOGIST", "GYNAC", "PEDIATRICIAN", "ORTHO", "CARDIAC", "PSYCHIATRIC"]
    worksheet.append_row(headers)
    
    # Add time slots
    time_slots = ["8-9", "9-10", "10-11", "11-12", "12-1", "1-2 (BREAK)", "2-3", "3-4", "4-5", "5-6"]
    for slot in time_slots:
        worksheet.append_row([slot] + [""] * 6)  # Add empty cells for doctors

# Function to book an appointment
def book_appointment(date, time_slot, specialty, patient_name):
    # Find the day's schedule
    date_row = worksheet.find(f"------------------------------------{date}------------------------------------------")
    if not date_row:
        return "Day's schedule not initialized. Please initialize the schedule first."
    
    # Find the time slot row
    time_row = worksheet.find(time_slot, in_column=1)
    if not time_row or time_row.row < date_row.row:
        return "Invalid time slot or date."
    
    # Find the column for the specialty
    header_row = worksheet.row_values(date_row.row + 1)
    try:
        col_index = header_row.index(specialty.upper()) + 1
    except ValueError:
        return f"Specialty '{specialty}' not found."
    
    # Check if the slot is already booked
    if worksheet.cell(time_row.row, col_index).value:
        return "Time slot already booked."
    
    # Book the slot
    worksheet.update_cell(time_row.row, col_index, patient_name)
    return f"Appointment booked successfully for {patient_name} under {specialty} on {date} at {time_slot}."

# Function to complete an appointment
def complete_appointment(date, time_slot, specialty):
    # Find the day's schedule
    date_row = worksheet.find(f"------------------------------------{date}------------------------------------------")
    if not date_row:
        return "Day's schedule not initialized."
    
    # Find the time slot row
    time_row = worksheet.find(time_slot, in_column=1)
    if not time_row or time_row.row < date_row.row:
        return "Invalid time slot or date."
    
    # Find the column for the specialty
    header_row = worksheet.row_values(date_row.row + 1)
    try:
        col_index = header_row.index(specialty.upper()) + 1
    except ValueError:
        return f"Specialty '{specialty}' not found."
    
    # Mark the appointment as completed
    if worksheet.cell(time_row.row, col_index).value:
        worksheet.update_cell(time_row.row, col_index, worksheet.cell(time_row.row, col_index).value + " ✔")
        return "Appointment marked as completed."
    else:
        return "No appointment found in this slot."

# Example Usage:
# Initialize a day's schedule
today = (datetime.date.today()).strftime("%d/%m/%Y")
initialize_day_schedule(today)

# Book an appointment
result = book_appointment(today, "9-10", "GYNAC", "Prathmesh bagve")
print(result)

# Complete an appointment
result = complete_appointment(today, "9-10", "GYNAC")
print(result)
