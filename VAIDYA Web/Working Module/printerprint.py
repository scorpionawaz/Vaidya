import time
import win32print
import win32ui

def print_patient_details(name,aadhar,dob):
    # Get the default printer name
    printer_name = win32print.GetDefaultPrinter()
    
    # Open the printer
    pdc = win32ui.CreateDC()
    pdc.CreatePrinterDC(printer_name)
    pdc.StartDoc('Patient Details')
    pdc.StartPage()
    
    # Set large font
    font = win32ui.CreateFont({"height": 160, "weight": 700})  # Enlarged 4x
    pdc.SelectObject(font)
    
    # Print details in large text to cover A4 size
    pdc.TextOut(100, 200, f"Book: {name}")
    pdc.TextOut(100, 400, f"AADHAR NO: {aadhar}")
    pdc.TextOut(100, 800, f"DoB: {dob}")
    
    # Draw horizontal line
    pdc.MoveTo(100, 1000)
    pdc.LineTo(1600, 1000)
    
    # Print footer text
    footer_font = win32ui.CreateFont({"height": 120, "weight": 700})  # Larger footer text
    pdc.SelectObject(footer_font)
    pdc.TextOut(100, 1100, "ADROIT SYSTEMS")
    
    pdc.EndPage()
    pdc.EndDoc()
    pdc.DeleteDC()


def print_registration_details(name, aadhar ,dob , gender):
    # Get the default printer name
    printer_name = win32print.GetDefaultPrinter()
    
    # Open the printer
    pdc = win32ui.CreateDC()
    pdc.CreatePrinterDC(printer_name)
    pdc.StartDoc('Patient Details')
    pdc.StartPage()
    
    # Set large font
    font = win32ui.CreateFont({"height": 160, "weight": 700})  # Enlarged 4x
    pdc.SelectObject(font)
    
    # Print details in large text to cover A4 size
    pdc.TextOut(100, 200, f"Registserd ID:216062")
    pdc.TextOut(100, 400, f"NAME: {name}")
    pdc.TextOut(100, 600, f"AADHAR NO: {aadhar}")
    pdc.TextOut(100, 800, f"DOB: {dob}")
    pdc.TextOut(100, 1000, f"GENDER: {gender}")
    
    # Draw horizontal line
    pdc.MoveTo(100, 1000)
    pdc.LineTo(1600, 1000)
    
    # Print footer text
    footer_font = win32ui.CreateFont({"height": 120, "weight": 700})  # Larger footer text
    pdc.SelectObject(footer_font)
    pdc.TextOut(100, 1200, "ADROIT SYSTEMS & H2S Hospital")
    
    pdc.EndPage()
    pdc.EndDoc()
    pdc.DeleteDC()
    
    
if __name__ == "__main__":
    name = "\nNawaz Sayyad\n"
    aadhar = "\n9490 8559 4067\n"
    dob = "\n20/5/2005n"
    
    time.sleep(60)
    print_registration_details(name,aadhar=aadhar,dob=dob,gender="male")
