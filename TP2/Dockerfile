FROM python:3

WORKDIR /usr/src/app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY api_requests.py ./
COPY .env ./

CMD [ "python", "./api_requests.py" ]