<br><br>

<p align=center>
  <img src=https://raw.githubusercontent.com/OpenSrcerer/BytesToJava/master/img/btjlogo.png width=500>
</p>

<br><br>

# BytesToJava [![](https://jitpack.io/v/OpenSrcerer/BytesToJava.svg)](https://jitpack.io/#OpenSrcerer/BytesToJava) [![](https://travis-ci.com/OpenSrcerer/BytesToJava.svg)](https://travis-ci.com/github/OpenSrcerer/BytesToJava) [![codecov](https://codecov.io/gh/OpenSrcerer/BytesToJava/branch/master/graph/badge.svg?token=KQUF1DIBQN)](https://codecov.io/gh/OpenSrcerer/BytesToJava)
BytesToJava is the most advanced wrapper for the [BytesToBits API](https://api.bytestobits.dev/). It is developed in Java and Kotlin, and provides easy access to all of the functions that the BytesToBits API has available.
# What is BytesToBits?
BytesToBits or BTB for short is an active community that revolves around software development. It is based on a Discord Server with over 2,000 members that you can join [here](https://discord.gg/N3fQbk6FyW).

---

# Getting Started

## > Retrieving Your API Token
1. Visit the [BytesToBits API Login](https://api.bytestobits.dev/login/).
2. Sign up using the "Register" button after inserting your details, or "Login" if you already have an account.
3. You will be redirected to the main page. Click "Account" on the top right corner of the website.
4. Copy the `API Token`. You will need this later to access the API.

# Setting Up Your Build Environment

## > BytesToJava for Maven
1. Make sure your Java Project uses Maven. Not sure how to do this? [Find out here](https://www.jetbrains.com/help/idea/convert-a-regular-project-into-a-maven-project.html).
2. Add this repository to your `pom.xml`:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
3. Add this dependency to your `pom.xml`:
```xml
<dependency>
    <groupId>com.github.OpenSrcerer</groupId>
    <artifactId>BytesToJava</artifactId>
    <version>-SNAPSHOT</version>
</dependency>
```
4. Refresh your changes. Well done, you can now start using BytesToJava.

## > BytesToJava for Gradle
1. Make sure your Java Project uses Gradle. Not sure how to do this? [Find out here](https://www.jetbrains.com/help/idea/gradle.html).
2. Add this repository to your `build.gradle`:
```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
3. Add this dependency to your `build.gradle`:
```groovy
dependencies {
        implementation 'com.github.OpenSrcerer:BytesToJava:-SNAPSHOT'
}
```
4. Refresh your changes. Well done, you can now start using BytesToJava.

---

# How to use BytesToJava?
Using BytesToJava is straightforward and simple. Firstly, create a `BTJ` instance as such:
```java
BTJ btj = BTJ.getBTJ("YOUR-TOKEN-HERE");
```
Please refrain from making multiple of these instances. You are supposed to work with one BTJ object per application.

You can make requests to the BytesToBits API using the following methods:
1. `getInfo()` - To Retrieve info about your current token
2. `getWord()` - Retrieve a random word from the API
3. `getText()` - Retrieve a random piece of text
4. `getMadLib()` - Get a random MadLib 
5. `getMeme()` - Get a meme from Reddit
6. `getLyrics()` - Get the lyrics of a song
7. `getRedditPosts()` - Get Reddit Posts

All of the above methods return a BTJRequest instance. BTJRequests are BytesToJava's way of handling requests made towards the BytesToBits API. **When using the methods above, you are not executing the request, but merely creating the Request object.** BytesToJava offers three handy ways of executing your requests:

1. `.queue()` - Execute a BTJRequest asynchronously using callbacks.
2. `.submit()` - Execute a BTJRequest asynchronously. This method returns a CompletableFuture that represents the request.
3. `.complete()` - Execute a BTJRequest synchronously, meaning that your thread will be blocked if you choose this method of completing the request. **This method is not managed by BytesToJava's ratelimit protection and as such you are exposed to possible ratelimits if you spam API calls!**

# Examples
Here's an example of making a request using callbacks:
```java
// initialize your BTJ instance here
...
btj.getText().queue(randomText -> /* do stuff with your random text */);
...
```
Or, if you want to handle exceptions yourself:
```java
// initialize your BTJ instance here
...
btj.getText().queue(randomText -> /* do stuff with your random text */, throwable -> /* do stuff with your throwable */);
...
```

Here's an example of making a request using Futures:
```java
CompletableFuture<RedditMeme> memeFuture = btj.getMeme().submit();
... // Wait for future to complete
if (memefuture.isDone()) {
...
```

Here's an example of making a request using a synchronous call:
```java
Madlib madlib = btj.getMadlib().complete(); // Blocks the thread
```

---

## Please note that this project is still under heavy development and some of its' features may not work as intended. Proceed with caution.

<h1 align=center>
  GNU © OpenSrcerer
  <h2 align=center>
    Please credit me if you're going to use my code!<br>
  </h2>
</h1>
