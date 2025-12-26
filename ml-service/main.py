from fastapi import FastAPI
from router import data_router
from router import predictions_router

app = FastAPI()

app.include_router(data_router.data_router, prefix="/api/data")
app.include_router(predictions_router.predictions_router, prefix="/api/predictions")

@app.get("/health")
def health():
    return {"status": "ok"}
