import json
import tensorflow as tf
from tensorflow.keras.layers import Embedding, GlobalAveragePooling1D, Dense # type: ignore
from tensorflow.keras.preprocessing.text import Tokenizer # type: ignore
from tensorflow.keras.preprocessing.sequence import pad_sequences # type: ignore
from tensorflow.keras.layers import Dropout # type: ignore
from sklearn.model_selection import train_test_split
import pandas as pd
import joblib

df = pd.read_csv('dataset.csv')

categories = df['Category'].unique().tolist()

X = df['Transaction Description'].values
y = pd.get_dummies(df['Category']).values

tokenizer = Tokenizer(num_words=20000, oov_token="<OOV>")
tokenizer.fit_on_texts(X)
joblib.dump(tokenizer, 'tokenizer.pkl')

with open('tokenizer.json', 'w') as f:
    f.write(tokenizer.to_json())

sequences = tokenizer.texts_to_sequences(X)
padded_sequences = pad_sequences(sequences, maxlen=100, padding='post')

X_train, X_test, y_train, y_test = train_test_split(padded_sequences, y, test_size=0.2, random_state=42)

model = tf.keras.Sequential([
    Embedding(input_dim=20000, output_dim=64, input_length=100),
    GlobalAveragePooling1D(),
    Dense(64, activation='relu'),
    Dropout(0.5),
    Dense(32, activation='relu'),
    Dropout(0.5),
    Dense(y_train.shape[1], activation='softmax')
])

model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

model.fit(X_train, y_train, epochs=250, validation_data=(X_test, y_test))

loss, accuracy = model.evaluate(X_test, y_test)
print(f'Test Accuracy: {accuracy}')

converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

with open('transaction_classifier.tflite', 'wb') as f:
    f.write(tflite_model)
