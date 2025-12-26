# DataVision (IN DEVELOPMENT)
DataVision is a comprehensive data visualization and machine learning platform. It empowers users to clean, transform, and visualize data, as well as create, train, and deploy machine learning models and pipelines without writing code.

dataVision is built using a modern microservices architecture, leveraging Java Spring Boot for the backend and Python FastAPI for high-performance ML operations.

## Architecture

The system consists of three main components:

1.  **Backend Service (Java 21 + Spring Boot)**:
    *   Acts as the API Gateway and core business logic handler.
    *   Manages user authentication (JWT), project organization, and file metadata.
    *   Orchestrates communication between the frontend, database, and ML service.
2.  **ML Service (Python 3.12 + FastAPI)**:
    *   Dedicated microservice for data processing and machine learning tasks.
    *   Utilizes libraries like `pandas`, `scikit-learn`, and `xgboost`.
    *   Handles dataset parsing, model training, pipeline creation, and prediction requests from the backend.
3.  **Storage & Database**:
    *   **PostgreSQL**: relational database for user data, project structure, and metadata.
    *   **MinIO**: S3-compatible object storage for raw CSV datasets and serialized model files (`.pkl`).

## Tech Stack
- **Backend**: Java 21, Spring Boot 3, Hibernate, Lombok
- **ML Service**: Python 3.12, FastAPI, Pandas, Scikit-learn, XGBoost
- **Infrastructure**: Docker, Docker Compose
- **Database**: PostgreSQL
- **Storage**: MinIO

## Features
###  Data Management
- **Project Structure**: Organize work into projects.
- **Dataset Upload**: Securely upload CSV files to MinIO.
- **Data Inspection**: View and analyze dataset columns and statistics.

###  Machine Learning
#### Model Creation
Create individual ML models for Regression or Classification tasks.
- **Supported Models**: Linear Regression, Logistic Regression, Random Forest (Regressor/Classifier), XGBoost.
- **Automatic Handling**: Data splitting, training, and metrics calculation.

#### Pipeline Creation
Build complex processing workflows using `sklearn.pipeline.Pipeline`.
- **Steps**: define a sequence of steps including preprocessors (`StandardScaler`, `MinMaxScaler`, `OneHotEncoder`) and estimators.
- **Dynamic Configuration**: Configure parameters for each step directly via the API.
- **Serialization**: Complete pipelines are serialized and stored.

#### Prediction & Inference
- **Unified Interface**: Predict on new data using either simple Models or complex Pipelines.
- **Batch Prediction**: Send multiple rows of data for batch processing.
- **Probabilities**: Option to return class probabilities for classification tasks.

## Getting Started
### Prerequisites
- Docker & Docker Compose
- Java 21 JDK (for local dev)
- Python 3.12 (for local dev)


### Configuration
Create `.env` file in main directory and generate a random JWT secret: 
```dotenv
JWT_SECRET=my_secret_key
```

### Running with Docker
```bash
docker-compose up --build
```
This command will start the Backend, ML Service, PostgreSQL, and MinIO containers.

## API Usage

### Authentication
#### Registration
`POST /api/auth/register`
**Body:**
```json
{
  "username" : "ex4mpl3",
  "password" : "ex4mpl3_password" // min 8 chars
}
```

#### Login
`POST /api/auth/login`
**Body:**
```json
{
  "username": "ex4mpl3",
  "password": "ex4mpl3_password"
}
```
*Returns a JWT token. Use this token in the `Authorization` header (`Bearer <token>`) for all subsequent requests.*

### User Management
#### Get Current User
`GET /api/user/me`
*Returns the currently authenticated user's details.*

### Project Management
#### Create Project
`POST /api/projects`
**Body:**
```json
{
  "name" : "Example Project" 
}
```

#### Get All Projects
`GET /api/projects/all`
*Returns a list of all projects belonging to the user.*

#### Get Project by ID
`GET /api/projects/{id}`

#### Delete Project
`DELETE /api/projects/{id}`

### Data Operations
#### Upload Dataset
`POST /api/data/{projectId}/upload`
**Request:** `multipart/form-data`
*   `file`: The CSV file to upload.

#### Analyze Dataset
`GET /api/data/{projectId}/analyze`
**Query Params:**
*   `useScaled` (boolean, optional, default: `false`): Whether to analyze scaled data.

#### Correlation Analysis
`GET /api/data/{projectId}/correlation`
**Query Params:**
*   `target` (string, required): The target column name.
*   `useScaled` (boolean, optional, default: `false`): Whether to use scaled data.

#### Plot Data
`POST /api/data/{projectId}/plot`
**Body:**
```json
{
  "plotId": 1,
  "plotType": "scatter",
  "column1": "feature_x",
  "column2": "feature_y",
  "useScaled": false
}
```
*Returns a PNG image of the plot.*

#### Clean & Scale Data
`POST /api/data/{projectId}/clean`
**Body:**
```json
{
  "fillNa": true,
  "fillMethod": "mean", // "mean" or "median"
  "scale": true
}
```

### Machine Learning
#### Create Model
`POST /api/predictions/{projectId}/create-model`
**Body:**
```json
{
  "model_name": "my_linear_regression",
  "model_type": "LinearRegression", 
  "target": "price",
  "features": ["sqft_living", "bathrooms"], // Optional list
  "train_size": 0.8, // 0.1 to 0.9
  "use_scaled": false,
  "random_state": 42,
  "dataset_name": "original_dataset" // use either "original_dataset" or "scaled_dataset"
}
```

#### Create Pipeline
`POST /api/predictions/{projectId}/create-pipeline`
**Body:**
```json
{
  "pipeline_name": "house_price_pipeline",
  "dataset_name": "original_dataset",
  "target": "price",
  "features": ["sqft_living", "bathrooms"],
  "train_size": 0.8,
  "random_state": 42,
  "steps": [
    {
      "name": "scaler",
      "type": "StandardScaler",
      "params": {}
    },
    {
      "name": "model",
      "type": "RandomForestRegressor",
      "params": {
        "n_estimators": 100
      }
    }
  ]
}
```

#### Get Model Info
`GET /api/predictions/{projectId}/models/{modelName}`

#### Get Pipeline Info
`GET /api/predictions/{projectId}/pipelines/{pipelineName}`

#### List Models
`GET /api/predictions/{projectId}/models`

#### List Pipelines
`GET /api/predictions/{projectId}/pipelines`

#### Delete Model
`DELETE /api/predictions/{projectId}/models/{modelName}`

#### Delete Pipeline
`DELETE /api/predictions/{projectId}/pipelines/{pipelineName}`

### Prediction
#### Predict with Model
`POST /api/predictions/{projectId}/models/{modelName}/predict`
**Body:**
```json
{
  "data": [
    {"sqft_living": 2000, "bathrooms": 2.5},
    {"sqft_living": 1500, "bathrooms": 1.0}
  ],
  "return_probabilities": false
}
```

#### Predict with Pipeline
`POST /api/predictions/{projectId}/pipelines/{pipelineName}/predict`
**Body:**
```json
{
  "data": [
    {"sqft_living": 2000, "bathrooms": 2.5}
  ],
  "return_probabilities": false
}
```