# Friendly Forked Socket Transfer Protocol

[![License: WTFPL](https://img.shields.io/badge/License-WTFPL-red.svg)](http://www.wtfpl.net/txt/copying/)
[![Build Status](https://travis-ci.org/Jezorko/ffstp.svg?branch=master)](https://travis-ci.org/Jezorko/ffstp)

## What is this?
This tiny project was born during the BookingGo 2018 hackathon.
I needed something for communicating between sockets.
My primary requirement was that it needs to be simple, small and easy to use.
I figured it would be faster to write something by myself than search for a library I could use and learn it.

## What is it good for?
If you are looking for something that is not necessary reliable, works maybe 99% of the time but is easy to plug in, this library is for you.
You can use it to implement a simple server-client communication using sockets.

## How do I use it?
I think [this](https://github.com/Jezorko/ffstp/blob/master/examples/MessagesExchangeExample.java) example should be
enough to explain it. It's simple, really.

If you would like to use some library for serialization / deserialization,
see example of how to do it [here](https://github.com/Jezorko/ffstp/blob/master/examples/GsonSerializer.java).

## Maven
Add repository to your pom.xml file:

```xml
<repositories>
    <repository>
        <id>jezorko-ffstp</id>
        <name>jezorko-ffstp</name>
        <url>https://raw.githubusercontent.com/jezorko/ffstp/repository</url>
    </repository>
</repositories>
```

And the dependency:

```xml
<dependency>
    <groupId>com.github.jezorko</groupId>
    <artifactId>ffstp</artifactId>
    <version>2.0.0</version>
</dependency>
```