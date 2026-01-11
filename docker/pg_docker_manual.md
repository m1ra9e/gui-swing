# Ubuntu

run postgresql:

```sh
docker run --name postgresql-13 -p 5432:5432 -e POSTGRES_PASSWORD=postgres -d postgres:13.3
```

login into the created database by postgres

```sh
psql -h localhost -p 5432 -U postgres -d postgres
password : postgres
```

create and config own user

```sql
CREATE ROLE mirage WITH SUPERUSER LOGIN PASSWORD 'mirage';
CREATE DATABASE mirage;
GRANT ALL ON DATABASE mirage TO mirage;
```

quite

```sh
\q
```

re-login into the created database by mirage

```sh
psql -h localhost -p 5432 -U mirage -d mirage
password : mirage
```

---

# Windows

I used Docker Toolbox, so containers run inside a virtual machine (VirtualBox) 

command to find out the IP address of the Docker virtual machine (usually 192.168.99.100) (Docker Quickstart Terminal)

```sh 
docker-machine ip
```

run postgresql (Docker Quickstart Terminal):

```sh
docker run --name postgresql-13 -p 5432:5432 -e POSTGRES_PASSWORD=postgres -d postgres:13.3
```

login into the created database by postgres

```sh
psql -h 192.168.99.100 -p 5432 -U postgres -d postgres
password : postgres
```

create and config own user

```sql
CREATE ROLE mirage WITH SUPERUSER LOGIN PASSWORD 'mirage';
CREATE DATABASE mirage;
GRANT ALL ON DATABASE mirage TO mirage;
```

quite

```sh
\q
```

re-login into the created database by mirage

```sh
psql -h 192.168.99.100 -p 5432 -U mirage -d mirage
password : mirage
```
