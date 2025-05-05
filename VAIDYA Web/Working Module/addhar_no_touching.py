import cv2

import numpy as np
import pytesseract
import re, os, time, json
from pathlib import Path

def image_processing(img, address=False):
    # Convert image to grayscale
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    # Apply adaptive thresholding to enhance text extraction
    if address:
        thresh = cv2.adaptiveThreshold(gray, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                       cv2.THRESH_BINARY_INV, 55, 17)           
    else:
        thresh = cv2.adaptiveThreshold(gray, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                       cv2.THRESH_BINARY, 77, 17)

    # Optional: Apply erosion to make text thinner (for address extraction)
    if address:
        kernel = np.ones((3, 2), np.uint8)
        thresh = cv2.erode(thresh, kernel, iterations=2)

    return thresh

def get_address(img, address=True):
    thresh = image_processing(img, address)
    img2str_config_name = "--psm 4 --oem 3"
    res_string_address = pytesseract.image_to_string(thresh, lang='eng', config=img2str_config_name)
    regex_address = res_string_address.replace("Address:", "").replace("Address :", "")
    regex_address = os.linesep.join([s for s in res_string_address.splitlines() if s])
    return regex_address

def get_values(img):
    regex_name = None
    regex_gender = None
    regex_dob = None
    regex_mobile_number = None
    regex_aadhaar_number = None

    thresh = image_processing(img)
    img2str_config_name = "--psm 4 --oem 3"
    res_string_name = pytesseract.image_to_string(thresh, lang='eng', config=img2str_config_name)

    # Directly extract name using regex
    regex_name = re.findall("[A-Z][a-z]+", res_string_name)
    if not regex_name:
        regex_name = re.findall("[A-Z][a-z]+", res_string_name)
    
    img2str_config_else = "--psm 3 --oem 3"
    res_string_else = pytesseract.image_to_string(thresh, lang='eng', config=img2str_config_else)

    if not regex_name:
        regex_name = re.findall("[A-Z][a-z]+", res_string_else)
    
    # Extracting gender
    regex_gender = re.findall("MALE|FEMALE|male|female|Male|Female", res_string_else)
    if regex_gender:
        regex_gender = regex_gender[0]

    # Extracting date of birth
    regex_dob = re.findall("\d\d/\d\d/\d\d\d\d", res_string_else)
    if regex_dob:
        regex_dob = regex_dob[0]
    if not regex_dob:
        regex_dob = re.findall("(\d\d\d\d){1}", res_string_else)[0]

    # Extracting mobile number
    regex_mobile_number = re.findall("\d\d\d\d\d\d\d\d\d\d", res_string_else)
    if regex_mobile_number:
        regex_mobile_number = int(regex_mobile_number[0].replace(" ", ""))

    # Extracting aadhaar number
    regex_aadhaar_number = re.findall("\d\d\d\d \d\d\d\d \d\d\d\d", res_string_else)
    if regex_aadhaar_number:
        regex_aadhaar_number = int(regex_aadhaar_number[0].replace(" ", ""))

    return regex_name, regex_gender, regex_dob, regex_mobile_number, regex_aadhaar_number



def send_to_json(regex_name, regex_gender, regex_dob,regex_aadhaar_number):
    time_sec = str(time.time()).replace(".", "_")
    json_string = {
        time_sec: {
            "name": regex_name,
            "gender": regex_gender,
            "dob": regex_dob,
            # "mobile number": regex_mobile_number,
            "aadhaar number": regex_aadhaar_number,
            # "address": regex_address,
        },
    }

    # Set JSON file path
    aadhaar_info_path = f"adharextract/aadhaar_info_{time_sec}.json"
    aadhaar_info = Path(aadhaar_info_path)

    with open(aadhaar_info, "a") as f:
        json.dump(json_string, f, indent=4)
        print("Success!, Sent to JSON")