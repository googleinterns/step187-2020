# GreySwan

## Config frontend project
Run `yarn` in `./frontend` to install dependencies.

## Run locally
```
// From frontend directory
yarn local
// From backend directory
mvn appengine:run
// From python directory
python3.7 main.py
```
Note: All three services need to be running at the same time.

## Testing
```
// From backend directory
mvn test
```

## Deployment
Only need to do for first time. If you're using cloud shell, should not need following commands.
```
gcloud init
gcloud config set project greyswan
```
Run every time you want to deploy:
```
yarn build // From frontend directory
gcloud app deploy // From frontend directory
mvn package appengine:deploy // From backend directory
```
To demo running cron jobs visit: https://greyswan.uc.r.appspot.com/blackswan/index.html

To see entities within datastore for deployment, visit [here](https://pantheon.corp.google.com/datastore/entities;kind=alert;ns=__$DEFAULT$__/query/kind?project=greyswan).

## Accessing Cloud Storage with key.json
`key.json` is needed when testing locally with the `CloudFileSystem` implementation of `FileSystem`.
To obtain `key.json` file, you can either:
1) Create a new service account key from GCP [here](https://pantheon.corp.google.com/iam-admin/serviceaccounts?project=greyswan) with the correct configurations. 
- Follow instructions [here](https://cloud.google.com/iam/docs/creating-managing-service-account-keys) to create a service account key 
- Make sure to go to Storage section [here](https://pantheon.corp.google.com/storage/browser/greyswan.appspot.com;tab=permissions?forceOnBucketsSortingFiltering=false&project=greyswan&prefix=) and give the key you created the following permissions: 
  - Storage Legacy Bucket Reader
  - Storage Legacy Object Reader
  - Storage Object Viewer
2) [Recommended] Ask @melodychn to send you the key via safe and secure means. 

Once `key.json` file is obtained:
1) Create a directory in `/resources` called `/keys` 
2) Move your service account key (make sure to rename to `key.json`) in to `/resources/keys` directory.
3) **Make sure there is a `.gitignore` file within `/resources` that git ignores the `/keys` directory.** (`.gitignore` should already be present)

Note 1: **Do not attempt to push the `/keys` directory on to Github or any other public locations.**
Note 2: If running `python` service, see below for where to store the key. 


## Deploy cron jobs
To view all active cron jobs on GCP: https://pantheon.corp.google.com/appengine/cronjobs?project=greyswan

To deploy a cron job:
1) Specify cron job details in `cron.yaml` file. 
2) Deploy the `cron.yaml` file. 
```
gcloud app deploy cron.yaml
```
To stop a cron job:
1) Remove cron job content in `cron.yaml`. 
2) Deploy the `cron.yaml` file with cron job content removed. 
```
gcloud app deploy cron.yaml
```

## Running Python service
To install dependencies:
```
sudo apt-get install python3-pip // installs pip3
pip3 install -r requirements.txt
```
TO run locally, make a directory within `python/` directory named `keys/` and copy the `key.json` into `python/keys/` directory. 
To run server locally (make sure you're in `python` directory):
```
python3.7 main.py
```

## Demo-ing Cron Job with simple interface 
1) Once you have all three services running, visit `localhost:8888/blackswan/index.html` or https://greyswan.uc.r.appspot.com/blackswan/index.html if deployed.
2) Make sure you have the `console` open up to check log messages. 
3) Before you click `Run job` button, you have to first click `Load updated data 1` button to load data through python service onto Cloud Storage.
4) Once you see an error message show up (The error will disappear once deployed), you know that the data is done being loaded.
5) Now you can click `Run job` button. When you see the message `job ran` the cron job has finished loading. Alerts should now be stored in the datastore and can be loaded to the frontend. 
6) To load more data, click `Load updated data 2`, wait for error message to show up to signify the job done. 
7) Click `Run job` again to run the cron job to detect anomalies in new data. Once the `job ran` message shows up in the console, the alerts should be in the datastore. 
8) Perform the same process to load the third set of data. 

**Note:** If you click load the same data set twice, the ID of the alerts will change in the datastore. So, do not do that unless you want the IDs to change. 
