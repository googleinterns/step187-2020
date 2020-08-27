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

## Testing
```
// From backend directory
mvn test
```

## Deploy to gcloud
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

## Running Python code
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
