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
REGION = ['US'] # , 'GB', 'JP']
TYPE = [''] # 'images', 'youtube']
TIME_FRAME = ['2019-07-01 2019-10-01', '2019-11-01 2020-02-01', '2020-03-01 2020-05-01']

def upload_blob(upload_string, type, region, category):
    APP_ROOT = os.path.dirname(os.path.abspath(__file__))   # refers to application_top
    key_path = os.path.join(APP_ROOT, "keys/key.json")
    credentials = service_account.Credentials.from_service_account_file(
        key_path, scopes=["https://www.googleapis.com/auth/cloud-platform"],
    )

    """Uploads a file to the bucket."""
    bucket_name = "greyswan.appspot.com"
    destination_blob_name = type.lower() + "-" + region.lower() + "-" + category.lower() + "-data.csv"

    storage_client = storage.Client(project="greyswan", credentials=credentials)
    bucket = storage_client.bucket(bucket_name)
    blob = bucket.blob(destination_blob_name)

    blob.upload_from_string(upload_string, content_type='text/csv')

    print(
        "File {} uploaded to cloud storage.".format(
            destination_blob_name
        )
    )


@app.route('/python/get-data', methods=['GET'])
def hello():
    time = int(request.args.get('time'))
    print(time)
    period = TIME_FRAME[time - 1]
    pytrend = TrendReq()
    for food in LIST:
        for region in REGION:
            for category in TYPE:
                pytrend.build_payload(
                    kw_list=[food],
                    cat=0,
                    timeframe=period,
                    geo=region,
                    gprop=category)
                dataset = []
                df = pytrend.interest_over_time()
                data = df.drop(labels=['isPartial'],axis='columns')
                dataset.append(data)
                result = pd.concat(dataset, axis=1)
                s = io.StringIO()
                result.to_csv(s)
                print(s.getvalue())
                upload_blob(s.getvalue(), food, region, category)
    return 'hi'



if __name__ == '__main__':
    # Used when running locally only. When deploying to Google App
    # Engine, a webserver process such as Gunicorn will serve the app. This
    # can be configured by adding an `entrypoint` to app.yaml.
    app.run(host='localhost', port=8889, debug=True)