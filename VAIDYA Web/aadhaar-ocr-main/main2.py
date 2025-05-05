import cv2
import pytesseract
from aadhaar_details import get_values, send_to_json
from pathlib import Path

def capture_image_from_camera():
    # Open the default camera
    cap = cv2.VideoCapture(0)
    
    # Set camera resolution to 640x480 for faster processing
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

    # Set a flag to check if image capture is successful
    success = False
    
    while not success:
        # Capture frame-by-frame
        ret, frame = cap.read()
        cv2.imshow('Camera', frame)
        
        # Check if the space bar is pressed
        if cv2.waitKey(1) & 0xFF == ord(' '):
            success = True
    
    # Release the camera
    cap.release()
    cv2.destroyAllWindows()
    
    return frame

if __name__ == "__main__":
    tesseract_path = Path("C:/Program Files/Tesseract-OCR/Tesseract.exe")
    pytesseract.pytesseract.tesseract_cmd = tesseract_path
    
    # Capture image from camera
    img = capture_image_from_camera()
    
    # Resize image
    img = cv2.resize(img, (0, 0), fx=0.5, fy=0.5)
    
    # Getting all values from the captured image
    regex_name, regex_gender, regex_dob, regex_mobile_number, regex_aadhaar_number = get_values(img)
    regex_name = " ".join(regex_name[:3])
    print(regex_name, regex_gender, regex_dob, regex_mobile_number, regex_aadhaar_number)
    
    # Send the extracted values to JSON
    send_to_json(regex_name, regex_gender, regex_dob, regex_mobile_number, regex_aadhaar_number)
