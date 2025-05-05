from rich.console import Console
from rich.table import Table
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet
from reportlab.platypus import SimpleDocTemplate, Table as PdfTable, TableStyle, Paragraph
import win32print
import win32api

def get_patient_details():
    patients = []
    while True:
        # Get patient details from the user
        name = input("Enter patient's name: ")
        aadhaar_no = input("Enter Aadhaar number: ")
        age = input("Enter patient's age: ")
        gender = input("Enter patient's gender (Male/Female/Other): ")

        # Add patient to the list
        patients.append({
            "Name": name,
            "Aadhaar No": aadhaar_no,
            "Age": age,
            "Gender": gender,
        })

        # Ask if the user wants to add another patient
        another = input("Do you want to add another patient? (yes/no): ").strip().lower()
        if another != "yes":
            break

    return patients

def generate_pdf(patients, filename="patient_records.pdf"):
    # Create a PDF document
    pdf = SimpleDocTemplate(filename, pagesize=letter)
    styles = getSampleStyleSheet()
    story = []

    # Add a title to the PDF
    title = Paragraph("Patient Records", styles['Title'])
    story.append(title)

    # Create a table for patient data
    data = [["Name", "Aadhaar No", "Age", "Gender"]]
    for patient in patients:
        data.append([patient["Name"], patient["Aadhaar No"], patient["Age"], patient["Gender"]])

    # Define the table style
    table = PdfTable(data)
    table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.grey),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
        ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
        ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
        ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
        ('GRID', (0, 0), (-1, -1), 1, colors.black),
    ]))

    # Add the table to the PDF
    story.append(table)
    pdf.build(story)

    print(f"PDF generated: {filename}")



def display_patient_data(patients):
    # Create a console instance
    console = Console()

    # Create a table
    table = Table(title="Patient Records", show_header=True, header_style="bold magenta")
    table.add_column("Name", style="bold cyan", width=20)
    table.add_column("Aadhaar No", style="bold green", width=20)
    table.add_column("Age", style="bold yellow", justify="right")
    table.add_column("Gender", style="bold blue")

    # Add rows to the table
    for patient in patients:
        table.add_row(
            patient["Name"],
            patient["Aadhaar No"],
            patient["Age"],
            patient["Gender"],
        )

    # Display the table in the console
    console.print(table)

if __name__ == "__main__":
    print("helloo")
    # Get patient details from the user
    patients = get_patient_details()

    # Display the patient data in the console
    display_patient_data(patients)

    # Generate a PDF with the patient data
    pdf_filename = "patient_reg.pdf"
    generate_pdf(patients, pdf_filename)

    