import random
import numpy as np
import tensorflow as tf
from tensorflow.keras import layers, models
import matplotlib.pyplot as plt
import librosa

from tensorflow.keras.callbacks import EarlyStopping


# 데이터 로드
X = []
Y = []

# 데이터 로드
carHorn_data_size = 874
other_data_size = 908

# positive와 negative 순서 정의
data_idx = []
for i in range(1, carHorn_data_size+1):
    data_idx.append(i)
for i in range(1, other_data_size+1):
    data_idx.append(-i)
    
random.shuffle(data_idx)

carHorn_path = 'D:/hajun/final/emergency-sound-notification-app/Deep Learning/dataset/training-data/car_horn_cut/'
other_path = 'D:/hajun/final/emergency-sound-notification-app/Deep Learning/dataset/training-data/other_cut/'
shuffle_path = 'D:/hajun/final/emergency-sound-notification-app/Deep Learning/dataset/training-data/shuffle/'


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
    mfcc = np.resize(mfcc, (64,44,1))
    X.append(mfcc)
        
    
X = np.array(X)
Y = np.array(Y)

print(X.shape, Y.shape)



#%% 모델 정의

model = models.Sequential()
model.add(layers.Conv2D(64, (3,3), activation='relu', input_shape=(64, 44, 1)))
model.add(layers.MaxPool2D(2,2))
model.add(layers.Conv2D(64, (3,3), activation='relu', input_shape=(64, 44, 1)))
model.add(layers.MaxPool2D(2,2))
model.add(layers.Conv2D(32, (3,3), activation='relu'))
model.add(layers.MaxPool2D(2,2))
model.add(layers.Conv2D(32, (3,3), activation='relu'))
model.add(layers.Dropout(0.2,))

model.add(layers.Flatten())
model.add(layers.Dense(16, activation='relu'))
model.add(layers.Dense(1, activation='sigmoid'))

# 컴파일
model.compile(optimizer=tf.keras.optimizers.SGD(lr=0.001), loss='binary_crossentropy', metrics=['accuracy'])

# 학습
X_train = X[:1200]
X_val = X[1200:]
Y_train = Y[:1200]
Y_val= Y[1200:]

early_stopping = EarlyStopping(patience=5)
trained = model.fit(X_train, Y_train, epochs=200, validation_data=(X_val, Y_val), callbacks = [early_stopping])


# 모델 저장

model.save('car_horn.h5')


# 시각화

import matplotlib.pyplot as plt

# 정확도
plt.plot(trained.history['accuracy'])
plt.plot(trained.history['val_accuracy'])
plt.title('model accuracy')
plt.ylabel('accuracy')
plt.xlabel('epoch')
plt.legend(['train', 'validataion'], loc='upper left')
plt.show()

# 손실
plt.plot(trained.history['loss'])
plt.plot(trained.history['val_loss'])
plt.title('model loss')
plt.ylabel('loss')
plt.xlabel('epoch')
plt.legend(['train', 'validation'], loc='upper left')
plt.show()

#model.summary()


