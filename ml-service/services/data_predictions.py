import time

import numpy as np
import os
import pandas as pd
from minio import Minio
from sklearn.linear_model import LinearRegression, LogisticRegression
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor
from xgboost import XGBClassifier, XGBRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, mean_squared_error, mean_absolute_error, r2_score
import pickle
import json
import uuid
from datetime import datetime
from . import BUCKET_NAME
from .data_analyze import detect_task_type
from sklearn.preprocessing import StandardScaler, MinMaxScaler, OneHotEncoder
from sklearn.pipeline import Pipeline

COMPONENT_MAP = {
    "StandardScaler" : StandardScaler,
    "MinMaxScaler" : MinMaxScaler,
    "OneHotEncoder" : OneHotEncoder,
    "LinearRegression" : LinearRegression,
    "LogisticRegression" : LogisticRegression,
    "RandomForestClassifier" : RandomForestClassifier,
    "RandomForestRegressor" : RandomForestRegressor,
    "XGBClassifier" : XGBClassifier,
    "XGBRegressor" : XGBRegressor
}

def create_model(model_name: str, model_type: str, target: str, features: list, train_size: float, use_scaled: bool, random_state: int, dataset_file_id: str, client: Minio):
    response = None
    model = None
    try:
        response = client.get_object(bucket_name=BUCKET_NAME, object_name=dataset_file_id)
        df = pd.read_csv(pd.io.common.BytesIO(response.read()))
        
        df = df.loc[:, ~df.columns.str.contains('^Unnamed')]

        if target not in df.columns:
            return {"error": f"Target '{target}' not found in data columns"}
        if features:
            missing_features = [f for f in features if f not in df.columns]
            if missing_features:
                return {"error": f"Features '{missing_features}' not found in data columns"}
        else:
            features = [col for col in df.columns if col != target]

        X = df[features]
        y = df[target]

        categorical_cols = X.select_dtypes(include=['object', 'category']).columns.tolist()
        if categorical_cols:
            X = pd.get_dummies(X, columns=categorical_cols)
            features = X.columns.tolist()

        task_type = detect_task_type(y)

        scaler = None
        if use_scaled:
            scaler = StandardScaler()
            numeric_cols = X.select_dtypes(include=['number']).columns
            if not numeric_cols.empty:
                X[numeric_cols] = scaler.fit_transform(X[numeric_cols])

        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=1-train_size, random_state=random_state, stratify=y if task_type == "classification" else None)


        if model_type == "LINEAR":
            model = LinearRegression()
        elif model_type == "LOGISTIC":
            model = LogisticRegression(random_state=random_state, max_iter=1000)
        elif model_type == "RANDOM_FOREST":
            if task_type == "regression":
                model = RandomForestRegressor(random_state=random_state)
            else:
                model = RandomForestClassifier(random_state=random_state)
        elif model_type == "XGBOOST":
            if task_type == "regression":
                model = XGBRegressor(random_state=random_state)
            else:
                model = XGBClassifier(random_state=random_state, eval_metric="logloss")
        else:
            return {"error": f"Model type '{model_type}' not supported"}

        time_start = time.time()
        model.fit(X_train, y_train)
        time_end = time.time()
        train_time = time_end - time_start

        y_train_pred = model.predict(X_train)
        y_test_pred = model.predict(X_test)
        if task_type == "classification":
            train_metrics = {
                "accuracy": round(accuracy_score(y_train, y_train_pred), 3),
                "precision": round(precision_score(y_train, y_train_pred, average="weighted", zero_division=0), 3),
                "recall": round(recall_score(y_train, y_train_pred, average="weighted", zero_division=0), 3),
                "f1": round(f1_score(y_train, y_train_pred, average="weighted", zero_division=0), 3)
            }
            test_metrics = {
                "accuracy": round(accuracy_score(y_test, y_test_pred), 3),
                "precision": round(precision_score(y_test, y_test_pred, average="weighted", zero_division=0), 3),
                "recall": round(recall_score(y_test, y_test_pred, average="weighted", zero_division=0), 3),
                "f1": round(f1_score(y_test, y_test_pred, average="weighted", zero_division=0), 3)
            }
        else:
            mse_train = mean_squared_error(y_train, y_train_pred)
            mse_test = mean_squared_error(y_test, y_test_pred)
            train_metrics = {
                "r2": round(r2_score(y_train, y_train_pred), 3),
                "mse": round(mse_train, 3),
                "mae": round(mean_absolute_error(y_train, y_train_pred), 3),
                "rmse": round(np.sqrt(mse_train), 3)
            }
            test_metrics = {
                "r2": round(r2_score(y_test, y_test_pred), 3),
                "mse": round(mse_test, 3),
                "mae": round(mean_absolute_error(y_test, y_test_pred), 3),
                "rmse": round(np.sqrt(mse_test), 3)
            }
        model_id = str(uuid.uuid4())

        metadata = {
            "modelName": model_name,
            "modelType": model_type,
            "taskType": task_type,
            "target": target,
            "features": features,
            "trainSize": train_size,
            "useScaled": use_scaled,
            "randomState": random_state,
            "trainMetrics": train_metrics,
            "trainingTime": round(train_time,3),
            "testMetrics": test_metrics,
            "createdAt": datetime.now().isoformat(),
            "updatedAt": datetime.now().isoformat(),
            "trainingDataSize": len(X_train),
            "testDataSize": len(X_test),
            "categoricalCols": categorical_cols,
        }

        os.makedirs("models", exist_ok=True)
        model_file_name = f"models/{model_id}.pkl"
        metadata_filename = f"models/{model_id}_metadata.json"
        scaler_filename = f"models/{model_id}_scaler.pkl" if scaler else None

        with open(model_file_name, 'wb') as outfile:
            pickle.dump(model, outfile)

        if scaler:
            with open(scaler_filename, 'wb') as outfile:
                pickle.dump(scaler, outfile)

        with open(metadata_filename, 'w') as outfile:
            json.dump(metadata, outfile, indent=2)

        for local_file in [model_file_name, metadata_filename, scaler_filename]:
            if not local_file: continue
            with open(local_file, 'rb') as file:
                client.put_object(
                    bucket_name=BUCKET_NAME,
                    object_name=local_file,
                    data=file,
                    length=os.path.getsize(local_file),
                )
            os.remove(local_file)

        return {"model_id": model_id, "model_file": model_file_name}
    except Exception as e:
        return {"error": f"Error training model: {str(e)}"}
    finally:
        if response:
            response.close()
            response.release_conn()

