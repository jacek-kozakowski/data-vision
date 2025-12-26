from fastapi import APIRouter, Depends, HTTPException
from typing import List, Optional
from minio import Minio
from pydantic import BaseModel
from dependencies import get_minio_client
from services import data_predictions
import asyncio
from functools import partial

predictions_router = APIRouter()

class CreateModelRequest(BaseModel):
    model_name: str
    model_type: str
    target: str
    features: list[str]
    train_size: float
    use_scaled: bool
    random_state: int
    dataset_file_id: str

class PredictionRequest(BaseModel):
    model_id: str
    data: dict[str, list]
    return_probabilities: bool

class PipelineStepRequest(BaseModel):
    name: str
    type: str
    params: dict[str, object]

class CreatePipelineRequest(BaseModel):
    pipeline_name: str
    dataset_file_id: str
    target: str
    features: Optional[List[str]] = None
    steps: list[PipelineStepRequest]
    train_size: float
    random_state: int

@predictions_router.post("/create-model")
async def create_model(
        request: CreateModelRequest,
        client: Minio = Depends(get_minio_client)
):
        try:
            loop = asyncio.get_running_loop()
            result = await loop.run_in_executor(
                None,
                partial(data_predictions.create_model,
                        model_name = request.model_name,
                        model_type = request.model_type,
                        target = request.target,
                        features = request.features,
                        train_size = request.train_size,
                        use_scaled = request.use_scaled,
                        random_state = request.random_state,
                        dataset_file_id = request.dataset_file_id,
                        client = client)
            )
            return result
        except Exception as e:
            raise HTTPException(status_code=500, detail=str(e))

@predictions_router.post("/predict")
async def predict(
        request: PredictionRequest,
        client: Minio = Depends(get_minio_client)
):
    try:
        loop = asyncio.get_running_loop()
        result = await loop.run_in_executor(
            None,
            partial(data_predictions.predict_data,
                    model_id=request.model_id,
                    input_data=request.data,
                    return_probabilities=request.return_probabilities,
                    client=client)
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@predictions_router.post("/create-pipeline")
async def create_pipeline(
        request: CreatePipelineRequest,
        client: Minio = Depends(get_minio_client)
):
    try:
        loop = asyncio.get_running_loop()
        result = await loop.run_in_executor(
            None,
            partial(data_predictions.create_pipeline,
                    pipeline_name = request.pipeline_name,
                    target = request.target,
                    features = request.features,
                    steps = [step.model_dump() for step in request.steps],
                    train_size = request.train_size,
                    random_state = request.random_state,
                    dataset_file_id = request.dataset_file_id,
                    client = client)
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@predictions_router.post("/predict-pipeline")
async def predict_pipeline(
        request: PredictionRequest,
        client: Minio = Depends(get_minio_client)
):
    try:
        loop = asyncio.get_running_loop()
        result = await loop.run_in_executor(
            None,
            partial(data_predictions.predict_data_pipeline,
                    pipeline_id=request.model_id,
                    input_data=request.data,
                    return_probabilities=request.return_probabilities,
                    client=client)
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))