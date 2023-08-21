# Data selecting
- I selected the car_horn category and maked another category using other dataset.
- car_horn: 264
- other: 322

# Preprocessing
- I selected car_horn data and other data
- I preprocessed these data before Learning
- These data has each differential time length, and I want all data has 1second time length.
  - Data with time that is short than 1 second:
    - I extended time with empty sound attaching start and end of original sound
    - As a result, Original data having short time created 2 datas
  - Data with time that is long than 1 second:
    - I cut the original sound per 1 second
    - As a result, Original data created 1 or more datas
- car_horn_cut: 874
- other_cut: 908

# Shuffle
- I combined these datas on shuffle directory
