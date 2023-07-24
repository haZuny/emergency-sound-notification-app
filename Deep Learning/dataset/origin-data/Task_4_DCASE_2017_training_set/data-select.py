import csv, wave, os, array
import numpy as np


category_cnt = {}

# route
data_file_route = os.getcwd() + '\\training-data\\'
save_route = os.getcwd() + '\\rename\\'


# csv file open
f = open('training-label.csv', 'r')
reader = csv.reader(f)

printMaker = 0

for i, line in enumerate(reader):
    # Read CSV line
    label = line[0].split()
    name = label[0]
    start = float(label[1])
    end = float(label[2])
    category = '-'.join(label[3:])
    
    # Counting by Category
    try:
        category_cnt[category] += 1
    except:
        category_cnt[category] = 0
    
    # open wav file
    w = w = wave.open(data_file_route + 'Y' + name, 'r')
    
    frameRate = w.getframerate()
    buf = w.readframes(w.getnframes())
    amp = np.frombuffer(buf, dtype='int16')
    
    # data cutting
    #cut_amp = amp[int(start*frameRate) : int(end*frameRate)]
    cut_amp = amp
    
    # save
    saveFileName = save_route + category + '-' + str(category_cnt[category]) + '.wav'
    save_wave = wave.Wave_write(saveFileName)
    save_wave.setparams(w.getparams())
    save_wave.writeframes(array.array('h', cut_amp).tobytes())
    save_wave.close()
    
    #print(start, end, saveFileName)
    
    if i // 500 >= printMaker:
        percent = i * 100 // 51172
        print(f'[{i}/51,172]\t\t' + '='*percent + '-'*(100-percent))
        printMaker += 1
    
f.close()

# write summary
f = open('summary.txt', 'w')
f.write('categoty: count-per-category\n\n')
for category in sorted(category_cnt.keys()):
    f.write(f'{category}: {category_cnt[category]} \n\n')
f.close