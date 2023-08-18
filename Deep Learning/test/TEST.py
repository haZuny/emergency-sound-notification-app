import random
import numpy as np
#import tensorflow as tf
#from tensorflow.keras import layers, models
import matplotlib.pyplot as plt
import librosa


from silence_tensorflow import silence_tensorflow
silence_tensorflow()
from tensorflow.keras.models import load_model


# audio wav open
audio, sample_rate = librosa.load('test.wav')
audio_len = len(audio)//sample_rate

pred_size = 1 # 예측 시간
search_step = 0.5 # 몇초에 한번 검사할지

max_audio_value = sorted(abs(audio))[int(len(audio)*0.9)]
# 검사
search_time = 0
while search_time+pred_size < audio_len:
    start, end = map(int, (search_time*sample_rate, (search_time+pred_size)*sample_rate))
    buf = audio[start:end]
    
    # 시각화
    print('Time:', search_time ,'\t\t volume:', '#'*int(np.mean(abs(buf)) / max_audio_value * 50))
    
    # 소리 전처리
    mfcc = librosa.feature.mfcc(y=buf, sr=sample_rate, n_mfcc=64)
    print(mfcc.shape)
    x = np.resize(mfcc, (1, 64, 44, 1))
    
    # 예측
    model = load_model('car_horn.h5')
    pred = model.predict(x)
    if pred[0][0] > 0.6: print('\t\t\t\t경적소리 발생\n\t\t\t\t' ,pred[0][0],)
    
    search_time += search_step
