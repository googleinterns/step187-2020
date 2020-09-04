# GreySwan

## Run project locally
```
// From frontend directory
yarn local
// From backend directory
mvn appengine:run
// From python directory
python3.7 main.py
```
*See `key.json` section if you don't already have the key locally.

## Project set up
### Frontend
1) Install Node v10.21.0.
```
nvm install v10.21.0
nvm list // Make sure v10.21.0 is included.
nvm use 10.21.0
```
2) Make sure you have `yarn` installed.
3) Run `yarn` in `./frontend` to install dependencies.
### Java Backend
1) Follow [instructions](https://cloud.google.com/sdk/docs/downloads-apt-ge) to install gCloud SDK.
2) Run `gcloud init` in root directory of our repository. Provide `greyswan` as gCloud project ID. 
3) Install Maven locally.
```
sudo apt update
sudo apt install maven
mvn -version // Check if maven is installed.
```
4) Make sure you have Java 8 installed locally. If not, follow instructions [here](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html).
### Python Microservice
1) Make sure you have Python 3.7 and pip3 installed.
2) To install dependencies:
```
pip3 install -r requirements.txt
```

## Testing
```
// From backend directory
mvn test
// From frontend directory
yarn test
```

## Deploy to gCloud
Only need to do for first time in project root directory. If you're using cloud shell, should not need following commands.
```
gcloud init
gcloud config set project greyswan
```
Run every time you want to deploy:
```
yarn build // From frontend directory
gcloud app deploy // From frontend directory
gcloud app deploy // From python directory
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
1) Create a directory in backend service `/resources` called `/keys`.
2) Move your service account key (make sure to rename to `key.json`) in to `/resources/keys` directory.
3) If you're running python service, create directory `/keys` in `/python` directory and copy `key.json` to `/keys`.
4) **Make sure there is a `.gitignore` file within `/resources` and `/python` that git ignores the `/keys` directory.** (`.gitignore` should already be present)

Note: **Do not attempt to push the `/keys` directory on to Github or any other public locations.**


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
