# Introduction
This showcases demonstrates onpremise IoT fabric, where a Temperature Sensor publishes every second temperature data to mosquitto MQTT Broker.
The Broker Topic is actually: `fabrik/zuffenhausen/sensors/tempsensorA`.
Another com.mhp.mobility.hackathon.DemoApplication subscribes on that topic (`fabrik/zuffenhausen/sensors/tempsensorA`) and writes the data to mongodb for storage.

# Architecture
![image info](./picture/arch.png)


# Start the App
- Install Docker
- ensure docker-compose is installed

# Install MQTT Publisher: tempsensor locally with docker
- mvn clean install
- docker build -t hackathonmhp/mqtt-temp-sensor .

# Install MQTT Subscriber and Mongo Writer: temperatureanalytics locally with docker
- mvn clean install
- docker build -t hackathonmhp/mqtt-temp-subscriber .

# Start the whole app with docker-compose
- docker-compose up

# See the published events with mosquitte client
- mosquitto_sub -h localhost -t "fabrik/zuffenhausen/sensors/tempsensorA"


# See the documents in mongo
- docker exec -it <container-id>  mongo --username root --password rootpassword
- <container-id> with docker ps and find mongo id number
- show dbs
- use temperature
- show collections -> output: enginetemperature
- db.enginetemperature.find({})

# Alternative you can use MongoDB Compass which automatically connects to you local Docker mongo Instance

# Troubleshooting
Try to kill everything `sudo docker rm -f $(docker ps -qa)` 
then clear any sudo docker volume prune volume and `sudo docker system prune -a` image.
Then start up again `sudo docker-compose up -d`