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

X_val = []
Y_val = []

# 데이터 로드
carHorn_data_size = 1078
dogBark_data_size = 1088
siren_data_size = 966
other_data_size = 1305

carHorn_train_size = 900
dogBark_train_size = 900
siren_train_size = 800
other_train_size = 1100


shuffle_path = 'D:/hajun/final/emergency-sound-notification-app/Deep Learning/dataset/training-data/shuffle/'


# positive와 negative 순서 정의
data_idx = []
for i in range(carHorn_data_size):
    if i < carHorn_train_size:
        Y.append([1,0,0,0])
        audio, sample_rate = librosa.load(shuffle_path+str(i)+'.wav')
        #mfcc = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=64)
        #mfcc = np.resize(mfcc, (64,44,1))
        #X.append(mfcc)
        audio = np.resize(audio, (22050,))
        X.append(audio)
    else:
        Y_val.append([1,0,0,0])
        audio, sample_rate = librosa.load(shuffle_path+str(i)+'.wav')
        #mfcc = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=64)
        #mfcc = np.resize(mfcc, (64,44,1))
        #X_val.append(mfcc)
        audio = np.resize(audio, (22050,))
        X_val.append(audio)
    
for i in range(dogBark_data_size):
    if i < dogBark_train_size:
        Y.append([0,1,0,0])
        audio, sample_rate = librosa.load(shuffle_path+str(carHorn_data_size+i)+'.wav')
        #mfcc = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=64)
        #mfcc = np.resize(mfcc, (64,44,1))
        #X.append(mfcc)
        audio = np.resize(audio, (22050,))
        X.append(audio)
    else:
        Y_val.append([0,1,0,0])
        audio, sample_rate = librosa.load(shuffle_path+str(carHorn_data_size+i)+'.wav')
        #mfcc = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=64)
        #mfcc = np.resize(mfcc, (64,44,1))
        #X_val.append(mfcc)
        audio = np.resize(audio, (22050,))
        X_val.append(audio)
          
   
for i in range(siren_data_size):
    if i < siren_train_size:
        Y.append([0,0,1,0])
        audio, sample_rate = librosa.load(shuffle_path+str(carHorn_data_size+dogBark_data_size+i)+'.wav')
        #mfcc = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=64)
        #mfcc = np.resize(mfcc, (64,44,1))
        #X.append(mfcc)
        audio = np.resize(audio, (22050,))
        X.append(audio)
    else:
        Y_val.append([0,0,1,0])
        audio, sample_rate = librosa.load(shuffle_path+str(carHorn_data_size+dogBark_data_size+i)+'.wav')
        #mfcc = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=64)
        #mfcc = np.resize(mfcc, (64,44,1))
        #X_val.append(mfcc)
        audio = np.resize(audio, (22050,))
        X_val.append(audio)
            
    
for i in range(other_data_size):
    if i < other_train_size:
        Y.append([0,0,0,1])
        audio, sample_rate = librosa.load(shuffle_path+str(carHorn_data_size+dogBark_data_size+siren_data_size+i)+'.wav')
        #mfcc = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=64)
        #mfcc = np.resize(mfcc, (64,44,1))
        #X.append(mfcc)
        audio = np.resize(audio, (22050,))
        X.append(audio)
    else:
        Y_val.append([0,0,0,1])
        audio, sample_rate = librosa.load(shuffle_path+str(carHorn_data_size+dogBark_data_size+siren_data_size+i)+'.wav')
        #mfcc = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=64)
        #mfcc = np.resize(mfcc, (64,44,1))
        #X_val.append(mfcc)
        audio = np.resize(audio, (22050,))
        X_val.append(audio)
            
        
        
    
X = np.array(X)
Y = np.array(Y)
X_val = np.array(X_val)
Y_val = np.array(Y_val)

print(X.shape, Y.shape)
print(X_val.shape, Y_val.shape)



#%% 모델 정의

X_T = []
X_V = []
for x in X:
    x = librosa.feature.mfcc(y=x, sr=22050, n_mfcc=64)
    X_T.append(np.resize(x, (64,44,1)))

for x in X_val:
    x = librosa.feature.mfcc(y=x, sr=22050, n_mfcc=64)
    X_V.append(np.resize(x, (64,44,1)))
    
X_T = np.array(X_T)
X_V = np.array(X_V)

#%%

model = models.Sequential()
'''
model.add(layers.Conv2D(64, (3,3), activation='relu'))
model.add(layers.Dropout(0.3))
model.add(layers.MaxPool2D(2,2))
model.add(layers.Conv2D(32, (3,3), activation='relu'))
model.add(layers.MaxPool2D(2,2))
model.add(layers.Dropout(0.3))

model.add(layers.Flatten())
model.add(layers.Dropout(0.1))
'''
model.add(layers.Dense(256, activation='relu'))
model.add(layers.Dense(128, activation='relu'))
model.add(layers.Dense(64, activation='relu'))
model.add(layers.Dense(16, activation='relu'))
model.add(layers.Dense(4, activation='softmax'))

# 컴파일
model.compile(optimizer=tf.keras.optimizers.SGD(lr=0.001), loss='categorical_crossentropy', metrics=['accuracy'])

# 학습
#X_train = X[:3500]
#X_val = X[3500:]
#Y_train = Y[:3500]
#Y_val= Y[3500:]

early_stopping = EarlyStopping(patience=6)
trained = model.fit(X, Y, epochs=500, validation_data=(X, Y_val), callbacks = [early_stopping])
#trained = model.fit(X_T, Y, epochs=20, validation_data=(X_V, Y_val))


# 모델 저장

model.save('sound_detection.h5')


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

model.summary()


