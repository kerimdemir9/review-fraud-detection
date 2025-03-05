from kafka import KafkaConsumer
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import json
import requests
import torch

# Load model and tokenizer once (from local if available)
MODEL_PATH = "./fraud_model_local"  # Change if needed
try:
    tokenizer = AutoTokenizer.from_pretrained(MODEL_PATH)
    model = AutoModelForSequenceClassification.from_pretrained(MODEL_PATH)
    print("Model loaded from local storage.")
except:
    tokenizer = AutoTokenizer.from_pretrained("austinb/fraud_text_detection")
    model = AutoModelForSequenceClassification.from_pretrained("austinb/fraud_text_detection")
    print("Model downloaded from Hugging Face.")

def predict_review(text):
    inputs = tokenizer(text, return_tensors="pt")  
    with torch.no_grad():
        outputs = model(**inputs)

    logits = outputs.logits
    probabilities = torch.nn.functional.softmax(logits, dim=1)

    # Get the class and its probability
    predicted_class = torch.argmax(probabilities, dim=1).item()
    fraud_prob = probabilities[0][1].item()  # Probability of being fraud

    labels = {0: False, 1: True}
    return labels[predicted_class], fraud_prob

# Kafka consumer setup
consumer = KafkaConsumer(
    "reviews-topic",
    bootstrap_servers="localhost:9092",
    auto_offset_reset="latest",  # Read latest messages sent // not from the start
    enable_auto_commit=True,
    value_deserializer=lambda m: json.loads(m.decode("utf-8"))
)


API_URL = "http://localhost:8080/reviews/update"

print("Listening for new reviews...")
for message in consumer:
    review = message.value
    text = review.get("review", "")

    if not text:
        print("Received empty review. Skipping...")
        continue

    is_fraud, confidence = predict_review(text)

    print(f"Processed Review: {text}")
    print(f"Prediction: {is_fraud} (Confidence: {confidence:.4f})")

    # Send the result to the Spring Boot API
    data = {
        "review": text,
        "isFraud": is_fraud,
        "probability": confidence  # Include confidence for better tracking
    }
    try:
        response = requests.post(API_URL, json=data)
        if response.status_code == 200:
            print("Successfully sent prediction to API.\n")
        else:
            print(f"API request failed: {response.status_code} - {response.text}\n")
    except requests.RequestException as e:
        print(f"Error sending request: {e}\n")
