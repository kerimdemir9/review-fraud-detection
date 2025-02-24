from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch

# Load model and tokenizer
tokenizer = AutoTokenizer.from_pretrained("austinb/fraud_text_detection")
model = AutoModelForSequenceClassification.from_pretrained("austinb/fraud_text_detection")

# Define a sample input text
text = "The price on this thing is too expensive, but I would recommend buying it as it helps in your daily chores."

# Tokenize input text
inputs = tokenizer(text, return_tensors="pt")  # Convert to PyTorch tensor

# Get model output
with torch.no_grad():  # No need to track gradients
    outputs = model(**inputs)

# Extract logits and apply softmax
logits = outputs.logits
probabilities = torch.nn.functional.softmax(logits, dim=1)

# Get the predicted class index
predicted_class = torch.argmax(probabilities, dim=1).item()

# Map class index to labels
labels = {0: "Legit", 1: "Fraud"}
predicted_label = labels[predicted_class]

# Print results
print(f"Input Text: {text}")
print(f"Predicted Class: {predicted_label}")
print(f"Fraud Probability: {probabilities[0][1].item():.4f}")
