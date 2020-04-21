

### Project Structure:

| Directory | Comments |
| ------ | ------ | 
| src/main/scala/backend/api/**domain** | Contains class models and business logic |
| src/main/scala/backend/api/**model** | Contains template class and interface | 
| src/main/scala/backend/api/**interceptors** | Classes with templates with errors and exceptions |
| src/main/scala/backend/api/**controller** | Game control services |
| src/test/scala/backend/api/**unit** | The unit testing of the application |
| src/test/scala/backend/api/**integration** | Client http application for integrated testing |


#### 1.1 Http Rest Server

To build a request and response Http Rest Server, **Finagle-Finch** was used:

  - [https://twitter.github.io/finagle/](https://twitter.github.io/finagle/)
  - [https://finagle.github.io/finch/](https://finagle.github.io/finch/)

#### 1.2 Redis/Cache
Scala Cache was used as a facade for cache implementations - Work in Progress


Fundamental approach would be to be able to abstract based on config to use in memory cache or other cache implementations
or plugin your own implementation:
  - Redis 
  - Google Guava
  - Memcached
  - Ehcache
  - Caffeine
  - cache2k
  - OHC
```

The EndPoints available on the server:

| Method | EndPoint | Example Parameter|
| ------ | ------ | ------ |
| POST | /createGame | {} |
| GET | /retrieveGame/<gameID> | *not required* |
| POST | /submitGuess | {"game_id": "1608240105","guess" : "P"} |



There are three endpoints
 (**createGame**) that can create a new Game  
 (**retriveGame**) that can retrieve  game state 
 (**submitGuess**) that allows to guess a character in the word 

#### 1.2 Transactional Memory and Concurrency Control

The **ScalaSTM** was used to store the data in memory and control of the concurrency.

  - [https://nbronson.github.io/scala-stm/](https://nbronson.github.io/scala-stm/)
  


#### 1.3 Other Tools Used

Other tools used in the project are in the order below:

  - [https://github.com/lightbend/config](https://github.com/lightbend/config) - Configuration library
  - [http://www.scalatest.org/](http://www.scalatest.org/) - Tests

## 2 Requirements

First, have the project folder unzipped on your machine and the terminal open.

### 2.1 Docker

A **Docker** image has been generated that can be used for testing, so it is necessary to have the docker installed on your machine. 

### 2.2 Create Docker Image



```sh
$ docker build -t <image-tag> . 
```

> Note: Please install docker in your Linux distribution.

With docker already installed on your machine, list docker images using the command:

```sh
$ docker images
```

Start docker with the image to do the tests:

```sh
$ docker run -p 9905:9905 <image-tag>
```

Docker will run the unit tests and then start the server. Wait until the server loads the message:

> *** Stating HTTP Server ****
> Host: 0.0.0.0 Port: 9905

> Note: See more command curl information in item 3.3, to run the integrated tests, see item 3.4. More information on removing and stopping the docker image see item 4.

### 2.2 Local SBT and Java

To build the project on the machine it is necessary to have the programs installed:

  - [JDK-1.8.0](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Java Development Kit
  - [SBT-1.2.8](https://www.scala-sbt.org/) - The interactive build tool

> Note: The programs above are with the version used to develop this solution.

## 3 Installation

T

#### 3.1 Install JDK

For JDK installation:

```sh
$ tar -xvzf jdk-8u212-linux-x64.tar.gz
$ sudo mv jdk-8xxx /opt/jdk1.8.0
$ export JAVA_HOME=/opt/jdk1.8.0
$ export PATH=/opt/jdk1.8.0/bin:${PATH}
```

#### 3.1 Install SBT

For SBT installation:

```sh
$ wget https://piccolo.link/sbt-1.2.8.tgz
$ tar xfv sbt-1.2.8.tgz
$ sudo mv sbt /opt/sbt
$ export PATH=/opt/sbt/bin:${PATH}
```

#### 3.2 Install Redis

```sh
$ brew install redis
$ redis-server /usr/local/etc/redis.conf
$ redis-cli ping
If it replies “PONG”, then it’s good to go!
```

> Note: You can also use OpenJDK, find out how to install OpenJDK in your distribution.

### 3.3 Building and Testing  Application

To build the application just run:

```sh
$ git clone https://github.com/santi81/hangman-api.git
$ sbt compile; sbt test; sbt run
```

You can also run unit tests like this:

```sh
$ sbt clean coverage test coverageReport
```

Checking the [ScalaStyle](http://www.scalastyle.org/) code:

```sh
$ sbt scalastyle
```

When you receive the message on the terminal after the *sbt run*:

> *** Stating  HTTP Server ****
> Host: 0.0.0.0 Port: 9905

On another terminal run the command:



### 3.3 More Test Commands

Creating a new game manually via curl: 

```sh
$ curl -i -H "Content-Type: application/json" -X POST -d '{}' http://127.0.0.1:9905/createGame
```

**The answer should be:**

> {"game_id": 1608240105}

Retrieve the game details:

```sh
$ curl -i -H "Content-Type: application/json" -X GET  http://127.0.0.1:9905/retrieveGame/1608240105
```

**The answer should be:**

> {
      "game_id": "1608240105",
      "guesses": {
          "num_guesses_left": 10,
          "num_total_guesses": 10,
          "num_guesses_used": 0
      },
      "status": {
          "total_chars": 8,
          "word": [
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              " "
          ],
          "wrong_guesses": [
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              " "
          ],
          "game_over": false
      }
  }

Make a guess:

```sh
$ curl -i -H "Content-Type: application/json" -X POST -d '{"game_id":"1608240105", "guess":"C"}' http://127.0.0.1:9905/submitGuess
```

**The answer should be:**

> {
      "game_id": "1608240105",
      "guesses": {
          "num_guesses_left": 10,
          "num_total_guesses": 10,
          "num_guesses_used": 0
      },
      "status": {
          "total_chars": 8,
          "word": [
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              "C"
          ],
          "wrong_guesses": [
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              " ",
              " "
          ],
          "game_over": false
      }
  }

If necessary, you can change the host and port configuration of the server. This can be checked at:  [src/main/resources/application.conf](https://github.com/edersoncorbari/sparrow-account/blob/master/src/main/resources/application.conf)

### 3.4 Integration Test

A small HTTP client server was created to do the integrated test. To run the server it is necessary to compile the code via SBT and it is necessary that the server is running via **local** compilation or in the **docker**.

In the project root directory run:

```sh
$ sbt "test:runMain backend.api.integration.HttpClientSuiteTest"
```
## 4 Docker commands

Stop the docker image:

```sh
$ docker container ls
$ docker stop <CONTAINER-ID>
```

Remove the docker image:

```sh
$ docker images
$ docker rmi -f <IMAGE-ID>
```

