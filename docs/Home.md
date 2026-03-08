# openapi-generator-samples — Wiki

This wiki documents the sample scenarios included in this repository, what each one validates, and how to read and extend them.

---

## Pages

| Page | Description |
|---|---|
| [Scenarios](Scenarios) | Detailed breakdown of every sample scenario and what it covers |
| [Edge Cases](Edge-Cases) | Tricky annotation patterns and how the plugin is expected to handle them |
| [Adding a Sample](Adding-a-Sample) | Step-by-step guide to contributing a new scenario |

---

## What is this repository?

This module is the reference sample suite for the `openapi-generator-maven-plugin`. It contains a set of Spring MVC controllers, abstract classes, interfaces, and DTOs designed specifically to exercise different plugin capabilities.

Each scenario is intentionally minimal — controllers are stubs that return hardcoded data. The point is not what the code does at runtime, but what annotations are present and how the plugin reads them.

Running `mvn process-classes` regenerates [`docs/swagger/openapi.yaml`](https://github.com/rspereiratech/openapi-generator-samples/blob/master/docs/swagger/openapi.yaml) from the compiled classes.
