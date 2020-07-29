# GreySwan

## Config React project
Run `yarn` in `./frontend` to install dependencies.

## Run locally
```
// From frontend directory
yarn local
// From backend directory
mvn appengine:run
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
Run to deploy a cron job:
```
gcloud app deploy cron.yaml
```
Run to stop a cron job:
```
// Remove content in cron.yaml. 
gcloud app deploy cron.yaml
```
