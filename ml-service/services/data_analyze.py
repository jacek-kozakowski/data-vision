from fastapi import UploadFile
import pandas as pd
import matplotlib.pyplot as plt
import io
from starlette.responses import StreamingResponse

available_plots = ["scatter", "histogram", "boxplot", "line"]

async def analyze_data_structure(file: UploadFile):
    contents = await file.read()
    df = pd.read_csv(pd.io.common.BytesIO(contents))
    
    target_column = df.columns[-1]
    target_dtype = df[target_column].dtype
    unique_values = df[target_column].nunique()
    
    regression_type = "linear"
    if target_dtype == 'object' or (unique_values / len(df) < 0.1 and unique_values < 50):
        regression_type = "logistic"

    result = {
        "num_columns" : len(df.columns),
        "num_rows" : len(df),
        "columns" : list(df.columns),
        "columns_types" : df.dtypes.astype(str).to_dict(),
        "null_counts" : df.isnull().sum().to_dict(),
        "predicted_regression_type": regression_type
    }
    return result

async def correlation_matrix(file, target):
    contents = await file.read()
    df = pd.read_csv(pd.io.common.BytesIO(contents))
    if target not in df.columns:
        return {"error": f"Target '{target}' not found in data columns"}
    
    corr = df.corr(numeric_only=True)[target].sort_values(ascending=False)
    corr = corr.drop(labels=[target])
    return corr.round(3).to_dict()


async def generate_plot(file: UploadFile, plot_type: str, column1: str, column2: str):
    contents = await file.read()
    df = pd.read_csv(pd.io.common.BytesIO(contents))

    if column1 not in df.columns or column2 not in df.columns:
        return {"error": f"Columns '{column1}' or '{column2}' not found in data columns"}

    if column1 == column2:
        return {"error": f"Columns '{column1}' and '{column2}' cannot be the same"}

    if plot_type not in available_plots:
        return {"error": f"Plot type '{plot_type}' not supported"}

    plt.figure(figsize=(10, 6))
    if plot_type == "scatter":
        plt.scatter(df[column1], df[column2])
    elif plot_type == "histogram":
        plt.hist(df[column1])
    elif plot_type == "boxplot":
        plt.boxplot(df[column1])
    elif plot_type == "line":
        plt.plot(df[column1], df[column2])
    plt.xlabel(column1)
    plt.ylabel(column2)

    plt.title(f"{plot_type} Plot of {column1} vs {column2}")

    buf = io.BytesIO()
    plt.savefig(buf, format='png')
    buf.seek(0)
    return StreamingResponse(buf, media_type="image/png")
