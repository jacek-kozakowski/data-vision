from fastapi import FastAPI
from router import data_router
import sys

app = FastAPI()

app.include_router(data_router.router, prefix="/api/data")

@app.get("/health")
def health():
    return {"status": "ok"}
