import os, librosa, soundfile
import numpy as np

source_path = './other/'
source_lst = os.listdir(source_path)
savePath = './other_cut/'

saveIdx = 0

for fileName in source_lst:
    
    audio, sample_rate = librosa.load(source_path+fileName)
        
    audioLen = len(audio)
    
    if audioLen < sample_rate:
        audio1 = np.concatenate((audio, np.zeros(sample_rate-audioLen)))
        audio2 = np.concatenate((np.zeros(sample_rate-audioLen), audio))
        soundfile.write(savePath+f'{saveIdx}.wav', audio1, sample_rate, format='WAV')
        saveIdx += 1
        soundfile.write(savePath+f'{saveIdx}.wav', audio2, sample_rate, format='WAV')
        saveIdx += 1
        
    else:
        for i in range(0, audioLen, sample_rate):
            soundfile.write(savePath+f'{saveIdx}.wav', audio[i:i+sample_rate], sample_rate, format='WAV')
            saveIdx += 1