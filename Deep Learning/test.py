import random
import numpy as np
import tensorflow as tf
from tensorflow.keras import layers, models
import matplotlib.pyplot as plt
import librosa


# 데이터 로드
arrSize = 1000
X = []
Y = []

# 데이터 로드
carHorn_data_size = 342
other_data_size = 403

# positive와 negative 순서 정의
data_idx = []
for i in range(1, carHorn_data_size+1):
    data_idx.append(i)
for i in range(1, other_data_size+1):
    data_idx.append(-i)
    
random.shuffle(data_idx)

carHorn_path = 'D:/hajun/final/emergency-sound-notification-app/Deep Learning/dataset/training-data/car_horn/'
other_path = 'D:/hajun/final/emergency-sound-notification-app/Deep Learning/dataset/training-data/other/'
shuffle_path = 'D:/hajun/final/emergency-sound-notification-app/Deep Learning/dataset/training-data/shuffle/'

audio, sample_rate = librosa.load(shuffle_path+'5.wav')
print(len(abs(audio)))


'''
for i in data_idx:
    isFindFace = False
    # 경적
    if i > 0:
        Y.append(1)
        # wav 파일 읽어서 stft
        #f, t, Zxx = sound_processing.wav_to_stft(shuffle_path+str(i-1)+'.wav')
        audio, sample_rate = librosa.load(shuffle_path+str(i-1)+'.wav')
        mfcc = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=64)
    # 그 외
    elif i < 0:
        Y.append(0)
        # wav 파일 읽어서 stft
        #f, t, Zxx = sound_processing.wav_to_stft(shuffle_path+str(carHorn_data_size - i -2)+'.wav')
        audio, sample_rate = librosa.load(shuffle_path+str(carHorn_data_size - i -2)+'.wav')
        mfcc = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=64)
    mfcc = np.resize(mfcc, (64,256,1))
    X.append(mfcc)
'''