import random
import numpy as np
import tensorflow as tf
from tensorflow.keras import layers, models
import sound_processing

#%% 데이터 로드
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

carHorn_path = './dataset/training-data/car_horn/'
other_path = './dataset/training-data/other/'
shuffle_path = './dataset/training-data/shuffle/'


for i in data_idx:
    isFindFace = False
    # 경적
    if i > 0:
        Y.append(1)
        # wav 파일 읽어서 stft
        f, t, Zxx = sound_processing.wav_to_stft(shuffle_path+str(i-1)+'.wav')
    # 그 외
    elif i < 0:
        Y.append(0)
        # wav 파일 읽어서 stft
        f, t, Zxx = sound_processing.wav_to_stft(shuffle_path+str(carHorn_data_size - i -2)+'.wav')
    Zxx = np.resize(Zxx, (129,1380, 1))
    X.append(Zxx)
        
    

X = np.array(X)
Y = np.array(Y)

print(X.shape, Y.shape)



#%% 모델 정의

model = models.Sequential()
model.add(layers.Conv2D(32, (3,3), activation='relu', input_shape=(129, 1380, 1)))
model.add(layers.MaxPool2D(2,2))
model.add(layers.Conv2D(64, (3,3), activation='relu'))
model.add(layers.MaxPool2D(2,2))
model.add(layers.Conv2D(64, (3,3), activation='relu'))
model.add(layers.Dropout(0.2,))

model.add(layers.Flatten())
model.add(layers.Dense(64, activation='relu'))
model.add(layers.Dense(32, activation='relu'))
model.add(layers.Dense(1, activation='sigmoid'))

# 컴파일
model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])

# 학습
X_train = X[:500]
X_val = X[500:]
Y_train = Y[:500]
Y_val= Y[500:]

trained = model.fit(X_train, Y_train, epochs=8, validation_data=(X_val, Y_val))


#%% 모델 저장

model.save('car_horn.h5')


#%% 시각화

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

model.summary()


