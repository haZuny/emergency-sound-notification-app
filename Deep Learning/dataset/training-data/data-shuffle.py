import os, shutil

path_p = './car_horn_cut/'
positive_lst = os.listdir(path_p)

path_n = './other_cut/'
negative_lst = os.listdir(path_n)

savePath = './shuffle/'
saveIdx = 0

for name in positive_lst:
    shutil.copy(path_p + name, savePath + str(saveIdx)+'.wav')
    saveIdx += 1

for name in negative_lst:
    shutil.copy(path_n + name, savePath + str(saveIdx)+'.wav')
    saveIdx += 1
