checking the created image

```sh
docker image ls
docker images
```

display of all containers and their IDs (including those not running)

```sh
docker ps -a
```

stopping a docker container

```sh
docker container stop id_запущенного_контейрена
```

launching a docker container

```sh
docker container start id_запущенного_контейрена
```

deleting all stopped containers

```sh
docker container prune
```

delete image by ID

```sh
docker image rm -f id_ненужного_образа
```

viewing images unrelated to image tags

```sh
docker images -f dangling=true
```

removing images that are not related to image tags

```sh
docker image prune
```
