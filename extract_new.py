

import os, sys, json, shutil
from pprint import pprint

try:
    shutil.rmtree('D:\\Yelp\\reviews_dump')
except:
    pass

try:
    shutil.rmtree('D:\\Yelp\\ratings_dump')
except:
    pass

if not os.path.exists('D:\\Yelp\\reviews_dump'):
    os.makedirs('D:\\Yelp\\reviews_dump')

if not os.path.exists('D:\\Yelp\\ratings_dump'):
    os.makedirs('D:\\Yelp\\ratings_dump')

reviewDataset = open('D:\\yelp_backup\\yelp_academic_dataset_review.json', 'r')
businessDataset = open('D:\\yelp_backup\\yelp_academic_dataset_business.json', 'r')
reviewDatasetTest = open('D:\\yelp_backup\\yelp_academic_dataset_review.json', 'r')
businessDatasetTest = open('D:\\yelp_backup\\yelp_academic_dataset_business.json', 'r')

businessNameDict = {}
userNameDict = {}
businessCount = {}
userCount = {}


def filterSlash(string):
    for i in range(0, len(string)):
        if string[i] == '/' or string[i] == '/ ' or string[i] == ' /' or string[i] == ':' or string[i] == ': ' or string[i] == "\""  :
            string = string[:i] +  '_' + string[i+1:]
        elif string[i] == "'" or string[i] == "' " or string[i] == ' ' or string[i] == "," or string[i] =="|" or string[i] == "-" or string[i] == "?":
            string = string[:i] + '!' + string[i+1:]
            if string[i] == "!" and string[i+1] == "!" :
                string = string[:i] + '!' + string[i+2:]
        elif string[i] == "?" and (string[i+1] == '!' or string[i+1] == "'" or string[i+1] == "' " or string[i+1] == ' ' or string[i+1] == "," or string[i+1] =="|" or string[i+1] == "-" or string[i+1] == "?" or string[i+1] == '/' or string[i+1] == '/ ' or string[i+1] == ' /' or string[i+1] == ':' or string[i+1] == ': ') :
            string = string[:i] + '!' + string[i+2:]
            
          
    return string

    
     
for line in businessDataset:
    temp = json.loads(line)
    businessID = temp["business_id"]
    name = temp["name"]
    
    businessNameDict[businessID] = name
    



for line in reviewDataset:
    temp = json.loads(line)
    businessID = temp["business_id"]
    print (businessID)
    userID = temp["user_id"]
    print (userID)
    name = businessNameDict[businessID] 
    rating = temp["stars"]
    review = temp["text"]
    
    try:
        businessCount[name] += 1
    except:
        businessCount[name] = 1

    reviewFolderPath = 'D:\\Yelp\\reviews_dump\\' + name
    reviewID = name + '_' + str(businessCount[name]) + '\>' + userID
    reviewOutputName = reviewID + '.txt'
    

    reviewFolderPath = 'D:\\Yelp\\reviews_dump\\' + filterSlash(name)
    reviewOutputPath = reviewFolderPath + '\\' + filterSlash(reviewOutputName)
    print (reviewOutputPath)
    
    ratingOutputPath = 'D:\\Yelp\\ratings_dump\\' + filterSlash(name) + '.txt'

    if not os.path.exists(reviewFolderPath):
        os.makedirs(reviewFolderPath)
       
    reviewOutput = open(reviewOutputPath, 'wb')
    reviewOutput.write(review.encode('utf8'))
    reviewOutput.close()
    
    ratingOutput = open(ratingOutputPath, 'ab')
    ratingOutputString = reviewID + ':' + str(rating) + '\n' 
    ratingOutput.write(ratingOutputString.encode('utf8'))
    ratingOutput.close()


reviewDataset.close()
businessDataset.close()
reviewDatasetTest.close()
businessDatasetTest.close()
