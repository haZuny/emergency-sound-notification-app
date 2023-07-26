import numpy as np
from scipy import signal
from scipy.io.wavfile import *
import matplotlib.pyplot as plt
#from make_wave_from_np import *

def open_wav(fileName):
    '''
    wav 파일 오픈
    '''
    wavF = read(fileName)
    sRate = wavF[0]  # 헤르츠 단위
    wavArr = np.array(wavF[1], dtype = float)
    n = len(wavArr)
    return wavArr

def stft(wave_arr):
    
    '''
    fft 수행
    '''
    f, t, Zxx = signal.stft(wave_arr, sRate)
    return (f, t, Zxx)

def filter_stft(f, t, Zxx):
    '''
    노이즈 제거
    '''
    indices = np.abs(Zxx) > 100
    filtered = indices * Zxx
    
    return filtered



def istft(f):
    '''
    푸리에 역변환
    '''
    # 푸리에 역변환
    istft = signal.istft(filtered, sRate)
    return istft


def wav_to_stft(fileName):
    wavF = read(fileName)
    sRate = wavF[0]  # 헤르츠 단위
    
    try:
        wavArr = (np.array(wavF[1][:, 0], dtype = float) + np.array(wavF[1][:, 0], dtype = float)) / 2
    except:
        wavArr = np.array(wavF[1], dtype = float)
        
    f, t, Zxx = signal.stft(wavArr, sRate)
    return (f, t, Zxx)