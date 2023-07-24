import csv, wave, os, array
import numpy as np


category_cnt = {}

# route
data_file_route = os.getcwd() + '\\data\\'
save_route = os.getcwd() + '\\rename\\'


# csv file open
f = open('UrbanSound8K.csv', 'r')
reader = csv.reader(f)
lineSize = 8733

printMaker = 0


for i, line in enumerate(reader):
    # pass index
    if i == 0:
        continue
    
    # Read CSV line
    name = line[0]
    #start = float(label[1])
    #end = float(label[2])
    category = line[7]
        
    # Counting by Category
    try:
        category_cnt[category] += 1
    except:
        category_cnt[category] = 0
        
    saveFileName = save_route + category + '-' + str(category_cnt[category]) + '-urban.wav'
    ''' 
    # open wav file
    try:
        w = w = wave.open(data_file_route + name, 'r')
    except:
        #print("[Error] Wave file open error: "+name+" can not open!")
        try:
            os.rename(data_file_route + name, saveFileName)
        except:
            print('[Error] cna not open '+name)
        #continue
    
    
    
    frameRate = w.getframerate()
    buf = w.readframes(w.getnframes())
    amp = np.frombuffer(buf, dtype='int16')
    
    # data cutting
    #cut_amp = amp[int(start*frameRate) : int(end*frameRate)]
    cut_amp = amp
    
    # save
    
    save_wave = wave.Wave_write(saveFileName)
    save_wave.setparams(w.getparams())
    save_wave.writeframes(array.array('h', cut_amp).tobytes())
    save_wave.close()
        
    if i // 200 >= printMaker:
        percent = i * 100 // lineSize
        print(f'[{i}/{lineSize}]\t\t' + '='*percent + '-'*(100-percent))
        printMaker += 1
    '''
f.close()


# write summary
f = open('summary.txt', 'w')
f.write('categoty: count-per-category\n\n')
for category in sorted(category_cnt.keys()):
    f.write(f'{category}: {category_cnt[category]} \n\n')
f.close