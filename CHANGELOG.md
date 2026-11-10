# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

[1.1.0] - 2026-11-10

### Changed
- Pin `vwo-fme-java-sdk` to a concrete version (1.65.0) instead of the dynamic
  `LATEST` version, which silently pulled the Wingify-refactored SDK and broke
  flag evaluation at runtime with `NoSuchMethodError` (issue #1).

### Added
- Unit tests exercising the provider evaluation path against a mocked `VWO`
  client, so future SDK signature changes fail the build.

[1.0.0] - 2024-07-12

### Added
- First release of VWO OpenFeature Provider JAVA
 