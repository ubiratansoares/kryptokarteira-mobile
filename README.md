# Krypto Karteira Mobile
 
> This is a practical project for an application to Android Software 
  Engineer position at Stone Payments. Check all details below

## Why

👀

## How

This is a small Android application that emulates operations over a virtual wallet.

This wallet itself can hold Bitcoins(BTC) or Britas(BTA, same as USDs), along with 
real brazilian currency,**Real**(BLR).

The solution must leverage pre-defined data sources for broking prices, both for 
Bitcon and USD dollars, as well emulate an inital giveway scenario for new users.

All this bussiness logic is holded by a dedicated online system, KryptoKarteira, 
built from scratch for this practical project and with a proper REST API 
[exposed](https://kryptokarteira.herokuapp.com/) for this client

Finally, this solution relies on several modern approaches to 
build Android applications

- 100% written in Kotlin
- Offline-first application
- Continuous Integration from the very first commit
- Rock-solid reactive architecture, powered with MVVM + RxJava2 with very unique approach
- Dependency Injection powered by KodeIn
- Massive test coverage, using both local JVM (JUnit and Roboletric) 
and Instrumentation (Espresso) test frameworks
- ETC

## Setup

This application uses
- Android Gradle Plugin 3+
- Kotlin 1.2+

In order to import and runs this project on Android Studio, please check your 
IDE plugins to meet these requirements.

## Building and running tests

All the build variants can be built by the straightfoward

```
./gradlew build
```

This Gradle tasks will build all the four variants for this project as well 
run the JVM local tests for all of them.

If you want to run instrumentation tests, you may want to leverage one of the `mock` 
variants of this build, since these variants rely on REST API calls stubbed with
Mockito and properly configured accorded the desired scenario per test.

```
./gradlew connectedMockDebugAndroidTest
```

This approach provides hermetic testing conditions for the acceptance tests 
implemented with Instrumentation/Espresso.
 
**NOTE** - You must have an online AVD or Android device to run this task

## Installing

Any one of the four outputs APKs may be used for manual checking of this solution.

However, note that `app-mock-*.apk` ones will **EVER** behave the same way, ignoring 
netwoking and assuming all the REST API call as successfull : therefore, you are 
not able to verify any error treatments using these artifacts.

In order to check all the full features delivered with this exercise, you must
use one of `app-live-*.apk` APK artifacts, grabing one APK from 
**app/build/outputs/apk** after the proper assemble Gradle tasks like

```
./gradlew assembleLive
```

or installing the APK directly on device, by instance

```
./gradlew installLiveDebug 
```

## Questions?

I`m glad to awser, just ping me on the internets! 😹

## License

```
The MIT License (MIT)

Copyright (c) 2017 Ubiratan Soares

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```

