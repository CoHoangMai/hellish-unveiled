# Hellish Unveiled: Secret Terrors

[![libGdx](https://img.shields.io/badge/libGDX-1.12.1-red?style=flat-square)](https://libgdx.com/)
[![Java](https://img.shields.io/badge/JDK-23-orange?style=flat-square)](https://www.oracle.com/java/technologies/downloads/)
[![Gradle](https://img.shields.io/badge/Gradle-8.10.1-blue?style=flat-square)](https://gradle.org/)

A short, simple top-down action RPG where the player fights enemies and defeats the final boss to win.

## Gameplay

### Objective

Defeat the final boss to win.

### Controls

- Arrow Keys: Move the player character.
- Spacebar: Attack enemies.
- C Key: Open/close the inventory.
- ECS Key: Pause the game.
- Mouse/Touchpad: Select buttons and interact with the inventory.

## Framework & Extensions

This project uses [libGDX](https://libgdx.com/) as the core framework, along with the following extensions:

- [Ashley](https://github.com/libgdx/ashley)
- [Box2D](https://libgdx.com/wiki/extensions/physics/box2d)
- [Box2DLights](https://github.com/libgdx/box2dlights)
- [GdxAI](https://github.com/libgdx/gdx-ai)

## Installation

### Prerequisites

- **Java Development Kit (JDK)**: [Version 23](https://www.oracle.com/java/technologies/downloads/#java23).
- **Git** (optional, only if you want to use Git Bash): [Download Git](https://git-scm.com/downloads).

### Steps

1. **Get the project code**  
   Choose one of the following options to get the project code:

   - **Option 1: Using Git**  
     Open your terminal (or Git Bash on Windows) and run the following commands:
     
     ```bash
     git clone https://github.com/CoHoangMai/hellish-unveiled.git
     cd hellish-unveiled
     ```

   - **Option 2: Without Git**  
     - Go to the repository page: [hellish-unveiled](https://github.com/CoHoangMai/hellish-unveiled).
     - Click the **Code** button, then select **Download ZIP**.
     - Extract the ZIP file to a folder on your computer.
     - Open your terminal (Command Prompt on Windows, Terminal on macOS/Linux) and navigate to the extracted folder using the `cd` command. For example:
       
       ```bash
       cd path/to/hellish-unveiled
       ```
       
2. **Build the project**  
   Run the following command to build the project using Gradle:
   
   ```bash
   ./gradlew build
   ```
   
3. **Run the project**
   
   After building, you can run the project with this command:
   
    ```bash
    ./gradlew lwjgl3:run
    ```
    
## Screenshots

![Intro](<assets/screenshots/Hellish Unveiled_ Secret Terrors 2025-02-17 00-18-19_Moment(2).jpg>)

![In-game](<assets/screenshots/Hellish Unveiled_ Secret Terrors 2025-02-16 23-41-23_Moment(4).jpg>)

![Night](<assets/screenshots/Hellish Unveiled_ Secret Terrors 2025-02-16 23-41-23_Moment.jpg>)

![Inventory](<assets/screenshots/Hellish Unveiled_ Secret Terrors 2025-02-16 23-41-23_Moment(3).jpg>)

![Boss](<assets/screenshots/Hellish Unveiled_ Secret Terrors 2025-02-17 00-25-08_Moment.jpg>)
