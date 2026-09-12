# PEPSE - Peaceful Endless Pandora Side-Scrolling Environment

A Java side-scrolling game environment developed as an Object-Oriented Programming project.

The project focuses on modular software design, procedural world generation, character state management, animation, collision handling, and dynamic game-object management.

## Features

- Procedurally generated terrain using noise-based generation
- Endless side-scrolling world
- Dynamic loading and removal of world objects
- Animated player movement
- Idle, running, jumping, and double-jump states
- Energy management system
- Day and night cycle
- Animated sun and sun halo
- Procedurally generated trees
- Animated leaves and collectible fruit
- Collision and ground-contact handling

## Player Controls

| Action | Key |
|--------|-----|
| Move Left | Left Arrow |
| Move Right | Right Arrow |
| Jump | Space |
| Double Jump | Space while airborne |

## Project Structure

```text
pepse/
├── PepseGameManager.java
├── utils/
│   ├── ColorSupplier.java
│   └── NoiseGenerator.java
└── world/
    ├── Block.java
    ├── Sky.java
    ├── Terrain.java
    │
    ├── avatar/
    │   ├── Avatar.java
    │   ├── AvatarState.java
    │   ├── Energy.java
    │   ├── IdleState.java
    │   ├── RunState.java
    │   └── JumpState.java
    │
    ├── daynight/
    │   ├── Night.java
    │   ├── Sun.java
    │   └── SunHalo.java
    │
    └── trees/
        ├── Flora.java
        ├── Fruit.java
        └── Leaf.java
