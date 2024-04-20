# Health Gateway

A gateway to data stored in Health Connect. It allows to transmit the data to local csv files.

# Get Started

Install apk file, enable demanded permissions. Data can be exported to csv files in `Downloads/Health Gateway Data` directory or to database using Appwrite platform.

## Avaliable Data

### Nutrition Data

| Column              | Description                                                                         |
|---------------------|-------------------------------------------------------------------------------------|
| `uid`               | Unique identifier for the nutrition record                                          |
| `time`              | Time instance of the nutrition record                                               |
| `name`              | Name of the food                                                                    |
| `protein`           | Protein content in grams                                                            |
| `dietaryFiber`      | Dietary fiber content in grams                                                      |
| `sugar`             | Sugar content in grams                                                              |
| `totalCarbohydrate` | Total carbohydrate (sugars + starch + fiber) content in grams                       |
| `saturatedFat`      | Saturated fat content in grams                                                      |
| `unsaturatedFat`    | Unsaturated fat content in grams                                                    |
| `totalFat`          | Total fat content in grams                                                          |
| `energy`            | Energy provided by the food in kilocalories                                         |
| `mealType`          | Type of meal (1 is breakfast, 2 is lunch, 3 is dinner, 4 is snack and 0 is unknown) |


### Daily Steps Records

| Name    | Description                            |
|---------|----------------------------------------|
| `uid`   | Unique identifier for the steps record |
| `date`  | Date of the steps record (up to day)   |
| `steps` | Number of steps taken in the given day |

### Exercise Sessions

Exercise data includes records about such activities as Walking, Running and Cycling. Each record contains the following information:

| Name            | Description                                                  |
|-----------------|--------------------------------------------------------------|
| `uid`           | Unique identifier for the exercise record                    |
| `startTime`     | Start time of the exercise session                           |
| `endTime`       | End time of the exercise session                             |
| `duration`      | Type or category of the exercise                             |
| `totalDistance` | Total distance covered during the exercise session in meters |
| `totalEnergy`   | Total energy expended during the exercise session  in kcal   |
| `minSpeed`      | Minimum speed over the session                               |
| `maxSpeed`      | Maximum speed over the session                               |
| `avgSpeed`      | Average speed over the session                               |

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
