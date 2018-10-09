---
layout: docs
title: "Team Documentation"
---

# Best Practices

* Integration with third parties should be behind an API that can be refused. Integration tests can then be applied beyond the API and unit tests before it

## Plan

1. Identify common code paths
2. 

## Architecture Review

* Reduce the time spent writing tests versus writing logic. Balance the number of integration/unit tests to reduce time spent checking logic
* Create a cleaner split between logic and 3rd party integrations to reduce coupling and improve testing ability
* Refactor the logic in the Orchestrator
* Be able to dynamically/in code change the config in a container
* Refactor the logic in the audit service and assosiated lambdas
