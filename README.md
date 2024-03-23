# Health Gateway

A gateway to data stored in Health Connect. It allows to transmit the data to local files (excel, csv) or to remote database.

# Get Started

Install apk file, enable demanded permissions. Data can be exported to csv files in `Downloads/healthgateway` directory or to database using Appwrite platform.

## Set up Appwrite database

Indicate your project and database name. 

## Export Exercise Data

Exercise data includes records about such activities as Walking, Running and Cycling. Each record contains the following information:

|Name           |Description                                          |
|---------------|-----------------------------------------------------|
| `uid`         | Unique identifier for the exercise record            |
| `startTime`   | Start time of the exercise session                  |
| `endTime`     | End time of the exercise session                    |
| `exerciseType`| Type or category of the exercise |
| `totalDistance` | Total distance covered during the exercise session in meters|
| `totalEnergy` | Total energy expended during the exercise session  in kcal|

## Export Nutrition Data

todo
