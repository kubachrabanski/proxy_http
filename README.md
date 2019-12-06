# proxy_http [![build status](https://travis-ci.com/kubachrabanski/proxy_http.svg?branch=master)](https://travis-ci.com/kubachrabanski/proxy_http) [![license](https://img.shields.io/badge/license-MIT-blue)](https://github.com/kubachrabanski/proxy_http/blob/master/LICENSE)

a basic, threaded HTTP proxy using Socket, ServerSocket and standard JDK,
written from scratch for learning purposes, according to Mozilla's Developer guide on HTTP/HTTPS protocol 

https://developer.mozilla.org/en-US/docs/Web/HTTP

### Build with Maven

maven [package] target is configured to build an executable jar archive,
by default the project will be built using Java 11

```
mvn clean package
```

### Usage

the executable takes one positional argument [port], by default
Java 11 or higher is required to run

```
java -jar proxy_http-[version].jar [port]
```