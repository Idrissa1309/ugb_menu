# Project Plan

UGB-Menu: A complete Android application in Java for university campus restaurant menus. Features include daily/weekly menus for two restaurants (Resto 1 & Resto 2) with lunch and dinner, food images, favorites system, local notifications, and offline support via local JSON. UI follows Material Design 3 with a green and yellow theme, rounded cards, and bottom navigation. Includes a splash screen and a simple AI assistant for menu queries.

## Project Brief

# Project Brief: UGB-Menu

UGB-Menu is a dedicated campus dining application designed for the students and staff of University Gaston Berger. The app provides a centralized platform to check daily restaurant schedules, manage food preferences, and receive timely meal updates through a vibrant, high-performance mobile interface.

### Features
*   **Dynamic Menu Browsing:** View daily and weekly lunch and dinner menus for Resto 1 and Resto 2, complete with high-quality dish imagery and descriptions.
*   **Favorites & Offline Support:** Save preferred meals to a personal list using local storage, ensuring menu access even without an active internet connection via local JSON data.
*   **Local Notification System:** Stay informed with automated push notification simulations for meal availability and daily specials.
*   **AI Menu Assistant:** Interact with a logic-based assistant designed to answer common queries regarding menu items and restaurant information.
*   **Branded Splash Screen:** A professional entry experience featuring the university logo and a consistent Material Design 3 aesthetic.

### High-Level Technical Stack
*   **Language:** Java
*   **UI Framework:** XML Layouts implementing Material Design 3 components, including **RecyclerView** for menu lists, **CardView** for meal details, and **BottomNavigationView** for core navigation.
*   **Architecture:** **MVVM (Model-View-ViewModel)** to ensure a clean separation of concerns and maintainable code.
*   **Image Loading:** **Glide** for efficient image fetching, caching, and circular transformations.
*   **Data Persistence:** **Local JSON** assets for primary menu data and **SharedPreferences** for persisting user favorites.
*   **Visual Identity:** Energetic **Green and Yellow** theme following M3 guidelines for a modern and accessible campus experience.

### UI Design Image
![UI Design](file://C:/Users/I-Dev Sow/AndroidStudioProjects/UGBMenu/input_images/image_1.png)

## Implementation Steps
**Total Duration:** 26m 53s

### Task_1_Foundation: Set up Material 3 theme with a green and yellow palette, define data models, and implement the data layer including JSON parsing for menu data and storage logic for favorites.
- **Status:** COMPLETED
- **Updates:** Completed the foundation phase.
- **Acceptance Criteria:**
  - Material 3 theme with green/yellow color scheme is implemented
  - JSON menu data is correctly parsed into Kotlin models
  - Storage mechanism for favorites is functional

### Task_2_Navigation_and_Menus: Implement the Splash screen, Bottom Navigation, and the Restaurant menu screens (Resto 1 & 2). Use Material 3 cards for menu items and Coil for image loading.
- **Status:** COMPLETED
- **Updates:** Implemented the Splash screen with logo, Bottom Navigation (Home, Menus, Favorites, Plus), and the Restaurant menu screens.
- **Acceptance Criteria:**
  - Splash screen appears on launch
  - Bottom navigation navigates between Resto 1, Resto 2, and Favorites
  - Menu screens display food items with images and details
  - The implemented UI must match the design provided in input_images/image_1.png
- **Duration:** 11m 41s

### Task_3_Features_and_AI: Develop the Favorites management screen and the logic-based AI Assistant for menu queries. Implement local notifications for meal alerts.
- **Status:** COMPLETED
- **Updates:** Developed the Favorites management screen with the ability to view and remove saved items. Implemented a logic-based AI Assistant that handles natural language queries about the menu. Added local notification support for meal alerts, including permission handling for Android 13+. All features are integrated into the main navigation flow. Project is ready for final verification.
- **Acceptance Criteria:**
  - Favorites screen correctly filters and displays saved items
  - AI Assistant responds to basic menu-related queries
  - Local notifications are scheduled and displayed correctly
- **Duration:** 4m 28s

### Task_4_Verification: Perform a final run and verify application stability, alignment with requirements, and UI fidelity.
- **Status:** COMPLETED
- **Updates:** Final verification successful. 
- App icon refined and matches theme.
- Stability verified (no crashes).
- All features (Home, Menus, Favorites, AI Assistant, Notifications) fully functional.
- UI fidelity matches Design Image with Material 3 and Green/Yellow theme.
Project is complete.
- **Acceptance Criteria:**
  - App builds and runs successfully without crashes
  - All navigation paths work as expected
  - UI matches design in input_images/image_1.png
  - Make sure all existing tests pass
- **Duration:** 10m 44s