def predict_data(model_id: str, input_data: dict, return_probabilities: bool, client: Minio):
    try:
        if not model_id.startswith("models/"):
            model_id = f"models/{model_id}"
            
        metadata_id = f"{model_id}_metadata.json"
        response = client.get_object(BUCKET_NAME, metadata_id)
        metadata = json.loads(response.read().decode('utf-8'))
        response.close()
        response.release_conn()

        model_file_id = f"{model_id}.pkl"
        response = client.get_object(BUCKET_NAME, model_file_id)
        model = pickle.loads(response.read())
        response.close()
        response.release_conn()

        scaler = None
        if metadata.get("useScaled"):
            scaler_id = f"{model_id}_scaler.pkl"
            response = client.get_object(BUCKET_NAME, scaler_id)
            scaler = pickle.loads(response.read())
            response.close()
            response.release_conn()

        df = pd.DataFrame(input_data)
        
        cat_cols = metadata.get("categoricalCols", [])
        if cat_cols:
             df = pd.get_dummies(df, columns=[c for c in cat_cols if c in df.columns])

        features = metadata.get("features", [])
        if features:
             df = df.reindex(columns=features, fill_value=0)

        if scaler:
            numeric_cols = df.select_dtypes(include=['number']).columns
            if not numeric_cols.empty:
                df[numeric_cols] = scaler.transform(df[numeric_cols])

        predictions = model.predict(df)

        result = {
            "predictions": predictions.tolist(),
            "task_type": metadata.get("taskType"),
            "prediction_count" : len(predictions)
        }

        if metadata.get("taskType") == "classification" and hasattr(model, "predict_proba") and return_probabilities:
            result["probabilities"] = model.predict_proba(df).tolist()
            result["classNames"] = model.get_params()["classes_"].tolist()

        return result
    except Exception as e:
        return {"error": f"Error during prediction: {str(e)}"}

def predict_data_pipeline(pipeline_id: str, input_data: dict, return_probabilities: bool, client: Minio):
    try:
        if not pipeline_id.startswith("pipelines/"):
            pipeline_id = f"pipelines/{pipeline_id}"

        metadata_id = f"{pipeline_id}_metadata.json"
        response = client.get_object(BUCKET_NAME, metadata_id)
        metadata = json.loads(response.read().decode('utf-8'))
        response.close()
        response.release_conn()

        pipeline_file_id = f"{pipeline_id}.pkl"
        response = client.get_object(BUCKET_NAME, pipeline_file_id)
        pipe = pickle.loads(response.read())
        response.close()
        response.release_conn()

        df = pd.DataFrame(input_data)

        features = metadata.get("features", [])
        if features:
            df = df.reindex(columns=features, fill_value=0)

        predictions = pipe.predict(df)

        result = {
            "predictions": predictions.tolist(),
            "prediction_count" : len(predictions),
            "task_type": metadata.get("taskType")
        }

        if metadata.get("taskType") == "classification" and hasattr(pipe, "predict_proba") and return_probabilities:
            result["probabilities"] = pipe.predict_proba(df).tolist()
            if hasattr(pipe, "classes_"):
                result["classNames"] = pipe.classes_.tolist()

        return result

    except Exception as e:
        return {"error": f"Error during prediction: {str(e)}"}

