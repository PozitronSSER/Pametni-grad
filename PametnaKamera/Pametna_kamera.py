
# Author: Filip Jurković
# Date: 12/2/2019

# Import packages
import os
import cv2
import numpy as np
from picamera.array import PiRGBArray
from picamera import PiCamera
import tensorflow as tf
import argparse
import sys
import json
import urllib.request  as urllib2 
from time import sleep
import RPi.GPIO as GPIO
import subprocess

# Set up camera constants
IM_WIDTH = 640
IM_HEIGHT = 480


camera_type = 'usb'
while(True):
        try:
            newFile  = open("novo.txt","w")

            baseURL = 'http://api.thingspeak.com/update?api_key=5RW8G73WIYYGQ8AQ&field1='

            #### Initialize TensorFlow model ####

            # This is needed since the working directory is the object_detection folder.
            sys.path.append('..')

            # Import utilites
            from utils import label_map_util
            from utils import visualization_utils as vis_util

            # Name of the directory containing the object detection module we're using
            MODEL_NAME = 'ssdlite_mobilenet_v2_coco_2018_05_09'

            # Grab path to current working directory
            CWD_PATH = os.getcwd()

            # Path to frozen detection graph .pb file, which contains the model that is used
            # for object detection.
            PATH_TO_CKPT = os.path.join(CWD_PATH,MODEL_NAME,'frozen_inference_graph.pb')

            # Path to label map file
            PATH_TO_LABELS = os.path.join(CWD_PATH,'data','mscoco_label_map.pbtxt')

            # Number of classes the object detector can identify
            NUM_CLASSES = 90

            ## Load the label map.
            # Label maps map indices to category names, so that when the convolution
            # network predicts `5`, we know that this corresponds to `airplane`.
            # Here we use internal utility functions, but anything that returns a
            # dictionary mapping integers to appropriate string labels would be fine
            label_map = label_map_util.load_labelmap(PATH_TO_LABELS)
            categories = label_map_util.convert_label_map_to_categories(label_map, max_num_classes=NUM_CLASSES, use_display_name=True)
            category_index = label_map_util.create_category_index(categories)

            # Load the Tensorflow model into memory.
            detection_graph = tf.Graph()
            with detection_graph.as_default():
                od_graph_def = tf.GraphDef()
                with tf.gfile.GFile(PATH_TO_CKPT, 'rb') as fid:
                    serialized_graph = fid.read()
                    od_graph_def.ParseFromString(serialized_graph)
                    tf.import_graph_def(od_graph_def, name='')

                sess = tf.Session(graph=detection_graph)


            # Define input and output tensors (i.e. data) for the object detection classifier

            # Input tensor is the image
            image_tensor = detection_graph.get_tensor_by_name('image_tensor:0')

            # Output tensors are the detection boxes, scores, and classes
            # Each box represents a part of the image where a particular object was detected
            detection_boxes = detection_graph.get_tensor_by_name('detection_boxes:0')

            # Each score represents level of confidence for each of the objects.
            # The score is shown on the result image, together with the class label.
            detection_scores = detection_graph.get_tensor_by_name('detection_scores:0')
            detection_classes = detection_graph.get_tensor_by_name('detection_classes:0')

            # Number of objects detected
            num_detections = detection_graph.get_tensor_by_name('num_detections:0')


            # Initialize frame rate calculation
            frame_rate_calc = 1
            freq = cv2.getTickFrequency()
            font = cv2.FONT_HERSHEY_SIMPLEX

            # Define inside box coordinates (top left and bottom right)
            TL_inside = (int(IM_WIDTH*0.05),int(IM_HEIGHT*0.05))
            BR_inside = (int(IM_WIDTH*0.95),int(IM_HEIGHT*0.95))


            # Initialize control variables used for pet detector
            detected_inside = False
            detected_outside = False

            inside_counter = 0
            outside_counter = 0

            pause = 0
            pause_counter = 0

            #### Pet detection function ####

            # This function contains the code to detect a pet, determine if it's
            # inside or outside, and send a text to the user's phone.
            def pet_detector(frame):

                # Use globals for the control variables so they retain their value after function exits
                global detected_inside, detected_outside
                global inside_counter, outside_counter
                global pause, pause_counter

                frame_expanded = np.expand_dims(frame, axis=0)

                # Perform the actual detection by running the model with the image as input
                (boxes, scores, classes, num) = sess.run(
                    [detection_boxes, detection_scores, detection_classes, num_detections],
                    feed_dict={image_tensor: frame_expanded})

                # Draw the results of the detection (aka 'visulaize the results')
                vis_util.visualize_boxes_and_labels_on_image_array(
                    frame,
                    np.squeeze(boxes),
                    np.squeeze(classes).astype(np.int32),
                    np.squeeze(scores),
                    category_index,
                    use_normalized_coordinates=True,
                    line_thickness=8,
                    min_score_thresh=0.40)
                
               
                # Draw boxes defining "outside" and "inside" locations.
                cv2.rectangle(frame,TL_inside,BR_inside,(20,20,255),3)
                cv2.putText(frame,"Inside box",(TL_inside[0]-30,TL_inside[1]-20),font,1,(20,255,255),3,cv2.LINE_AA)
                
                # Check the class of the top detected object by looking at classes[0][0].
                # If the top detected object is a cat (17) or a dog (18) (or a teddy bear (88) for test purposes),
                # find its center coordinates by looking at the boxes[0][0] variable.
                # boxes[0][0] variable holds coordinates of detected objects as (ymin, xmin, ymax, xmax)
                if (((int(classes[0][0]) == 3) or (int(classes[0][0] == 18) or (int(classes[0][0]) == 88))) and (pause == 0)):
                    x = int(((boxes[0][0][1]+boxes[0][0][3])/2)*IM_WIDTH)
                    y = int(((boxes[0][0][0]+boxes[0][0][2])/2)*IM_HEIGHT)

                    # Draw a circle at center of object
                    cv2.circle(frame,(x,y), 5, (75,13,180), -1)

                    # If object is in inside box, increment inside counter variable
                    if ((x > TL_inside[0]) and (x < BR_inside[0]) and (y > TL_inside[1]) and (y < BR_inside[1])):
                        inside_counter = inside_counter + 1

                   

                # If pet has been detected inside for more than 10 frames, set detected_inside flag
                # and send a text to the phone.
                if inside_counter > 8:
                    detected_inside = True

                    inside_counter = 0
                    outside_counter = 0
                    # Pause pet detection by setting "pause" flag
                    pause = 1


                return frame

            #### Initialize camera and perform object detection ####
            ### USB webcam ###
                
            if camera_type == 'usb':
                # Initialize USB webcam feed
                camera = cv2.VideoCapture(0)
                ret = camera.set(3,IM_WIDTH)
                ret = camera.set(4,IM_HEIGHT)

                # Continuously capture frames and perform object detection on them
                
                while(True):

                    t1 = cv2.getTickCount()

                    # Acquire frame and expand frame dimensions to have shape: [1, None, None, 3]
                    # i.e. a single-column array, where each item in the column has the pixel RGB value
                    ret, frame = camera.read()

                    # Pass frame into pet detection function
                    frame = pet_detector(frame)

                    # All the results have been drawn on the frame, so it's time to display it.
                    cv2.imshow('Object detector', frame)

                    # FPS calculation
                    t2 = cv2.getTickCount()
                    time1 = (t2-t1)/freq
                    frame_rate_calc = 1/time1
                
                
                    if detected_inside == True:
                        break

                    # Press 'q' to quit
                    if cv2.waitKey(1) == ord('q'):
                        break
                slika  = open("car-photo.jpeg","w")
                img_name = "car-photo.jpeg".format(0)
                cv2.imwrite(img_name, frame)
                print("{} written!".format(img_name))    
                camera.release()   
                
                sleep(1)
                
               
                subprocess.call('sudo curl -X POST "https://api.openalpr.com/v2/recognize?secret_key=sk_b1c2cbf4d7950f5cbe468277&recognize_vehicle=1&country=eu&return_image=0&topn=10" -F image=@/home/pi/tensorflow1/models/research/object_detection/car-photo.jpeg > test.txt', shell=True)
                print("dobro je")
                
                #promjeniti ulaznu datoteku 
                
                with open('test.txt') as json_file:  
                    data = json.load(json_file)
                    for p in data['results']:
                        print('plate: ' + p['plate'])
                
                        newFile.write(p['plate'])
                        print('')
                
                        f = urllib2.urlopen(baseURL + str(p['plate']))
                        f.read()
                        f.close()
                        sleep(3)
                    
        except:
            print('Nema tablice')
            try:
                os.remove("car-photo.jpeg")
            except:
                print('Slika nije bila spremljena')
            continue
                
        if cv2.waitKey(1) == ord('x'):
                break
                    
        sleep(7)
        
        
cv2.destroyAllWindows()



