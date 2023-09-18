import tensorflow as tf

# Load the Keras model from the H5 file
keras_model = tf.keras.models.load_model('sound_detection.h5')

# Convert the Keras model to a TFLite model
converter = tf.lite.TFLiteConverter.from_keras_model(keras_model)
tflite_model = converter.convert()

# Save the TFLite model to a file
with open('sound_detection.tflite', 'wb') as f:
    f.write(tflite_model)