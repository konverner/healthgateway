# Health Gateway

A gateway to data stored in Health Connect. It allows to transmit the data to local files (excel, csv) or to remote database.

# Get Started

Install apk file, enable demanded permissions. Data can be exported to csv files in `Downloads/Health Gateway Data` directory or to database using Appwrite platform.

## Set up Appwrite database

Indicate your project and database name. 

## Avaliable Data

### Exercise Sessions

Exercise data includes records about such activities as Walking, Running and Cycling. Each record contains the following information:

| Name        | Description                                                  |
|-------------|--------------------------------------------------------------|
| `uid`       | Unique identifier for the exercise session                   |
| `startTime` | Start time of the exercise session                           |
| `endTime`   | End time of the exercise session                             |
| `duration`  | Type or category of the exercise                             |
| `title`     | Total distance covered during the exercise session in meters |
| `notes`     | Total energy expended during the exercise session  in kcal   |

### Sleep Sessions

| Name        | Description                              |
|-------------|------------------------------------------|
| `uid`       | Unique identifier for the sleep session  |
| `startTime` | Start time of the sleep session          |
| `endTime`   | End time of the sleep session            |
| `duration`  | Duration of the sleep session in minutes |
| `title`     | Title                                    |
| `notes`     | Notes                                    |

### Weight Records

| Name     | Description                             |
|----------|-----------------------------------------|
| `uid`    | Unique identifier for the weight record |
| `time`   | Time instance of a weight record        |
| `weight` | Weight value in kilograms               |

### Nutrition Data

todo
