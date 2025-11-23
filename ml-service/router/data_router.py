from fastapi import APIRouter, Depends, HTTPException
from minio import Minio
from pydantic import BaseModel
from services import data_analyze
from dependencies import get_minio_client
from starlette.responses import StreamingResponse
import asyncio
from functools import partial

router = APIRouter()


class FileIdRequest(BaseModel):
    file_id: str

class CorrelationRequest(BaseModel):
    file_id: str
    target: str

class PlotRequest(BaseModel):
    file_id: str
    plot_type: str
    column1: str
    column2: str

class CleanScaleRequest(BaseModel):
    file_id: str
    fill_na: bool = True
    fill_method: str = "mean"
    scale: bool = False
@router.post("/analyze")
async def analyze(
    request: FileIdRequest,
    client: Minio = Depends(get_minio_client)
):
    try:
        loop = asyncio.get_running_loop()
        result = await loop.run_in_executor (
            None,
            partial(data_analyze.analyze_data_structure,
                    file_id = request.file_id,
                    client = client)
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/correlation")
async def correlation(
    request: CorrelationRequest,
    client: Minio = Depends(get_minio_client)
):
    try:
        loop = asyncio.get_running_loop()
        result = await loop.run_in_executor(
            None,
            partial(data_analyze.correlation_matrix,
                    file_id =request.file_id,
                    target = request.target,
                    client = client)
        )
        return {
            "target": request.target,
            "correlation": result
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/plot")
async def plot(
    request: PlotRequest,
    client: Minio = Depends(get_minio_client)
):
    try:
        loop = asyncio.get_running_loop()
        result = await loop.run_in_executor(
            None,
            partial(data_analyze.generate_plot,
                    file_id = request.file_id,
                    plot_type = request.plot_type,
                    column1 = request.column1,
                    column2 = request.column2,
                    client = client)
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
@router.post("/clean")
async def clean_data(
    request: CleanScaleRequest,
    client: Minio = Depends(get_minio_client)
):
    try:
        loop = asyncio.get_running_loop()
        result = await loop.run_in_executor(
            None,
            partial(data_analyze.clean_scale_file,
                    file_id=request.file_id,
                    client=client,
                    fill_na=request.fill_na,
                    fill_method=request.fill_method,
                    scale=request.scale)
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))