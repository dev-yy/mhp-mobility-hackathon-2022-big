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
${owner} is e.g. your unique team name (keep it short):
${profile} is your aws account profile in your credentials file

Example of deployment with the owner `mhp` and the profile `XXXXXXXXXXXX_MHPFoundation-ProjectDeveloper`:

`sls deploy --owner mhp --aws-profile XXXXXXXXXXXX_MHPFoundation-ProjectDeveloper -v`
