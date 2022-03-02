#### mosquitto setup:

   

```
$ docker pull toke/mosquitto

$ docker run -ti --name mosquitto -p 1883:1883 toke/mosquitto
 
```



#### MongoDB setup:

   

```
$ docker pull mongo:latest
$ docker run -itd --name mongo -p 27017:27017 mongo --auth
$ docker exec -it mongo mongo admin
$ db.createUser({ user:'admin',pwd:'123456',roles:[ { role:'userAdminAnyDatabase', db: 'admin'},"readWriteAnyDatabase"]});
$ db.auth('admin', '123456')
```

