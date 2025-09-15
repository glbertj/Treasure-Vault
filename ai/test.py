import json
import joblib
import tensorflow as tf
import numpy as np
from tensorflow.keras.preprocessing.sequence import pad_sequences # type: ignore

interpreter = tf.lite.Interpreter(model_path='transaction_classifier.tflite')
interpreter.allocate_tensors()

tokenizer = joblib.load('tokenizer.pkl')

with open('categories.json', 'r') as f:
    categories = json.load(f)

def preprocess_text(text, tokenizer, max_length=100):
    sequences = tokenizer.texts_to_sequences([text])
    padded_sequences = pad_sequences(sequences, maxlen=max_length, padding='post')
    return padded_sequences

def predict_category(description, interpreter, tokenizer, categories):
    padded_input = preprocess_text(description, tokenizer)
    
    input_details = interpreter.get_input_details()[0]
    interpreter.set_tensor(input_details['index'], padded_input.astype(np.float32))
    
    interpreter.invoke()
    
    output_details = interpreter.get_output_details()[0]
    output_data = interpreter.get_tensor(output_details['index'])
    
    predicted_index = np.argmax(output_data[0])
    predicted_category = categories[predicted_index]
    confidence_score = output_data[0][predicted_index]
    
    return predicted_category, confidence_score

def main():
    description = input("Enter transaction description: ")
    predicted_category, confidence_score = predict_category(description, interpreter, tokenizer, categories)

    if (confidence_score < 0.65):
        predicted_category = "Other"
    
    print(f"Predicted Category: {predicted_category}")
    print(f"Confidence Score: {confidence_score:.2f}")

if __name__ == "__main__":
    main()