# Foundations development rules

## Development strategy

Foundations is developed in small, complete increments.

Every feature follows:

1. Define the responsibility.
2. Design the data and API.
3. Implement the smallest useful version.
4. Test normal and failure cases.
5. Review performance and persistence.
6. Commit only a coherent, reviewable change.

## Architectural rules

- No global gameplay manager unless its ownership is explicit.
- Server authority is the default for persistent settlement state.
- Client code must not own world truth.
- Systems are added only when they have a concrete responsibility.
- Avoid speculative abstractions and empty future classes.
- Prefer deterministic, inspectable task state over opaque autonomous behavior.

## Current milestone

**FOUNDATIONS-001 — Bootstrap**

Goal: establish the smallest clean mod foundation before introducing gameplay systems.
