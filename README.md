WebDriver Extensions
====================

This project consists of a set of WebDriver extensions:

* [[webdriver-factory]] is an utility to simplify managing instances of WebDriver
* [[webdriver-repeatable-actions]] is an alternative to "expected conditions": instead of waiting for an element to be ready for an action one can try to perform an action until succeeded
* [[webdriver-wrapper]] allows to create WebDriver extensions following Decorator design pattern; it also contains several ready-to-use extensions: a wrapper that highlights elements before an action, a wrapper that handles unhandled alerts, a wrapper that handles "stale" elements and attempts to find them again and perform the action on the "restored" element
* [[webdriver-logging-wrapper]] is a WebDriver wrapper that logs all commands to slf4j
* [[webdriver-implicit-wait-wrapper]] implements client-side waits that are similar to browser-side impicit waits, but are more intelligent and flexible