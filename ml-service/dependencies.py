import os
from minio import Minio

minio_client = Minio(
    os.environ.get("MINIO_URL"),
    access_key=os.environ.get("MINIO_ACCESS_KEY"),
    secret_key=os.environ.get("MINIO_SECRET_KEY"),
    secure=False
)

def get_minio_client():
    return minio_client