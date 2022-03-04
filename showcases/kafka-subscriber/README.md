# kafka-subscriber
## Introduction
This example will show you how to subscribe a kafka topic via aws lambda function.
## Prerequisites
1. Install JDK 8
2. Install Maven
3. Install NPM
4. Install [Serverless Framework](https://www.serverless.com/framework/docs/getting-started)

## Build
`mvn clean install -s ../../maven-settings/settings.xml`

## Deploy
${owner} is e.g. your unique team name (keep it short like wincent or 42mobil)
${profile} is your aws account profile in your credentials file

Example of deployment with the owner `mhp` and the profile `XXXXXXXXXXXX_MHPFoundation-ProjectDeveloper`:

`sls deploy --owner mhp --aws-profile XXXXXXXXXXXX_MHPFoundation-ProjectDeveloper -v`

## Test
Just let us know, if you've successfully deployed your lambda function. We will publish a new topic for the integration test.