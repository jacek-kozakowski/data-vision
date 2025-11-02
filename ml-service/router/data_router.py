from fastapi import APIRouter, UploadFile, File, Query
from services import data_analyze
router = APIRouter()

@router.post("/analyze")
async def analyze(file: UploadFile = File(...)):
    result = await data_analyze.analyze_data_structure(file)
    return result

@router.post("/correlation")
async def correlation(file: UploadFile = File(...), target: str = Query(...)):
    result = await data_analyze.correlation_matrix(file, target)
    return {
        "target": target,
        "correlation": result
    }

@router.post("/plot")
async def plot(file: UploadFile = File(...), plot_type: str = Query(...), column1: str = Query(...), column2: str = Query(...)):
    result = await data_analyze.generate_plot(file,plot_type, column1, column2)
    return result
