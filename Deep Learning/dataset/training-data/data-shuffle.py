import os, shutil

path_0 = './car_horn_cut/'
list_0 = os.listdir(path_0)

path_1 = './dog_bark_cut/'
list_1 = os.listdir(path_1)

path_2 = './siren_cut/'
list_2 = os.listdir(path_2)

path_3 = './other_cut/'
list_3 = os.listdir(path_3)

savePath = './shuffle/'
saveIdx = 0

for name in list_0:
    shutil.copy(path_0 + name, savePath + str(saveIdx)+'.wav')
    saveIdx += 1
    
for name in list_1:
    shutil.copy(path_1 + name, savePath + str(saveIdx)+'.wav')
    saveIdx += 1
    
for name in list_2:
    shutil.copy(path_2 + name, savePath + str(saveIdx)+'.wav')
    saveIdx += 1
    
for name in list_3:
    shutil.copy(path_3 + name, savePath + str(saveIdx)+'.wav')
    saveIdx += 1

