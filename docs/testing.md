# Testing PAM Native Toast

Moved out of the README; nothing was removed. (Translated from the original Portuguese.)

The PHP regressions were moved to `tests/core-compat.php`. To run them, use PHP 8.5 in the framework checkout:

```sh
PAM_TEST_PLUGIN_ROOTS=/path/to/this-plugin php packages/native/tests/run.php
```

The Android tests live in `android/src/test` or `android/src/androidTest`, depending on the package. Instrumented tests need a PAM test host.
The iOS test package `ios/Package.swift` accepts `PAM_NATIVE_CORE=/checkout/core`.
Run the Swift tests on an iOS simulator; the test module is independent of the module name the launcher generates in the production host.

Emulator validation must cover light/dark theme, keyboard and safe areas, reduced motion, replacement and screen dismissal. See [`RELATORIO.md`](../RELATORIO.md) (Portuguese) for the evidence actually executed for this version.
