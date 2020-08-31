# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Module fetches data through Pytrends and uploads as csv to Cloud Storage.
"""

from flask import Flask, request
from flask_api import status
from pytrends.request import TrendReq
from google.cloud import storage
from google.oauth2 import service_account
import pandas as pd                        
import os
import io
import logging

# If `entrypoint` is not defined in app.yaml, App Engine will look for an app
# called `app` in `main.py`.
app = Flask(__name__)

# TODO: Make not hard-coded and part of external config file.
TOPIC = ["Ramen", "Udon", "Pho"]
REGION = ['US'] #, 'GB', 'JP']
TYPE = [''] #, 'images', 'youtube']
TIME_FRAME = ['2019-07-01 2019-10-01', '2019-11-01 2020-02-01', '2020-03-01 2020-05-01']

KEY_LOCATION = os.path.join(os.path.dirname(os.path.abspath(__file__)), "keys/key.json")
CREDENTIAL_SCOPE = "https://www.googleapis.com/auth/cloud-platform"
BUCKET_NAME = "greyswan.appspot.com"
PROJECT_NAME = "greyswan"
DELIMITER = "-"
EXTENSION = "-data.csv"
SUCCESS_MESSAGE = "success"
FAILURE_MESSAGE = "failure"


def upload_blob(upload_string, topic, region, category):
    """Uploads string as csv file with unique file name to Cloud Storage.
    
    Args:
      upload_string:
        A string representing content of file that will be uploaded.
      topic:
        A string designating the topic of content of upload_string (ex. ramen).
      region:
        A string designating the region of data of upload_string (ex. JP).
      category:
        A string designating the category of data of upload_string (ex. images).
        Empty string defaults to category all search results.

    Returns: None.
    """
    # Authenticate with key.json to access Cloud Storage.
    credentials = service_account.Credentials.from_service_account_file(
        KEY_LOCATION, scopes=[CREDENTIAL_SCOPE],
    )

    destination_blob_name = get_filename(topic, region, category)

    storage_client = storage.Client(project=PROJECT_NAME, credentials=credentials)
    bucket = storage_client.bucket(BUCKET_NAME)
    blob = bucket.blob(destination_blob_name)
    
    # Create and upload csv file from string provided.
    blob.upload_from_string(upload_string, content_type='text/csv')

    logging.info(
        "File {} uploaded to cloud storage.".format(destination_blob_name)
    )


def get_filename(topic, region, category):
    """Generates unique filename based on topic, region, and category of file.
    
    Sample filename generated: ramen-us-images-data.csv

    Args:
      topic:
        A string designating the topic of content of upload_string (ex. ramen).
      region:
        A string designating the region of data of upload_string (ex. JP).
      category:
        A string designating the category of data of upload_string (ex. images).
        Empty string defaults to category all search results.
    
    Returns:
      A string representing filename. 
    """
    return topic.lower() + DELIMITER + region.lower() \
            + DELIMITER + category.lower() + EXTENSION


def get_specific_trends_data(period, topic, region, category):
    """Get specific data from trends into string format.

    Args:
      period:
        A string containing time period the trends data should span. 
        An example period is "2019-07-01 2019-10-01". 
      topic:
        A string designating the topic of content of upload_string (ex. ramen).
      region:
        A string designating the region of data of upload_string (ex. JP).
      category:
        A string designating the category of data of upload_string (ex. images).
        Empty string defaults to category all search results.

    Returns:
      String of requested data in csv format. 
    """
    pytrend = TrendReq()
    pytrend.build_payload(
        kw_list = [topic],
        cat = 0,
        timeframe = period,
        geo = region,
        gprop = category)
    dataset = []
    # Get dataframe of data requested by payload.
    df = pytrend.interest_over_time()
    # Remove labels from dataframe returned. 
    data = df.drop(labels=['isPartial'], axis='columns')
    dataset.append(data)
    # Concatenate dataset into pandas object along first axis.
    result = pd.concat(dataset, axis=1)
    # Store concatenated dataset as csv in a string.
    string_result = io.StringIO()
    result.to_csv(string_result)

    return string_result.getvalue()


@app.route('/python/get-data', methods=['GET'])
def get_data():
    """GET request endpoint for fetching trends data.
    
    Parameters:
      time:
        A string ranging from 1 - 3 specifying time period of data requested.
    
    Returns:
      A tuple of {message, status code}.
    """
    try:
        time = int(request.args.get('time'))
    except ValueError as e:
        logging.error("Invalid time parameter: " + request.args.get('time'))
        return FAILURE_MESSAGE, status.HTTP_400_BAD_REQUEST
    
    if time < 1 or time > len(TOPIC):
        logging.error("Invalid time parameter: " + time)
        return FAILURE_MESSAGE, status.HTTP_400_BAD_REQUEST
    
    period = TIME_FRAME[time - 1]

    for topic in TOPIC:
        for region in REGION:
            for category in TYPE:
                string_result = get_specific_trends_data(period, topic, region, category)
                # Create file from string_result and upload to cloud storage.
                upload_blob(string_result, topic, region, category)
    
    return SUCCESS_MESSAGE, status.HTTP_202_ACCEPTED



if __name__ == '__main__':
    # Used when running locally only. When deploying to Google App
    # Engine, a webserver process such as Gunicorn will serve the app. This
    # can be configured by adding an `entrypoint` to app.yaml.
    app.run(host='localhost', port=8889, debug=True)