def create_pipeline(pipeline_name: str, target: str, features: list, train_size: float, random_state: int, dataset_file_id: str, steps: list, client: Minio):
    response = None
    pipe = None
    pipeline_steps = []
    
    for step in steps:
        step_name = step.get("name")
        step_type = step.get("type")
        step_params = step.get("params", {})

        if step_type not in COMPONENT_MAP:
            return {"error": f"Step type '{step_type}' not supported"}

        component_class = COMPONENT_MAP[step_type]
        try:
            instance = component_class(**step_params)
        except TypeError as e:
            return {"error": f"Error creating step '{step_name}': {str(e)}"}

        pipeline_steps.append((step_name, instance))

    try:
        pipe = Pipeline(pipeline_steps)
        
        response = client.get_object(bucket_name=BUCKET_NAME, object_name=dataset_file_id)
        df = pd.read_csv(pd.io.common.BytesIO(response.read()))

        df = df.loc[:, ~df.columns.str.contains('^Unnamed')]

        if target not in df.columns:
            return {"error": f"Target '{target}' not found in data columns"}

        if features:
            missing_features = [f for f in features if f not in df.columns]
            if missing_features:
                return {"error": f"Features '{missing_features}' not found in data columns"}
        else:
            features = [c for c in df.columns if c != target]

        X = df[features]
        y = df[target]

        task_type = detect_task_type(y)
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=1-train_size, random_state=random_state)


        time_start = time.time()
        pipe.fit(X_train, y_train)
        time_end = time.time()
        train_time = time_end - time_start

        y_train_pred = pipe.predict(X_train)
        y_test_pred = pipe.predict(X_test)

        if task_type == "classification":
            train_metrics = {
                "accuracy": round(accuracy_score(y_train, y_train_pred), 3),
                "precision": round(precision_score(y_train, y_train_pred, average="weighted", zero_division=0), 3),
                "recall": round(recall_score(y_train, y_train_pred, average="weighted", zero_division=0), 3),
                "f1": round(f1_score(y_train, y_train_pred, average="weighted", zero_division=0), 3)
            }
            test_metrics = {
                "accuracy": round(accuracy_score(y_test, y_test_pred), 3),
                "precision": round(precision_score(y_test, y_test_pred, average="weighted", zero_division=0), 3),
                "recall": round(recall_score(y_test, y_test_pred, average="weighted", zero_division=0), 3),
                "f1": round(f1_score(y_test, y_test_pred, average="weighted", zero_division=0), 3)
            }
        else:
            mse_train = mean_squared_error(y_train, y_train_pred)
            mse_test = mean_squared_error(y_test, y_test_pred)
            train_metrics = {
                "r2": round(r2_score(y_train, y_train_pred), 3),
                "mse": round(mse_train, 3),
                "mae": round(mean_absolute_error(y_train, y_train_pred), 3),
                "rmse": round(np.sqrt(mse_train), 3)
            }
            test_metrics = {
                "r2": round(r2_score(y_test, y_test_pred), 3),
                "mse": round(mse_test, 3),
                "mae": round(mean_absolute_error(y_test, y_test_pred), 3),
                "rmse": round(np.sqrt(mse_test), 3)
            }

        pipeline_id = str(uuid.uuid4())

        metadata = {
            "pipeline": pipeline_name,
            "steps": steps,
            "taskType": task_type,
            "target": target,
            "features": features,
            "trainSize": train_size,
            "randomState": random_state,
            "trainMetrics": train_metrics,
            "trainingTime": round(train_time, 3),
            "testMetrics": test_metrics,
            "createdAt": datetime.now().isoformat(),
            "updatedAt": datetime.now().isoformat(),
            "trainingDataSize": len(X_train),
            "testDataSize": len(X_test),
        }
        os.makedirs("pipelines", exist_ok=True)
        pipeline_file_name = f"pipelines/{pipeline_id}.pkl"
        metadata_filename = f"pipelines/{pipeline_id}_metadata.json"

        with open(pipeline_file_name, 'wb') as outfile:
            pickle.dump(pipe, outfile)

        with open(metadata_filename, 'w') as outfile:
            json.dump(metadata, outfile, indent=2)

        for localfile in [pipeline_file_name, metadata_filename]:
            if not localfile: continue
            with open(localfile, 'rb') as file:
                client.put_object(
                    bucket_name=BUCKET_NAME,
                    object_name=localfile,
                    data=file,
                    length=os.path.getsize(localfile),
                )
            os.remove(localfile)

        return {"pipeline_id": pipeline_id, "pipeline_file": pipeline_file_name}

    except Exception as e:
        return {"error": f"Error loading dataset: {str(e)}"}
    finally:
        if response:
            response.close()
            response.release_conn()


