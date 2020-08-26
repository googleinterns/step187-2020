# GreySwan

## Config frontend project
Run `yarn` in `./frontend` to install dependencies.

## Run locally
```
// From frontend directory
yarn local
// From backend directory
mvn appengine:run
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

pip3 install -r requirements.txt
```
To run server locally (make sure you're in `python` directory):
```
python3.7 main.py
```
