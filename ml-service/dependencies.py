from minio import Minio
from services import MINIO_URL, MINIO_ACCESS_KEY, MINIO_SECRET_KEY

minio_client = Minio(
    MINIO_URL,
    access_key=MINIO_ACCESS_KEY,
    secret_key=MINIO_SECRET_KEY,
    secure=False
)

def get_minio_client():
    return minio_client