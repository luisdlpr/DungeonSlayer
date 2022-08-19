# DungeonSlayer
A browser Dungeon Crawler made using Java in the OOP style

## Description
DungeonSlayer is a 2D top view dungeon rogue like developed in Java that runs on the web browser. The back-end was built in collaboration with 3 other students using OOP principles and design patterns (factory, state, strategy, observer, etc).  The front end was mostly constructed by the course staff with some modifications made by my team (see PROJECTSPEC.md for more details).

<img width="709" alt="MenuScreen" src="https://user-images.githubusercontent.com/102662773/185531989-1dba8f0a-243c-4a77-8a13-1b28ac956ecf.png">

<img width="709" alt="InDungeon" src="https://user-images.githubusercontent.com/102662773/185531850-6beed166-f854-46e8-97b8-d9f1472a5239.png">

## Features
See PROJECTSPEC.md for an in depth overview of the features.
- 5 unique types of dungeon goals
- Take in custom maps through JSON
- battles with 5 different types of hostile monsters
- battle system taking into account equippable items and weapons
- Save/Load persistence features
- Time travel items
- Mercenary bribing system
- Random map generation system
- Equippable Potions (Invisibility, Invincibility)
- Placeable Bombs

### Notable Design Features
See design.pdf for an in depth overview of the backend structure and class modelling.
- Composite Pattern used to create compound/complex goals consisting of goal types
- Observer Pattern between dungeon entities and player
- Strategy Pattern for monster movement for easy reusability of movement types
- State Patterns for Bombs, Bribing States (Mercenary) 
- Strategy Pattern for goal checking
- Factory Pattern to create entities
- Polymorphism through the use of interfaces for most entities and features
- Main inheritance structure for different entity types with interfaces for other unique functionalities
- Composition of entities in dungeon and aggregation of inventory items in player

## Install and Run Instructions
- Install Java (18 Recommmended)
- Clone Repository
- Open Repository Folder on VSCode or similar IDE and navigate to and open the controller file DungeonSlayer/src/main/java/dungeonmania/DungeonManiaController.java
- in VSCODE or similar IDE run Java
<img width="1016" alt="runController" src="https://user-images.githubusercontent.com/102662773/185533794-e882ebf5-5a37-452d-a6ed-56122755eff2.png">
- select App.java
- The application should open on a new tab on your default web browser
- the "advanced" dungeon and "simple" configuration are recommended as a good place to start -> custom maps and configs can be added to DungeonSlayer/src/main/resources

## Credits
The backend was not solely developed by myself, I would like to attribute Stan K, Nancy H, and Jordan L as key collaborators in this project. Furthermore university CSE staff for the given frontend and assessment (university will not be listed to avoid academic misconduct).
