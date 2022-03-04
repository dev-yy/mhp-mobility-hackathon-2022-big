#s3-downloader
##Introduction
This example will show you how to trigger a aws lambda function via sqs message to download a file with the given url to a s3 bucket.
##Prerequisites
1. Install JDK 8
2. Install Maven
3. Install NPM
4. Install [Serverless Framework](https://www.serverless.com/framework/docs/getting-started)

##Build
`mvn clean install -s ../../maven-settings/settings.xml`

##Deploy
${owner} is e.g. your unique team name (keep it short like wincent or 42mobil)
${profile} is your aws account profile in your credentials file

Example of deployment with the owner `mhp` and the profile `XXXXXXXXXXXX_MHPFoundation-ProjectDeveloper`:

`sls deploy --owner mhp --aws-profile XXXXXXXXXXXX_MHPFoundation-ProjectDeveloper -v`

##Test
Just go to the Simple Queue Service in your management console, find your deployed queue and publish a message body like:

`{
  "downloadUrl":"https://325khd2o7k.execute-api.eu-west-1.amazonaws.com/prod/mhp/big/api/v1/services/datenverteilung/NUTZDATENINFORMATION/fbdf30d8-e0a0-4d1d-8b91-9498ebc62871",
  "filename":"Nutzdateninformation.json",
  "filetype":"json"}`