
# Review Fraud Detection
A Spring Boot API sends reviews into a kafka topic and a fraud detection ML model reads from the kafka topic and classifies the review and sends back to the API. 
## API Usage

#### Submit a Review

```http
  POST http://localhost:8888/reviews/submit
```

| Body       | Type     | Value              |
| :--------  | :------- | :------------------------- |
| `review` | `string` | Enter your review 


#### See Submitted Legit Reviews
```http
  GET http://localhost:8888/reviews/legit
```

#### See Submitted Fraud Reviews
```http
  GET http://localhost:8888/reviews/fraud
```
### KafkaConsumer
```text
First checks for the model in local directory and if no model is found, imports the model from Hugging Face.

Reads from Kafka Topic for latest reviews sent.
Sends each review to model for classification and sends the results back to the API.
```
