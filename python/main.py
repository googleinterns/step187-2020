from flask import Flask, request
import pandas as pd                        
from pytrends.request import TrendReq
import os
import io
from google.cloud import storage
from google.oauth2 import service_account

# If `entrypoint` is not defined in app.yaml, App Engine will look for an app
# called `app` in `main.py`.
app = Flask(__name__)

""" TODO: Make not hard-coded. """
LIST = ["Ramen", "Udon", "Pho"]
TIME_FRAME = ['2019-07-01 2019-08-01', '2019-08-01 2019-09-01', '2019-09-01 2019-10-01']

def upload_blob(upload_string):
    APP_ROOT = os.path.dirname(os.path.abspath(__file__))   # refers to application_top
    key_path = os.path.join(APP_ROOT, "keys/key.json")
    credentials = service_account.Credentials.from_service_account_file(
        key_path, scopes=["https://www.googleapis.com/auth/cloud-platform"],
    )

    """Uploads a file to the bucket."""
    bucket_name = "greyswan.appspot.com"
    source_file_name = "search_trends.csv"
    destination_blob_name = "new-test-file"

    storage_client = storage.Client(project="greyswan", credentials=credentials)
    bucket = storage_client.bucket(bucket_name)
    blob = bucket.blob(destination_blob_name)

    blob.upload_from_string(upload_string, content_type='text/csv')

    print(
        "File {} uploaded to {}.".format(
            source_file_name, destination_blob_name
        )
    )


@app.route('/python/get-data', methods=['GET'])
def hello():
    time = int(request.args.get('time'))
    print(time)
    period = TIME_FRAME[time - 1]
    """Return a friendly HTTP greeting."""
    pytrend = TrendReq()
    pytrend.build_payload(
        kw_list=["Ramen"],
        cat=0,
        timeframe=period,
        geo='US')
    dataset = []
    df = pytrend.interest_over_time()
    data = df.drop(labels=['isPartial'],axis='columns')
    dataset.append(data)
    result = pd.concat(dataset, axis=1)
    s = io.StringIO()
    result.to_csv(s)
    print(s.getvalue())
    upload_blob(s.getvalue())
    return 'hi'



if __name__ == '__main__':
    # Used when running locally only. When deploying to Google App
    # Engine, a webserver process such as Gunicorn will serve the app. This
    # can be configured by adding an `entrypoint` to app.yaml.
    app.run(host='localhost', port=8888, debug=True)