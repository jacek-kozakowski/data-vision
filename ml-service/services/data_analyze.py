import pandas as pd
import matplotlib.pyplot as plt
import io
from minio import Minio
from sklearn.preprocessing import StandardScaler
import os

from . import BUCKET_NAME

available_plots = ["scatter", "histogram", "boxplot", "line"]
available_cleaning_methods = ["mean", "median"]


def analyze_data_structure(file_id: str, client: Minio):
    response = None
    try:
        response = client.get_object(bucket_name=BUCKET_NAME, object_name=file_id)
        df = pd.read_csv(pd.io.common.BytesIO(response.read()))
        target_column = df.columns[-1]

        result = {
            "num_columns": len(df.columns),
            "num_rows": len(df),
            "columns": list(df.columns),
            "columns_types": df.dtypes.astype(str).to_dict(),
            "null_counts": df.isnull().sum().to_dict(),
            "predicted_task_type": detect_task_type(df[target_column])
        }
        return result
    finally:
        if response:
            response.close()
            response.release_conn()

def detect_task_type(target_series):
    if target_series.dtype == 'object' or target_series.dtype.name == 'category':
        return "classification"

    unique_ratio = target_series.nunique() / len(target_series)
    if unique_ratio < 0.05 and target_series.nunique() < 20:
        return "classification"

    return "regression"

def correlation_matrix(file_id: str, target: str, client: Minio):
    response = None
    try:
        response = client.get_object(BUCKET_NAME, file_id)
        df = pd.read_csv(pd.io.common.BytesIO(response.read()))
        if target not in df.columns:
            return {"error": f"Target '{target}' not found in data columns"}

        corr = df.corr(numeric_only=True)[target].sort_values(ascending=False)
        corr = corr.drop(labels=[target])
        return corr.round(3).to_dict()
    finally:
        if response:
            response.close()
            response.release_conn()


def generate_plot(file_id: str, plot_type: str, column1: str, column2: str, client: Minio):
    response = None
    try:
        response = client.get_object(bucket_name=BUCKET_NAME, object_name=file_id)
        df = pd.read_csv(pd.io.common.BytesIO(response.read()))

        if column1 not in df.columns or column2 not in df.columns:
            return {"error": f"Columns '{column1}' or '{column2}' not found in data columns"}

        if column1 == column2:
            return {"error": f"Columns '{column1}' and '{column2}' cannot be the same"}

        if plot_type not in available_plots:
            return {"error": f"Plot type '{plot_type}' not supported"}
        fig, ax = plt.subplots(figsize=(10, 6))

        if plot_type == "scatter":
            ax.scatter(df[column1], df[column2])
        elif plot_type == "histogram":
            ax.hist(df[column1])
        elif plot_type == "boxplot":
            ax.boxplot(df[column1])
        elif plot_type == "line":
            ax.plot(df[column1], df[column2])

        ax.set_xlabel(column1)
        ax.set_ylabel(column2)

        ax.set_title(f"{plot_type} Plot of {column1} vs {column2}")

        buf = io.BytesIO()
        fig.savefig(buf, format='png')
        plt.close(fig)
        buf.seek(0)

        root, _ = os.path.splitext(file_id)
        new_file_id = f"{root}_{plot_type}_{column1}_{column2}.png"
        client.put_object(BUCKET_NAME, new_file_id, data=buf, length=buf.getbuffer().nbytes, content_type='image/png')
        return {"plot_file_id" : new_file_id}
    finally:
        if response:
            response.close()
            response.release_conn()

def clean_scale_file(file_id: str, client: Minio, fill_na = True, fill_method = "mean",  scale = False):
    response = None
    try:
        response = client.get_object(BUCKET_NAME, file_id)
        df = pd.read_csv(pd.io.common.BytesIO(response.read()))
        root, ext = os.path.splitext(file_id)
        new_file_id = f"{root}_cleaned{ext}"
        return_data = {"success": True}
        if fill_na:
            if fill_method not in available_cleaning_methods:
                return {"error": f"Fill method '{fill_method}' not supported"}
            na_counts = df.isna().sum().sum()
            if na_counts > 0:
                if fill_method == "mean":
                    df = df.fillna(df.mean())
                elif fill_method == "median":
                    df = df.fillna(df.median())
                return_data["filled_na_count"] = na_counts
            else:
                return_data["filled_na"] = "Nothing to fill"
        if scale:
            features_to_scale = df.iloc[:, :-1].select_dtypes(include=['number']).columns
            if not features_to_scale.empty:
                scaler = StandardScaler()
                df[features_to_scale] = scaler.fit_transform(df[features_to_scale])
                return_data["scaler_mean"] = scaler.mean_.tolist()
                return_data["scaler_std"] = scaler.scale_.tolist()
        try:
            csv_bytes = df.to_csv(index=False).encode('utf-8')
            csv_stream = io.BytesIO(csv_bytes)
            client.put_object(bucket_name=BUCKET_NAME, object_name=new_file_id, data=csv_stream, length= len(csv_bytes), content_type='application/csv')
            return_data["cleaned_file_id"] = new_file_id
            return return_data
        except Exception as e:
            return {"success" : False,"error": "Error saving cleaned file to Minio: " + str(e)}
    finally:
        if response:
            response.close()
            response.release_conn()